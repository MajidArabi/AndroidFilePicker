package com.github.file_picker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.file_picker.FilePicker
import com.github.file_picker.extension.isValidPosition
import com.github.file_picker.model.Media
import ir.one_developer.file_picker.databinding.ItemLayoutBinding

class ItemAdapter(
    private var accentColor: Int = FilePicker.DEFAULT_ACCENT_COLOR,
    private var limitSelectionCount: Int = FilePicker.DEFAULT_LIMIT_COUNT,
    private var listener: ((Int) -> Unit)? = null
) : ListAdapter<Media, ItemVH>(COMPARATOR) {

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

    /**
     * Set selected
     *
     * @param position the selected item position
     */
    fun setSelected(position: Int) {
        if (limitSelectionCount > 1) {
            val item = getItem(position)
            val selectedItems = currentList.filter { it.isSelected }
            val selectedItemCount = selectedItems.size

            if (item.isSelected) {
                item.isSelected = false
                selectedItems.forEach {
                    if (it.order > 1) it.order -= 1
                    notifyItemChanged(currentList.indexOf(it))
                }
                notifyItemChanged(position)
                return
            }

            if (selectedItemCount < limitSelectionCount) {
                item.isSelected = true
                item.order = selectedItemCount + 1
                notifyItemChanged(position)
            }
            return
        }

        if (!currentList.isValidPosition(lastSelectedPosition)) {
            lastSelectedPosition = position
        }
        getItem(lastSelectedPosition).isSelected = false
        notifyItemChanged(lastSelectedPosition)
        lastSelectedPosition = position
        getItem(lastSelectedPosition).isSelected = true
        notifyItemChanged(lastSelectedPosition)
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