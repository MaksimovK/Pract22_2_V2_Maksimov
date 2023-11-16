package com.example.pract22_2v2_maksimov

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Аннотация, обозначающая, что это база данных с использованием Room
@Database(entities = [Pokemon::class], version = 1)
abstract class PokemonDatabase : RoomDatabase() {
    // Абстрактная функция, предоставляющая доступ к Data Access Object (DAO) для работы с базой данных
    abstract fun pokemonDao(): PokemonDao

    // Статический компаньон-объект для реализации паттерна Singleton
    companion object {
        // Volatile используется для того, чтобы значение переменной INSTANCE всегда было актуальным
        @Volatile
        private var INSTANCE: PokemonDatabase? = null

        // Функция, возвращающая единственный экземпляр базы данных (Singleton)
        fun getInstance(context: Context): PokemonDatabase {
            // Если INSTANCE не равен null, возвращаем его
            return INSTANCE ?: synchronized(this) {
                // Если INSTANCE все еще равен null, создаем новый экземпляр базы данных
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonDatabase::class.java,
                    "poke_db"
                ).build()
                // Присваиваем INSTANCE созданный экземпляр
                INSTANCE = instance
                // Возвращаем созданный или уже существующий экземпляр базы данных
                instance
            }
        }
    }
}
