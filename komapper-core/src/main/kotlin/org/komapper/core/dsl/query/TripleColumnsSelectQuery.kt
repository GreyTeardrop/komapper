package org.komapper.core.dsl.query

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.komapper.core.dsl.context.SelectContext
import org.komapper.core.dsl.expression.ColumnExpression
import org.komapper.core.dsl.expression.SubqueryExpression
import org.komapper.core.dsl.visitor.FlowQueryVisitor
import org.komapper.core.dsl.visitor.QueryVisitor

internal class TripleColumnsSelectQuery<A : Any, B : Any, C : Any>(
    override val context: SelectContext<*, *, *>,
    private val expressions: Triple<ColumnExpression<A, *>, ColumnExpression<B, *>, ColumnExpression<C, *>>
) : FlowSubquery<Triple<A?, B?, C?>> {

    private val support: FlowSubquerySupport<Triple<A?, B?, C?>> =
        FlowSubquerySupport(context) { TripleColumnsSetOperationQuery(it, expressions = expressions) }

    override fun <VISIT_RESULT> accept(visitor: QueryVisitor<VISIT_RESULT>): VISIT_RESULT {
        return visitor.tripleColumnsSelectQuery(context, expressions) { it.toList() }
    }

    override fun <VISIT_RESULT> accept(visitor: FlowQueryVisitor<VISIT_RESULT>): VISIT_RESULT {
        return visitor.tripleColumnsQuery(context, expressions)
    }

    override fun <R> collect(collect: suspend (Flow<Triple<A?, B?, C?>>) -> R): Query<R> = object : Query<R> {
        override fun <VISIT_RESULT> accept(visitor: QueryVisitor<VISIT_RESULT>): VISIT_RESULT {
            return visitor.tripleColumnsSelectQuery(context, expressions, collect)
        }
    }

    override fun except(other: SubqueryExpression<Triple<A?, B?, C?>>): FlowSetOperationQuery<Triple<A?, B?, C?>> {
        return support.except(other)
    }

    override fun intersect(other: SubqueryExpression<Triple<A?, B?, C?>>): FlowSetOperationQuery<Triple<A?, B?, C?>> {
        return support.intersect(other)
    }

    override fun union(other: SubqueryExpression<Triple<A?, B?, C?>>): FlowSetOperationQuery<Triple<A?, B?, C?>> {
        return support.union(other)
    }

    override fun unionAll(other: SubqueryExpression<Triple<A?, B?, C?>>): FlowSetOperationQuery<Triple<A?, B?, C?>> {
        return support.unionAll(other)
    }
}