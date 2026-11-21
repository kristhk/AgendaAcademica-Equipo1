package com.equipo1.agendaacademica

import android.app.Application
import android.content.Context
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.data.local.AgendaDatabase

/** Clase Application: crea el contenedor de dependencias una sola vez por proceso. */
class AgendaApp : Application() {
    lateinit var contenedor: ContenedorApp
        private set

    override fun onCreate() {
        super.onCreate()
        contenedor = ContenedorApp(this)
    }
}

/**
 * Inyección de dependencias manual: un solo lugar donde se construyen la base de datos
 * y el repositorio. Se crean de forma diferida (lazy) la primera vez que se usan.
 */
class ContenedorApp(context: Context) {
    val baseDatos: AgendaDatabase by lazy { AgendaDatabase.crear(context) }
    val repositorio: RepositorioAcademico by lazy { RepositorioAcademico(baseDatos) }
}
