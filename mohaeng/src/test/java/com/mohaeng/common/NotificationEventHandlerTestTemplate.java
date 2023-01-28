package com.mohaeng.common;

import com.mohaeng.common.annotation.ApplicationTest;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

@ApplicationTest
public abstract class NotificationEventHandlerTestTemplate {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Test
    public void test() {
        validate();

        DataSourceTransactionManager tx = new DataSourceTransactionManager(dataSource);
        TransactionStatus status = tx.getTransaction(new DefaultTransactionDefinition());

        givenAndWhen();

        tx.commit(status);

        then();
    }

    private void validate() {
        if (!activeProfile.equals("test")) {
            throw new IllegalArgumentException("test 환경에서만 실행되어야 합니다.");
        }

        if (!(dataSource instanceof HikariDataSource)) {
            throw new IllegalArgumentException("HikariDataSource 여야 합니다.");
        }

        if (!((HikariDataSource) dataSource).getDriverClassName().equals("org.h2.Driver")) {
            throw new IllegalArgumentException("H2 DB여야 합니다.");
        }
    }

    protected abstract void givenAndWhen();

    protected abstract void then();
}
