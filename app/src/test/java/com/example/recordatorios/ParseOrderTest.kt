package com.example.recordatorios

import com.example.recordatorios.domain.ReminderOrder
import com.example.recordatorios.domain.parseOrder
import org.junit.Assert.assertEquals
import org.junit.Test

class ParseOrderTest {

    @Test
    fun validValueIsKept() {
        assertEquals(ReminderOrder.BY_PRIORITY, parseOrder("BY_PRIORITY"))
    }

    @Test
    fun invalidValueUsesDefault() {
        assertEquals(ReminderOrder.BY_DATE, parseOrder("XYZ"))
    }

    @Test
    fun missingValueUsesDefault() {
        assertEquals(ReminderOrder.BY_DATE, parseOrder(null))
    }
}
