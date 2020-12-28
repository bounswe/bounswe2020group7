package com.cmpe451.platon.network.models

import java.util.*
import kotlin.collections.ArrayList

data class Issues(
    val number_of_pages: Int,
    val result: ArrayList<Issue>
)

data class Issue(
    val issue_id: Int,
    val workspace_id: Int,
    val title: String,
    val description: String,
    val deadline: String,
    val is_open: Boolean,
    val creator_id: Int, //
    val creator_name: String,
    val creator_surname: String,
    val creator_e_mail: String,//
    val creator_rate: Double,
    val creator_job_name: String,
    val creator_institution: String,
    val creator_is_private: Boolean
)

data class IssueAssignee(
    val number_of_pages: Int,
    val result: ArrayList<Assignee>
)

data class Assignee(
    val assignee_e_mail: String,
    val assignee_id: Int,
    val assignee_institution: String,
    val assignee_job_name: String,
    val assignee_name: String,
    val assignee_rate: Double,
    val assignee_surname: String
)

data class IssueAllComments(
    val number_of_pages: Int,
    val result: ArrayList<Issue>
)

data class IssueComment(
    val comment_id: Int,
    val comment: String,
    val owner_id: Int,
    val owner_name: String,
    val owner_surname: String,
    val owner_e_mail: String,
    val owner_rate: Int
)








