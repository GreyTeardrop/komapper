package org.komapper.jdbc

import org.komapper.core.Logger
import org.komapper.core.spi.findByPriority
import org.komapper.jdbc.spi.JdbcSessionFactory
import java.util.ServiceLoader
import javax.sql.DataSource

object JdbcSessions {
    fun get(dataSource: DataSource, logger: Logger): JdbcSession {
        val loader = ServiceLoader.load(JdbcSessionFactory::class.java)
        val factory = loader.findByPriority()
        return factory?.create(dataSource, logger) ?: DefaultJdbcSession(dataSource)
    }
}