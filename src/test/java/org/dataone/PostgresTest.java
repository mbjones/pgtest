package org.dataone;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
// import org.testcontainers.db.AbstractContainerDatabaseTest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Basic Postgres test using TestContainers.
 */
public class PostgresTest {

    /* The embedded postgres database from Testcontainers, used in all tests */
    private static PostgreSQLContainer<?> pg;

    /* A connection to the database */
    static Connection connection;

    /* The Flyway database migrator used to manage database schema integrity */
    private static Flyway flyway;

    @Before
    public void initAll() {

        // Configure the embedded PG database
        pg = new PostgreSQLContainer<>("postgres:14");
        pg.withExposedPorts(5432)
                .withDatabaseName("dataone")
                .withUsername("dataone")
                .withPassword("not_too_secret");
        pg.start();
    }

    @Test
    public void pgShouldStart() {
        assertNotNull(pg);
        String jdbc = pg.getJdbcUrl();
        assertNotNull(jdbc);
        assertTrue(jdbc.startsWith("jdbc:postgresql"));
        assertTrue(pg.isRunning());
    }

    @Test
    public void pgWasConfigured() {
        assertTrue(pg.getDatabaseName().equals("dataone"));
        assertTrue(pg.getUsername().equals("dataone"));
        var ports = pg.getExposedPorts();
        assertTrue(ports.contains(Integer.valueOf(5432)));
    }

    @Test
    public void testFlywayMigrate() {
        // Use flyway to initialize schema
        flyway = Flyway.configure()
                .dataSource(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())
                .locations("filesystem:migrations")
                .cleanDisabled(false)
                .load();
        flyway.migrate();
    }

    @Test
    public void select() {
        try {
            ResultSet resultSet = runQuery(pg, "SELECT 1");
            int resultSetInt = resultSet.getInt(1);
            assertTrue(resultSetInt == 1);
            resultSet.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }

        // try {
        //     String sql = "SELECT object amount, name FROM products WHERE amount = 126000";
        //     ResultSet resultSet = runQuery(pg, sql);
        //     String object = resultSet.getString(1);
        //     int amount = resultSet.getInt(2);
        //     String name = resultSet.getString(3);
        //     assertTrue(object.equals("product"));
        //     assertTrue(amount == 126000);
        //     assertTrue(name.equals("Additional Data Storages"));
        //     resultSet.close();
        // } catch (SQLException e) {
        //     fail(e.getMessage());
        // }
    }

    @After
    public void tearDownAll() {

        // Close the database
        pg.close();
    }

    private ResultSet runQuery(PostgreSQLContainer<?> container, String sql) throws SQLException {
        DataSource ds = dsFromContainer(container);
        Statement statement = ds.getConnection().createStatement();
        statement.execute(sql);
        ResultSet results = statement.getResultSet();
        results.next();
        return results;
    }

    private DataSource dsFromContainer(PostgreSQLContainer<?> container) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(container.getDriverClassName());
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        return new HikariDataSource(config);
    }
}
