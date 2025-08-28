package com.example.smsurltrigger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MsgAdapter(private val ctx: Context, private var list: MutableList<SmsItem>) : RecyclerView.Adapter<MsgAdapter.VH>() {
    class VH(v: View): RecyclerView.ViewHolder(v) {
        val from: TextView = v.findViewById(R.id.from)
        val body: TextView = v.findViewById(R.id.body)
        val time: TextView = v.findViewById(R.id.time)
        val status: TextView = v.findViewById(R.id.status)
        val retry: Button = v.findViewById(R.id.retryBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_msg, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = list[position]
        holder.from.text = it.from
        holder.body.text = it.body
        holder.time.text = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(it.timestamp))
        holder.status.text = it.status
        holder.retry.visibility = if (it.status.startsWith("FAILED")) View.VISIBLE else View.GONE
        holder.retry.setOnClickListener {
            // mark as pending and restart service
            it.status = "PENDING"
            Storage.updateItem(ctx, it)
            ctx.startService(android.content.Intent(ctx, QueueProcessingService::class.java))
            update(Storage.loadList(ctx))
        }
    }

    fun update(newList: MutableList<SmsItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
