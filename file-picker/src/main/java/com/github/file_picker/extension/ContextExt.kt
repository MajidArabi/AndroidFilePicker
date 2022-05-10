package com.github.file_picker.extension

import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.github.file_picker.FileType
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
    fileType: FileType = FileType.IMAGE
): List<File> {

    val media = when (fileType) {
        FileType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        FileType.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        FileType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
    val cursor = applicationContext.contentResolver.query(
        media,
        columns,
        null,
        null,
        MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC"
    )
    //Total number of images
    val count = cursor?.count ?: return arrayListOf()

    //Create an array to store path to all the images
    val files = arrayListOf<File>()

    for (i in 0 until count) {
        cursor.moveToPosition(i)
        val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        //Store the path of the image
        val file = File(cursor.getString(dataColumnIndex))
        if (file.size > 0.0) files.add(file)
    }

    // The cursor should be freed up after use with close()
    cursor.close()
    return files
}
