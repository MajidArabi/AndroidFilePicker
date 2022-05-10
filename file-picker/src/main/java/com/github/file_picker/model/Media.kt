package com.github.file_picker.model

import android.os.Parcelable
import com.github.file_picker.FileType
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class Media(
    val file: File,
    val type: FileType,
    var order: Int = 0,
    var isSelected: Boolean = false,
    val id: Int = file.path.hashCode()
) : Parcelable
