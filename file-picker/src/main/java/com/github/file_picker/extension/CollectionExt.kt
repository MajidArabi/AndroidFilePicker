package com.github.file_picker.extension

/**
 * Is valid position in list
 *
 * @param position
 * @return
 */
fun List<*>.isValidPosition(position: Int): Boolean {
    return if (isNotEmpty()) position in 0 until size else position >= 0
}
