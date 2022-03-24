package com.github.file_picker.extension

import androidx.appcompat.app.AppCompatActivity
import com.github.file_picker.FilePicker
import com.github.file_picker.FileType
import com.github.file_picker.ListDirection
import com.github.file_picker.listener.OnItemClickListener
import com.github.file_picker.listener.OnSubmitClickListener
import com.github.file_picker.model.Media

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
fun AppCompatActivity.showFilePicker(
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
) = FilePicker.Builder(this)
    .setTitle(title)
    .setTitleTextColor(titleTextColor)
    .setSubmitText(submitText)
    .setSubmitTextColor(submitTextColor)
    .setAccentColor(accentColor)
    .setFileType(fileType)
    .setListDirection(listDirection)
    .setCancellable(cancellable)
    .setGridSpanCount(gridSpanCount)
    .setLimitItemSelection(limitItemSelection)
    .setSelectedFiles(selectedFiles)
    .setOnSubmitClickListener(onSubmitClickListener)
    .setOnItemClickListener(onItemClickListener)
    .buildAndShow()