package com.example.metronomo

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

class Sonidos {

    private val frecuenciaMuestreo = 44100
    private var pistas: Map<String, AudioTrack> = emptyMap()

    fun crear() {
        if (pistas.isNotEmpty()) return
        pistas = mapOf(
            "clic" to crearPista(1000.0, 55, cuadrada = true),
            "clicAcento" to crearPista(1500.0, 55, cuadrada = true),
            "tono" to crearPista(880.0, 130, cuadrada = false),
            "tonoAcento" to crearPista(1320.0, 130, cuadrada = false)
        )
    }

    private fun crearPista(frecuencia: Double, duracionMs: Int, cuadrada: Boolean): AudioTrack {
        val muestras = frecuenciaMuestreo * duracionMs / 1000
        val datos = ShortArray(muestras) { i ->
            val t = i.toDouble() / frecuenciaMuestreo
            val onda = sin(2 * PI * frecuencia * t)
            val base = if (cuadrada) (if (onda >= 0) 0.6 else -0.6) else onda
            val envolvente = 1.0 - i.toDouble() / muestras
            (base * envolvente * 32767.0 * 0.8).toInt().toShort()
        }
        val pista = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(frecuenciaMuestreo)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(datos.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        pista.write(datos, 0, datos.size)
        return pista
    }

    fun sonar(tono: Boolean, acento: Boolean, volumen: Int) {
        val clave = (if (tono) "tono" else "clic") + (if (acento) "Acento" else "")
        val pista = pistas[clave] ?: return
        pista.setVolume(volumen / 100f)
        pista.stop()
        pista.reloadStaticData()
        pista.play()
    }

    fun liberar() {
        pistas.values.forEach { it.release() }
        pistas = emptyMap()
    }
}