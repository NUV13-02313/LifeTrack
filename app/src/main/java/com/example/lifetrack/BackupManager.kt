package com.example.lifetrack

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object BackupManager {

    // Export the database to a file chosen by the user
    fun exportDatabase(context: Context, uri: Uri) {
        val dbFile = context.getDatabasePath("lifetrack_database")
        if (!dbFile.exists()) return

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            FileInputStream(dbFile).use { inputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
    }

    // Import a database file from the user's storage
    fun importDatabase(context: Context, uri: Uri) {
        val dbFile = context.getDatabasePath("lifetrack_database")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(dbFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
    }
}