package com.starter.issuechecker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.net.URL
import java.util.concurrent.Executor

class IssueChecker(
    val config: Config
) {

    data class Config(
        val githubToken: String? = null,
        val okHttpClient: OkHttpClient = OkHttpClient(),
        val executor: Executor = Dispatchers.IO.asExecutor(),
    )

    private val checker = defaultChecker(config)

    suspend fun findAllLinks(text: String): Collection<List<URL>> =
        checker.getLinks(text = text).filterKeys { it != null }.values

    suspend fun report(text: String): Collection<CheckResult> =
        checker.report(text = text)
}

fun IssueChecker.reportBlocking(text: String) = runBlocking { report(text) }
fun IssueChecker.findAllLinksBlocking(text: String) = runBlocking { findAllLinks(text) }
