package com.example.smsurltrigger

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import java.io.OutputStreamWriter

class QueueProcessingService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            try {
                processQueue()
            } finally {
                stopSelf()
            }
        }.start()
        return START_NOT_STICKY
    }

    private fun processQueue() {
        // sequential processing: pop first queued item, POST, update status, repeat
        val ctx = applicationContext
        while (true) {
            val item = Storage.dequeueNext(ctx) ?: break
            val prefs = ctx.getSharedPreferences("sms_url_prefs", 0)
            val target = prefs.getString("target_url", "")
            if (target.isNullOrEmpty()) {
                item.status = "NO_URL"
                Storage.updateItem(ctx, item)
                continue
            }
            // POST JSON {from, body, timestamp}
            try {
                val url = URL(target)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                val json = JSONObject()
                json.put("from", item.from)
                json.put("body", item.body)
                json.put("timestamp", item.timestamp)
                val out = OutputStreamWriter(conn.outputStream, "UTF-8")
                out.write(json.toString())
                out.flush()
                out.close()
                val code = conn.responseCode
                if (code in 200..299) {
                    item.status = "SENT"
                } else {
                    item.status = "FAILED:\$code"
                }
            } catch (e: Exception) {
                item.status = "FAILED:\${e.message}"
            }
            Storage.updateItem(ctx, item)
            // slight delay to avoid hammering
            try { Thread.sleep(300) } catch (e: Exception) {}
        }
    }
}
