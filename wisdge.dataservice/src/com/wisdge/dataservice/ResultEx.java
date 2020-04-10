package com.wisdge.dataservice;

import java.io.Serializable;

/**
 * 执行方法的对象结果
 * <pre>
 * code: ERROR=-1, NORMAL=0, or SUCCESS=1
 * message: 对象结果信息
 * field: 字段名
 * value: 返回的对象值
 * </pre>
 * 
 * @author Kevin MOU
 */
public class ResultEx implements Serializable {
	private static final long serialVersionUID = 1202283157592991089L;
	public static final int ERROR = -1;
	public static final int NORMAL = 0;
	public static final int SUCCESS = 1;

	private int code;
	private String message;
	private String field;
	private Object value;

	public ResultEx() {
		this.code = ERROR;
	}
	public ResultEx(int code) {
		this.code = code;
	}

	public ResultEx(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResultEx(int code, String message, Object value) {
		this.code = code;
		this.message = message;
		this.value = value;
	}

	public ResultEx(int code, String message, String field, Object value) {
		this.code = code;
		this.message = message;
		this.field = field;
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
	 * @return 返回执行结果的名称
	 */
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
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
		if (this.field != null)
			sb.append("[FIELD:").append(field).append("]");
		if (this.message != null)
			sb.append("[MESSAGE:").append(message).append("]");
		if (this.value != null)
			sb.append("[OBJECT:").append(value.toString()).append("]");
		return sb.toString();
	}
}
