package com.wisdge.dataservice;

import java.io.Serializable;

/**
 * 执行方法的对象结果
 * <pre>
 * code: ERROR=-1, NORMAL=0, or SUCCESS=1
 * message: 对象结果信息
 * value: 返回的对象值
 * </pre>
 * 
 * @author Kevin MOU
 */
public class Result implements Serializable {
	private static final long serialVersionUID = 8241012103955662769L;
	public static final int ERROR = -1;
	public static final int WARN = 0;
	public static final int SUCCESS = 1;

	private int code;
	private String message;
	private Object value;

	public Result() {
		this.code = ERROR;
	}
	public Result(int code) {
		this.code = code;
	}

	public Result(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public Result(int code, String message, Object value) {
		this.code = code;
		this.message = message;
		this.value = value;
	}

	/**
	 * @return 返回执行结果，包括ERROR,NORMAL和SUCCESS，或者自定义的其他的值
	 */
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return 返回执行结果信息
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return 返回执行结果的对象
	 */
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[CODE:").append(code).append("]");
		if (this.message != null)
			sb.append("[MESSAGE:").append(message).append("]");
		if (this.value != null)
			sb.append("[OBJECT:").append(value.toString()).append("]");
		return sb.toString();
	}
}
