/*
 * Copyright (c) 2022 chr_56
 */

package lib.phonograph.misc

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@Throws(IOException::class)
suspend fun Call.emit(reportFailure: Boolean = true): Result<Response> =
    suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(Result.success(response)) { }
            }

            override fun onFailure(call: Call, e: IOException) {
                if (!reportFailure || continuation.isCancelled) {
                    continuation.cancel()
                } else {
                    continuation.resume(Result.failure(e)) { }
                }
            }
        })
    }
