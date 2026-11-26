package com.example.metronomo

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MetronomoViewModel : ViewModel() {

    var corriendo = false
        private set
    var sesionIniciada = false
        private set
    var compresiones = 0
        private set
    var ciclos = 0
        private set
    var ventilaciones = 0
        private set
    var descargas = 0
        private set
    var emergenciasActivadas = false
        private set
    var horaEmergencias = ""
        private set
    var alertaVisible = false
        private set
    var bpm = 110
        private set
    var volumen = 80
        private set
    var tonoActivo = false
        private set

    private var enCiclo = 0
    private var inicioMs = 0L
    private val _registro = mutableListOf<String>()
    val registro: List<String> get() = _registro

    val intervaloMs: Long
        get() = 60000L / bpm

    val tiempoMs: Long
        get() = if (sesionIniciada) SystemClock.elapsedRealtime() - inicioMs else 0L

    init {
        Log.d("CICLO", "ViewModel creado")
    }

    private fun hora(): String =
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    private fun anotar(texto: String) {
        _registro.add(0, "${hora()}  $texto")
        if (_registro.size > 30) _registro.removeAt(_registro.size - 1)
    }

    fun alternar() {
        if (!corriendo) {
            if (!sesionIniciada) {
                sesionIniciada = true
                inicioMs = SystemClock.elapsedRealtime()
                anotar("Compresiones iniciadas a $bpm/min")
            } else {
                anotar("Compresiones reanudadas")
            }
        } else {
            anotar("Compresiones pausadas")
        }
        corriendo = !corriendo
    }

    fun latir(): Boolean {
        compresiones++
        enCiclo++
        if (enCiclo >= 30) {
            enCiclo = 0
            ciclos++
            alertaVisible = true
            anotar("Ciclo $ciclos completado (30 compresiones)")
            return true
        }
        return false
    }

    fun cambiarBpm(nuevo: Int) {
        bpm = nuevo.coerceIn(100, 120)
    }

    fun cambiarVolumen(nuevo: Int) {
        volumen = nuevo.coerceIn(0, 100)
    }

    fun alternarTono() {
        tonoActivo = !tonoActivo
    }

    fun anadirVentilaciones(n: Int) {
        ventilaciones += n
        alertaVisible = false
        anotar(if (n == 1) "1 ventilación registrada" else "$n ventilaciones registradas")
    }

    fun cerrarAlerta() {
        alertaVisible = false
    }

    fun registrarDescarga() {
        descargas++
        anotar("Descarga DEA nº $descargas")
    }

    fun activarEmergencias() {
        if (emergenciasActivadas) return
        emergenciasActivadas = true
        horaEmergencias = hora()
        anotar("Servicios de emergencia activados a las $horaEmergencias")
    }

    fun reiniciar() {
        corriendo = false
        sesionIniciada = false
        compresiones = 0
        ciclos = 0
        ventilaciones = 0
        descargas = 0
        emergenciasActivadas = false
        horaEmergencias = ""
        alertaVisible = false
        enCiclo = 0
        inicioMs = 0L
        _registro.clear()
        anotar("Sesión reiniciada")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CICLO", "ViewModel destruido")
    }
}