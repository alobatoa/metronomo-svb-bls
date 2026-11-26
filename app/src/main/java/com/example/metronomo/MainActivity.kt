package com.example.metronomo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.metronomo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MetronomoViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private val sonidos = Sonidos()
    private var confirmandoReinicio = false

    private val latido = object : Runnable {
        override fun run() {
            val acento = viewModel.latir()
            sonidos.sonar(viewModel.tonoActivo, acento, viewModel.volumen)
            actualizarEstadisticas()
            actualizarAviso()
            actualizarRegistro()
            handler.postDelayed(this, viewModel.intervaloMs)
        }
    }

    private val reloj = object : Runnable {
        override fun run() {
            actualizarTiempo()
            handler.postDelayed(this, 500)
        }
    }

    private val cancelarReinicio = Runnable {
        confirmandoReinicio = false
        binding.btnReiniciar.text = "Reiniciar todo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CICLO", "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIniciar.setOnClickListener {
            viewModel.alternar()
            actualizarBoton()
            actualizarRegistro()
            handler.removeCallbacks(latido)
            if (viewModel.corriendo) handler.postDelayed(latido, viewModel.intervaloMs)
            handler.removeCallbacks(reloj)
            if (viewModel.sesionIniciada) handler.post(reloj)
        }

        binding.btnMenos.setOnClickListener {
            viewModel.cambiarBpm(viewModel.bpm - 1)
            actualizarBpm()
        }
        binding.btnMas.setOnClickListener {
            viewModel.cambiarBpm(viewModel.bpm + 1)
            actualizarBpm()
        }
        binding.sbBpm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.cambiarBpm(100 + progress)
                    binding.tvBpm.text = viewModel.bpm.toString()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.sbVolumen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.cambiarVolumen(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) { probarSonido() }
        })
        binding.btnSonido.setOnClickListener {
            viewModel.alternarTono()
            actualizarSonido()
            probarSonido()
        }

        binding.btnVent1.setOnClickListener { ventilar(1) }
        binding.btnVent2.setOnClickListener { ventilar(2) }
        binding.btnContinuar.setOnClickListener {
            viewModel.cerrarAlerta()
            actualizarAviso()
        }

        binding.btnDescarga.setOnClickListener {
            viewModel.registrarDescarga()
            actualizarEstadisticas()
            actualizarRegistro()
        }
        binding.btnEmergencias.setOnClickListener {
            viewModel.activarEmergencias()
            actualizarEstadisticas()
            actualizarRegistro()
        }

        binding.btnReiniciar.setOnClickListener {
            if (!confirmandoReinicio) {
                confirmandoReinicio = true
                binding.btnReiniciar.text = "¿Confirmar reinicio?"
                handler.postDelayed(cancelarReinicio, 3000)
            } else {
                handler.removeCallbacks(cancelarReinicio)
                cancelarReinicio.run()
                handler.removeCallbacks(latido)
                handler.removeCallbacks(reloj)
                viewModel.reiniciar()
                actualizarTodo()
            }
        }
    }

    private fun ventilar(n: Int) {
        viewModel.anadirVentilaciones(n)
        actualizarEstadisticas()
        actualizarAviso()
        actualizarRegistro()
    }

    private fun probarSonido() {
        sonidos.sonar(viewModel.tonoActivo, false, viewModel.volumen)
    }

    private fun actualizarBoton() {
        binding.btnIniciar.text = when {
            viewModel.corriendo -> "Pausar"
            viewModel.sesionIniciada -> "Reanudar RCP"
            else -> "Iniciar RCP"
        }
    }

    private fun actualizarSonido() {
        binding.btnSonido.text = if (viewModel.tonoActivo) "Sonido: Tono" else "Sonido: Clic"
        binding.sbVolumen.progress = viewModel.volumen
    }

    private fun actualizarEstadisticas() {
        binding.tvCompresiones.text = "Compresiones: ${viewModel.compresiones}"
        binding.tvCiclos.text = "Ciclos 30:2: ${viewModel.ciclos}"
        binding.tvVentilaciones.text = "Ventilaciones: ${viewModel.ventilaciones}"
        binding.tvDescargas.text = "Descargas DEA: ${viewModel.descargas}"
        if (viewModel.emergenciasActivadas) {
            binding.tvEmergencias.text = "Emergencias: Activadas · ${viewModel.horaEmergencias}"
            binding.btnEmergencias.text = "Emergencias activadas ✓"
            binding.btnEmergencias.isEnabled = false
        } else {
            binding.tvEmergencias.text = "Emergencias: No activadas"
            binding.btnEmergencias.text = "Activar emergencias"
            binding.btnEmergencias.isEnabled = true
        }
    }

    private fun actualizarAviso() {
        binding.panelAviso.visibility = if (viewModel.alertaVisible) View.VISIBLE else View.GONE
    }

    private fun actualizarBpm() {
        binding.tvBpm.text = viewModel.bpm.toString()
        binding.sbBpm.progress = viewModel.bpm - 100
    }

    private fun actualizarRegistro() {
        binding.tvRegistro.text =
            if (viewModel.registro.isEmpty()) "Los eventos aparecerán aquí al iniciar."
            else viewModel.registro.joinToString("\n")
    }

    private fun actualizarTiempo() {
        val segundos = viewModel.tiempoMs / 1000
        binding.tvTiempo.text = "Tiempo total: %02d:%02d".format(segundos / 60, segundos % 60)
    }

    private fun actualizarTodo() {
        actualizarBoton()
        actualizarSonido()
        actualizarEstadisticas()
        actualizarAviso()
        actualizarBpm()
        actualizarRegistro()
        actualizarTiempo()
    }

    override fun onStart() {
        super.onStart()
        Log.d("CICLO", "onStart")
        sonidos.crear()
    }

    override fun onResume() {
        super.onResume()
        Log.d("CICLO", "onResume")
        actualizarTodo()
        if (viewModel.corriendo) handler.postDelayed(latido, viewModel.intervaloMs)
        if (viewModel.sesionIniciada) handler.post(reloj)
    }

    override fun onPause() {
        super.onPause()
        Log.d("CICLO", "onPause")
        handler.removeCallbacks(latido)
        handler.removeCallbacks(reloj)
        handler.removeCallbacks(cancelarReinicio)
        cancelarReinicio.run()
    }

    override fun onStop() {
        super.onStop()
        Log.d("CICLO", "onStop")
        sonidos.liberar()
    }

    override fun onDestroy() { super.onDestroy(); Log.d("CICLO", "onDestroy") }
}
