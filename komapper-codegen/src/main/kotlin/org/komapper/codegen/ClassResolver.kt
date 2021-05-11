package org.komapper.codegen

import org.komapper.core.Database
import org.komapper.core.jdbc.Column
import org.komapper.core.jdbc.DataType
import kotlin.reflect.KClass

fun interface ClassResolver {
    fun resolve(column: Column): KClass<*>

    companion object {
        fun create(dataTypes: List<DataType<*>>): ClassResolver {
            return DefaultClassResolver(dataTypes)
        }

        fun create(database: Database): ClassResolver {
            return DefaultClassResolver(database.config.dialect.dataTypes)
        }
    }
}

internal class DefaultClassResolver(private val dataTypes: List<DataType<*>>) : ClassResolver {

    override fun resolve(column: Column): KClass<*> {
        val dataType = dataTypes.find {
            it.name.lowercase() == column.typeName.lowercase()
        } ?: dataTypes.find {
            it.jdbcType.vendorTypeNumber == column.dataType
        }
        return dataType?.klass ?: String::class
    }
}