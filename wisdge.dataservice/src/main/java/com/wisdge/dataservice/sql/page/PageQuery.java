package com.wisdge.dataservice.sql.page;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {
    /**
     * 分页大小
     * <p>默认20</p>
     */
    private int pageSize;

    /**
     * 当前分页,默认第一页
     */
    private int pageIndex;

    public PageQuery(int pageIndex, int pageSize) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    public PageQuery() {
        this.pageIndex = 1;
        this.pageSize = 50;
    }
}
