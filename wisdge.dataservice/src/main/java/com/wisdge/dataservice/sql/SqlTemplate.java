package com.wisdge.dataservice.sql;

import java.util.List;

public class SqlTemplate {
    private String sql;
    private List<WhereExpress> expresses;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<WhereExpress> getExpresses() {
        return expresses;
    }

    public void setExpresses(List<WhereExpress> expresses) {
        this.expresses = expresses;
    }
}


class WhereExpress {
    private String expression;
    private String fragment;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }
}