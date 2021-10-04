package com.wisdge.dataservice.sql.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageInfo implements Serializable {
    private int pageIndex = 1;
    private int pageSize = 50;
}
