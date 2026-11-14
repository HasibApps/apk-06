package com.example.ytdl

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

/**
 * Logs in to Facebook inside a WebView and saves the session cookies (c_user, xs, …) for yt-dlp.
 * Result: RESULT_OK when cookies were saved.
 */
class CookieLoginActivity : ComponentActivity() {
    private lateinit var web: WebView
    private val loginUrl = "https://www.facebook.com/login/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DM.init(this)
        val d = resources.displayMetrics.density
        fun dp(x: Int) = (x * d).toInt()

        val info = TextView(this).apply {
            text = "Facebook এ login করুন (যে account এ private video দেখা যায়)।\nLogin শেষ হলে নিচের “Save Cookies” বাটন চাপুন।"
            setTextColor(Color.WHITE); textSize = 13f
            setPadding(dp(14), dp(10), dp(14), dp(10))
            setBackgroundColor(Color.parseColor("#302B63"))
        }

        web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            // Same desktop User-Agent that yt-dlp uses → Facebook accepts the saved session for downloads
            settings.userAgentString = DM.UA
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webViewClient = WebViewClient()
            loadUrl(loginUrl)
        }

        fun btn(label: String, bg: String, w: Float, onClick: () -> Unit) = Button(this).apply {
            text = label; setTextColor(Color.WHITE); isAllCaps = false
            setBackgroundColor(Color.parseColor(bg))
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, w).apply { setMargins(dp(6), dp(6), dp(6), dp(6)) }
            setOnClickListener { onClick() }
        }

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#0A0820"))
            addView(btn("↺ Reset", "#444455", 1f) {
                CookieManager.getInstance().removeAllCookies(null)
                web.loadUrl(loginUrl)
            })
            addView(btn("✅ Save Cookies", "#FF416C", 2f) { save() })
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0F0C29"))
            addView(info, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(web, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
            addView(bar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { if (web.canGoBack()) web.goBack() else finish() }
        })
    }

    private fun save() {
        CookieManager.getInstance().flush()
        val cm = CookieManager.getInstance()
        val seen = LinkedHashMap<String, String>()
        listOf("https://www.facebook.com", "https://facebook.com", "https://m.facebook.com")
            .mapNotNull { cm.getCookie(it) }
            .flatMap { it.split(";") }
            .forEach { p ->
                val s = p.trim(); val i = s.indexOf('=')
                if (i > 0) seen.putIfAbsent(s.substring(0, i).trim(), s.substring(i + 1).trim())
            }
        val raw = seen.entries.joinToString("; ") { it.key + "=" + it.value }
        if (!raw.contains("c_user=") || !raw.contains("xs=")) {
            Toast.makeText(this, "Login এখনো সম্পন্ন হয়নি — আগে Facebook এ login করুন", Toast.LENGTH_LONG).show()
            return
        }
        if (Cookies.mergeFacebook(raw) > 0) {
            Toast.makeText(this, "Cookies saved ✅", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else Toast.makeText(this, "Cookies সেভ করা যায়নি", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        try { web.destroy() } catch (_: Throwable) {}
        super.onDestroy()
    }
}
