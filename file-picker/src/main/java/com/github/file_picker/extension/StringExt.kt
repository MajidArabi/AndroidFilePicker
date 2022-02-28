package com.github.file_picker.extension

/**
 * check path is video
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