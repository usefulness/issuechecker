package com.starter.issuechecker.resolvers

import com.starter.issuechecker.IssueStatus
import java.net.URI

internal interface StatusResolver {

    suspend fun resolve(url: URI): IssueStatus

    fun handles(url: URI): Boolean
}
