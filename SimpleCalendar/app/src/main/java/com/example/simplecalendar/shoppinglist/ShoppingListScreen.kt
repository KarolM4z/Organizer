package com.example.simplecalendar.shoppinglist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.simplecalendar.CalendarViewModel
import com.example.simplecalendar.ShoppingListViewModel

@Composable
fun ShoppingListScreen(navController: NavHostController, calendarViewModel: CalendarViewModel, shoppingViewModel: ShoppingListViewModel) {
    val themeMode by calendarViewModel.themeMode.collectAsState()
    val colorScheme = when (themeMode) {
        CalendarViewModel.ThemeMode.LIGHT -> lightColorScheme()
        CalendarViewModel.ThemeMode.DARK -> darkColorScheme()
        CalendarViewModel.ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    var itemName by remember { mutableStateOf("") }
    var itemCalories by remember { mutableStateOf("") }

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = itemCalories,
                    onValueChange = { itemCalories = it },
                    label = { Text("Calories") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (itemName.isNotBlank() && itemCalories.isNotBlank()) {
                            shoppingViewModel.addItem(itemName.trim(), itemCalories.trim())
                            itemName = ""
                            itemCalories = ""
                            keyboardController?.hide()
                        }
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (itemName.isNotBlank() && itemCalories.isNotBlank()) {
                            shoppingViewModel.addItem(itemName.trim(), itemCalories.trim())
                            itemName = ""
                            itemCalories = ""
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Add Item")
                }

                Spacer(modifier = Modifier.height(16.dp))

                shoppingViewModel.shoppingItems.forEach { (item, calories) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "$item, $calories cal", modifier = Modifier.weight(1f))
                        Button(
                            onClick = { shoppingViewModel.removeItem(item, calories) },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Remove")
                        }
                    }
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save and Return")
                }
            }
        }
    }
}