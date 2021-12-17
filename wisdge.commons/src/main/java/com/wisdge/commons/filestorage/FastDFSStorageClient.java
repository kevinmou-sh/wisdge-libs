package com.wisdge.commons.filestorage;

import com.wisdge.commons.interfaces.IFileExecutor;
import com.wisdge.commons.interfaces.IFileStorageClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class FastDFSStorageClient implements IFileStorageClient {
	private int connectTimeout = 2000;
	private int networkTimeout = 3000;
	private String charset = "UTF-8";
	private boolean httpAntiStealToken = false;
	private String httpSecretKey = "";
	private int httpTrackerHttpPort;
	private String httpTrackerServers;
	private ConnectionPool connectionPool;
	private String remoteRoot;
	private int poolSize = 5;
	private boolean security;

	class ConnectionPool {
	    //被使用的连接
	    private ConcurrentHashMap<StorageClient1,Object> busyConnectionPool = null;
	    //空闲的连接
	    private ArrayBlockingQueue<StorageClient1> idleConnectionPool = null;
	    private Object obj = new Object();

	    public ConnectionPool(){
	        busyConnectionPool = new ConcurrentHashMap<StorageClient1, Object>();
	        idleConnectionPool = new ArrayBlockingQueue<StorageClient1>(poolSize);
	        init();
	    }

	    // 初始化连接池
	    private void init(){
	        initClientGlobal();
	        TrackerServer trackerServer = null;
	        try {
	            TrackerClient trackerClient = new TrackerClient();
	            // 只需要一个tracker server连接
	            trackerServer = trackerClient.getConnection();
	            StorageServer storageServer = null;
	            StorageClient1 storageClient1 = null;
	            for(int i=0; i<poolSize; i++){
	                storageClient1 = new StorageClient1(trackerServer, storageServer);
	                idleConnectionPool.add(storageClient1);
	                log.debug("FastDFS Connections: connection +1");
	            }
	        } catch (Exception e) {
	        	log.error(e.getMessage(), e);
	        } finally {
	            if(trackerServer != null){
	                try {
	                    trackerServer.close();
	                } catch (IOException e) {
	                	log.error(e.getMessage(), e);
	                }
	            }
	        }
	    }

	    // 初始化客户端
	    private void initClientGlobal(){
	        // 连接超时时间
	        ClientGlobal.setConnectTimeout(connectTimeout);
	        // 网络超时时间
	        ClientGlobal.setNetworkTimeout(networkTimeout);
	        ClientGlobal.setAntiStealToken(httpAntiStealToken);
	        // 字符集
	        ClientGlobal.setCharset(charset);
	        ClientGlobal.setSecretKey(httpSecretKey);
	        // HTTP访问服务的端口号
	        ClientGlobal.setTrackerHttpPort(httpTrackerHttpPort);

	        String[] servers = httpTrackerServers.split(";");
	        InetSocketAddress[] trackerServers = new InetSocketAddress[servers.length];
	        for(int i=0; i<servers.length; i++) {
	        	String[] cs = servers[i].split(":");
	        	trackerServers[i] = new InetSocketAddress(cs[0], Integer.parseInt(cs[1]));
	        }
	        TrackerGroup trackerGroup = new TrackerGroup(trackerServers);
	        // Tracker server 集群
	        ClientGlobal.setTrackerGroup(trackerGroup);
	    }

	    //取出连接
	    public StorageClient1 checkout(int waitTime){
	        StorageClient1 storageClient1 = null;
	        try {
	            storageClient1 = idleConnectionPool.poll(waitTime, TimeUnit.SECONDS);
	            if(storageClient1 != null){
	                busyConnectionPool.put(storageClient1, obj);
	            }
	        } catch (InterruptedException e) {
	            storageClient1 = null;
	            log.error(e.getMessage(), e);
	        }
	        return storageClient1;
	    }

	    //回收连接
	    public void checkin(StorageClient1 storageClient1){
	        if(busyConnectionPool.remove(storageClient1) != null){
	            idleConnectionPool.add(storageClient1);
	        }
	    }

	    //如果连接无效则抛弃，新建连接来补充到池里
	    public void drop(StorageClient1 storageClient1){
	        if(busyConnectionPool.remove(storageClient1) != null){
	            TrackerServer trackerServer = null;
	            TrackerClient trackerClient = new TrackerClient();
	            try {
	                trackerServer = trackerClient.getConnection();
	                StorageClient1 newStorageClient1 = new StorageClient1(trackerServer,null);
	                idleConnectionPool.add(newStorageClient1);
	                log.debug("FastDFS Connections: connection +1");
	            } catch (Exception e) {
	                log.error(e.getMessage(), e);
	            } finally {
	                if(trackerServer != null){
	                    try {
	                        trackerServer.close();
	                    } catch (IOException e) {
	                        log.error(e.getMessage(), e);
	                    }
	                }
	            }
	        }
	    }
	}

	@Override
	public void init(boolean security) {
		this.security = security;
		connectionPool = new ConnectionPool();
	}

	@Override
	public String save(String filepath, byte[] data) throws Exception {
		String fileext = FilenameUtils.getExtension(filepath);
		String filename = FilenameUtils.getName(filepath);

	    NameValuePair[] metaList = new NameValuePair[4];
	    metaList[0] = new NameValuePair("fileName", filename);
	    metaList[1] = new NameValuePair("fileLength", String.valueOf(data.length));
	    metaList[2] = new NameValuePair("fileExt", fileext);
	    metaList[3] = new NameValuePair("fileAuthor", "wisdge");

        StorageClient1 storageClient1 = connectionPool.checkout(10);
        try {
            return storageClient1.uploadFile1(data, fileext, metaList);
        } catch (Exception e) {
            //如果出现了IO异常应该销毁此连接
        	connectionPool.drop(storageClient1);
            throw e;
        } finally {
        	connectionPool.checkin(storageClient1);
        }
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception {
		return saveStream(filePath, inputStream, size);
	}

	@Override
	public String saveStream(String filePath, InputStream inputStream, long size) throws Exception {
		String fileext = FilenameUtils.getExtension(filePath);
		String filename = FilenameUtils.getName(filePath);

		NameValuePair[] metaList = new NameValuePair[4];
		metaList[0] = new NameValuePair("fileName", filename);
		metaList[1] = new NameValuePair("fileLength", String.valueOf(size));
		metaList[2] = new NameValuePair("fileExt", fileext);
		metaList[3] = new NameValuePair("fileAuthor", "elite.ngs");

		StorageClient1 storageClient1 = connectionPool.checkout(10);
		try {
			return storageClient1.uploadFile1(null, size, os -> {
				byte[] bs = new byte[1024];
				int i;
				try (InputStream source = inputStream){
					while ((i = source.read(bs)) != -1) {
						os.write(bs, 0, i);
					}
				}
				return 0;
			}, fileext, metaList);
		} catch (Exception e) {
			//如果出现了IO异常应该销毁此连接
			connectionPool.drop(storageClient1);
			throw e;
		} finally {
			connectionPool.checkin(storageClient1);
		}
	}

	@Override
	public byte[] retrieve(String filepath) throws Exception {
		StorageClient1 storageClient1 = null;
        storageClient1 = connectionPool.checkout(10);
        try {
        	if (filepath.startsWith("/"))
        		filepath = filepath.substring(1);
            return storageClient1.downloadFile1(filepath);
        } catch (Exception e) {
        	log.error("download_file1 failed", e);
            //如果出现了IO异常应该销毁此连接
        	connectionPool.drop(storageClient1);
            throw e;
        } finally {
        	connectionPool.checkin(storageClient1);
        }
	}

	@Override
	public void retrieveStream(String filepath, IFileExecutor executor) throws Exception {
		StorageClient1 storageClient1 = connectionPool.checkout(10);
		try {
			if (filepath.startsWith("/"))
				filepath = filepath.substring(1);
			//TODO: Do not attain the stream based downloading, need to be updated
			byte[] data = storageClient1.downloadFile1(filepath);
			FileMetadata metadata = new FileMetadata();
			metadata.setContentLength(data.length);
			executor.execute(new ByteArrayInputStream(data), metadata);
		} catch (Exception e) {
			log.error("download_file1 failed", e);
			//如果出现了IO异常应该销毁此连接
			connectionPool.drop(storageClient1);
			throw e;
		} finally {
			connectionPool.checkin(storageClient1);
		}
	}

	@Override
	public void delete(String filepath) throws Exception {
		StorageClient1 storageClient1 = null;
        storageClient1 = connectionPool.checkout(10);
        try {
            if (storageClient1.deleteFile1(filepath) < 1)
            	throw new FileException("File delete failed");
        } catch (Exception e) {
            //如果出现了IO异常应该销毁此连接
        	connectionPool.drop(storageClient1);
            throw e;
        } finally {
        	connectionPool.checkin(storageClient1);
        }
	}

	@Override
	public void destroy() {

	}

}

