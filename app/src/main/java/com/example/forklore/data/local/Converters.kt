package com.example.forklore.data.local

import androidx.room.TypeConverter
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list.orEmpty().joinToString(",")
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String {
        return JSONObject(map.orEmpty()).toString()
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, String> {
        if (value.isNullOrBlank()) return emptyMap()
        val jsonObject = JSONObject(value)
        return jsonObject.keys().asSequence().associateWith { key -> jsonObject.optString(key, "") }
    }
}
