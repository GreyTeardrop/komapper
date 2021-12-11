package org.komapper.jdbc.dsl.runner

import org.komapper.core.dsl.context.EntityUpdateContext
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.runner.checkOptimisticLock
import org.komapper.jdbc.JdbcDatabaseConfig
import org.komapper.jdbc.JdbcExecutor

internal class EntityUpdateJdbcRunnerSupport<ENTITY : Any, ID : Any, META : EntityMetamodel<ENTITY, ID, META>>(
    private val context: EntityUpdateContext<ENTITY, ID, META>,
) {

    fun preUpdate(config: JdbcDatabaseConfig, entity: ENTITY): ENTITY {
        val clock = config.clockProvider.now()
        return context.target.preUpdate(entity, clock)
    }

    fun <T> update(config: JdbcDatabaseConfig, execute: (JdbcExecutor) -> T): T {
        val executor = JdbcExecutor(config, context.options)
        return execute(executor)
    }

    fun postUpdate(entity: ENTITY, count: Int, index: Int? = null): ENTITY {
        if (context.target.versionProperty() != null) {
            checkOptimisticLock(context.options, count, index)
        }
        return if (!context.options.disableOptimisticLock) {
            context.target.postUpdate(entity)
        } else {
            entity
        }
    }
}