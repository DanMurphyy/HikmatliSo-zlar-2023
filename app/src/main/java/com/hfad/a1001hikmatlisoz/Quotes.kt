package com.hfad.a1001hikmatlisoz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote_table")
data class Quotes(
    @PrimaryKey(autoGenerate = true)
    val author: String,
    val quote: String,
)
