package com.starter.issuechecker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

internal fun restApi(
    baseUrl: String,
) = restApi(baseUrl, okhttp3.OkHttpClient(), Dispatchers.Unconfined.asExecutor())
