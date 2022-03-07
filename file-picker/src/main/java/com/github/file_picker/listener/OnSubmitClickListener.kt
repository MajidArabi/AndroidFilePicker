package com.github.file_picker.listener

import com.github.file_picker.model.Media

interface OnSubmitClickListener {
    fun onClick(files: List<Media>)
}