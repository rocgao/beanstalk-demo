package org.rocgao.beanstalkdemo.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

fun List<String>.joinWithComma(): String = fold("") { x, y -> "${x},${y}" }

private val CUSTOM_TIME_FORMAT: DateTimeFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .optionalStart()
    .appendLiteral(':')
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
    .optionalStart()
    .toFormatter()
fun LocalDateTime.customTime(): String = format(CUSTOM_TIME_FORMAT)