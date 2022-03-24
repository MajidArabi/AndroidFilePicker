package com.github.file_picker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ListDirection : Parcelable {
    LTR,
    RTL
}

@Parcelize
enum class FileType : Parcelable {
    VIDEO,
    IMAGE,
    AUDIO,
}