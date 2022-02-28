package com.github.file_picker.extension

import androidx.fragment.app.Fragment
import com.github.file_picker.FileType
import java.io.File

/**
 * check has runtime permission
 *
 * @param permission permission name ex: Manifest.permission.READ_EXTERNAL_STORAGE
 * @return if has permission return true otherwise false
 */
fun Fragment.hasPermission(
    permission: String
): Boolean = requireContext().hasPermission(permission)


/**
 * Get storage files path
 *
 * @return list of file path, ex: /storage/0/emulated/download/image.jpg
 */
fun Fragment.getStorageFiles(
    fileType: FileType = FileType.IMAGE
): List<File> = requireActivity().getStorageFiles(fileType = fileType)