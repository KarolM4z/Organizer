package com.example.simplecalendar

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShoppingListViewModel(context: Context) : ViewModel() {
    private val sharedPreferencesKey = "ShoppingListPreferences"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
    val shoppingItems = mutableStateListOf<Pair<String, String>>()

    init {
        loadItems()
    }

    private fun loadItems() {
        shoppingItems.clear()
        val itemsSet = sharedPreferences.getStringSet("shoppingItems", emptySet()) ?: emptySet()
        itemsSet.map { entry ->
            val (item, calories) = entry.split("||", limit = 2)
            shoppingItems.add(item to calories)
        }
    }

    fun addItem(item: String, calories: String) {
        val items = shoppingItems.map { it.first + "||" + it.second }.toMutableSet()
        items.add("$item||$calories")
        sharedPreferences.edit().putStringSet("shoppingItems", items).apply()
        shoppingItems.add(item to calories)
    }

    fun removeItem(item: String, calories: String) {
        val items = shoppingItems.map { it.first + "||" + it.second }.toMutableSet()
        items.remove("$item||$calories")
        sharedPreferences.edit().putStringSet("shoppingItems", items).apply()
        shoppingItems.remove(item to calories)
    }
}

class ShoppingListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingListViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
