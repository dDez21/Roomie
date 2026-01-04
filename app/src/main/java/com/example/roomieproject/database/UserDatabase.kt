package com.example.roomieproject.database

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

fun localAvatarUri(context: Context, uid: String): Uri? {
    val f = File(context.filesDir, "avatar_$uid.jpg")
    return if (f.exists()) f.toUri() else null
}

fun modifyAvatar(context: Context, uid: String, sourceUri: Uri) {
    val dest = File(context.filesDir, "avatar_$uid.jpg")
    context.contentResolver.openInputStream(sourceUri).use { input ->
        requireNotNull(input)
        FileOutputStream(dest).use { out -> input.copyTo(out) }
    }
}