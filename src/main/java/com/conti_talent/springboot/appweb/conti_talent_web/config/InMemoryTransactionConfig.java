package com.conti_talent.springboot.appweb.conti_talent_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

@Configuration
public class InMemoryTransactionConfig {

    @Bean
    AbstractPlatformTransactionManager transactionManager() {
        return new AbstractPlatformTransactionManager() {
            @Override
            protected Object doGetTransaction() {
                return new Object();
            }

            @Override
            protected void doBegin(Object transaction, TransactionDefinition definition) {
                // No hay recurso externo que abrir en modo memoria.
            }

            @Override
            protected void doCommit(DefaultTransactionStatus status) {
                // Los repositorios en memoria aplican los cambios al guardar.
            }

            @Override
            protected void doRollback(DefaultTransactionStatus status) {
                // No hay rollback real sin una unidad de trabajo persistente.
            }
        };
    }
}
