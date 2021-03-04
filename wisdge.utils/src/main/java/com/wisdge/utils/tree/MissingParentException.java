package com.wisdge.utils.tree;

public class MissingParentException extends Exception {
	private static final long serialVersionUID = 2124141791567628730L;
	private TreeBean bean;

	public MissingParentException(TreeBean bean) {
		this.bean = bean;
	}

	@Override
	public String toString() {
		return ("Miss parent[" + bean.getParentId() + "] at TreeBean[" + bean.getId() + "]");
	}
}
