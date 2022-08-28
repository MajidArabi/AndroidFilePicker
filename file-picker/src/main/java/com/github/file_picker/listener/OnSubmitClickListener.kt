package com.github.file_picker.listener

import com.github.file_picker.data.model.Media

interface OnSubmitClickListener {
    fun onClick(files: List<Media>)
}