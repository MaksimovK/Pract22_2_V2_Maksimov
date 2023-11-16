package com.example.pract22_2v2_maksimov

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Аннотация Dao (Data Access Object) указывает, что это интерфейс для работы с базой данных Room
@Dao
interface PokemonDao {
    // Аннотация @Query используется для выполнения SQL-запроса к базе данных
    @Query("SELECT * FROM pokemons ORDER BY id DESC LIMIT 10")
    // Функция для получения списка последних 10 покемонов из базы данных
    suspend fun getRecentPokemons(): List<Pokemon>

    // Аннотация @Insert используется для вставки данных в базу данных
    @Insert
    // Функция для вставки нового покемона в базу данных
    suspend fun insertPokemon(pokemon: Pokemon)

    // Аннотация @Delete используется для удаления данных из базы данных
    @Delete
    // Функция для удаления покемона из базы данных
    suspend fun deletePokemon(pokemon: Pokemon)

    // Аннотация @Query используется для выполнения SQL-запроса к базе данных
    @Query("DELETE FROM pokemons")
    // Функция для удаления всех покемонов из базы данных
    suspend fun deleteAllPokemons()

    // Аннотация @Update используется для обновления данных в базе данных
    @Update
    // Функция для обновления информации о покемоне в базе данных
    suspend fun updatePokemons(pokemon: Pokemon)
}
