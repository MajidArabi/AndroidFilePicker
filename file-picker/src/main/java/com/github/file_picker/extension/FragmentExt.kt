package com.github.file_picker.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.file_picker.FilePicker
import com.github.file_picker.FileType
import com.github.file_picker.ListDirection
import com.github.file_picker.listener.OnItemClickListener
import com.github.file_picker.listener.OnSubmitClickListener
import com.github.file_picker.model.Media
import com.github.file_picker.showFilePicker
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
): List<File> = requireContext().getStorageFiles(fileType = fileType)

/**
 * Show file picker
 *
 * @param title
 * @param titleTextColor
 * @param submitText
 * @param submitTextColor
 * @param accentColor
 * @param fileType
 * @param listDirection
 * @param cancellable
 * @param gridSpanCount
 * @param limitItemSelection
 * @param selectedFiles
 * @param onSubmitClickListener
 * @param onItemClickListener
 */
fun Fragment.showFilePicker(
    title: String = FilePicker.DEFAULT_TITLE,
    titleTextColor: Int = FilePicker.DEFAULT_TITLE_TEXT_COLOR,
    submitText: String = FilePicker.DEFAULT_SUBMIT_TEXT,
    submitTextColor: Int = FilePicker.DEFAULT_SUBMIT_TEXT_COLOR,
    accentColor: Int = FilePicker.DEFAULT_ACCENT_COLOR,
    fileType: FileType = FilePicker.DEFAULT_FILE_TYPE,
    listDirection: ListDirection = FilePicker.DEFAULT_LIST_DIRECTION,
    cancellable: Boolean = FilePicker.DEFAULT_CANCELABLE,
    gridSpanCount: Int = FilePicker.DEFAULT_SPAN_COUNT,
    limitItemSelection: Int = FilePicker.DEFAULT_LIMIT_COUNT,
    selectedFiles: ArrayList<Media> = arrayListOf(),
    onSubmitClickListener: OnSubmitClickListener? = null,
    onItemClickListener: OnItemClickListener? = null,
) {
    if (requireActivity() !is AppCompatActivity) {
        throw IllegalAccessException("Fragment host must be extend AppCompatActivity")
    }
    (requireActivity() as AppCompatActivity).showFilePicker(
        title = title,
        titleTextColor = titleTextColor,
        submitText = submitText,
        submitTextColor = submitTextColor,
        accentColor = accentColor,
        fileType = fileType,
        listDirection = listDirection,
        cancellable = cancellable,
        gridSpanCount = gridSpanCount,
        limitItemSelection = limitItemSelection,
        selectedFiles = selectedFiles,
        onSubmitClickListener = onSubmitClickListener,
        onItemClickListener = onItemClickListener,
    )
}