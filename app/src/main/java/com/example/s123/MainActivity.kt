package com.example.s123

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.util.concurrent.Executors

suspend fun parseUrl(url:String) : ArrayList<String> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()
    val htmlContent = withContext(Dispatchers.IO){
        client.newCall(request).execute().body?.string()?: ""
    }

    val doc:Document = Jsoup.parse(htmlContent)

    val tempInfo = ArrayList<String>()

    tempInfo.add(doc.select(".cur")[2].text())
    tempInfo.add(doc.select(".cur")[3].text())
    tempInfo.add(doc.select(".cur")[4].text())
    tempInfo.add(doc.select(".cur")[5].text())
    tempInfo.add(doc.select(".cur")[7].text())
    tempInfo.add(doc.select(".cur")[1].child(0).child(0).attr("src"))

    return tempInfo
}

class MainActivity : AppCompatActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val l1 :TextView = findViewById(R.id.temp_label)
        val l2 :TextView = findViewById(R.id.temp_label2)
        val l3 :TextView = findViewById(R.id.pressure_label)
        val l4 :TextView = findViewById(R.id.humidity_label)
        val l5 :TextView = findViewById(R.id.precipitation_label)

        val sep = resources.getString(R.string.separator)
        l1.text = resources.getString(R.string.temperature) + sep + ' '
        l2.text = resources.getString(R.string.feels_like) + sep + ' '
        l3.text = resources.getString(R.string.pressure) + sep + ' '
        l4.text = resources.getString(R.string.humidity) + sep + ' '
        l5.text = resources.getString(R.string.precipitation) + sep + ' '

        val t1 :TextView = findViewById(R.id.temp)
        val t2 :TextView = findViewById(R.id.temp2)
        val t3 :TextView = findViewById(R.id.pressure)
        val t4 :TextView = findViewById(R.id.humidity)
        val t5 :TextView = findViewById(R.id.precipitation)
        val t6 :TextView = findViewById(R.id.city_name)
        val i1 :ImageView = findViewById(R.id.imageView)

        var image : Bitmap? = null

        GlobalScope.launch {
            val tempInfo:ArrayList<String> = parseUrl("https://ua.sinoptik.ua/%D0%BF%D0%BE%D0%B3%D0%BE%D0%B4%D0%B0-%D0%B6%D0%B8%D1%82%D0%BE%D0%BC%D0%B8%D1%80")
            withContext(Dispatchers.Main){
                t1.text = tempInfo[0] + ' ' +  resources.getString(R.string.celsius)
                t2.text = tempInfo[1] + ' ' + resources.getString(R.string.celsius)
                t3.text = tempInfo[2] + ' ' + resources.getString(R.string.pressure_units)
                t4.text = tempInfo[3] + ' ' + resources.getString(R.string.percent)
                t5.text = tempInfo[4] + ' ' + resources.getString(R.string.percent)
                t6.text = resources.getString(R.string.city_name)
                Executors.newSingleThreadExecutor().execute {
                    val stream = URL("https:"+tempInfo[5].replace("/s/","/b/").replace(".gif",".jpg")).openStream()
                    image = BitmapFactory.decodeStream(stream)
                    Handler(Looper.getMainLooper()).post{
                        i1.setImageBitmap(image)
                    }
                }
            }
        }

    }
}