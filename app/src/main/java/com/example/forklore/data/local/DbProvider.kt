
package com.example.forklore.data.local

import android.content.Context

object DbProvider {
    private var db: AppDatabase? = null

    fun getDb(context: Context): AppDatabase {
        return db ?: AppDatabase.getDatabase(context).also {
            db = it
        }
    }
}
