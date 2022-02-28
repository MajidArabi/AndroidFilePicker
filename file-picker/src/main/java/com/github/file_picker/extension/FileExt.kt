package com.github.file_picker.extension

import java.io.File

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024
val File.sizeInTb get() = sizeInGb / 1024

/**
 * Format file size
 *
 * @return string ex: 2.35 MB
 */
fun File.size(): String = when {
    sizeInGb > 1024 -> "${sizeInTb.roundTo()} TB"
    sizeInMb > 1024 -> "${sizeInGb.roundTo()} GB"
    sizeInKb > 1024 -> "${sizeInMb.roundTo()} MB"
    size > 1024     -> "${sizeInKb.roundTo()} KB"
    else            -> "${size.roundTo()} Bytes"
}