package com.github.file_picker.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.file_picker.extension.isVideo
import com.github.file_picker.extension.pathName
import com.github.file_picker.extension.size
import com.github.file_picker.model.Media
import ir.one_developer.file_picker.databinding.ItemLayoutBinding

class ItemVH(
    private val listener: ((Int) -> Unit)?,
    private val binding: ItemLayoutBinding,
    private val accentColor: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.apply {
            card.setOnClickListener {
                listener?.invoke(bindingAdapterPosition)
            }
            frameChecked.setBackgroundColor(accentColor)
        }
    }

    fun bind(item: Media) = binding.apply {
        Glide.with(ivImage)
            .load(item.file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivImage)

        ivImage.alpha = .75f
        tvPath.text = item.file.pathName()
        tvFileSize.text = item.file.size()

        ivChecked.isVisible = item.isSelected
        frameChecked.isVisible = item.isSelected
        playIconContainer.isVisible = item.file.name.isVideo()
    }

}