package com.jjsa.cryptomxn

import java.net.HttpURLConnection
import java.net.URL

import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.ProtocolException
import java.io.BufferedInputStream

class HTTPHandler {

    private var URL = "https://api.bitso.com/v3/ticker/"

    fun makeServiceCall(): String? {
        var response: String = "NO RESPONSE"
        try {
            val url = URL(URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            // read the response
            val `in` = BufferedInputStream(conn.inputStream)
            response = convertStreamToString(`in`)
        } catch (e: MalformedURLException) {
            Log.e(TAG, "MalformedURLException: " + e.message)
        } catch (e: ProtocolException) {
            Log.e(TAG, "ProtocolException: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "IOException: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }

        return response
    }

    private fun convertStreamToString(`is`: InputStream): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()

        var line: String
        try {
            line = reader.readLine()
            while (line != null) {
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return sb.toString()
    }

    companion object {
            private val TAG = this::class.java.simpleName
    }

}



