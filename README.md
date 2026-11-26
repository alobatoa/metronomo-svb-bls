# Metrónomo SVB/BLS para Android

Aplicación Android nativa, escrita en **Kotlin**, que ayuda a mantener el ritmo de las compresiones durante una reanimación cardiopulmonar (RCP) y a llevar el registro de la sesión.

Nace de mi experiencia como instructor de Soporte Vital Básico (SVB/BLS) y es, a la vez, un proyecto de aprendizaje de desarrollo Android: ciclo de vida de una `Activity`, `ViewModel`, `ViewBinding` y generación de audio en tiempo real.

> Proyecto educativo y de demostración técnica. No es un producto sanitario ni sustituye a la formación en RCP.

## Qué hace

- **Marca el ritmo** con un latido sonoro ajustable entre 100 y 120 compresiones por minuto, con un sonido acentuado en la compresión número 30 de cada serie.
- **Ciclos 30:2:** al completar 30 compresiones muestra un aviso para realizar las ventilaciones, que se registran con un botón (1 o 2).
- **Contadores en tiempo real:** compresiones, ciclos completados, ventilaciones y descargas de DEA.
- **Cronómetro** del tiempo total de la sesión.
- **Registro de eventos** con lo ocurrido durante la sesión (los últimos 30), y botón para marcar la activación de emergencias con su hora.
- **Sonido configurable:** tono o clic, con control de volumen y prueba al ajustarlo.
- **Reinicio con confirmación** en dos pasos para evitar borrados accidentales.
- Tema oscuro de alto contraste, pensado para verse bien de un vistazo.

## Tecnologías y conceptos aplicados

| Área | Detalle |
| --- | --- |
| Lenguaje | Kotlin |
| Interfaz | XML con **ViewBinding** |
| Arquitectura | `ViewModel` para conservar el estado al rotar la pantalla |
| Ciclo de vida | Recursos de audio creados en `onStart` y liberados en `onStop`; temporizadores detenidos en `onPause` |
| Temporización | `Handler` con `Looper` principal |
| Audio | `AudioTrack` en modo estático: los sonidos (tono y clic) se **sintetizan por código**, sin archivos de audio |
| Requisitos | Android 8.0 (API 26) o superior |

## Cómo ejecutarla

1. Clona el repositorio.
2. Ábrelo con **Android Studio** (versión reciente).
3. Espera a que Gradle sincronice el proyecto.
4. Ejecuta la app en un emulador o en un móvil con Android 8.0 o superior.

## Estructura

```
app/src/main/
├── java/com/example/metronomo/
│   ├── MainActivity.kt        # Interfaz, eventos y ciclo de vida
│   ├── MetronomoViewModel.kt  # Estado y lógica de la sesión
│   └── Sonidos.kt             # Síntesis y reproducción de audio
└── res/                       # Layout, colores, temas y estilos
```

## Qué aprendí

- Cómo se comporta una `Activity` al rotar, pausarse o cerrarse, observándolo con los registros del ciclo de vida.
- Separar el estado de la pantalla con `ViewModel`.
- Sintetizar audio con `AudioTrack` y gestionar bien sus recursos.
- Mantener un temporizador estable sin bloquear el hilo principal.

## Autor

**Antonio Lobato Alconada** · Técnico Superior en Desarrollo de Aplicaciones Multiplataforma (DAM)
[GitHub](https://github.com/alobatoa) · [LinkedIn](https://www.linkedin.com/in/antoniolobato-alconada-46468056)
