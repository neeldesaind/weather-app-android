package com.techprenuer.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class credites : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credites)
    }
    fun shareLink(view: View) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Download our Weather App: https://play.google.com/store/apps/details?id=com.techprenuer.weatherapp")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}