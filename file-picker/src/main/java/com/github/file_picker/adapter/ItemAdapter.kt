package com.github.file_picker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.file_picker.FilePicker
import com.github.file_picker.extension.isValidPosition
import com.github.file_picker.model.Media
import ir.one_developer.file_picker.databinding.ItemLayoutBinding

class ItemAdapter : ListAdapter<Media, ItemVH>(COMPARATOR) {

    private var listener: ((Int) -> Unit)? = null
    private var accentColor: Int = FilePicker.DEFAULT_ACCENT_COLOR
    private var limitSelectionCount: Int = FilePicker.DEFAULT_LIMIT_COUNT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemVH(
        listener = listener,
        accentColor = accentColor,
        binding = ItemLayoutBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
    )

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelect(position: Int) {
        if (limitSelectionCount > 1) {
            val item = getItem(position)

            if (item.isSelected) {
                item.isSelected = !item.isSelected
                notifyItemChanged(position)
                return
            }

            if (currentList.filter { it.isSelected }.size < limitSelectionCount) {
                item.isSelected = !item.isSelected
                notifyItemChanged(position)
            }

        } else {
            if (!currentList.isValidPosition(lastSelectedPosition)) {
                lastSelectedPosition = position
            }
            getItem(lastSelectedPosition).isSelected = false
            notifyItemChanged(lastSelectedPosition)
            lastSelectedPosition = position
            getItem(lastSelectedPosition).isSelected = true
            notifyItemChanged(lastSelectedPosition)
        }
    }

    fun setAccentColor(color: Int) {
        accentColor = color
    }

    fun setLimitCount(limitCount: Int) {
        limitSelectionCount = limitCount
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    companion object {
        private var lastSelectedPosition = -1

        private val COMPARATOR = object : DiffUtil.ItemCallback<Media>() {
            override fun areItemsTheSame(
                oldItem: Media,
                newItem: Media
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Media,
                newItem: Media
            ) = oldItem == newItem
        }
    }

}