package com.example.pract22_2v2_maksimov

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// Адаптер для RecyclerView, использующий ListAdapter для автоматического обновления списка
class PokemonAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(DIFF_CALLBACK) {

    // Слушатель для обработки кликов на элементах списка
    private var onDeleteClickListener: ((Pokemon) -> Unit)? = null

    // Установка слушателя для обработки кликов на кнопке удаления
    fun setOnDeleteClickListener(listener: (Pokemon) -> Unit) {
        onDeleteClickListener = listener
    }

    // Создание ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        // Инфлейт разметки элемента списка
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return PokemonViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val currentPokemon = getItem(position)
        holder.bind(currentPokemon)
    }

    // ViewHolder для элементов списка
    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Элементы разметки, с которыми будет взаимодействовать ViewHolder
        private val textHistory: TextView = itemView.findViewById(R.id.factText)

        init {
            // Обработка клика на элементе списка
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val pokemon = getItem(position)
                    listener.onItemClick(pokemon)
                }
            }

            // Обработка долгого клика на элементе списка (удаление элемента)
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val pokemon = getItem(position)
                    onDeleteClickListener?.invoke(pokemon)
                    return@setOnLongClickListener true
                }
                false
            }
        }

        // Привязка данных к элементам разметки
        fun bind(pokemon: Pokemon) {
            textHistory.text = pokemon.text
        }
    }

    // Интерфейс для обработки кликов на элементах списка
    interface OnItemClickListener {
        fun onItemClick(pokemon: Pokemon)
    }

    // Объект для сравнения элементов списка при обновлении
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Pokemon>() {
            // Проверка, являются ли элементы одним и тем же объектом
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem.id == newItem.id
            }

            // Проверка, имеют ли элементы одинаковые данные
            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem == newItem
            }
        }
    }
}
