package com.arka.purchase_service.infraestructure.driven.persistence.config;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostgreSqlConnectionPool {
    public static final int INITIAL_SIZE = 5;
    public static final int MAX_SIZE = 10;
    public static final int MAX_IDLE_TIME = 60;
    public static final int CONNECTION_TIMEOUT = 30;

    @Value("${postgresql.host}")
    private String host;

    @Value("${postgresql.port}")
    private int port;

    @Value("${postgresql.database}")
    private String database;

    @Value("${postgresql.schema}")
    private String schema;

    @Value("${postgresql.username}")
    private String username;

    @Value("${postgresql.password}")
    private String password;

    @Value("${postgresql.ssl:allow}")
    private String sslMode; // default a "allow" si no está

    @Bean
    public ConnectionPool getConnectionConfig() {

        PostgresqlConnectionConfiguration dbConfiguration = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .schema(schema)
                .username(username)
                .password(password)
                .connectTimeout(java.time.Duration.ofSeconds(CONNECTION_TIMEOUT))
                .sslMode(SSLMode.valueOf(sslMode.toUpperCase()))
                .build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
                .name("api-postgres-connection-pool")
                .initialSize(INITIAL_SIZE)
                .maxSize(MAX_SIZE)
                .maxIdleTime(java.time.Duration.ofMinutes(MAX_IDLE_TIME))
                .maxCreateConnectionTime(java.time.Duration.ofSeconds(CONNECTION_TIMEOUT))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfiguration);
    }
}


