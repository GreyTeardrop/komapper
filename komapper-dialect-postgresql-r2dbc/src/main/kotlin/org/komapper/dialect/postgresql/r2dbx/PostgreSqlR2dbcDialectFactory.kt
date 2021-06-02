package org.komapper.dialect.postgresql.r2dbx

import org.komapper.dialect.postgresql.PostgreSqlDialect
import org.komapper.r2dbc.R2dbcDialect
import org.komapper.r2dbc.spi.R2dbcDialectFactory

class PostgreSqlR2dbcDialectFactory : R2dbcDialectFactory {
    override fun supports(driver: String): Boolean {
        return driver.lowercase() == PostgreSqlDialect.driver
    }

    // TODO
    override fun create(): R2dbcDialect {
        return PostgreSqlR2dbcDialect()
    }
}
