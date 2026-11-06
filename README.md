# Recordatorios

Aplicación Android de recordatorios con persistencia local (Room y DataStore), cuentas de usuario con Firebase Authentication y sincronización con Cloud Firestore.

## Etapas del proyecto

1. **Actividad 1:** proyecto con Kotlin y Jetpack Compose, modelo de datos con data classes y sealed classes, datos simulados y pantalla inicial.
2. **Actividad 2:** pantallas de listado, creación y detalle, formulario validado, ViewModel, UiState, StateFlow y Navigation Compose.
3. **Actividad 3:** preferencias con DataStore, persistencia con Room, repositorio local-first y sincronización con WorkManager.
4. **Actividad 4:** Firebase Authentication, Cloud Firestore por usuario, reglas de seguridad y versión release firmada.

## Firebase

1. Crear un proyecto en Firebase y agregar una app Android con el paquete `com.example.recordatorios`.
2. Copiar `google-services.json` en la carpeta `app/`.
3. Habilitar **Authentication → Correo electrónico/contraseña**.
4. Crear la base **Cloud Firestore** y publicar las reglas de `firestore.rules`.

Los datos se guardan en `users/{uid}/reminders/{reminderId}`. Las reglas solo permiten a cada usuario leer y escribir sus propios recordatorios.

## Ejecución en desarrollo

Ejecutar la configuración `app` (variante `debug`) en un emulador desde Android Studio, o:

```
gradlew installDebug
```

## Versión release

1. Crear un almacén de claves fuera del repositorio.
2. Copiar `keystore.properties.example` como `keystore.properties` y completar la ruta y las contraseñas. Este archivo no se sube al repositorio.
3. Generar los artefactos:

```
gradlew assembleRelease bundleRelease
```

- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

Versión actual: `versionCode 4`, `versionName 1.0.0`. La variante debug agrega el sufijo `-debug`.
