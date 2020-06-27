package com.wisdge.dataservice.sql;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;

public class ExecuteBatch {

    private SqlFactory factory;
    private List<Batch> batches = new ArrayList<>();

    public ExecuteBatch(SqlFactory factory) {
        this.factory = factory;
    }

    public void add(String sql, Object...objects) {
        batches.add(new Batch(sql, objects));
    }

    public void run() throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // explicitly setting the transaction name is something that can be done only programmatically
//		def.setName("dataservice.transaction.definition");
//		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        PlatformTransactionManager txManager = factory.getTransactionManager();
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // execute your business logic here
            for(Batch batch : batches) {
                factory.getJdbcTemplate().update(batch.getSql(), batch.getObjects());
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

    public Object[] getObjects() {
        return objects;
    }

    public Batch(String sql, Object...objects) {
        this.sql = sql;
        this.objects = objects;
    }
}
