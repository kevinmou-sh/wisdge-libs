package com.wisdge.commons.img;

public class ImgOperateException extends Exception {
	private static final long serialVersionUID = 1L;


	public ImgOperateException(String message) {
        super(message);
    }


    @Override
    public ImgOperateException fillInStackTrace() {
        return this;
    }
}