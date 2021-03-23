package com.starter.issuechecker

import com.starter.issuechecker.resolvers.github.GithubService
import com.starter.issuechecker.resolvers.github.GithubStatusResolver
import com.starter.issuechecker.resolvers.youtrack.YoutrackService
import com.starter.issuechecker.resolvers.youtrack.YoutrackStatusResolver
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executor

internal fun defaultChecker(
    config: IssueChecker.Config
): DefaultChecker {
    val dispatcher = Dispatchers.IO

    val supportedTrackers = setOf(
        createGithub(config),
        createYoutrack(config)
    )

    return DefaultChecker(
        supportedTrackers = supportedTrackers,
        dispatcher = Dispatchers.IO
    )
}

internal fun restApi(
    baseUrl: String,
    okHttpClient: OkHttpClient,
    executor: Executor,
) = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(MoshiConverterFactory.create())
    .callbackExecutor(executor)
    .client(okHttpClient)
    .build()

private fun createYoutrack(
    config: IssueChecker.Config,
) = YoutrackStatusResolver(
    service = restApi(
        baseUrl = "https://youtrack.jetbrains.com/",
        okHttpClient = config.okHttpClient,
        executor = config.executor,
    ).create(YoutrackService::class.java)
)

private fun createGithub(
    config: IssueChecker.Config,
) = GithubStatusResolver(
    service = restApi(
        baseUrl = "https://api.github.com/",
        okHttpClient = config.okHttpClient.githubOkHttpClient { config.githubToken },
        executor = config.executor,
    ).create(GithubService::class.java)
)

private fun OkHttpClient.githubOkHttpClient(auth: () -> String?) =
    newBuilder()
        .addInterceptor { chain ->
            val newRequest = auth()?.let { token ->
                chain.request().newBuilder()
                    .addHeader("Authorization", "token $token")
                    .build()
            } ?: chain.request()

            chain.proceed(newRequest)
        }
        .build()
