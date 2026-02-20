package com.example.forklore.data

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.example.forklore.data.local.PostsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageCacheManager(private val context: Context, private val postsDao: PostsDao) {

    suspend fun cacheImage(postId: String, imageUrl: String) {
        withContext(Dispatchers.IO) {
            try {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                val file = File(context.cacheDir, "$postId.jpg")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                outputStream.flush()
                outputStream.close()

                postsDao.updateLocalImagePath(postId, file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
