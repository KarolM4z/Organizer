package com.example.simplecalendar.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.simplecalendar.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*


@Composable
fun CalendarScreen(navController: NavHostController, viewModel: CalendarViewModel) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current // To use for saving preferences

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeekDaysRow()
            MonthView(yearMonth = currentMonth, navController = navController)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { currentMonth = currentMonth.minusMonths(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Previous", color = Color.White)
                }

                Button(
                    onClick = { currentMonth = currentMonth.plusMonths(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Next", color = Color.White)
                }
            }

            val currentMonthProfit = viewModel.calculateMonthlyProfit(currentMonth)
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year} Profit: $currentMonthProfit",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
            )

            // Theme switch dropdown
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(onClick = { expanded = true }) {
                    Text("Theme")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Light") },
                        onClick = {
                            viewModel.saveThemePreference(context, CalendarViewModel.ThemeMode.LIGHT)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Dark") },
                        onClick = {
                            viewModel.saveThemePreference(context, CalendarViewModel.ThemeMode.DARK)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("System") },
                        onClick = {
                            viewModel.saveThemePreference(context, CalendarViewModel.ThemeMode.SYSTEM)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun WeekDaysRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )

        daysOfWeek.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MonthView(yearMonth: YearMonth, navController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(8.dp)
    ) {
        val firstDayOffset = calculateOffset(yearMonth)
        items(firstDayOffset) {
            Box(modifier = Modifier
                .aspectRatio(1f)
                .padding(4.dp))
        }

        items(yearMonth.lengthOfMonth()) { day ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clickable {
                        navController.navigate("note/${yearMonth.atDay(day + 1)}")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${day + 1}")
            }
        }
    }
}

private fun calculateOffset(yearMonth: YearMonth): Int {
    val firstDayOfWeekValue = DayOfWeek.MONDAY.value
    val firstOfMonth = yearMonth.atDay(1).dayOfWeek.value
    return (firstOfMonth - firstDayOfWeekValue + 7) % 7
}