<div align="center">
  <h1>Truce - Android App</h1>
  <p><b>Gamificando el bienestar digital y el control de tiempo en pantalla mediante presión social positiva.</b></p>
</div>

## 🚀 Acerca del Proyecto

**Truce** es una aplicación móvil diseñada para ayudar a los usuarios a reducir y controlar su tiempo de pantalla. A diferencia de los controles parentales tradicionales, Truce introduce dinámicas de **gamificación** y **presión de pares** (peer pressure).

Los usuarios establecen límites de uso diario para sus aplicaciones. Si un usuario agota su tiempo permitido, su aplicación se bloquea (gracias a un servicio de Accesibilidad) y deberá **solicitar tiempo extra a sus amigos** dentro de la app. Los amigos tienen el poder de aprobar o rechazar estas peticiones de tiempo, fomentando un uso de pantalla consciente, compartido y responsable.

Este repositorio contiene la **Aplicación Móvil para Android**, responsable de la interacción con el usuario, monitoreo de métricas de uso y bloqueo de aplicaciones.

## 🛠️ Arquitectura y Stack Tecnológico

El proyecto está construido utilizando **Clean Architecture** estructurada en paquetes (Package by Feature) en conjunto con el patrón **MVVM / MVI (Unidirectional Data Flow)**, asegurando escalabilidad, testeabilidad y fácil mantenimiento siguiendo las guías más modernas de Android.

### Frontend & UI
- **Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) con Material Design 3.
- **Navegación:** Navigation Compose.

### Core & Framework
- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **Inyección de Dependencias:** [Dagger Hilt](https://dagger.dev/hilt/)
- **Operaciones Asíncronas:** Kotlin Coroutines & Flows
- **Trabajos en Segundo Plano:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) para sincronización en lote de las métricas.
- **Monitoreo & Bloqueo:** Integración con `UsageStatsManager` y `AccessibilityService` para una detección y bloqueo eficientes en tiempo real.

### Data & Networking
- **Base de Datos Local:** [Room](https://developer.android.com/training/data-storage/room) para caché offline de límites y uso.
- **Cliente HTTP:** [Retrofit2](https://square.github.io/retrofit/) + [OkHttp3](https://square.github.io/okhttp/).
- **Autenticación:** [Supabase Auth (Kotlin SDK)](https://github.com/supabase-community/supabase-kt) delegando la seguridad de JWT hacia el backend.
- **Notificaciones Push:** [Firebase Cloud Messaging (FCM)](https://firebase.google.com/docs/cloud-messaging) para alertas en tiempo real sobre solicitudes de tiempo.
- **Preferencias:** Jetpack DataStore.

## ⚙️ Estructura del Proyecto

```text
app/src/main/java/com/example/truce/
├── data/           # Repositorios, fuentes de datos locales (Room) y remotas (Retrofit)
├── domain/         # Casos de uso y modelos de negocio puros
├── ui/             # UI Components (Compose), ViewModels, Navigation y Themes
├── services/       # AccessibilityService, FirebaseMessagingService, Workers
├── di/             # Módulos de provisión de dependencias (Hilt)
└── TruceApplication.kt
```

## 🔗 Backend API

El backend correspondiente para esta aplicación está desarrollado en **NestJS + Prisma + PostgreSQL**. Puedes encontrar el código fuente e instrucciones de despliegue aquí:
👉 [Truce App Backend](https://github.com/ivanmacieldxz/truce-app-backend)

## 📝 Configuración Local

1. Clona este repositorio.
2. Abre el proyecto con **Android Studio** (Koala o superior recomendado).
3. Sincroniza las dependencias de Gradle.
4. Genera tu archivo `google-services.json` desde la consola de Firebase y colócalo en el directorio `app/`.
5. Asegúrate de configurar la URL base de tu backend local/remoto.
6. ¡Ejecuta la app en tu emulador o dispositivo físico!

---
*Desarrollado para fomentar hábitos digitales saludables.*
