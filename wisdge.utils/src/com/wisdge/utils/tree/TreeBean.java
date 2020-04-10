package com.wisdge.utils.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于进行树状结构管理的Bean对象
 * 
 * @author Kevin MOU
 * @version 1.0.1.20130121
 * @see TreeBeanFactory
 */
public abstract class TreeBean {
	private TreeBean parent;
	private List<TreeBean> children;

	public TreeBean() {
		children = new ArrayList<TreeBean>();
	}

	public void addChild(TreeBean child) {
		child.setParent(this);
		children.add(child);
	}
	
	public void removeChild(TreeBean child) {
		children.remove(child);
	}

	public List<TreeBean> getChildren() {
		return children;
	}

	public TreeBean getParent() {
		return parent;
	}

	public void setParent(TreeBean parent) {
		this.parent = parent;
	}

	public boolean hasChild() {
		return children.size() > 0;
	}

	public abstract String getId();

	public abstract String getParentId();
}
