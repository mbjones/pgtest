package org.dataone;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void postgresShouldStart() {
        var postgres = new PostgreSQLContainer<>(DockerImageName.parse(("postgres:9.6.12")));
        assertNotNull(postgres);
        postgres.start();
        String jdbc = postgres.getJdbcUrl();
        assertNotNull(jdbc);
        assertTrue(jdbc.startsWith("jdbc:postgresql"));
    }
}
