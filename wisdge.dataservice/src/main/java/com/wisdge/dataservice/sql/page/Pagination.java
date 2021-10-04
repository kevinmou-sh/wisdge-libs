package com.wisdge.dataservice.sql.page;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class Pagination {
    private int totalCount;
    private int pageSize;
    private int pageIndex;
    private int pageCount;
    private List columns;
    private List fields;

    public Pagination(int totalCount, int pageIndex, int pageSize) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;

        if (totalCount == 0) {
            this.pageCount = 1;
            this.pageIndex = 1;
        } else {
            if ((totalCount % pageSize) == 0) {
                this.pageCount = totalCount / pageSize;
            } else {
                this.pageCount = totalCount / pageSize + 1;
            }

            if (pageIndex > this.pageCount) {
                this.pageIndex = Math.max(1, this.pageCount); // 当前页最小为1
            } else {
                this.pageIndex = pageIndex;
            }
        }
    }
}
