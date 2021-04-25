package org.komapper.core.dsl.query

import org.komapper.core.DatabaseConfig
import org.komapper.core.DatabaseConfigHolder
import org.komapper.core.data.Statement
import org.komapper.core.dsl.context.EntityDeleteContext
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.option.EntityDeleteOption

interface EntityDeleteQuery<ENTITY : Any> : Query<Unit> {
    fun option(configurator: (EntityDeleteOption) -> EntityDeleteOption): EntityDeleteQuery<ENTITY>
}

internal data class EntityDeleteQueryImpl<ENTITY : Any, ID, META : EntityMetamodel<ENTITY, ID, META>>(
    private val context: EntityDeleteContext<ENTITY, ID, META>,
    private val entity: ENTITY,
    private val option: EntityDeleteOption = EntityDeleteOption()
) :
    EntityDeleteQuery<ENTITY> {

    private val support: EntityDeleteQuerySupport<ENTITY, ID, META> = EntityDeleteQuerySupport(context, option)

    override fun option(configurator: (EntityDeleteOption) -> EntityDeleteOption): EntityDeleteQueryImpl<ENTITY, ID, META> {
        return copy(option = configurator(option))
    }

    override fun run(holder: DatabaseConfigHolder) {
        val config = holder.config
        val (count) = delete(config)
        postDelete(count)
    }

    private fun delete(config: DatabaseConfig): Pair<Int, LongArray> {
        val statement = buildStatement(config)
        return support.delete(config) { it.executeUpdate(statement) }
    }

    private fun postDelete(count: Int) {
        support.postDelete(count)
    }

    override fun dryRun(holder: DatabaseConfigHolder): String {
        val config = holder.config
        return buildStatement(config).sql
    }

    private fun buildStatement(config: DatabaseConfig): Statement {
        return support.buildStatement(config, entity)
    }
}
