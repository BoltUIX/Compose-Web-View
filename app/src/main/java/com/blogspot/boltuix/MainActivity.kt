package com.blogspot.boltuix

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader

var loadURL = "https://www.boltuix.com/"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewPage("https://www.boltuix.com/")
            //WebViewPage("file:///android_asset/shop.html") //OFFLINE
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage(url: String){

    val openFullDialogCustom = remember { mutableStateOf(false) }
    if (openFullDialogCustom.value) {

        // Dialog function
        Dialog(
            onDismissRequest = {
                openFullDialogCustom.value = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // experimental
            )
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {



                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),

                        )

                    Spacer(modifier = Modifier.height(20.dp))
                    //.........................Text: title
                    Text(
                        text = "Loading...",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    //.........................Text : description
                    Text(
                        text = "Please wait",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        letterSpacing = 3.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    //.........................Spacer
                    Spacer(modifier = Modifier.height(24.dp))

                }

            }
        }

    }
    //..........................................................................




    val context  = LocalContext.current

    //.................................................
    // Compose WebView Part 9 | Removes or Stop Ad in web
    val adServers = StringBuilder()
    var line: String? = ""
    val inputStream = context.resources.openRawResource(R.raw.adblockserverlist)
    val br = BufferedReader(InputStreamReader(inputStream))
    try {
        while (br.readLine().also { line = it } != null) {
            adServers.append(line)
            adServers.append("\n")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }



    var backEnabled by remember { mutableStateOf(false) }
    var webView: WebView? = null


    val mutableStateTrigger = remember { mutableStateOf(false) }
    val infoDialog = remember { mutableStateOf(false) }



    //The Configuration object represents all of the current configurations, not just the ones that have changed.
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // Toast.makeText(context, "landscape", Toast.LENGTH_SHORT).show()
        }
        else -> {
            //Toast.makeText(context, "portrait", Toast.LENGTH_SHORT).show()
        }
    }

    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(
        factory = {
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


                webViewClient = object : WebViewClient() {


                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        super.onReceivedError(view, request, error)
                        Log.d("test001","error")
                        loadURL = "file:///android_asset/404.html"
                        mutableStateTrigger.value = true

                    }

                    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                        val empty = ByteArrayInputStream("".toByteArray())
                        val kk5 = adServers.toString()
                        if (kk5.contains(":::::" + request.url.host))
                            return WebResourceResponse("text/plain", "utf-8", empty)
                        return super.shouldInterceptRequest(view, request)
                    }


                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        openFullDialogCustom.value = true
                        backEnabled = view.canGoBack()
                    }

                    // Compose WebView Part 7 | Hide elements from web view
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        openFullDialogCustom.value = false
                        removeElement(view!!)
                    }

                    // Compose WebView Part 5 | Should Override URL Loading
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        if (url.contains("facebook.com")) {
                            loadURL = "https://www.instagram.com/boltuix/"
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
                webView = this
            }
        }, update = {
            webView = it
            // it.loadUrl(url)
        })


    if (mutableStateTrigger.value) {
        // WebViewPage("https://www.instagram.com/boltuix/")
         WebViewPage(loadURL)
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


    BackHandler(enabled = backEnabled) {
        removeElement(webView!!)
        webView?.goBack()
    }

}

fun removeElement(webView: WebView) {

    // hide element by id
    webView.loadUrl("javascript:(function() { document.getElementById('blog-pager').style.display='none';})()");

    // we can also hide class name
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[0].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[1].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[2].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[3].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[4].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[5].style.display='none';})()")
    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[6].style.display='none';})()")
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
