package com.example.majormuscle

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.majormuscle.ui.theme.MajorMuscleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Context.scheduleAlert(timeIntervalMillis: Long, message: String) {
    val intent = Intent(this, AlertReceiver::class.java).apply {
        putExtra("alert_message", message)
    }
    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeIntervalMillis, timeIntervalMillis, pendingIntent)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MajorMuscleTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    containerColor = Color.Black
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = Screen.Main.route) {
                        composable(Screen.Main.route) {
                            MainContent(navController, Modifier.padding(innerPadding))
                        }
                        composable(Screen.Exercises.route) {
                            ExercisesScreen()
                        }
                        composable(Screen.Alerts.route) {
                            AlertsScreen()
                        }
                        composable(Screen.Info.route) {
                            InfoScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Major: Gains",
            color = Color(0xFFCA9510),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Button(
            onClick = { navController.navigate(Screen.Exercises.route) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(text = "Exercises", fontSize = 18.sp)
        }

        Button(
            onClick = { navController.navigate(Screen.Alerts.route) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(text = "Alerts", fontSize = 18.sp)
        }

        Button(
            onClick = { navController.navigate(Screen.Info.route) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(text = "More Info", fontSize = 18.sp)
        }
    }
}

@Composable
fun ExercisesScreen() {
    val context = LocalContext.current
    val videoUrl = "https://www.youtube.com/watch?v=YvrKIQ_Tbsk"  // Link to the YouTube video

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Exercise Videos", fontSize = 24.sp, color = Color(0xFFCA9510))
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                context.startActivity(intent)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Watch on YouTube", fontSize = 18.sp, fontStyle = FontStyle.Italic)
        }
    }
}


@Composable
fun YouTubeVideoView(videoId: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true  // Enable DOM storage API
                    useWideViewPort = true   // Enables the viewport
                    loadWithOverviewMode = true // Loads the WebView completely zoomed out
                }
                loadUrl("https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1")
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AlertsScreen() {
    val context = LocalContext.current
    var timeInterval by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    val confirmationMessage = remember { mutableStateOf("") } // Use mutableStateOf here

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = timeInterval,
            onValueChange = { timeInterval = it },
            label = { Text("Time Interval (seconds)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = alertMessage,
            onValueChange = { alertMessage = it },
            label = { Text("Alert Message") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (timeInterval.isNotEmpty() && alertMessage.isNotEmpty()) {
                    setAlert(context, timeInterval.toLong() * 1000, alertMessage, confirmationMessage)
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Set Alert")
        }
        if (confirmationMessage.value.isNotEmpty()) { // Access the 'value' here correctly
            Text(confirmationMessage.value, color = MaterialTheme.colorScheme.primary) // Use 'value' to read state
        }
    }
}

// Function to handle setting the alert
fun setAlert(context: Context, timeIntervalMillis: Long, message: String, confirmationMessage: MutableState<String>) {
    context.scheduleAlert(timeIntervalMillis, message)
    confirmationMessage.value = "Alert set for every ${timeIntervalMillis / 1000} seconds: $message" // Update 'value' here
}

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("alert_message") ?: "Time to check the app!"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        // Here, you might want to show a notification instead
    }
}



@Composable
fun InfoScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Welcome To The More Info Screen! Unfortunately, This app is still a work in progress. Stay Tuned For Updates!", fontSize = 20.sp,color = Color(0xFFCA9510),)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MajorMuscleTheme {
        MainContent(rememberNavController())
    }
}
@Composable
fun ShootingStarsBackground() {
    // List to hold our shooting stars
    val stars = remember { mutableStateListOf<Animatable<Offset, AnimationVector2D>>() }
    val scope = rememberCoroutineScope()

    // Launching an effect that continually adds new stars
    LaunchedEffect(Unit) {
        while (true) {
            // Random delay before the next star appears
            delay((1000..5000).random().toLong())
            // Create a new star Animatable
            val startX = (0..1000).random().toFloat()  // Random starting X coordinate
            val startY = -100f  // Start above the visible screen area
            val endX = startX + (-200..200).random()  // End position has a slight horizontal variance
            val endY = startY + 1000f  // Move downwards off the screen

            val animatable = Animatable(Offset(startX, startY), Offset.VectorConverter)
            stars.add(animatable)

            // Animate star to the end position and then remove it from the list
            scope.launch {
                animatable.animateTo(
                    targetValue = Offset(endX, endY),
                    animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
                )
                stars.remove(animatable)
            }
        }
    }

    // Drawing each star as a circle on the canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { star ->
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = star.value
            )
        }
    }
}