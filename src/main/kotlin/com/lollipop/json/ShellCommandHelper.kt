package com.lollipop.json

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object ShellCommandHelper {

    suspend fun exec(
        cmd: String,
        outputCallback: (value: String, error: Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        val process = Runtime.getRuntime().exec(cmd)
        async {
            readStream(process.inputStream) {
                outputCallback(it, false)
            }
        }
        async {
            readStream(process.errorStream) {
                outputCallback(it, true)
            }
        }
        process.waitFor()
    }

    private fun readStream(inputStream: InputStream, callback: (String) -> Unit) {
        val reader = BufferedReader(InputStreamReader(inputStream))
        do {
            val line = reader.readLine() ?: break
            callback(line)
        } while (true)
    }

}