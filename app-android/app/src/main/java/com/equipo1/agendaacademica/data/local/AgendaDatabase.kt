package com.equipo1.agendaacademica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/** Base de datos SQLite local de la aplicación, administrada por Room. */
@Database(
    entities = [MateriaEntity::class, TareaEntity::class, HistorialEstadoEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Convertidores::class)
abstract class AgendaDatabase : RoomDatabase() {
    abstract fun tareaDao(): TareaDao
    abstract fun materiaDao(): MateriaDao

    companion object {
        const val NOMBRE_ARCHIVO = "agenda_academica.db"

        fun crear(context: Context): AgendaDatabase =
            Room.databaseBuilder(context.applicationContext, AgendaDatabase::class.java, NOMBRE_ARCHIVO)
                .build()
    }
}
