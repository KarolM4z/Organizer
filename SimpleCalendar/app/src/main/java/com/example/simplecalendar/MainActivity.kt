package com.example.simplecalendar

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.simplecalendar.calendar.CalendarScreen
import com.example.simplecalendar.calendar.NoteScreen
import com.example.simplecalendar.ui.theme.SimpleCalendarTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: CalendarViewModel = viewModel()
            viewModel.loadNotesFromSharedPreferences(applicationContext)
            viewModel.loadThemeFromPreferences(applicationContext)

            val navController = rememberNavController()
            val context = LocalContext.current
            val themeMode by viewModel.themeMode.collectAsState()

            SimpleCalendarTheme(
                darkTheme = when (themeMode) {
                    CalendarViewModel.ThemeMode.DARK -> true
                    CalendarViewModel.ThemeMode.LIGHT -> false
                    CalendarViewModel.ThemeMode.SYSTEM -> isSystemInDarkTheme(context)
                    // Cover all possibilities
                }
            ) {
                NavHost(navController = navController, startDestination = "calendar") {
                    composable("calendar") {
                        CalendarScreen(navController, viewModel)
                    }
                    composable("note/{date}", arguments = listOf(navArgument("date") { type = NavType.StringType })) { backStackEntry ->
                        val dateStr = backStackEntry.arguments?.getString("date")
                        val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                        NoteScreen(navController, viewModel, date)
                    }
                }
            }
        }
    }
}

@Composable
fun isSystemInDarkTheme(context: Context): Boolean {
    val uiMode = context.resources.configuration.uiMode
    return uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

