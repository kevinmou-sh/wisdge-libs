package com.wisdge.dataservice.sql;

public class SqlTemplate {
    private String sql;
    private String process;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public SqlTemplate(String sql, String process) {
        this.sql = sql;
        this.process = process;
    }
}
