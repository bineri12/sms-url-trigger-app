package com.example.smsurltrigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                for (pdu in pdus) {
                    val msg = SmsMessage.createFromPdu(pdu as ByteArray)
                    val from = msg.originatingAddress ?: ""
                    val body = msg.messageBody ?: ""
                    // enqueue message
                    Storage.enqueue(context, SmsItem(from, body, System.currentTimeMillis()))
                    // start service to process queue
                    val svc = Intent(context, QueueProcessingService::class.java)
                    context.startService(svc)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
