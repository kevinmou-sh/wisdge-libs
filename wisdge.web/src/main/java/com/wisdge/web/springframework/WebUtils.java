package com.wisdge.web.springframework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.wisdge.dataservice.utils.JSonUtils;
import com.wisdge.utils.ByteUtils;

@Slf4j
public class WebUtils extends org.springframework.web.util.WebUtils {
	/**
	 * 从request请求中读取payload字符串。不同于getRequestPayload, 该方法支持重复读取
	 * @param request HttpServletRequest
	 * @return String
	 * @throws IOException
	 */
	public static String getPayload(HttpServletRequest request) throws IOException {
		return new MultiReadHttpServletRequest(request).getPayload();
	}

	public static String getRequestPayload(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader reader = request.getReader();
			if (reader.markSupported())
				reader.mark(0);
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				builder.append(buff, 0, len);
			}
			if (reader.markSupported()){
				try {
					reader.reset();
				} catch(Exception e) {}
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		return builder.toString();
	}

	public static Map<String, Object> getRequestPayloadBean(HttpServletRequest request) {
		try {
			return getRequestPayloadBean(request, Map.class);
		} catch (Exception e) {
			log.error("Convert payload string to map failed", e);
			return Collections.emptyMap();
		}
	}

	public static <T> T getRequestPayloadBean(HttpServletRequest request, Class<T> beanClass) throws Exception {
		String payload = getRequestPayload(request);
		return JSonUtils.read(payload, beanClass);
	}

	private static Properties ContentTypeProperties = null;
	public static String getContentType(String extension) throws IOException {
		if (ContentTypeProperties != null)
			return ContentTypeProperties.getProperty(extension);

		try (InputStream is = WebUtils.class.getClassLoader().getResourceAsStream("contentType.properties")) {
			if (is == null) throw new NullPointerException("contentType资源文件丢失");
			ContentTypeProperties = new Properties();
			ContentTypeProperties.load(is);
			return ContentTypeProperties.getProperty(extension);
		} catch(Exception e) {
			throw e;
		}
	}

	public static String getString(HttpServletRequest request, String parameter) {
		return getString(request, parameter, "");
	}

	public static String getString(HttpServletRequest request, String parameter, String defaultValue) {
		String value = request.getParameter(parameter);
		if (StringUtils.isEmpty(value))
			return defaultValue;
		return value;
	}

	public static Boolean getBoolean(HttpServletRequest request, String parameter) {
		String value = request.getParameter(parameter);
		if (StringUtils.isEmpty(value))
			return false;
		return Boolean.parseBoolean(value);
	}

	public static long getLong(HttpServletRequest request, String parameter) {
		return getLong(request, parameter, 0L);
	}

	public static long getLong(HttpServletRequest request, String parameter, long defaultValue) {
		String value = request.getParameter(parameter);
		if (StringUtils.isEmpty(value))
			return defaultValue;
		return Long.parseLong(value);
	}

	public static int getInteger(HttpServletRequest request, String parameter) {
		return getInteger(request, parameter, 0);
	}

	public static int getInteger(HttpServletRequest request, String parameter, int defaultValue) {
		String value = request.getParameter(parameter);
		if (StringUtils.isEmpty(value))
			return defaultValue;
		return Integer.parseInt(value);
	}

	public static double getDouble(HttpServletRequest request, String parameter) {
		return getDouble(request, parameter, 0D);
	}

	public static double getDouble(HttpServletRequest request, String parameter, double defaultValue) {
		String value = request.getParameter(parameter);
		if (StringUtils.isEmpty(value))
			return defaultValue;
		return Double.parseDouble(value);
	}

	public static void removeSessionAttribute(HttpServletRequest request, String name) {
		Assert.notNull(request, "Request must not be null");
		HttpSession session = request.getSession(false);
		if (session != null)
			session.removeAttribute(name);
	}

	public static byte[] getFile(HttpServletRequest request, String filename) {
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
			MultipartFile mFile = mr.getFile(filename);
			if (mFile != null) {
				try {
					return mFile.getBytes();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			} else
				log.error("文件不存在:" + filename);
		}
		return null;
	}

    /**
     * 断点续传支持
     * @param data byte[]
     * @param request
     * @return 跳过多少字节
     */
    private static RangeSettings getRange(byte[] data, HttpServletRequest request) {
        String range = request.getHeader("Range");
        if (range == null)
        	return new RangeSettings(0, data.length, data.length, data.length);
        return getSettings(data.length, range.replaceAll("bytes=", ""));
    }

    private static RangeSettings getSettings(long len, String range) {
        long contentLength = 0;
        long start = 0;
        long end = 0;
        if (range.startsWith("-"))// -500，最后500个
        {
             contentLength = Long.parseLong(range.substring(1));//要下载的量
             end = len-1;
             start = len - contentLength;
        }
        else if (range.endsWith("-"))//从哪个开始
        {
            start = Long.parseLong(range.replace("-", ""));
            end = len -1;
            contentLength = len - start;
        }
        else//从a到b
        {
            String[] se = range.split("-");
            start = Long.parseLong(se[0]);
            end = Long.parseLong(se[1]);
            contentLength = end-start+1;
        }
        return new RangeSettings(start, end, contentLength, len);
    }

	public static void out(HttpServletRequest request, HttpServletResponse response, byte[] data, String filename, Boolean download) {
		if (data == null)
			data = new byte[0];

        RangeSettings rangeSettings = getRange(data, request);
		try {
			//response.reset();
			//response.setHeader("Pragma", "no-cache");
			//response.addHeader("Cache-Control", "must-revalidate");
			//response.addHeader("Cache-Control", "no-cache");
			//response.addHeader("Cache-Control", "no-store");
			//response.setDateHeader("Expires", 0);
			if (download) {
				setContentDisposition(request, response, filename);
				response.addHeader("Content-Length", "" + data.length);
				response.setContentType("application/octet-stream");
			} else {
				setContentDisposition(request, response, filename, true);
				response.addHeader("Content-Length", "" + data.length);
				response.setContentType(WebUtils.getContentType(FilenameUtils.getExtension(filename)));
			}

			if (!rangeSettings.isRange()) {
				response.addHeader("Content-Length", String.valueOf(rangeSettings.getTotalLength()));
			} else {
				log.info("Find seeking: " + rangeSettings.getStart());
				long start = rangeSettings.getStart();
				long end = rangeSettings.getEnd();
				long contentLength = rangeSettings.getContentLength();
				response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
				response.addHeader("Content-Length", String.valueOf(contentLength));
				StringBuffer contentRange = new StringBuffer("bytes ");
				contentRange.append(start).append("-").append(end).append("/").append(rangeSettings.getTotalLength());
				response.setHeader("Content-Range", contentRange.toString());
				data = ByteUtils.subBytes(data, (int)start, (int)contentLength);
			}

			ServletOutputStream out = response.getOutputStream();
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setContentDisposition(HttpServletRequest request, HttpServletResponse response, String filename, boolean inline) throws UnsupportedEncodingException {
		filename = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
		String type = inline ? "inline" : "attachment";
		response.setHeader("Content-Disposition", type + ";"
				+ "filename=\"" + filename + "\";"
				+ "filename*=UTF-8''" + filename);
	}

	public static void setContentDisposition(HttpServletRequest request, HttpServletResponse response, String filename) throws UnsupportedEncodingException {
		setContentDisposition(request, response, filename, false);
	}

}

class RangeSettings{
    private long start;
    private long end;
    private long contentLength;
    private long totalLength;

    public RangeSettings(){
        super();
    }

    public RangeSettings(long start, long end, long contentLength,long totalLength) {
        this.start = start;
        this.end = end;
        this.contentLength = contentLength;
        this.totalLength = totalLength;
    }

    public RangeSettings(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public boolean isRange() {
        return (start > 0 || contentLength < totalLength);
    }
}
