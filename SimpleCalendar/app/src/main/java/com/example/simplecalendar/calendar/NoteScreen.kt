package com.example.simplecalendar.calendar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.simplecalendar.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.*

@Composable
fun NoteScreen(navController: NavHostController, viewModel: CalendarViewModel, date: LocalDate) {
    val themeMode by viewModel.themeMode.collectAsState()
    val colorScheme = when (themeMode) {
        CalendarViewModel.ThemeMode.LIGHT -> lightColorScheme()
        CalendarViewModel.ThemeMode.DARK -> darkColorScheme()
        CalendarViewModel.ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    }

    val context = LocalContext.current
    val dateString = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    val dayData = viewModel.getDayData(date)

    var earnings by remember { mutableStateOf(dayData.earnings.toString()) }
    var spendings by remember { mutableStateOf(dayData.spendings.toString()) }
    var note by remember { mutableStateOf(dayData.note) }

    var isEditingEarnings by remember { mutableStateOf(false) }
    var isEditingSpendings by remember { mutableStateOf(false) }
    var isEditingNote by remember { mutableStateOf(note.isBlank()) }

    val profit = calculateProfit(earnings, spendings)

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {  // Set the background color here
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                // All your existing UI components
                Text(
                    text = "Note for day $dateString",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (isEditingNote) {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        label = { Text("Your Note") },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Button(
                        onClick = {
                            viewModel.saveDayData(
                                context,
                                date,
                                CalendarViewModel.DayData(
                                    earnings.toFloatOrNull() ?: 0f,
                                    spendings.toFloatOrNull() ?: 0f,
                                    note
                                )
                            )
                            isEditingNote = false
                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Accept Note", color = MaterialTheme.colorScheme.onPrimary)
                    }
                } else if (note.isNotEmpty()) {
                    Text(
                        text = note,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = { isEditingNote = true },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Edit Note", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEditingEarnings) {
                        OutlinedTextField(
                            value = earnings,
                            onValueChange = { earnings = it },
                            label = { Text("Earnings") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Button(
                            onClick = {
                                viewModel.saveDayData(
                                    context,
                                    date,
                                    CalendarViewModel.DayData(
                                        earnings.toFloatOrNull() ?: 0f,
                                        spendings.toFloatOrNull() ?: 0f,
                                        note
                                    )
                                )
                                isEditingEarnings = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Accept Earnings", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        Text("Earnings: $$earnings", color = MaterialTheme.colorScheme.onSurface)
                        Button(
                            onClick = { isEditingEarnings = true },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Edit Earnings", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditingSpendings) {
                        OutlinedTextField(
                            value = spendings,
                            onValueChange = { spendings = it },
                            label = { Text("Spendings") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Button(
                            onClick = {
                                viewModel.saveDayData(
                                    context,
                                    date,
                                    CalendarViewModel.DayData(
                                        earnings.toFloatOrNull() ?: 0f,
                                        spendings.toFloatOrNull() ?: 0f,
                                        note
                                    )
                                )
                                isEditingSpendings = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Accept Spendings", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        Text("Spendings: $$spendings", color = MaterialTheme.colorScheme.onSurface)
                        Button(
                            onClick = { isEditingSpendings = true },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Edit Spendings", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                    // Display Profit
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$dateString Profit: $profit",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Button(
                    onClick = { navController.navigate("shoppingList") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Go to Shopping List", color = MaterialTheme.colorScheme.onPrimary)
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Back", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
fun calculateProfit(earnings: String, spendings: String): Float {
    return (earnings.toFloatOrNull() ?: 0f) - (spendings.toFloatOrNull() ?: 0f)
}