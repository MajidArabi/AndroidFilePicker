package com.github.file_picker.extension

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.github.file_picker.FileType
import com.github.file_picker.PAGE_SIZE
import com.github.file_picker.data.model.Media
import java.io.File

/**
 * check has runtime permission
 *
 * @param permission permission name ex: Manifest.permission.READ_EXTERNAL_STORAGE
 * @return if has permission return true otherwise false
 */
internal fun Context.hasPermission(
    permission: String
): Boolean = ActivityCompat.checkSelfPermission(
    this,
    permission
) == PackageManager.PERMISSION_GRANTED

/**
 * Get storage files path
 *
 * @return list of file path, ex: /storage/0/emulated/download/image.jpg
 */
internal fun Context.getStorageFiles(
    fileType: FileType = FileType.IMAGE,
    limit: Int = PAGE_SIZE,
    offset: Int = 0
): List<Media> {

    val resolver = applicationContext.contentResolver

    val media = when (fileType) {
        FileType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        FileType.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        FileType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns._ID)
    val modified = MediaStore.Files.FileColumns.DATE_MODIFIED

    val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val sortArgs = arrayOf(modified)
        val bundle = Bundle().apply {
            putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
            putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, sortArgs)
            putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
        }
        resolver.query(
            media,
            projection,
            bundle,
            null
        )
    } else resolver.query(
        media,
        projection,
        null,
        null,
        "$modified DESC LIMIT $limit OFFSET $offset",
    )

    //Total number of images
    val count = cursor?.count ?: return emptyList()

    //Create an array to store path to all the images
    val files = arrayListOf<Media>()

    for (i in 0 until count) {
        cursor.moveToPosition(i)
        val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        //Store the path of the image
        val file = File(cursor.getString(dataColumnIndex))
        if (file.size > 0.0) {
            files.add(Media(file = file, type = fileType))
        }
    }

    // The cursor should be freed up after use with close()
    cursor.close()
    return files
}
