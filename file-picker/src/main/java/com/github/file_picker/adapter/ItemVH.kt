package com.github.file_picker.adapter

import android.graphics.drawable.Drawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.file_picker.FileType
import com.github.file_picker.extension.getMusicCoverArt
import com.github.file_picker.extension.pathName
import com.github.file_picker.extension.size
import com.github.file_picker.model.Media
import ir.one_developer.file_picker.R
import ir.one_developer.file_picker.databinding.ItemLayoutBinding

class ItemVH(
    private val listener: ((Int) -> Unit)?,
    private val binding: ItemLayoutBinding,
    private val accentColor: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.apply {
            frameChecked.setBackgroundColor(accentColor)
            card.setOnClickListener {
                listener?.invoke(bindingAdapterPosition)
            }
        }
    }

    fun bind(item: Media) = binding.apply {
        cardErrorState.isVisible = false
        cardOrder.isVisible = item.isSelected
        ivChecked.isVisible = item.isSelected && item.order >= 1
        frameChecked.isVisible = item.isSelected

        tvOrder.text = "${item.order}"
        tvFileSize.text = item.file.size()

        val previewImage = when (item.type) {
            FileType.AUDIO -> {
                tvPath.text = item.file.name
                ivMediaIcon.setImageResource(R.drawable.ic_audiotrack)
                item.file.getMusicCoverArt()
            }
            FileType.IMAGE -> {
                tvPath.text = item.file.pathName()
                ivMediaIcon.setImageResource(R.drawable.ic_image)
                item.file
            }
            FileType.VIDEO -> {
                tvPath.text = item.file.pathName()
                ivMediaIcon.setImageResource(R.drawable.ic_play)
                item.file
            }
        }

        Glide.with(ivImage)
            .load(previewImage)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    setErrorState(item.type)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivImage)
    }

    private fun setErrorState(type: FileType) = binding.apply {
        cardErrorState.isVisible = true
        when (type) {
            FileType.AUDIO -> ivErrorIcon.setImageResource(R.drawable.ic_audiotrack)
            FileType.IMAGE -> ivErrorIcon.setImageResource(R.drawable.ic_image)
            FileType.VIDEO -> ivErrorIcon.setImageResource(R.drawable.ic_play)
        }
    }

}