package integration.r2dbc.mariadb

import integration.r2dbc.R2dbcEnv
import integration.r2dbc.inTransaction
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase
import kotlin.test.Test

@ExtendWith(R2dbcEnv::class)
class R2dbcMariadbMappingTest(private val db: R2dbcDatabase) {
    @Test
    fun test(info: TestInfo) = inTransaction(db, info) {
        db.runQuery {
            QueryDsl.create(Meta.r2dbcMariadbMapping)
        }
        db.runQuery {
            QueryDsl.from(Meta.r2dbcMariadbMapping)
        }
        db.runQuery {
            QueryDsl.drop(Meta.r2dbcMariadbMapping)
        }
    }
}
