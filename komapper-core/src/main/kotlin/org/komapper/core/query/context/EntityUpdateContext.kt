package org.komapper.core.query.context

import org.komapper.core.metamodel.EntityMetamodel

internal data class EntityUpdateContext<ENTITY>(val entityMetamodel: EntityMetamodel<ENTITY>) : Context<ENTITY> {

    override fun getReferencingEntityMetamodels(): List<EntityMetamodel<*>> {
        return listOf(entityMetamodel)
    }
}