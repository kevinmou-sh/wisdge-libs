package com.wisdge.dataservice.sql;

import java.io.Serializable;
import java.util.Map;

public interface IRowMapper extends Serializable {
    public void rowMap(Map<String, Object> map);
}
