package com.example.smsurltrigger

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var urlEdit: EditText
    private lateinit var saveBtn: Button
    private lateinit var rv: RecyclerView
    private lateinit var adapter: MsgAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        urlEdit = findViewById(R.id.urlEdit)
        saveBtn = findViewById(R.id.saveBtn)
        rv = findViewById(R.id.rv)
        adapter = MsgAdapter(this, mutableListOf())
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("sms_url_prefs", Context.MODE_PRIVATE)
        urlEdit.setText(prefs.getString("target_url", ""))

        saveBtn.setOnClickListener {
            prefs.edit().putString("target_url", urlEdit.text.toString()).apply()
        }

        // request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS), 101)
        }

        // load recent list
        val list = Storage.loadList(this)
        adapter.update(list)
    }

    override fun onResume() {
        super.onResume()
        adapter.update(Storage.loadList(this))
    }
}
