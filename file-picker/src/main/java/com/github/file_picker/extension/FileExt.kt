package com.github.file_picker.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.File

internal val File.size get() = if (!exists()) 0.0 else length().toDouble()
internal val File.sizeInKb get() = size / 1024
internal val File.sizeInMb get() = sizeInKb / 1024
internal val File.sizeInGb get() = sizeInMb / 1024
internal val File.sizeInTb get() = sizeInGb / 1024

/**
 * Format file size
 *
 * @return string ex: 2.35 MB
 */
internal fun File.size(): String = when {
    sizeInGb > 1024 -> "${sizeInTb.roundTo()} TB"
    sizeInMb > 1024 -> "${sizeInGb.roundTo()} GB"
    sizeInKb > 1024 -> "${sizeInMb.roundTo()} MB"
    size > 1024     -> "${sizeInKb.roundTo()} KB"
    else            -> "${size.roundTo()} Bytes"
}

/**
 * Path name
 *
 * @return
 */
internal fun File.pathName(): CharSequence {
    val paths = path.split("/")
    paths.forEachIndexed { index, title ->
        if (index == paths.lastIndex - 1) {
            return title
        }
    }
    return ""
}

/**
 * Is video
 */
internal val File.isVideo
    get() = this.name.isVideo()

/**
 * Get music cover art
 *
 * @return
 */
internal fun File.getMusicCoverArt(): Bitmap? {
    val mData = MediaMetadataRetriever()
    mData.setDataSource(path)
    return try {
        val art = mData.embeddedPicture
        BitmapFactory.decodeByteArray(art, 0, art?.size ?: 0)
    } catch (e: Exception) {
        null
    }
}