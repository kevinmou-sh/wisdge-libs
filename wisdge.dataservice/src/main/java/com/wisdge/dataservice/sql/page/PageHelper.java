package com.wisdge.dataservice.sql.page;

public class PageHelper {
    /**
     * 得到查询总数的sql
     */
    public static String getCountString(String querySelect) {
        return new StringBuffer(querySelect.length()).append("select count(1) count from (").append(querySelect).append(" ) t").toString();
    }

    /**
     * 得到分页的SQL
     *
     * @param offset 偏移量
     * @param limit  位置
     * @return 分页SQL
     */
    public static String getLimitString(String querySelect, int offset, int limit) {
        return querySelect + " limit " + offset + "," + limit;
    }
}
