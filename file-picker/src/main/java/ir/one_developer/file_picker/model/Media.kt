package ir.one_developer.file_picker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class Media(
    val file: File,
    var isSelected: Boolean = false,
    val id: Int = file.path.hashCode()
) : Parcelable
