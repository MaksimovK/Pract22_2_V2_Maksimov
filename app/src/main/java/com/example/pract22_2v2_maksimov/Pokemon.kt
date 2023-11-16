package com.example.pract22_2v2_maksimov

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pokemons")
data class Pokemon(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var text: String
)
