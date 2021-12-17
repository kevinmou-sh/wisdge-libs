package com.wisdge.commons.interfaces;

import com.wisdge.commons.filestorage.IProgressListener;

import java.io.InputStream;

public interface IFileStorageClient {

	/**
	 * 初始化服务
	 */
	void init(boolean security);

	/**
	 * @return String 文件服务的根路径
	 */
	String getRemoteRoot();

	/**
	 * 保存文件
	 * @param filepath String 文件路径
	 * @param data byte[] 文件内容
	 * @return String 保存后的文件新路径
	 * @throws Exception
	 */
	String save(String filepath, byte[] data) throws Exception;

	/**
	 * 保存文件流
	 * @param filepath String 文件路径
	 * @param inputStream InputStream 文件流
	 * @param size long 文件长度
	 * @return String 保存后的文件新路径
	 * @throws Exception
	 */
	String saveStream(String filepath, InputStream inputStream, long size) throws Exception;

	/**
	 * 保存文件流
	 * @param filepath String 文件路径
	 * @param inputStream InputStream 文件流
	 * @param size long 文件长度
	 * @param progressListener IProgressListener 上传文件时的监听
	 * @return String 保存后的文件新路径
	 * @throws Exception
	 */
	String saveStream(String filepath, InputStream inputStream, long size, IProgressListener progressListener) throws Exception;

	/**
	 * 读取文件
	 * @param filepath String 文件路径
	 * @return byte[] 文件内容
	 * @throws Exception
	 */
	byte[] retrieve(String filepath) throws Exception;

	/**
	 * 读取文件流
	 * @param filepath String 文件路径
	 * @return InputStream
	 * @throws Exception
	 */
	void retrieveStream(String filepath, IFileExecutor executor) throws Exception;

	/**
	 * 删除文件
	 * @param filepath String 文件路径
	 * @throws Exception
	 */
	void delete(String filepath) throws Exception;

	/**
	 * 判断该服务是否需要安全控制
	 * @return boolean
	 */
	boolean isSecurity();

	/**
	 * 销毁服务
	 */
	void destroy();
}
