package com.example.smsurltrigger

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class SmsItem(var from: String, var body: String, var timestamp: Long, var status: String = "PENDING")

object Storage {
    private const val FILE = "sms_queue.json"
    private const val MAX = 1000

    private fun file(ctx: Context): File = File(ctx.filesDir, FILE)

    fun enqueue(ctx: Context, item: SmsItem) {
        val list = loadAll(ctx)
        list.add(0, item)
        saveAll(ctx, list)
    }

    fun dequeueNext(ctx: Context): SmsItem? {
        val list = loadAll(ctx)
        // find first with status PENDING
        for (i in list.indices.reversed()) {
            val it = list[i]
            if (it.status == "PENDING") {
                it.status = "SENDING"
                saveAll(ctx, list)
                return it
            }
        }
        return null
    }

    fun updateItem(ctx: Context, item: SmsItem) {
        val list = loadAll(ctx)
        // replace first matching by timestamp & from & body
        for (i in list.indices) {
            val it = list[i]
            if (it.timestamp == item.timestamp && it.from == item.from && it.body == item.body) {
                list[i] = item
                saveAll(ctx, list)
                return
            }
        }
        // if not found, append
        list.add(0, item)
        saveAll(ctx, list)
    }

    fun loadList(ctx: Context): MutableList<SmsItem> {
        val all = loadAll(ctx)
        // return latest 10 in descending time order
        return all.sortedByDescending { it.timestamp }.take(10).toMutableList()
    }

    private fun loadAll(ctx: Context): MutableList<SmsItem> {
        val f = file(ctx)
        val res = mutableListOf<SmsItem>()
        if (!f.exists()) return res
        try {
            val s = f.readText(Charsets.UTF_8)
            val arr = JSONArray(s)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                res.add(SmsItem(o.optString("from"), o.optString("body"), o.optLong("timestamp"), o.optString("status", "PENDING")))
            }
        } catch (e: Exception) { e.printStackTrace() }
        return res
    }

    private fun saveAll(ctx: Context, list: MutableList<SmsItem>) {
        val arr = JSONArray()
        for (it in list) {
            val o = JSONObject()
            o.put("from", it.from)
            o.put("body", it.body)
            o.put("timestamp", it.timestamp)
            o.put("status", it.status)
            arr.put(o)
        }
        try {
            file(ctx).writeText(arr.toString(), Charsets.UTF_8)
        } catch (e: Exception) { e.printStackTrace() }
    }
}
