/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic;

import com.yugabyte.data.jdbc.datasource.YugabyteTransactionManager;
import com.yugabyte.data.jdbc.repository.config.AbstractYugabyteJdbcConfiguration;
import com.yugabyte.data.jdbc.repository.config.EnableYsqlRepositories;
import com.yugabyte.data.jdbc.repository.config.YugabyteDialectResolver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.PGProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 */
@SpringBootApplication
public class PetClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }

    // TODO until we have an official starter
    @Configuration
    @EnableYsqlRepositories
    static class YsqlConfig extends AbstractYugabyteJdbcConfiguration {

        //https://github.com/brettwooldridge/HikariCP

        @Bean
        DataSource dataSource() {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:postgresql://127.0.0.1:5433,127.0.0.2:5433,127.0.0.3:5433/yugabyte");
            hikariConfig.setUsername("yugabyte");
            hikariConfig.addDataSourceProperty("load-balance", "true");  // Load Balance connections across multiple tservers
            return new HikariDataSource(hikariConfig);
        }

//        @Bean
//        DataSource dataSourceConfig2() {
//            Properties poolProperties = new Properties();
//            poolProperties.setProperty("dataSourceClassName", "com.yugabyte.ysql.YBClusterAwareDataSource");
//            poolProperties.setProperty("dataSource.serverName", "127.0.0.1");
//            poolProperties.setProperty("dataSource.portNumber", "5433");
//            poolProperties.setProperty("dataSource.user", "yugabyte");
//            poolProperties.setProperty("dataSource.password", "yugabyte");
//            poolProperties.setProperty("dataSource.loadBalance", "true");
//            poolProperties.setProperty("dataSource.additionalEndpoints", "127.0.0.2:5433,127.0.0.3:5433");
//
//            HikariConfig hikariConfig = new HikariConfig(poolProperties);
//            DataSource ybClusterAwareDataSource = new HikariDataSource(hikariConfig);
//            return ybClusterAwareDataSource;
//        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        TransactionManager transactionManager(DataSource dataSource) {
            return new YugabyteTransactionManager(dataSource);
        }
    }
}
