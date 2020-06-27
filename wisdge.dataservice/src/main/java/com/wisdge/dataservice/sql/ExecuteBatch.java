package com.wisdge.dataservice.sql;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class ExecuteBatch {

    private SqlTemplateManager sqlTemplateManager;
    private PlatformTransactionManager txManager;
    private JdbcTemplate jdbcTemplate;
    private List<Batch> batches;

    public ExecuteBatch(SqlTemplateManager sqlTemplateManager, JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {
        this.sqlTemplateManager = sqlTemplateManager;
        this.txManager = transactionManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(String sqlKey, Object...objects) {
        String sql = sqlTemplateManager.getTemplate(sqlKey);
        batches.add(new Batch(sql, objects));
    }

    public void run() throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // explicitly setting the transaction name is something that can be done only programmatically
//		def.setName("elite.dataservice.transaction.definition");
//		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // execute your business logic here
            for(Batch batch : batches) {
                jdbcTemplate.update(batch.getSql(), batch.getObjects());
            }
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }
}

class Batch {
    private String sql;
    private Object[] objects;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object... objects) {
        this.objects = objects;
    }

    public Batch(String sql, Object...objects) {
        this.sql = sql;
        this.objects = objects;
    }
}
