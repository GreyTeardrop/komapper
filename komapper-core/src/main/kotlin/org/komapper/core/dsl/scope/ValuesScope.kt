package org.komapper.core.dsl.scope

import org.komapper.core.Scope
import org.komapper.core.dsl.element.Operand
import org.komapper.core.dsl.metamodel.PropertyMetamodel

@Scope
class ValuesScope<ENTITY : Any> internal constructor(
    private val context: MutableList<Pair<PropertyMetamodel<ENTITY, *>, Operand.Argument>> = mutableListOf()
) : List<Pair<PropertyMetamodel<ENTITY, *>, Operand.Argument>> by context {

    companion object {
        operator fun <E : Any> ValuesDeclaration<E>.plus(other: ValuesDeclaration<E>): ValuesDeclaration<E> {
            return {
                this@plus(this)
                other(this)
            }
        }
    }

    infix fun <T : Any> PropertyMetamodel<ENTITY, T>.set(value: T?) {
        val right = Operand.Argument(this.klass, value)
        context.add(this to right)
    }

    infix fun <T : Any> PropertyMetamodel<ENTITY, T>.setIfNotNull(value: T?) {
        if (value == null) return
        val right = Operand.Argument(this.klass, value)
        context.add(this to right)
    }
}
