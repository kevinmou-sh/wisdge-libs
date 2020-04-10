package com.wisdge.eventcluster.multicast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class provides utility methods for assembling and disassembling a heartbeat payload.
 * <p/>
 * Care is taken to fit the payload into the MTU of ethernet, which is 1500 bytes. The algorithms in this class are capable of creating
 * payloads for CacheManagers containing approximately 500 cache peers to be replicated.
 */
final class PayloadUtil {
    private static final Log logger = LogFactory.getLog(PayloadUtil.class);

    /**
     * The maximum transmission unit. This varies by link layer. For ethernet, fast ethernet and
     * gigabit ethernet it is 1500 bytes, the value chosen.
     * <p/>
     * Payloads are limited to this so that there is no fragmentation and no necessity for a complex reassembly protocol.
     */
    public static final int MTU = 1500;

    /**
     * Delmits URLS sent via heartbeats over sockets
     */
    public static final String URL_DELIMITER = "|";

    /**
     * {@link #URL_DELIMITER} as a regular expression. Package protected, used in tests only
     */
    static final String URL_DELIMITER_REGEXP = "\\|";

    /**
     * Utility class therefore precent construction
     */
    private PayloadUtil() {
        // noop
    }

    /**
     * Gzips a byte[]. For text, approximately 10:1 compression is achieved.
     *
     * @param ungzipped
     *            the bytes to be gzipped
     * @return gzipped bytes
     */
    public static byte[] gzip(byte[] ungzipped) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bytes);
            gzipOutputStream.write(ungzipped);
            gzipOutputStream.close();
        } catch (IOException e) {
        	logger.error("Could not gzip " + Arrays.toString(ungzipped));
        }
        return bytes.toByteArray();
    }

    /**
     * The fastest UnGZip implementation
     */
    public static byte[] ungzip(final byte[] gzipped) {
        byte[] ungzipped = new byte[0];
        try {
            final GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(gzipped));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(gzipped.length);
            final byte[] buffer = new byte[PayloadUtil.MTU];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = inputStream.read(buffer, 0, PayloadUtil.MTU);
                if (bytesRead != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
            }
            ungzipped = byteArrayOutputStream.toByteArray();
            inputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
        	logger.error("Could not ungzip. Heartbeat will not be working. " + e.getMessage());
        }
        return ungzipped;
    }

}
