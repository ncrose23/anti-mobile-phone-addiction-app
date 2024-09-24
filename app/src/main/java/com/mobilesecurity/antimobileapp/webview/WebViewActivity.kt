package com.mobilesecurity.antimobileapp.webview

import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView


class WebViewActivity : ComponentActivity(){


    override fun onCreate(savedInstanceState : Bundle?){
       super.onCreate(savedInstanceState)
        setContent {
          App()
        }
   }

    @Composable
    fun App(modifier: Modifier = Modifier) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(innerPadding)) {
                // composables here
                Text("PRESS ESCAPE TO EXIT", textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center,fontSize = 16.sp, fontFamily = FontFamily.SansSerif, fontStyle = FontStyle.Italic)
                WebViewComposable( url = "https://www.addictioncenter.com/drugs/phone-addiction/)")
            }
        }
    }
@Composable
    fun WebViewComposable (url : String ){
        var backEnable by remember {mutableStateOf(false)}
        var webView : WebView? = null
        AndroidView(modifier = Modifier, factory = { context ->
            WebView(context).apply{
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            webViewClient = object : WebViewClient(){
                override fun onPageStarted(view : WebView?, url : String?, favicon : Bitmap?){
                    backEnable = view!!.canGoBack()
                }
            }
            //settings.javaScriptEnabled = true
            loadUrl(url)
            webView = this
        }
        },update = {
            webView = it
        })
    BackHandler(enabled = backEnable) {
        webView?.goBack()
    }
    }
/*
Aadil's take
    @Composable
    fun WebViewComposable(url: String) {
        // Declare a string that contains a url
    // val url = https://www.addictioncenter.com/drugs/phone-addiction/

        // Adding a WebView inside AndroidView
        // with layout as full screen
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        }, update = {
            it.loadUrl(url)
        })
    }
*/

}