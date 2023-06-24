package com.starter.issuechecker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.net.URI
import java.util.concurrent.Executor

public class IssueChecker(
    config: Config,
) {

    public data class Config(
        val githubToken: String? = null,
        val okHttpClient: OkHttpClient = OkHttpClient(),
        val executor: Executor = Dispatchers.IO.asExecutor(),
    )

    private val checker = defaultChecker(config)

    public suspend fun findAllLinks(text: String): Collection<List<URI>> = checker.getLinks(text = text).filterKeys { it != null }.values

    public suspend fun report(text: String): Collection<CheckResult> = checker.report(text = text)
}

public fun IssueChecker.reportBlocking(text: String): Collection<CheckResult> = runBlocking { report(text) }
public fun IssueChecker.findAllLinksBlocking(text: String): Collection<List<URI>> = runBlocking { findAllLinks(text) }
