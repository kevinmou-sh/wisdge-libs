package com.wisdge.dataservice.sql;

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
}
