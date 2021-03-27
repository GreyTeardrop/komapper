package org.komapper.core.dsl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.komapper.core.EntityQuery
import org.komapper.core.ScriptQuery
import org.komapper.core.TemplateQuery
import org.komapper.core.dsl.scope.WhereDeclaration
import org.komapper.core.dsl.scope.WhereScope.Companion.plus

class EntitySelectQueryTest {

    @Test
    fun entity_from() {
        val a = Address.metamodel()
        val query = EntityQuery.from(a).where { a.id eq 1 }
        assertEquals(
            "select t0_.ID, t0_.STREET, t0_.VERSION from ADDRESS t0_ where t0_.ID = ?",
            query.peek().sql
        )
    }

    @Test
    fun entity_innerJoin() {
        val a = Address.metamodel()
        val e = Emp.metamodel()
        val query = EntityQuery.from(a).innerJoin(e) {
            a.id eq e.addressId
        }.where { a.id eq 1 }
        assertEquals(
            "select t0_.ID, t0_.STREET, t0_.VERSION from ADDRESS t0_ inner join EMP t1_ on (t0_.ID = t1_.ADDRESS_ID) where t0_.ID = ?",
            query.peek().sql
        )
    }

    @Test
    fun entity_leftJoin() {
        val a = Address.metamodel()
        val e = Emp.metamodel()
        val query = EntityQuery.from(a).leftJoin(e) {
            a.id eq e.addressId
        }.where { a.id eq 1 }
        assertEquals(
            "select t0_.ID, t0_.STREET, t0_.VERSION from ADDRESS t0_ left outer join EMP t1_ on (t0_.ID = t1_.ADDRESS_ID) where t0_.ID = ?",
            query.peek().sql
        )
    }

    @Test
    fun entity_delete() {
        val a = Address.metamodel()
        val query = EntityQuery.delete(a, Address(1, "street", 0))
        assertEquals(
            "delete from ADDRESS t0_ where t0_.ID = ? and t0_.VERSION = ?",
            query.peek().sql
        )
    }

    @Test
    fun entity_update() {
        val a = Address.metamodel()
        val query = EntityQuery.update(a, Address(1, "street", 0))
        assertEquals(
            "update ADDRESS t0_ set t0_.STREET = ?, t0_.VERSION = ? + 1 where t0_.ID = ? and t0_.VERSION = ?",
            query.peek().sql
        )
    }

    @Test
    fun entity_insert() {
        val a = Address.metamodel()
        val query = EntityQuery.insert(a, Address(1, "street", 0))
        assertEquals(
            "insert into ADDRESS (ID, STREET, VERSION) values (?, ?, ?)",
            query.peek().sql
        )
    }

    @Test
    fun template() {
        val query = TemplateQuery.select(
            "select id, street, version from Address where id = /*id*/0",
            object {
                @Suppress("unused")
                val id = 1
            }
        ) {
            Address(asInt("id"), asString(""), asInt("version"))
        }
        assertEquals("select id, street, version from Address where id = ?", query.peek().sql)
    }

    @Test
    fun template_transform() {
        val params = object {
            @Suppress("unused")
            val id = 1
        }
        val query = TemplateQuery
            .select(
                "select id, street, version from Address where id = /*id*/0",
                params
            ) {
                Address(asInt("id"), asString(""), asInt("version"))
            }.transform { seq -> seq.map { it.id }.first() }
        assertEquals("select id, street, version from Address where id = ?", query.peek().sql)
    }

    @Test
    fun script() {
        val script = "insert into Address (id, street, version) values (2, 'Kyoto', 0)"
        val query = ScriptQuery.execute(script)
        assertEquals(script, query.peek().sql)
    }

    @Test
    fun composeWhereDeclaration() {
        val a = Address.metamodel()
        val w1: WhereDeclaration = {
            a.id eq 1
        }
        val w2: WhereDeclaration = {
            a.street eq "a"
        }
        val w3 = w1 + w2
        val query = EntityQuery.from(a).where(w3)
        assertEquals(
            "select t0_.ID, t0_.STREET, t0_.VERSION from ADDRESS t0_ where t0_.ID = ? and t0_.STREET = ?",
            query.peek().sql
        )
    }
}