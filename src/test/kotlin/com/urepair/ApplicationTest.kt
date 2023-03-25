package com.urepair

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class OrderRouteTests {
    @Test
    fun testEquipment() = testApplication {
        val response = client.get("/equipment/1")
        assertEquals(
            """{
    "id": 1,
    "name": "name",
    "equipmentType": "type",
    "manufacturer": "man",
    "model": "model",
    "serialNumber": "serial",
    "location": "loc",
    "dateInstalled": {
        "year": 2023,
        "month": 3,
        "day": 19
    },
    "lastMaintenanceDate": {
        "year": 2023,
        "month": 3,
        "day": 20
    }
}""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
