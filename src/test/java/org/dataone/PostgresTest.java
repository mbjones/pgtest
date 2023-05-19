package org.dataone;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Basic Postgres test using TestContainers.
 */
public class PostgresTest {

    private static PostgreSQLContainer pg = new PostgreSQLContainer<>("postgres:14")
            .withExposedPorts(5432)
            .withDatabaseName("dataone")
            .withUsername("dataone")
            .withPassword("not_too_secret");

    @Before
    public void before() {
        pg.start();
    }

    @After
    public void after() {
        pg.stop();
        pg.close();
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
}
