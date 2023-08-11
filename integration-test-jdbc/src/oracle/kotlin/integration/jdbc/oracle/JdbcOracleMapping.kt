package integration.jdbc.oracle

import org.komapper.annotation.KomapperColumn
import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperTable
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Blob
import java.sql.Clob
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

@KomapperEntity
@KomapperTable
data class JdbcOracleMapping(
    @KomapperId val id: Int,
    @KomapperColumn(alwaysQuote = true) val bigDecimal: BigDecimal,
    @KomapperColumn(alwaysQuote = true) val bigInteger: BigInteger,
    @KomapperColumn(alwaysQuote = true) val blob: Blob,
    @KomapperColumn(alwaysQuote = true) val boolean: Boolean,
    @KomapperColumn(alwaysQuote = true) val byte: Byte,
    @Suppress("ArrayInDataClass")
    @KomapperColumn(alwaysQuote = true) val byteArray: ByteArray,
    @KomapperColumn(alwaysQuote = true) val double: Double,
    @KomapperColumn(alwaysQuote = true) val clob: Clob,
    @KomapperColumn(alwaysQuote = true) val float: Float,
    @KomapperColumn(alwaysQuote = true) val instant: Instant,
    @KomapperColumn(alwaysQuote = true) val localDateTime: LocalDateTime,
    @KomapperColumn(alwaysQuote = true) val localDate: LocalDate,
    @KomapperColumn(alwaysQuote = true) val localTime: LocalTime,
    @KomapperColumn(alwaysQuote = true) val long: Long,
    @KomapperColumn(alwaysQuote = true) val offsetDateTime: OffsetDateTime,
    @KomapperColumn(alwaysQuote = true) val short: Short,
    @KomapperColumn(alwaysQuote = true) val string: String,
    @KomapperColumn(alwaysQuote = true) val uByte: UByte,
    @KomapperColumn(alwaysQuote = true) val uInt: UInt,
    @KomapperColumn(alwaysQuote = true) val uShort: UShort,
)
