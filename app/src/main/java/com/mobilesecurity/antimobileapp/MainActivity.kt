package com.mobilesecurity.antimobileapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.ColorSpace.Rgb
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mobilesecurity.antimobileapp.network.RetrofitInstance
import com.mobilesecurity.antimobileapp.notifications.AlarmManagerModel
import com.mobilesecurity.antimobileapp.notifications.NotificationsPermissionRequesterViewModel
import com.mobilesecurity.antimobileapp.storage.StorageModel
import com.mobilesecurity.antimobileapp.ui.theme.AntimobileappTheme
import com.mobilesecurity.antimobileapp.userdata.ScreenTimeBroadcastReceiver
import com.mobilesecurity.antimobileapp.userdata.ScreenTimeViewModel
import com.mobilesecurity.antimobileapp.webview.WebViewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    private var context = this
    private val mainColor = Color(0, 139, 139)
    private val viewModel by viewModels<NotificationsPermissionRequesterViewModel>()
    private val alarmManagerModel by lazy {
        AlarmManagerModel(this.applicationContext)
    }
    private val storageModel by lazy {
        StorageModel(this.applicationContext)
    }
    private val screenTimeViewModel by viewModels<ScreenTimeViewModel>()
    private val screenTimeBroadcastReceiver by lazy {
        ScreenTimeBroadcastReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // REGISTER RECEIVER

        val lockFilter = IntentFilter()
        lockFilter.addAction(Intent.ACTION_SCREEN_ON)
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenTimeBroadcastReceiver, lockFilter)

        // GET screen time, clear storage once a day
        storageModel.setRepeatingClearStorageAlarm()
        screenTimeViewModel.screenTime = StorageModel.getScreenTime(this)
        enableEdgeToEdge()
        setContent {
            AntimobileappTheme {
                App()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPermission(this)
        screenTimeViewModel.screenTime = StorageModel.getScreenTime(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenTimeBroadcastReceiver)
    }

    @Preview(showBackground = true)
    @Composable
    fun App(modifier: Modifier = Modifier) {
        val scrollState = rememberScrollState()
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(scrollState)

            ) {
                // composables here
                MainUI()
            }
        }
    }

    @Composable
    fun MainUI() {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                color = mainColor, fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.SansSerif,
                text = "Mobile Phone Addiction App",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Permissions Button
            GetPermissionsButton()

            // only show the switch if the notification permission is granted
            if (viewModel.isGranted) {
                Spacer(modifier = Modifier.height(20.dp))
                ShowRepeatingNotificationsSwitch(onShowNotification = {
                    alarmManagerModel.cancelAlarm()
                    alarmManagerModel.setRepeatingAlarm()
                })
            }

            // Screentime viewer
            Spacer(modifier = Modifier.height(20.dp))
            ScreenTimeViewer()

            // text input for setting max screen time
            Spacer(modifier = Modifier.height(20.dp))
            TimeSetter()

            // motivational quote generator
            Spacer(modifier = Modifier.height(20.dp))
            MotivationalQuoteGenerator()

            // WebView and YouTube buttons
            WebViewComposable()
            PhoneAddictionVideoButton()
        }
    }


    @Composable
    fun TimeSetter() {
        var value by remember { mutableStateOf("") }

        // if the permission is not granted, don't show the text field
        if (!viewModel.isGranted) {
            return
        }
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number,

                ),
            value = value,
            onValueChange = { value = it },
            label = { Text("Enter Your Desired Screen Time!", color = Color.Black) },
            minLines = 1,
            maxLines = 1,colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Green,
                unfocusedIndicatorColor = mainColor
            ),
            modifier = Modifier.padding(20.dp).background(color = Color.White,RoundedCornerShape(10.dp)),
            placeholder = { Text("Enter a number in minutes") },
            keyboardActions = KeyboardActions(onDone = {
                if (value.isBlank()) {
                    this.defaultKeyboardAction(ImeAction.Done)
                    return@KeyboardActions
                }
                // set storage for repeating notifications preference, min 1 minute.
                StorageModel.setUserScreenAlertTime(context, 1.coerceAtLeast(value.toInt()))

                Toast.makeText(context, "Screen time set to $value minutes", Toast.LENGTH_LONG)
                    .show()
                // dismiss keyboard
                this.defaultKeyboardAction(ImeAction.Done)
            })
        )
    }

    // dont worry about this
    fun onCopyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    @Composable
    fun MotivationalQuoteGenerator() {
        var quote by remember {
            mutableStateOf("")
        }
        var isLoading by remember {
            mutableStateOf(false)
        }
        val scope = rememberCoroutineScope()

        // fetch only once at beginning
        LaunchedEffect(key1 = true) {
            this.launch {
                withContext(Dispatchers.Main) {
                    isLoading = true
                }
                withContext(Dispatchers.IO) {
                    val fetchedQuote = RetrofitInstance.getQuote(context)
                    Log.d("quote", "fetched quote: ${fetchedQuote?.content}")
                    withContext(Dispatchers.Main) {
                        if (fetchedQuote?.content != null) {
                            quote = "\"${fetchedQuote.content}\" - ${fetchedQuote.author}"
                        } else {
                            quote = ""
                        }
                        isLoading = false
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {

            // if loading, show progress spinner
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
            }
            if (!isLoading) {
                Text(quote, modifier = Modifier.fillMaxWidth(0.7f), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // button to fetch new quote each time you want new one
            Button(
                colors = ButtonDefaults.buttonColors(
                containerColor = mainColor

            ),
                onClick = {
                    // get quote
                    scope.launch {
                        withContext(Dispatchers.Main) {
                            isLoading = true
                        }
                        val fetchedQuote = RetrofitInstance.getQuote(context)
                        withContext(Dispatchers.Main) {
                            if (fetchedQuote?.content != null) {
                                quote = "\"${fetchedQuote.content}\" - ${fetchedQuote.author}"
                            } else {
                                quote = ""
                            }
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Text("Get a new quote")
            }
        }
    }

    @Composable
    fun ScreenTimeViewer() {
        Text(
            "Screen time in minutes: ${screenTimeViewModel.screenTime / 1000 / 60}",
            fontWeight = FontWeight.Bold,

            )
    }

    @Composable
    fun GetPermissionsButton(modifier: Modifier = Modifier) {
        // render different UI based on whether the permission is granted
        viewModel.CreateLauncher()
        if (viewModel.isGranted) {
            Text(
                "Notifications permission is granted. You're ready to go!",
                modifier = Modifier.fillMaxWidth(0.7f),
                textAlign = TextAlign.Center
            )
        } else {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = mainColor

            ), onClick = {
                viewModel.requestPermission()
            }) {
                Text("Activate Notifications")
            }
        }
    }


    @Composable
    fun ShowRepeatingNotificationsSwitch(
        onShowNotification: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        var isChecked by remember {
            mutableStateOf(
                StorageModel.getRepeatingNotificationsStatus(
                    context
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Toggle Repeating Notifications")
            Switch(colors = SwitchDefaults.colors(
                checkedThumbColor = mainColor,
                checkedTrackColor = Color.LightGray,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ), checked = isChecked, onCheckedChange = {
                isChecked = it
                if (isChecked) {
                    onShowNotification()
                    StorageModel.setRepeatingNotificationsStatus(context, true)
                } else {
                    StorageModel.setRepeatingNotificationsStatus(context, false)
                }
            })
        }
    }


    @Composable
    fun WebViewComposable() {
        val context = LocalContext.current
        Spacer(modifier = Modifier.height(10.dp))
        // Creating a Button that on-click
        // implements an Intent to go to WebView
        //escape will work as your back option

        Button(
            onClick = {
                context.startActivity(Intent(context, WebViewActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = mainColor,
                contentColor = mainColor
            ),
        ) {
            Text("Learn More: Go to Web-View", color = Color.White)
        }
    }

    @Composable
    fun PhoneAddictionVideoButton(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Spacer(modifier = Modifier.height(10.dp))
        // Creating a button that redirects to a YouTube video about phone addiction

        Button(
            onClick = {
                val videoId = "aNvvOQMx0jY"

                val appIntent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://www.youtube.com/watch?v=$videoId"
                    )
                )
                appIntent.setPackage("com.google.android.youtube")


                // Check if the YouTube app is installed
                if(appIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(appIntent) // Open the video in the YouTube app
                } else {
                    // If the YouTube app is not installed, open the video in a web browser
                    val webIntent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "https://www.youtube.com/watch?v=$videoId"
                        )
                    )
                    context.startActivity(webIntent)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = mainColor,
                contentColor = mainColor
            ),
        ) {
            Text("Learn More: Open YouTube", color = Color.White)
        }
    }
}


