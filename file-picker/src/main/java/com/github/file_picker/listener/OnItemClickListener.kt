package com.github.file_picker.listener

import com.github.file_picker.adapter.ItemAdapter
import com.github.file_picker.model.Media

interface OnItemClickListener {
    fun onClick(media: Media, position: Int, adapter: ItemAdapter)
}