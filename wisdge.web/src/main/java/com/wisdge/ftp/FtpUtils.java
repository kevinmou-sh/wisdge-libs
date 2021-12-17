package com.wisdge.ftp;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.wisdge.utils.StringUtils;

/**
 * FTP操作类，快速读取下载文件
 * @author Kevin MOU
 */
@Slf4j
public class FtpUtils {
	/**
	 * FTP协议里面，规定文件名编码为ISO-8859-1
	 */
	private static String SERVER_CHARSET = "ISO-8859-1";

	public static FTPClient getClient(FTPConfig config) throws SocketException, IOException, FTPException {
		return getClient(config, System.out);
	}

	public static FTPClient getClient(FTPConfig config, OutputStream out) throws SocketException, IOException, FTPException {
		return getClient(config, new PrintWriter(out));
	}

	public static FTPClient getClient(FTPConfig config, PrintWriter writer) throws SocketException, IOException, FTPException {
		return getClient(config, new PrintCommandListener(writer, true));
	}

	public static FTPClient getClient(FTPConfig config, ProtocolCommandListener listener) throws SocketException, IOException, FTPException {
		FTPClient ftpClient;

		if (config.isSsl()) {
			FTPSClient ftps;
			if (!StringUtils.isEmpty(config.getProtocol())) {
				ftps = new SSLSessionReuseFTPSClient(config.getProtocol(), config.isImplicit());
				ftps.setAuthValue(config.getProtocol());
			} else
				ftps = new FTPSClient(config.isImplicit());
			if ("all".equals(config.getTrustManager())) {
				ftps.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
			} else if ("valid".equals(config.getTrustManager())) {
				ftps.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
			} else if ("none".equals(config.getTrustManager())) {
				ftps.setTrustManager(null);
			}
			ftpClient = ftps;
		} else
			ftpClient = new FTPClient();

		ftpClient.addProtocolCommandListener(listener);
		connect(ftpClient, config);

		return ftpClient;
	}

	public static void connect(FTPClient ftpClient, FTPConfig config) throws IOException, FTPException {
		if (ftpClient.isConnected())
			return;

		ftpClient.connect(config.getHostname(), config.getPort());

		if (! FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			ftpClient.disconnect();
			throw new FTPException("FTP服务器拒绝了链接请求");
		}
		if (! ftpClient.login(config.getUsername(), config.getPassword())) {
			ftpClient.disconnect();
			throw new FTPException("FTP服务登录失败");
		}
		if (config.isSsl() && config.isForcePortP()) {
			((FTPSClient) ftpClient).execPBSZ(0);
			((FTPSClient) ftpClient).execPROT("P");
		}

		// 开启服务器对UTF-8的支持
		ftpClient.sendCommand("OPTS UTF8", "ON");
		ftpClient.setControlEncoding("UTF-8");

		if (config.isPassive()) // 设置FTP为被动模式
			ftpClient.enterLocalPassiveMode();
	}

	public static byte[] retrieveFile(FTPConfig config, String remote) throws SocketException, IOException, FTPException, JSchException, SftpException {
		if (config.isSsh()) {
			ChannelSftp sftp = null;
			try {
				sftp = getChannel(config);
				return retrieveFile(sftp, remote);
			} finally {
				closeChannel(sftp);
			}
		} else {
			FTPClient ftpClient = getClient(config);
			try {
				return retrieveFile(ftpClient, remote);
			} finally {
				ftpClient.disconnect();
			}
		}
	}

	/**
	 * Retrieve file from FTP/FTPS server
	 * @param ftpClient
	 * @param remote
	 * @return
	 * @throws IOException
	 * @throws FTPException
	 */
	public static byte[] retrieveFile(FTPClient ftpClient, String remote) throws IOException, FTPException {
		String encodeRemote = new String(remote.getBytes(StandardCharsets.UTF_8), SERVER_CHARSET);
		ftpClient.setBufferSize(1024);
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			if (! ftpClient.retrieveFile(encodeRemote, baos)) {
				throw new FTPException("Retrieve file failed: " + remote);
			}
			return baos.toByteArray();
		} finally {
			IOUtils.closeQuietly(baos);
		}
	}

	/**
	 * Retrieve input stream from FTP/FTPS server
	 * @param ftpClient
	 * @param remote
	 * @return
	 * @throws IOException
	 * @throws FTPException
	 */
	public static InputStream retrieveStream(FTPClient ftpClient, String remote) throws IOException {
		String encodeRemote = new String(remote.getBytes(StandardCharsets.UTF_8), SERVER_CHARSET);
		ftpClient.setBufferSize(1024);
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		return ftpClient.retrieveFileStream(encodeRemote);
	}

	/**
	 * Retrieve file from SFTP server
	 * @param sftp
	 * @param remote
	 * @return
	 * @throws SftpException
	 * @throws IOException
	 */
	public static byte[] retrieveFile(ChannelSftp sftp, String remote) throws SftpException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			sftp.get(remote, baos);
	    	return baos.toByteArray();
		} finally {
			IOUtils.closeQuietly(baos);
		}
	}

	/**
	 * Retrieve input stream from SFTP server
	 * @param sftp
	 * @param remote
	 * @return
	 * @throws SftpException
	 * @throws IOException
	 */
	public static InputStream retrieveStream(ChannelSftp sftp, String remote) throws SftpException, IOException {
		return sftp.get(remote);
	}

	public static void storeFile(FTPConfig config, String remote, byte[] data) throws SftpException, JSchException, IOException, FTPException {
		if (config.isSsh()) {
			ChannelSftp sftp = null;
			try {
				sftp = getChannel(config);
				storeFile(sftp, remote, data);
			} finally {
				closeChannel(sftp);
			}
		} else {
			FTPClient ftpClient = getClient(config);
			try {
				storeFile(ftpClient, remote, data);
			} finally {
				ftpClient.disconnect();
			}
		}
	}

	public static void storeStream(FTPConfig config, String remote, InputStream inputStream) throws SftpException, JSchException, IOException, FTPException {
		if (config.isSsh()) {
			ChannelSftp sftp = null;
			try {
				sftp = getChannel(config);
				storeStream(sftp, remote, inputStream);
			} finally {
				closeChannel(sftp);
			}
		} else {
			FTPClient ftpClient = getClient(config);
			try {
				storeStream(ftpClient, remote, inputStream);
			} finally {
				ftpClient.disconnect();
			}
		}
	}

	/**
	 * Store file into FTP/FTPS server
	 * @param ftpClient
	 * @param remote
	 * @param data
	 * @throws IOException
	 * @throws FTPException
	 */
	public static void storeFile(FTPClient ftpClient, String remote, byte[] data) throws IOException, FTPException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		storeStream(ftpClient, remote, bais);
	}

	public static void storeStream(FTPClient ftpClient, String remote, InputStream inputStream) throws IOException, FTPException {
		String encodeRemote = new String(remote.getBytes(StandardCharsets.UTF_8), SERVER_CHARSET);
		String path = FilenameUtils.getPath(encodeRemote);
		String saveName = FilenameUtils.getName(encodeRemote);
		ftpClient.changeWorkingDirectory("/");
		if (! StringUtils.isEmpty(path)) {
			if (! cwd(ftpClient, path)) {
				throw new FTPException("Create directory faild: " + path);
			}
		}

		ftpClient.setBufferSize(1024);
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		try (InputStream source = inputStream) {
			if (! ftpClient.storeFile(saveName, source)) {
				throw new FTPException("Store file faild: " + remote);
			}
		}
	}

	/**
	 * Store file into SFTP server
	 * @param sftp
	 * @param remote
	 * @param data
	 * @throws SftpException
	 * @throws UnsupportedEncodingException
	 */
	public static void storeFile(ChannelSftp sftp, String remote, byte[] data) throws SftpException, IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		storeStream(sftp, remote, bais);
	}

	public static void storeStream(ChannelSftp sftp, String remote, InputStream inputStream) throws SftpException, IOException {
		String path = FilenameUtils.getPath(remote);
		sftp.cd("/");
		if (! StringUtils.isEmpty(path)) {
			cwd(sftp, path);
		}

		try (InputStream source = inputStream) {
			sftp.put(source, remote, ChannelSftp.OVERWRITE);
		}
	}

	public static void deleteFile(FTPConfig config, String remote) throws SftpException, JSchException, SocketException, IOException, FTPException {
		if (config.isSsh()) {
			ChannelSftp sftp = null;
			try {
				sftp = getChannel(config);
				sftp.rm(remote);
			} finally {
				closeChannel(sftp);
			}
		} else {
			String encodeRemote = new String(remote.getBytes(StandardCharsets.UTF_8), SERVER_CHARSET);
			FTPClient ftpClient = getClient(config);
			try {
				if (! ftpClient.deleteFile(encodeRemote)) {
					throw new FTPException("Delete file faild: " + remote);
				}
			} finally {
				ftpClient.disconnect();
			}
		}
	}

	public static void moveFile(FTPConfig config, String source, String target) throws SftpException, JSchException, SocketException, IOException, FTPException {
		if (config.isSsh()) {
			ChannelSftp sftp = null;
			try {
				sftp = getChannel(config);
				String path = FilenameUtils.getPath(target);
				sftp.cd("/");
				if (! StringUtils.isEmpty(path)) {
					cwd(sftp, path);
				}

				sftp.cd("/");
				sftp.rename(source, target);
			} finally {
				closeChannel(sftp);
			}
		} else {
			source = new String(source.getBytes(StandardCharsets.UTF_8), SERVER_CHARSET);
			target = new String(target.getBytes(StandardCharsets.UTF_8), SERVER_CHARSET);

			FTPClient ftpClient = getClient(config);
			try {
				String path = FilenameUtils.getPath(target);
				ftpClient.changeWorkingDirectory("/");
				if (! StringUtils.isEmpty(path)) {
					if (! cwd(ftpClient, path)) {
						throw new FTPException("Create directory failed: " + path);
					}
				}
				ftpClient.changeWorkingDirectory("/");
				if (ftpClient.rename(source, target)) {
					throw new FTPException("Move file failed: " + source + " to " + target);
				}
			} finally {
				ftpClient.disconnect();
			}
		}
	}

	public static void clearFolder(FTPConfig config, String folder) throws JSchException, SftpException, SocketException, IOException, FTPException {
		folder = new String(folder.getBytes(StandardCharsets.UTF_8), SERVER_CHARSET);
		if (config.isSsh()) {
			ChannelSftp sftp = null;
			try {
				sftp = FtpUtils.getChannel(config);
				final List<ChannelSftp.LsEntry> files = sftp.ls(folder);
			    for (ChannelSftp.LsEntry le : files) {
			        final String name = le.getFilename();
			        if (le.getAttrs().isDir()) {
			            if (".".equals(name) || "..".equals(name)) {
			                continue;
			            }
			            sftp.rmdir(folder + "/" + name);
			        } else {
			            sftp.rm(folder + "/" + name);
			        }
			    }
			} finally {
				closeChannel(sftp);
			}
		} else {
			FTPClient ftpClient = FtpUtils.getClient(config);
			try {
				for(FTPFile ftpFile : ftpClient.listFiles(folder)) {
					ftpClient.deleteFile(folder + "/" + ftpFile.getName());
				}
			} finally {
				ftpClient.disconnect();
			}
		}
	}

	private static boolean cwd(FTPClient ftpClient, String directory) {
		String root = "";
		for(String dir : directory.split("/")) {
			if (StringUtils.isEmpty(dir))
				continue;

			root += "/" + dir;
			if (! changeWorkingDirectory(ftpClient, root)) {
				log.error("无效的文件路径：" + root);
				return false;
			}
		}
		return true;
	}

	private static boolean changeWorkingDirectory(FTPClient ftpClient, String directory) {
		try {
			//logger.debug("entry: " + directory);
			if (! ftpClient.changeWorkingDirectory(directory)) {
				try {
					if (! ftpClient.makeDirectory(directory)) {
						log.error("无法创建文件路径：" + directory);
						return false;
					}
					return ftpClient.changeWorkingDirectory(directory);
				} catch(Exception e) {
					log.error(e.getMessage(), e);
					return false;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	private static void cwd(ChannelSftp sftp, String path) throws SftpException {
		String[] folders = path.split("/");
		for (String folder : folders) {
			if (folder.length() > 0) {
				try {
					// System.out.println("Current Folder path before cd:" + folder);
					sftp.cd(folder);
				} catch (SftpException e) {
					// System.out.println("Inside create folders: ");
					sftp.mkdir(folder);
					sftp.cd(folder);
				}
			}
		}
	}

	public static ChannelSftp getChannel(FTPConfig config) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(config.getUsername(), config.getHostname(), config.getPort());
        if (! StringUtils.isEmpty(config.getPassword())) {
            session.setPassword(config.getPassword()); // 设置密码
        }
        Properties props = new Properties();
        props.put("StrictHostKeyChecking", "no");
        session.setConfig(props); // 为Session对象设置properties
        if (config.getTimeout() > 0)
        	session.setTimeout(config.getTimeout()); // 设置timeout时间
        session.connect(); // 通过Session建立链接

        Channel channel = session.openChannel("sftp"); // 打开SFTP通道
        channel.connect(); // 建立SFTP通道的连接
        return (ChannelSftp) channel;
    }

    public static void closeChannel(Channel channel) throws JSchException {
        if (channel != null) {
        	Session session = channel.getSession();
            channel.disconnect();
            session.disconnect();
        }
    }
}

@Slf4j
class SSLSessionReuseFTPSClient extends FTPSClient {
	public SSLSessionReuseFTPSClient(String protocol, boolean impicit) {
		super(protocol, impicit);
	}

    @Override
    protected void _prepareDataSocket_(final Socket socket) throws IOException {
        if(socket instanceof SSLSocket) {
            final SSLSession session = ((SSLSocket) _socket_).getSession();
            final SSLSessionContext context = session.getSessionContext();
            try {
                final Field sessionHostPortCache = context.getClass().getDeclaredField("sessionHostPortCache");
                sessionHostPortCache.setAccessible(true);
                final Object cache = sessionHostPortCache.get(context);
                final Method method = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                method.setAccessible(true);
                final String key = String.format("%s:%s", socket.getInetAddress().getHostName(), String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);
                method.invoke(cache, key, session);
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}

