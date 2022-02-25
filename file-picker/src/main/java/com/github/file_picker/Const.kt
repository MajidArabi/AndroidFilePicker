package com.github.file_picker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class FileType : Parcelable {
    @Parcelize
    object VIDEO : FileType()

    @Parcelize
    object IMAGE : FileType()
}

sealed class ListDirection : Parcelable {
    @Parcelize
    object LTR : ListDirection()

    @Parcelize
    object RTL : ListDirection()
}