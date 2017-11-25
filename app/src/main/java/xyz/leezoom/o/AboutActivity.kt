package xyz.leezoom.o

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.browse

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        about_version.text = "v" +getVersionName()
        about_page.setOnClickListener {
            browse("https://github.com/LeeReindeer/O/")
        }
        about_me.setOnClickListener {
            browse("https://t.me/LeeReindeer")
        }

        val webView = WebView(applicationContext)
        webView.webViewClient = WebViewClient()
        about_icon.setOnClickListener {
            alert {
                webView.loadUrl("https://github.com/LeeReindeer/O/blob/master/ATTRIBUTIONS.md")
            }
        }
    }

    private fun getVersionName(): String {
        val manager = this.packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(this.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return info!!.versionName
    }
}
