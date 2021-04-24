package integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.komapper.core.Database
import org.komapper.core.dsl.SqlQuery
import org.komapper.core.dsl.count
import org.komapper.core.dsl.max
import org.komapper.core.dsl.runQuery

@ExtendWith(Env::class)
class SqlSelectQuerySubqueryTest(private val db: Database) {

    @Test
    fun subquery_selectClause() {
        val d = Department.alias
        val e = Employee.alias
        val subquery = SqlQuery.from(e).where { d.departmentId eq e.departmentId }.select(count())
        val query = SqlQuery.from(d)
            .orderBy(d.departmentId)
            .select(d.departmentName, subquery)
        val list = db.runQuery { query }
        val expected = listOf("ACCOUNTING" to 3L, "RESEARCH" to 5L, "SALES" to 6L, "OPERATIONS" to 0L)
        assertEquals(expected, list)
    }

    @Test
    fun subquery_whereClause() {
        val d = Department.alias
        val e = Employee.alias
        val subquery = SqlQuery.from(d)
            .where {
                d.departmentName eq "SALES"
            }.select(max(d.departmentId))
        val query = SqlQuery.from(e).where {
            e.departmentId eq subquery
        }
        val list = db.runQuery { query }
        assertEquals(6, list.size)
    }
}