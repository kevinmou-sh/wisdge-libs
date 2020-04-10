package com.wisdge.utils.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 树形记录整理器，将数据库查询结果队列中的记录按照parentId进行整理。<br>
 * 查询结果队列中的对象必须继承 TreeBean类.
 * 
 * @author Kevin MOU
 * @version 1.0.1.20130119
 * @see TreeBean
 */
public class TreeBeanFactory {
	private static final Log logger = LogFactory.getLog(TreeBeanFactory.class);

	/**
	 * Generate beans in tree<br>
	 * The beans has to extended TreeBean
	 * 
	 * @param beanList
	 *            对象列表
	 * @return 树形对象列表
	 * @throws Exception
	 *             编译错误
	 */
	public static <T extends TreeBean> List<T> generator(List<T> beanList) throws Exception {
		return generator(beanList, null);
	}

	/**
	 * Generate beans in tree<br>
	 * The beans has to extended TreeBean
	 * 
	 * @param beanList
	 *            进行整理的数据库查询结果记录集合
	 * @param compare
	 *            TbCompare对象，用于进行归类树形时候在同级别中的排序
	 * @return 树形对象列表
	 * @throws Exception
	 *             编译错误
	 */
	public static <T extends TreeBean> List<T> generator(List<T> beanList, TbCompare<T> compare) throws Exception {
		return generator(beanList, compare, false);
	}

	/**
	 * Generate beans in tree<br>
	 * The beans has to extended TreeBean
	 * 
	 * @param beanList
	 *            进行整理的数据库查询结果记录集合
	 * @param compare
	 *            TbCompare对象，用于进行归类树形时候在同级别中的排序
	 * @param keepResult
	 *            遇到异常情况是，是否继续编译树结构
	 * @return 树形对象列表
	 * @throws Exception
	 *             编译错误
	 */
	@SuppressWarnings("unchecked")
	public static <T extends TreeBean> List<T> generator(List<T> beanList, TbCompare<T> compare, boolean keepResult) throws Exception {
		Map<String, T> treeMap = new HashMap<String, T>();
		List<T> roots = new ArrayList<T>();

		// 将队列中所有对象放入HASHMAP中，便于检索
		for (T bean : beanList) {
			treeMap.put(bean.getId(), bean);
		}

		// 为每一个对象进行父子节点归属，并找到根节点
		for (T bean : beanList) {
			if (isRoot(bean)) {
				addBeanToList(bean, roots, compare);
			} else {
				String parentId = bean.getParentId();
				T parent = treeMap.get(parentId);
				if (parent != null) {
					bean.setParent(parent);
					addBeanToList(bean, (List<T>) parent.getChildren(), compare);
				} else {
					if (keepResult)
						logger.error(new MissingParentException(bean));
					else
						throw new MissingParentException(bean);
				}
			}
		}

		return roots;
	}

	public static boolean isRoot(TreeBean bean) {
		return (bean.getParentId() == null || bean.getParentId().isEmpty());
	}

	/**
	 * Add target bean to list, with compare adapter
	 * 
	 * @param child
	 *            Target bean who implements ITreeBean
	 * @param children
	 *            List&lt;ITreeBean&gt;
	 * @param compare
	 *            TbCompare&lt;ITreeBean&gt;
	 */
	public static <T extends TreeBean> void addBeanToList(T child, List<T> children, TbCompare<T> compare) {
		if (compare == null) {
			children.add(child);
		} else {
			for (int i = 0; i < children.size(); i++) {
				if (compare.compare(child, children.get(i)) < 0) {
					children.add(i, child);
					return;
				}
			}
			children.add(child);
		}
	}
}
