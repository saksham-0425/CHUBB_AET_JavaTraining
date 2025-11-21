package com.flightapp.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class R2dbcConfig {

    private final ConnectionFactory connectionFactory;

    @Bean
    public R2dbcTransactionManager transactionManager() {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public TransactionalOperator transactionalOperator(R2dbcTransactionManager txm) {
        return TransactionalOperator.create(txm);
    }
}
