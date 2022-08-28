package com.github.file_picker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.file_picker.FilePicker
import com.github.file_picker.extension.isValidPosition
import com.github.file_picker.data.model.Media
import ir.one_developer.file_picker.databinding.ItemLayoutBinding
import java.util.Locale.filter

internal class ItemAdapter(
    private var accentColor: Int = FilePicker.DEFAULT_ACCENT_COLOR,
    private var overlayAlpha: Float = FilePicker.DEFAULT_OVERLAY_ALPHA,
    private var limitSelectionCount: Int = FilePicker.DEFAULT_LIMIT_COUNT,
    private var listener: ((Int) -> Unit)? = null
) : PagingDataAdapter<Media, ItemVH>(COMPARATOR), FilePickerAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemVH(
        listener = listener,
        accentColor = accentColor,
        overlayAlpha =overlayAlpha,
        limitSelectionCount = limitSelectionCount,
        binding = ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    /**
     * Set selected
     *
     * @param position the selected item position
     */
    override fun setSelected(position: Int) {
        if (limitSelectionCount > 1) {
            val item = getItem(position) ?: return
            val selectedItems = snapshot().items.filter { it.isSelected && it.id != item.id }
            val selectedItemCount = selectedItems.size

            if (item.isSelected) {
                item.isSelected = false
                notifyItemChanged(position)
                selectedItems.forEach { media ->
                    if (media.order > item.order) {
                        media.order--
                        notifyItemChanged(snapshot().items.indexOf(media))
                    }
                }
                return
            }

            if (selectedItemCount < limitSelectionCount) {
                item.isSelected = true
                item.order = selectedItemCount + 1
                notifyItemChanged(position)
            }
            return
        }

        if (!snapshot().items.isValidPosition(lastSelectedPosition)) {
            lastSelectedPosition = position
        }
        getItem(lastSelectedPosition)?.isSelected = false
        notifyItemChanged(lastSelectedPosition)
        lastSelectedPosition = position
        getItem(lastSelectedPosition)?.isSelected = true
        notifyItemChanged(lastSelectedPosition)
    }

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