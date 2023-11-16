package com.example.pract22_2v2_maksimov

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class InfoActivity : AppCompatActivity() {
    private lateinit var editTextPokemon: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonSearchHistory: Button
    private lateinit var textViewResult: TextView
    private lateinit var imageViewPokemon: ImageView
    private lateinit var database: PokemonDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Инициализация элементов интерфейса
        editTextPokemon = findViewById(R.id.searchPokemon)
        buttonSearch = findViewById(R.id.button_search)
        buttonSearchHistory = findViewById(R.id.button_search_history)
        textViewResult = findViewById(R.id.result)
        imageViewPokemon = findViewById(R.id.imagePokemon)

        // Нажатие кнопки поиска
        buttonSearch.setOnClickListener {
            val pokemonNameOrId = editTextPokemon.text.toString()
            searchPokemon(pokemonNameOrId)
            textViewResult.visibility = View.VISIBLE
        }

        // Переход на экран с базой данных истории поиска
        buttonSearchHistory.setOnClickListener {
            val intent = Intent(this@InfoActivity, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun searchPokemon(nameOrId: String) {
        // Инициализация базы данных Room
        database = Room.databaseBuilder(
            applicationContext,
            PokemonDatabase::class.java, "poke_db"
        ).build()

        // Создание корутины для асинхронной работы
        GlobalScope.launch(Dispatchers.IO) {
            // Формирование URL-адреса для запроса к API покемонов
            val apiUrl = "https://pokeapi.co/api/v2/pokemon/$nameOrId"

            try {
                // Открытие соединения с API
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Получение кода ответа от сервера
                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Инициализация потока чтения для ответа от сервера.
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))

                    // Создание объекта StringBuilder для хранения ответа
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // Парсинг JSON-объекта из ответа.
                    val jsonObject = JSONObject(response.toString())

                    // Получение данных о покемоне
                    val name = jsonObject.getString("name")
                    val id = jsonObject.getInt("id")
                    val types = jsonObject.getJSONArray("types")
                    val typeList = mutableListOf<String>()
                    for (i in 0 until types.length()) {
                        val typeObj = types.getJSONObject(i)
                        val typeName = typeObj.getJSONObject("type").getString("name")
                        typeList.add(typeName)
                    }
                    val abilities = jsonObject.getJSONArray("abilities")
                    val abilityList = mutableListOf<String>()
                    for (i in 0 until abilities.length()) {
                        val abilityObj = abilities.getJSONObject(i)
                        val abilityName = abilityObj.getJSONObject("ability").getString("name")
                        abilityList.add(abilityName)
                    }
                    val speciesUrl = jsonObject.getJSONObject("species").getString("url")
                    val speciesInfo = fetchSpeciesInfo(speciesUrl)
                    val imageUrl = jsonObject.getJSONObject("sprites").getString("front_default")

                    runOnUiThread {
                        // Вывод информации
                        val resultText = "Имя: $name\nID: $id\nТипы: ${typeList.joinToString()}\nСпособности: ${abilityList.joinToString()}\nХарактеристики: $speciesInfo"
                        textViewResult.text = resultText

                        // Загрузка изображения с использованием Picasso
                        Picasso.get().load(imageUrl).into(imageViewPokemon)

                        // Сохранение запроса в базе данных
                        val pokemon = Pokemon(text = resultText)
                        GlobalScope.launch(Dispatchers.IO) {
                            database.pokemonDao().insertPokemon(pokemon)
                            Log.d(ContentValues.TAG, "Покемон сохранен в базе данных: $pokemon")
                        }
                    }
                } else {
                    runOnUiThread {
                        textViewResult.text = "Покемон не найден"
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                runOnUiThread {
                    textViewResult.text = "Ошибка при выполнении запроса"
                }
            }
        }
    }

    // Функция для получения характеристик покемона
    private fun fetchSpeciesInfo(speciesUrl: String): String {
        val connection = URL(speciesUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()

            val jsonObject = JSONObject(response.toString())
            val flavorTextEntries = jsonObject.getJSONArray("flavor_text_entries")

            var englishFlavorText = "Информация о характеристиках не найдена"

            for (i in 0 until flavorTextEntries.length()) {
                val entry = flavorTextEntries.getJSONObject(i)
                val language = entry.getJSONObject("language").getString("name")
                val flavorText = entry.getString("flavor_text")

                if (language == "en") {
                    englishFlavorText = flavorText
                    break
                }
            }
            englishFlavorText
        } else {
            "Информация о характеристиках не найдена"
        }
    }
}
