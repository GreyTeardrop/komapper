package integration

import integration.setting.Dbms
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.komapper.core.Database
import org.komapper.core.dsl.EntityQuery
import org.komapper.core.dsl.SqlQuery
import org.komapper.core.dsl.alias
import org.komapper.core.dsl.desc
import org.komapper.core.dsl.runQuery

@ExtendWith(Env::class)
class SqlSetOperationQueryTest(private val db: Database) {

    @Run(unless = [Dbms.MYSQL])
    @Test
    fun except_entity() {
        val e = Employee.alias
        val q1 = SqlQuery.from(e).where { e.employeeId inList listOf(1, 2, 3, 4, 5) }
        val q2 = SqlQuery.from(e).where { e.employeeId inList listOf(2, 4, 6, 8) }
        val query = (q1 except q2).orderBy(e.employeeId)
        val list = db.runQuery { query }
        assertEquals(3, list.size)
        val e1 = list[0]
        val e2 = list[1]
        val e3 = list[2]
        assertEquals(1, e1.employeeId)
        assertEquals(3, e2.employeeId)
        assertEquals(5, e3.employeeId)
    }

    @Run(unless = [Dbms.MYSQL])
    @Test
    fun intersect_entity() {
        val e = Employee.alias
        val q1 = SqlQuery.from(e).where { e.employeeId inList listOf(1, 2, 3, 4, 5) }
        val q2 = SqlQuery.from(e).where { e.employeeId inList listOf(2, 4, 6, 8) }
        val query = (q1 intersect q2).orderBy(e.employeeId)
        val list = db.runQuery { query }
        assertEquals(2, list.size)
        val e1 = list[0]
        val e2 = list[1]
        assertEquals(2, e1.employeeId)
        assertEquals(4, e2.employeeId)
    }

    @Test
    fun union_entity() {
        val e = Employee.alias
        val q1 = SqlQuery.from(e).where { e.employeeId eq 1 }
        val q2 = SqlQuery.from(e).where { e.employeeId eq 1 }
        val q3 = EntityQuery.from(e).where { e.employeeId eq 5 }
        val query = (q1 union q2 union q3).orderBy(e.employeeId.desc())
        val list = db.runQuery { query }
        assertEquals(2, list.size)
        val e1 = list[0]
        val e2 = list[1]
        assertEquals(5, e1.employeeId)
        assertEquals(1, e2.employeeId)
    }

    @Test
    fun union_subquery() {
        val e = Employee.alias
        val q1 = SqlQuery.from(e).where { e.employeeId eq 1 }.select(e.employeeId)
        val q2 = SqlQuery.from(e).where { e.employeeId eq 6 }.select(e.employeeId)
        val subquery = q1 union q2
        val query = SqlQuery.from(e).where {
            e.managerId inList { subquery }
        }
        val list = db.runQuery { query }
        assertEquals(5, list.size)
    }

    @Test
    fun union_columns() {
        val e = Employee.alias
        val a = Address.alias
        val d = Department.alias
        val q1 =
            SqlQuery.from(e).where { e.employeeId eq 1 }
                .select(e.employeeId alias "ID", e.employeeName alias "NAME")
        val q2 = SqlQuery.from(a).where { a.addressId eq 2 }
            .select(a.addressId alias "ID", a.street alias "NAME")
        val q3 = SqlQuery.from(d).where { d.departmentId eq 3 }
            .select(d.departmentId alias "ID", d.departmentName alias "NAME")
        val query = (q1 union q2 union q3).orderBy("ID", desc("NAME"))
        val list = db.runQuery { query }
        assertEquals(3, list.size)
        assertEquals(1 to "SMITH", list[0])
        assertEquals(2 to "STREET 2", list[1])
        assertEquals(3 to "SALES", list[2])
    }

    @Test
    fun unionAll_entity() {
        val e = Employee.alias
        val q1 = SqlQuery.from(e).where { e.employeeId eq 1 }
        val q2 = SqlQuery.from(e).where { e.employeeId eq 1 }
        val q3 = SqlQuery.from(e).where { e.employeeId eq 5 }
        val query = (q1 unionAll q2 unionAll q3).orderBy(e.employeeId.desc())
        val list = db.runQuery { query }
        assertEquals(3, list.size)
        val e1 = list[0]
        val e2 = list[1]
        val e3 = list[2]
        assertEquals(5, e1.employeeId)
        assertEquals(1, e2.employeeId)
        assertEquals(1, e3.employeeId)
    }

    @Test
    fun emptyWhereClause() {
        val e = Employee.alias
        val q1 = SqlQuery.from(e).where { e.employeeId eq 1 }
        val q2 = SqlQuery.from(e)
        val query = (q1 union q2).option { it.copy(allowEmptyWhereClause = false) }
        val ex = assertThrows<IllegalStateException> {
            db.runQuery { query }.let { }
        }
        assertEquals("Empty where clause is not allowed.", ex.message)
    }
}