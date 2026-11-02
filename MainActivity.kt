package ke.co.cerealshopkinoo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView

    inner class ShareBridge {
        @JavascriptInterface
        fun shareText(text: String) {
            runOnUiThread { share(text, null) }
        }

        @JavascriptInterface
        fun shareWhatsApp(text: String) {
            runOnUiThread { share(text, "com.whatsapp") }
        }
    }

    private fun share(text: String, packageName: String?) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            if (packageName != null) setPackage(packageName)
        }
        try {
            startActivity(if (packageName == null) Intent.createChooser(send, "Share receipt")
                         else send)
        } catch (_: Exception) {
            if (packageName != null) {
                val fallback = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                startActivity(Intent.createChooser(fallback, "Share receipt"))
            } else {
                Toast.makeText(this, "No sharing app is available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Suppress("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(ShareBridge(), "CerealAndroid")
        setContentView(webView)
        if (savedInstanceState == null) {
            webView.loadUrl("file:///android_asset/index.html")
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
