package com.equipo1.agendaacademica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.equipo1.agendaacademica.ui.navegacion.AgendaNavHost
import com.equipo1.agendaacademica.ui.theme.AgendaAcademicaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgendaAcademicaTheme {
                AgendaNavHost()
            }
        }
    }
}
