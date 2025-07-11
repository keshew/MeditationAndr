package com.keshevv.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import androidx.navigation.navArgument
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState


class NotesViewModel : ViewModel() {
    private val _notes = mutableMapOf<String, String>()
    private val _currentNote = MutableStateFlow("")

    val currentNote: StateFlow<String> = _currentNote

    fun loadNote(date: String) {
        _currentNote.value = _notes[date] ?: ""
    }

    fun saveNote(date: String, text: String) {
        _notes[date] = text
        _currentNote.value = text
    }
}

val motivationalPhrases = listOf(
    "Every day is a new beginning. Take a deep breath and start again.",
    "You are stronger than you think.",
    "Don't give up; great things take time.",
    "Believe in yourself and your abilities.",
    "Small steps lead to big victories.",
    "Today is the best day for new beginnings.",
    "Be grateful for every moment.",
    "Mistakes are lessons, not failures.",
    "Focus on what you can change.",
    "You are capable of more than you imagine.",
    "Keep pushing forward, no matter what.",
    "Stay positive, work hard, make it happen.",
    "Your potential is endless.",
    "Success is the sum of small efforts repeated daily.",
    "Dream it. Wish it. Do it.",
    "Believe you can and you're halfway there.",
    "Stay patient and trust your journey.",
    "Progress, not perfection.",
    "You are your only limit.",
    "Push yourself because no one else is going to do it for you.",
    "The harder you work for something, the greater you'll feel when you achieve it.",
    "Don't watch the clock; do what it does. Keep going.",
    "Great things never come from comfort zones.",
    "Dream bigger. Do bigger.",
    "Don't stop until you're proud.",
    "Work hard in silence, let success be your noise.",
    "The key to success is to focus on goals, not obstacles.",
    "Believe in your dreams and they may come true.",
    "Success doesn't just find you. You have to go out and get it.",
    "Stay focused and never give up."
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            MyAppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToMeditations = { navController.navigate("topics") },
                            onNavigateToCalendar = { navController.navigate("calendar") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isDarkTheme = it },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("topics") {
                        TopicsScreen(
                            onTopicSelected = { topic -> navController.navigate("meditation/$topic") },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("meditation/{topic}") { backStackEntry ->
                        val topic = backStackEntry.arguments?.getString("topic") ?: "Unknown"
                        MeditationScreen(
                            topic = topic,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("calendar") {
                        CalendarScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNoteSelected = { date -> navController.navigate("note/$date") },
                            onAddNote = { /* Можно убрать кнопку добавления, если не нужна */ }
                        )
                    }
                    composable(
                        "note/{date}",
                        arguments = listOf(navArgument("date") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val date = backStackEntry.arguments?.getString("date") ?: ""
                        MotivationalPhraseScreen(
                            date = date,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MotivationalPhraseScreen(
    date: String,
    onNavigateBack: () -> Unit
) {
    // Извлекаем день из даты, пример: "2025-07-03" -> 3
    val day = date.takeLast(2).toIntOrNull() ?: 1
    val phrase = motivationalPhrases.getOrNull(day - 1) ?: "Stay positive and keep going!"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Motivational Phrase for $date") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = phrase,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun MyAppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    if (darkTheme) {
        MaterialTheme(
            colors = darkColors(),
            shapes = Shapes(
                small = RoundedCornerShape(12.dp),
                medium = RoundedCornerShape(24.dp),
                large = RoundedCornerShape(32.dp)
            ),
            content = content
        )
    } else {
        MaterialTheme(
            colors = lightColors(),
            shapes = Shapes(
                small = RoundedCornerShape(12.dp),
                medium = RoundedCornerShape(24.dp),
                large = RoundedCornerShape(32.dp)
            ),
            content = content
        )
    }
}

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToMeditations: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = MaterialTheme.colors.background,
        animationSpec = tween(600)
    )

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Micro Meditation", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Short meditations for 1-3 minutes to reduce stress and improve focus",
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "“Every day is a new beginning. Take a deep breath and start again.”",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToMeditations,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Choose Meditation", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToCalendar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Calendar & Notes", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Settings", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Theme", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Theme")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colors.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Notifications", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Enable daily reminders")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colors.primary
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Daily motivational notifications are not implemented in this demo.",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TopicsScreen(
    onTopicSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val topics = listOf("Relaxation", "Energy", "Focus")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Meditation Topic") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            items(topics) { topic ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onTopicSelected(topic) },
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    backgroundColor = when (topic) {
                        "Relaxation" -> Color(0xFFB2DFDB)
                        "Energy" -> Color(0xFFFFCC80)
                        "Focus" -> Color(0xFF90CAF9)
                        else -> MaterialTheme.colors.surface
                    }
                ) {
                    Text(
                        text = topic,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun MeditationScreen(
    topic: String,
    onNavigateBack: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(180) } // 3 minutes

    val targetBackgroundColor = when (topic) {
        "Relaxation" -> Color(0xFFB2DFDB)
        "Energy" -> Color(0xFFFFF3E0)
        "Focus" -> Color(0xFFE3F2FD)
        else -> MaterialTheme.colors.background
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(durationMillis = 1000)
    )

    val waveColor = when (topic) {
        "Relaxation" -> Color(0xFF4DB6AC)
        "Energy" -> Color(0xFFFFB74D)
        "Focus" -> Color(0xFF42A5F5)
        else -> MaterialTheme.colors.primary
    }

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meditation: $topic") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.surface
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .background(Color.Transparent)
                ) {
                    val maxRadius = size.minDimension / 2
                    drawCircle(
                        color = waveColor.copy(alpha = alpha),
                        radius = maxRadius * scale
                    )
                    drawCircle(
                        color = waveColor.copy(alpha = alpha * 0.6f),
                        radius = maxRadius * scale * 0.7f
                    )
                    drawCircle(
                        color = waveColor.copy(alpha = alpha * 0.3f),
                        radius = maxRadius * scale * 0.4f
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = formatTime(timeLeft),
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { isRunning = !isRunning },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(if (isRunning) "Stop" else "Start")
                }
            }
        }
    }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
        if (timeLeft == 0) {
            isRunning = false
        }
    }
}

fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}

// --- Экран календаря с выбором даты ---
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onNoteSelected: (String) -> Unit,
    onAddNote: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddNote) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Note")
                    }
                }
            )
        }
    ) { paddingValues ->
        val dates = remember {
            List(30) { index ->
                val day = index + 1
                "2025-07-${if (day < 10) "0$day" else "$day"}"
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(dates) { date ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNoteSelected(date) },
                    elevation = 4.dp
                ) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

// --- Экран заметки ---
@Composable
fun NoteScreen(
    date: String,
    onNavigateBack: () -> Unit,
    notesViewModel: NotesViewModel = viewModel()
) {
    val noteText by notesViewModel.currentNote.collectAsState()

    LaunchedEffect(date) {
        notesViewModel.loadNote(date)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note for $date") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = noteText,
                onValueChange = { notesViewModel.saveNote(date, it) },
                label = { Text("Enter your note") },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
