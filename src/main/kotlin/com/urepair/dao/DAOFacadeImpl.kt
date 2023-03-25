package com.urepair.dao

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import com.urepair.dao.DatabaseFactory.dbQuery
import com.urepair.models.Equipment
import com.urepair.models.EquipmentTable
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class DAOFacadeImpl : DAOFacade {
    private fun resultRowToEquipment(row: ResultRow) = Equipment(
        id = row[EquipmentTable.id],
        name = row[EquipmentTable.name],
        equipmentType = row[EquipmentTable.equipmentType],
        manufacturer = row[EquipmentTable.manufacturer],
        model = row[EquipmentTable.model],
        serialNumber = row[EquipmentTable.serialNumber],
        location = row[EquipmentTable.location],
        dateInstalled = row[EquipmentTable.dateInstalled].toKotlinLocalDate(),
        lastMaintenanceDate = row[EquipmentTable.lastMaintenanceDate]?.toKotlinLocalDate(),
    )
    private fun setEquipmentValues(
        it: UpdateBuilder<*>,
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate? = null
    ) {
        it[EquipmentTable.name] = name
        it[EquipmentTable.equipmentType] = equipmentType
        it[EquipmentTable.manufacturer] = manufacturer
        it[EquipmentTable.model] = model
        it[EquipmentTable.serialNumber] = serialNumber
        it[EquipmentTable.location] = location
        it[EquipmentTable.dateInstalled] = dateInstalled.toJavaLocalDate()
        it[EquipmentTable.lastMaintenanceDate] = lastMaintenanceDate?.toJavaLocalDate()
    }
    override suspend fun allEquipment(): List<Equipment> = dbQuery {
        EquipmentTable.selectAll().map(::resultRowToEquipment)
    }

    override suspend fun equipment(id: Int): Equipment? = dbQuery {
        EquipmentTable
            .select { EquipmentTable.id eq id}
            .map(::resultRowToEquipment)
            .singleOrNull()
    }

    override suspend fun addNewEquipment(
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate?
    ): Equipment? = dbQuery {
        val insertStatement = EquipmentTable.insert {
            setEquipmentValues(it, name, equipmentType, manufacturer, model, serialNumber, location, dateInstalled, lastMaintenanceDate)
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEquipment)
    }

    override suspend fun editEquipment(
        id: Int,
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate?
    ): Boolean = dbQuery {
        EquipmentTable.update({EquipmentTable.id eq id}) {
            setEquipmentValues(it, name, equipmentType, manufacturer, model, serialNumber, location, dateInstalled, lastMaintenanceDate)

        } > 0
    }

    override suspend fun deleteEquipment(id: Int): Boolean = dbQuery {
        EquipmentTable.deleteWhere { EquipmentTable.id eq id } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
        if(allEquipment().isEmpty()) {
            addNewEquipment("name", "type", "man", "model", "serial", "loc", LocalDate(2023, 3, 19), LocalDate(2023, 3, 20))
        }
    }
}
