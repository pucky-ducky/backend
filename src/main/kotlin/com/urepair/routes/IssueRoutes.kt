package com.urepair.routes

import com.urepair.dao.dao
import com.urepair.models.Issue
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listIssuesRoute() {
    get ("/issue"){
        call.respond(mapOf("issue_table" to dao.allIssues()))
    }
}
fun Route.getIssueRoute() {
    get("/issue/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val equip = dao.issue(id.toInt()) ?: return@get call.respondText(
            "No issue with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(equip)
    }
}

fun Route.addIssueRoute() {
    post ("/issue"){
        val issue = call.receive<Issue>()
        dao.addNewIssue(
            equipmentId = issue.equipmentId,
            description = issue.description,
            status = issue.status,
            dateReported = issue.dateReported,
            priority = issue.priority,
            assignedTo = issue.assignedTo,
            dateResolved = issue.dateResolved,
            resolutionDetails = issue.resolutionDetails,
            notes = issue.notes,
        )
        call.respondText("Equipment stored correctly", status = HttpStatusCode.Created)
    }
}

fun Route.removeIssueRoute() {
    delete("/issue/{id?}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (dao.deleteIssue(id.toInt())) {
            call.respondText("Issue removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}
