package integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.komapper.core.Database
import org.komapper.core.dsl.SqlQuery
import org.komapper.core.dsl.runQuery

@ExtendWith(Env::class)
class SqlSelectQueryTest(private val db: Database) {

    @Test
    fun list() {
        val a = Address.alias
        val list: List<Address> = db.runQuery {
            SqlQuery.from(a)
        }
        assertEquals(15, list.size)
    }

    @Test
    fun first() {
        val a = Address.alias
        val address: Address = db.runQuery {
            SqlQuery.from(a).where { a.addressId eq 1 }.first()
        }
        assertNotNull(address)
    }

    @Test
    fun firstOrNull() {
        val a = Address.alias
        val address: Address? = db.runQuery {
            SqlQuery.from(a).where { a.addressId eq 99 }.firstOrNull()
        }
        assertNull(address)
    }

    @Test
    fun collect() {
        val a = Address.alias
        val count = db.runQuery {
            SqlQuery.from(a).collect { it.count() }
        }
        assertEquals(15, count)
    }

    @Test
    fun option() {
        val e = Employee.alias
        val emp = db.runQuery {
            SqlQuery.from(e)
                .option {
                    it.copy(
                        fetchSize = 10,
                        maxRows = 100,
                        queryTimeoutSeconds = 1000,
                        allowEmptyWhereClause = true,
                    )
                }
                .where {
                    e.employeeId eq 1
                }.first()
        }
        println(emp)
    }

    @Test
    fun shortcut_first() {
        val a = Address.alias
        val address = db.runQuery { SqlQuery.first(a) { a.addressId eq 1 } }
        assertNotNull(address)
    }

    @Test
    fun shortcut_firstOrNull() {
        val a = Address.alias
        val address = db.runQuery { SqlQuery.firstOrNull(a) { a.addressId eq -1 } }
        assertNull(address)
    }

    @Test
    fun shortcut_first_multipleCondition() {
        val a = Address.alias
        val address = db.runQuery {
            SqlQuery.first(a) { a.addressId eq 1; a.version eq 1 }
        }
        assertNotNull(address)
    }
}