package org.dataone;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Basic Postgres test using TestContainers.
 */
public class PostgresTest {

    /* The embedded postgres database from Testcontainers, used in all tests */
    private static PostgreSQLContainer pg;

    /* A connection to the database */
    static Connection connection;

    /* The Flyway database migrator used to manage database schema integrity */
    private static Flyway flyway;

    @Before
    public void initAll() {

        // Configure the embedded PG database
        pg = new PostgreSQLContainer<>("postgres:14")
                .withExposedPorts(5432)
                .withDatabaseName("dataone")
                .withUsername("dataone")
                .withPassword("not_too_secret");
        pg.start();

        // Set up a PostgreSQL datasource for testing (Stores)
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
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
        assertNotNull(pg);
        assertTrue(pg.getDatabaseName().equals("dataone"));
        assertTrue(pg.getUsername().equals("dataone"));
        var ports = pg.getExposedPorts();
        assertTrue(ports.contains(Integer.valueOf(5432)));
    }

    @Test
    public void testFlywayMigrate() {
        // Use flyway to initialize schema
        String jdbc = pg.getJdbcUrl();
        flyway = Flyway.configure()
                .dataSource(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())
                .locations("filesystem:migrations")
                .cleanDisabled(false)
                .load();
        flyway.migrate();
        //System.out.println(flyway.info());
    }

    @After
    public void tearDownAll() {

        // Close the database
        pg.close();
    }
}
