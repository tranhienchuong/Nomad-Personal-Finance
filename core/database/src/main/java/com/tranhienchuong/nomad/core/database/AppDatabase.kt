package com.tranhienchuong.nomad.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Empty Room database shell. Room's compiler requires at least one entity, so the concrete schema
 * annotation and build call are intentionally deferred until a real entity is introduced.
 */
abstract class AppDatabase : RoomDatabase()

fun appDatabaseBuilder(context: Context) =
    Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = "nomad.db",
    )
