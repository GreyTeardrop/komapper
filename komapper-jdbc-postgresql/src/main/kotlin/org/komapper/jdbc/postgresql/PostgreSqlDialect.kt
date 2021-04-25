package org.komapper.jdbc.postgresql

import org.komapper.core.AbstractDialect
import org.komapper.core.dsl.builder.EntityMultipleUpsertStatementBuilder
import org.komapper.core.dsl.builder.EntityUpsertStatementBuilder
import org.komapper.core.dsl.builder.SchemaStatementBuilder
import org.komapper.core.dsl.context.EntityUpsertContext
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.jdbc.ArrayType
import org.komapper.core.jdbc.BigDecimalType
import org.komapper.core.jdbc.BigIntegerType
import org.komapper.core.jdbc.BlobType
import org.komapper.core.jdbc.BooleanType
import org.komapper.core.jdbc.ByteArrayType
import org.komapper.core.jdbc.ByteType
import org.komapper.core.jdbc.ClobType
import org.komapper.core.jdbc.DataType
import org.komapper.core.jdbc.DoubleType
import org.komapper.core.jdbc.FloatType
import org.komapper.core.jdbc.IntType
import org.komapper.core.jdbc.LocalDateTimeType
import org.komapper.core.jdbc.LocalDateType
import org.komapper.core.jdbc.LocalTimeType
import org.komapper.core.jdbc.LongType
import org.komapper.core.jdbc.NClobType
import org.komapper.core.jdbc.OffsetDateTimeType
import org.komapper.core.jdbc.SQLXMLType
import org.komapper.core.jdbc.ShortType
import org.komapper.core.jdbc.StringType
import java.sql.SQLException

open class PostgreSqlDialect(dataTypes: Set<DataType<*>> = emptySet(), val version: Version = Version.V42_2) :
    AbstractDialect(defaultDataTypes + dataTypes) {

    companion object {
        enum class Version { V42_2 }

        /** the state code that represents unique violation  */
        const val UNIQUE_CONSTRAINT_VIOLATION_STATE_CODE = "23505"

        val defaultDataTypes: Set<DataType<*>> = setOf(
            ArrayType("array"),
            BigDecimalType("decimal"),
            BigIntegerType("decimal"),
            BlobType("blob"),
            BooleanType("boolean"),
            ByteType("smallint"),
            ByteArrayType("bytea"),
            DoubleType("double precision"),
            ClobType("text"),
            FloatType("real"),
            IntType("integer"),
            LocalDateTimeType("timestamp"),
            LocalDateType("date"),
            LocalTimeType("time"),
            LongType("bigint"),
            NClobType("text"),
            OffsetDateTimeType("timestamp with time zone"),
            ShortType("smallint"),
            StringType("varchar(500)"),
            SQLXMLType("text")
        )
    }

    override fun isUniqueConstraintViolation(exception: SQLException): Boolean {
        val cause = getCause(exception)
        return cause.sqlState == UNIQUE_CONSTRAINT_VIOLATION_STATE_CODE
    }

    override fun getSequenceSql(sequenceName: String): String {
        return "select nextval('$sequenceName')"
    }

    override fun getSchemaStatementBuilder(): SchemaStatementBuilder {
        return PostgreSqlSchemaStatementBuilder(this)
    }

    override fun <ENTITY : Any, ID, META : EntityMetamodel<ENTITY, ID, META>> getEntityUpsertStatementBuilder(
        context: EntityUpsertContext<ENTITY, ID, META>,
        entity: ENTITY
    ): EntityUpsertStatementBuilder<ENTITY> {
        return PostgreSqlEntityMultipleUpsertStatementBuilder(this, context, listOf(entity))
    }

    override fun <ENTITY : Any, ID, META : EntityMetamodel<ENTITY, ID, META>> getEntityMultipleUpsertStatementBuilder(
        context: EntityUpsertContext<ENTITY, ID, META>,
        entities: List<ENTITY>
    ): EntityMultipleUpsertStatementBuilder<ENTITY> {
        return PostgreSqlEntityMultipleUpsertStatementBuilder(this, context, entities)
    }
}
