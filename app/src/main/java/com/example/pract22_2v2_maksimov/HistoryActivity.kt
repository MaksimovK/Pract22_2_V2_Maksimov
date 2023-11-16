package com.example.pract22_2v2_maksimov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private lateinit var database: PokemonDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Инициализация базы данных
        database = PokemonDatabase.getInstance(applicationContext)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PokemonAdapter(object : PokemonAdapter.OnItemClickListener {
            override fun onItemClick(pokemon: Pokemon) {
                showEditDialog(pokemon)
            }
        })
        recyclerView.adapter = adapter

        // Загрузка истории фактов из базы данных и отображение их в RecyclerView
        lifecycleScope.launch(Dispatchers.IO) {
            val histories = database.pokemonDao().getRecentPokemons()
            withContext(Dispatchers.Main) {
                adapter.submitList(histories)
            }
        }

        // Устанавливаем слушатель для удаления факта
        adapter.setOnDeleteClickListener { fact ->
            lifecycleScope.launch(Dispatchers.IO) {
                // Удаление выбранного факта из базы данных
                database.pokemonDao().deletePokemon(fact)

                // Обновление списка фактов на экране
                val histories = database.pokemonDao().getRecentPokemons()
                withContext(Dispatchers.Main) {
                    adapter.submitList(histories)
                }
            }
        }
    }

    // Отображение диалогового окна для редактирования текста покемона
    private fun showEditDialog(pokemon: Pokemon) {
        val editText = EditText(this)
        editText.setText(pokemon.text)

        AlertDialog.Builder(this)
            .setTitle("Редактировать элемент")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newText = editText.text.toString()
                val updatedPokemon = Pokemon(pokemon.id, newText)

                lifecycleScope.launch(Dispatchers.IO) {
                    // Обновление текста покемона в базе данных
                    database.pokemonDao().updatePokemons(updatedPokemon)

                    // Обновление списка покемонов на экране
                    val histories = database.pokemonDao().getRecentPokemons()
                    withContext(Dispatchers.Main) {
                        adapter.submitList(histories)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}