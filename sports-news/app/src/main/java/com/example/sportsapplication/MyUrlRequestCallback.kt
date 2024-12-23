package com.example.sportsapplication

import android.util.Log
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.Charset

class MyUrlRequestCallback(private val callback: (String?) -> Unit) : UrlRequest.Callback() {

    private val responseBuilder = StringBuilder()

    override fun onRedirectReceived(request: UrlRequest?, info: UrlResponseInfo?, newLocationUrl: String?) {
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        if (info?.httpStatusCode == 200) {
            request?.read(ByteBuffer.allocateDirect(1024))
        } else {
            Log.e("callback_onResponseStarted", "Unexpected status code: ${info?.httpStatusCode}")
            callback(null)
        }
    }

    override fun onReadCompleted(request: UrlRequest?, info: UrlResponseInfo?, byteBuffer: ByteBuffer?) {
        byteBuffer?.let {
            it.flip()
            val responsePart = Charset.forName("UTF-8").decode(it).toString()
            responseBuilder.append(responsePart)
            it.clear()
            request?.read(it)
        }
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        Log.i("callback_succeeded", "Request succeeded")
        callback(responseBuilder.toString())
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        Log.e("callback_failed", "Request failed: ${error?.message}")
        callback(null)
    }

    private fun byteBufferToString(buffer: ByteBuffer?, charset: Charset = Charsets.UTF_8): String? {
        return buffer?.let {
            val byteArray = ByteArray(it.remaining())
            it.get(byteArray)
            String(byteArray, charset)
        }
    }

}
