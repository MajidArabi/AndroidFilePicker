package ir.one_developer.file_picker

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.*

/**
 * Get storage images path
 *
 * @return list of image path, ex: /storage/0/emulated/download/image.jpg
 */
fun Activity.getStorageFiles(
    fileType: FileType = FileType.IMAGE
): List<File> {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            4096
        )
        return arrayListOf()
    }

    val media = when (fileType) {
        is FileType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        is FileType.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
    val cursor = contentResolver.query(
        media,
        columns,
        null,
        null,
        null
    )
    //Total number of images
    val count = cursor?.count ?: return arrayListOf()

    //Create an array to store path to all the images
    val files = arrayListOf<File>()

    for (i in 0 until count) {
        cursor.moveToPosition(i)
        val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        //Store the path of the image
        files.add(File(cursor.getString(dataColumnIndex)))
    }
    // The cursor should be freed up after use with close()
    cursor.close()
    return files
}

fun List<*>.isValidPosition(position: Int): Boolean {
    return if (isNotEmpty()) position in 0 until size else position >= 0
}

/**
 * check url is video
 * ex : https://example.com/video.mp4
 * @return boolean
 */
fun String?.isVideo(): Boolean {

    if (this == null) return false

    val formats = listOf(
        ".mp4",
        ".m4b",
        ".m4v",
        ".m4a",
        ".f4a",
        ".f4b",
        ".mov",
        ".3gp",
        ".3gp2",
        ".3g2",
        ".3gpp",
        ".3gpp2",
        ".wmv",
        ".wma",
        ".FLV",
        ".AVI"
    )

    val searched = formats.find {
        this.endsWith(it, ignoreCase = true)
    }

    return !searched.isNullOrBlank()
}

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024
val File.sizeInTb get() = sizeInGb / 1024

fun File.size(): String = when {
    sizeInGb > 1024 -> "${sizeInTb.roundTo()} TB"
    sizeInMb > 1024 -> "${sizeInGb.roundTo()} GB"
    sizeInKb > 1024 -> "${sizeInMb.roundTo()} MB"
    size > 1024     -> "${sizeInKb.roundTo()} KB"
    else            -> "${size.roundTo()} Bytes"
}

fun Number.roundTo(
    numFractionDigits: Int = 2
) = "%.${numFractionDigits}f".format(this, Locale.ENGLISH)