package com.angelstudio.angelskitchen.data.remote

import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal suspend fun <T> Call<T>.awaitResponse(): Response<T> =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancel() }
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                continuation.resume(
                    response,
                    onCancellation = { _, discarded, _ -> discarded.errorBody()?.close() },
                )
            }

            override fun onFailure(call: Call<T>, failure: Throwable) {
                continuation.resumeWithException(failure)
            }
        })
    }
