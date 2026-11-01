# Recordatorios

Aplicación Android de recordatorios con persistencia local (Room y DataStore), repositorio local-first y sincronización con un servicio simulado.

## Etapas del proyecto

1. **Actividad 1:** proyecto con Kotlin y Jetpack Compose, modelo de datos con data classes y sealed classes, datos simulados y pantalla inicial.
2. **Actividad 2:** pantallas de listado, creación y detalle, formulario validado, ViewModel, UiState, StateFlow y Navigation Compose.
3. **Actividad 3:** preferencias con DataStore, persistencia con Room, repositorio local-first, servicio remoto con Retrofit y OkHttp, y sincronización con WorkManager.

## Configuración

1. Abrir el proyecto en Android Studio.
2. Copiar `local.properties.example` como `local.properties` y completar `sdk.dir`.
3. `reminders.baseUrl` indica la URL del servicio. Si no se define, se usa la URL de `local.properties.example`. No se necesitan credenciales.

## Servicio remoto

La aplicación usa un recurso `Reminders` en MockAPI con los campos `localId`, `title`, `description`, `categoryId`, `priority`, `state`, `finishDate`, `date` y `updatedAt`.

## Ejecución

Ejecutar la configuración `app` en un emulador desde Android Studio, o:

```
gradlew installDebug
```

Los cambios sin conexión quedan pendientes. WorkManager los envía cuando vuelve la conexión; también se puede pulsar "Sincronizar".
