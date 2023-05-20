# pgtest

An example repository showing how to use [Testcontainers](https://www.testcontainers.org/) to 
use stateful container dependencies in unit tests. In this case:

- Set up a postgresql database
- Use [Flyway](https://flywaydb.org/) to initialize the database
- Run a SQL select test against the database

I tried to keep the dependencies to a minimum, and some of these could be replaced with other choices:

- Testcontainers
- Postgresql JDBC driver
- FlywayDB (optional, could be replaced)
- [HikariCP](https://github.com/brettwooldridge/HikariCP) connection pool (optional, could be replaced)
