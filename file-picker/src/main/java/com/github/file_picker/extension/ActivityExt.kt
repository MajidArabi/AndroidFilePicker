package com.github.file_picker.extension

import android.app.Activity
import android.provider.MediaStore
import com.github.file_picker.FileType
import java.io.File


/**
 * Get storage files path
 *
 * @return list of file path, ex: /storage/0/emulated/download/image.jpg
 */
fun Activity.getStorageFiles(
    fileType: FileType = FileType.IMAGE
): List<File> {
//    if (ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ),
//            4096
//        )
//        return arrayListOf()
//    }

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