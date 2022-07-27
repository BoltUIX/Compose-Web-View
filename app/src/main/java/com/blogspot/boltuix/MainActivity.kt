package com.blogspot.boltuix

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewPage("https://www.boltuix.com/")
        }
    }
}




@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage(url: String){
    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()

            // to play video on a web view
            settings.javaScriptEnabled = true

            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })

}
