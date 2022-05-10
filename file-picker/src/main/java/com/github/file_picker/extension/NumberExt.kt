package com.github.file_picker.extension

import java.util.*

/**
 * Round to
 *
 * @param numFractionDigits
 */
internal fun Number.roundTo(
    numFractionDigits: Int = 2
) = "%.${numFractionDigits}f".format(this, Locale.ENGLISH)