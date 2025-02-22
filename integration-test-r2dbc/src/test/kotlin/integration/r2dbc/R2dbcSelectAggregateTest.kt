package integration.r2dbc

import integration.core.address
import integration.core.employee
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.operator.avg
import org.komapper.core.dsl.operator.count
import org.komapper.core.dsl.operator.max
import org.komapper.core.dsl.operator.min
import org.komapper.core.dsl.operator.sum
import org.komapper.r2dbc.R2dbcDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(R2dbcEnv::class)
class R2dbcSelectAggregateTest(private val db: R2dbcDatabase) {
    @Test
    fun aggregate_avg(info: TestInfo) = inTransaction(db, info) {
        val a = Meta.address
        val avg = db.runQuery {
            QueryDsl.from(a).select(avg(a.addressId))
        }
        assertEquals(8.0, avg!!, 0.0)
    }

    @Test
    fun aggregate_countAsterisk(info: TestInfo) = inTransaction(db, info) {
        val a = Meta.address
        val count = db.runQuery {
            QueryDsl.from(a).select(count())
        }
        assertEquals(15, count)
    }

    @Test
    fun aggregate_count(info: TestInfo) = inTransaction(db, info) {
        val a = Meta.address
        val count = db.runQuery {
            QueryDsl.from(a).select(count(a.street))
        }
        assertEquals(15, count)
    }

    @Test
    fun aggregate_sum(info: TestInfo) = inTransaction(db, info) {
        val a = Meta.address
        val sum = db.runQuery { QueryDsl.from(a).select(sum(a.addressId)) }
        assertEquals(120, sum)
    }

    @Test
    fun aggregate_max(info: TestInfo) = inTransaction(db, info) {
        val a = Meta.address
        val max = db.runQuery { QueryDsl.from(a).select(max(a.addressId)) }
        assertEquals(15, max)
    }

    @Test
    fun aggregate_min(info: TestInfo) = inTransaction(db, info) {
        val a = Meta.address
        val min = db.runQuery { QueryDsl.from(a).select(min(a.addressId)) }
        assertEquals(1, min)
    }

    @Test
    fun having(info: TestInfo) = inTransaction(db, info) {
        val e = Meta.employee
        val list = db.runQuery {
            QueryDsl.from(e)
                .groupBy(e.departmentId)
                .having {
                    count(e.employeeId) greaterEq 4L
                }
                .orderBy(e.departmentId)
                .select(e.departmentId, count(e.employeeId))
        }
        assertEquals(listOf(2 to 5L, 3 to 6L), list)
    }

    @Test
    fun having_empty_groupBy(info: TestInfo) = inTransaction(db, info) {
        val e = Meta.employee
        val list = db.runQuery {
            QueryDsl.from(e)
                .having {
                    count(e.employeeId) greaterEq 4L
                }
                .orderBy(e.departmentId)
                .select(e.departmentId, count(e.employeeId))
        }
        assertEquals(listOf(2 to 5L, 3 to 6L), list)
    }
}
