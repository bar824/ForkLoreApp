
package com.example.forklore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.forklore.data.model.ExternalRecipe
import com.example.forklore.data.model.Post
import com.example.forklore.data.model.Recipe

@Database(entities = [Post::class, ExternalRecipe::class, Recipe::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postsDao(): PostsDao
    abstract fun externalRecipesDao(): ExternalRecipesDao
    abstract fun myRecipesDao(): MyRecipesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forklore_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
