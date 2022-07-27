package com.blogspot.boltuix

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView


class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            WebViewPage("https://www.boltuix.com/")
            //WebViewPage("file:///android_asset/shop.html") //OFFLINE
        }
    }
}




@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage(url: String){
    val mutableStateTrigger = remember { mutableStateOf(false) }
    val infoDialog = remember { mutableStateOf(false) }

    val context  = LocalContext.current

    //The Configuration object represents all of the current configurations, not just the ones that have changed.
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Toast.makeText(context, "landscape", Toast.LENGTH_SHORT).show()
        }
        else -> {
            Toast.makeText(context, "portrait", Toast.LENGTH_SHORT).show()
        }
    }


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

            // to verify that the client requesting your web page is actually your Android app.
            settings.userAgentString = System.getProperty("http.agent") //Dalvik/2.1.0 (Linux; U; Android 11; M2012K11I Build/RKQ1.201112.002)

            settings.useWideViewPort = true


            // Bind JavaScript code to Android code
            addJavascriptInterface(WebAppInterface(context,infoDialog), "Android")

            // Compose WebView Part 5 | Should Override URL Loading
            webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.contains("facebook.com")) {
                         mutableStateTrigger.value = true
                         Toast.makeText(context, "Custom Action", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    else{
                        view.loadUrl(url)
                    }
                    return false
                }
            }


            loadUrl(url)


        }
    }, update = {
        it.loadUrl(url)
    })


    if (mutableStateTrigger.value) {
        WebViewPage("https://www.instagram.com/boltuix/")
    }
    if (infoDialog.value) {
        InfoDialog(
            title = "TEKHEIST",
            desc = "We are at the forefront of innovation.\n" +
                    "Discover with us the possibilities of your next project.",
            onDismiss = {
                infoDialog.value = false
            }
        )
    }
}

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context, var infoDialog: MutableState<Boolean>) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        infoDialog.value=true
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}
