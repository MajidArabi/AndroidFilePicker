package com.github.file_picker.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


/**
 * check has runtime permission
 *
 * @param permission permission name ex: Manifest.permission.READ_EXTERNAL_STORAGE
 * @return if has permission return true otherwise false
 */
fun Context.hasPermission(
    permission: String
): Boolean = ActivityCompat.checkSelfPermission(
    this,
    permission
) == PackageManager.PERMISSION_GRANTED
