# Snake Game with Firebase Authentication

Este proyecto es un juego clásico de Snake desarrollado para Android utilizando Kotlin y Jetpack Compose. El juego permite a los usuarios jugar mientras están autenticados a través de Firebase Authentication. El inicio de sesión se puede realizar con una cuenta de correo electrónico y contraseña o con Google Sign-In.

## Características

* **Autenticación con Firebase:** Los usuarios pueden iniciar sesión utilizando una cuenta de correo electrónico y contraseña o a través de Google Sign-In.
* **Juego Snake:** El clásico juego de Snake donde los jugadores deben comer comida para crecer y evitar chocar con su propio cuerpo.
* **Interfaz de usuario sencilla:** La interfaz se ha diseñado utilizando Jetpack Compose para ofrecer una experiencia de usuario moderna y fluida.
* **Integración con Firebase Analytics:** Se han agregado eventos de Firebase Analytics para rastrear la actividad del usuario en la aplicación.

## Requisitos

* Android Studio con soporte para Kotlin.
* **Firebase:** Se requiere una cuenta de Firebase para configurar la autenticación y Firebase Analytics.
* **Google Services:** Debes descargar el archivo `google-services.json` desde la consola de Firebase y agregarlo a tu proyecto.

## Instrucciones de instalación

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/snake-game-firebase.git](https://github.com/tu-usuario/snake-game-firebase.git)
    cd snake-game-firebase
    ```
    *(Reemplaza `https://github.com/tu-usuario/snake-game-firebase.git` con la URL real de tu repositorio)*

2.  **Configuración de Firebase:**
    * Crea un nuevo proyecto en [Firebase Console](https://console.firebase.google.com/).
    * Agrega tu aplicación Android y descarga el archivo `google-services.json`.
    * Coloca el archivo `google-services.json` en el directorio `app/` de tu proyecto Android.

3.  **Configurar dependencias:** Asegúrate de que el archivo `build.gradle` en el nivel del proyecto tenga la siguiente línea en el bloque `buildscript`:
    ```gradle
    classpath 'com.google.gms:google-services:4.3.10'
    ```
    Y en el archivo `build.gradle` a nivel de la aplicación (`app/build.gradle`):
    ```gradle
    apply plugin: 'com.google.gms.google-services'
    ```

4.  **Sincronizar el proyecto:** En Android Studio, sincroniza tu proyecto con los archivos de Gradle para asegurarte de que todas las dependencias estén correctamente configuradas.

## Uso

1.  **Iniciar sesión:** El usuario puede iniciar sesión utilizando su correo electrónico y contraseña o con su cuenta de Google.
2.  **Jugar:** Una vez que el usuario inicie sesión, puede empezar a jugar al juego Snake.
3.  **Cerrar sesión:** El usuario puede cerrar sesión y volver a la pantalla de inicio de sesión.

## Estructura del Proyecto

* **LoginActivity:** Actividad para manejar el inicio de sesión.
* **MainActivity:** Actividad principal donde se muestra el juego Snake.
* **SnakeGame:** Composable que implementa la lógica del juego Snake.
* **Firebase Authentication:** Manejo de la autenticación utilizando Firebase y Google Sign-In.

## Contribuciones

Si deseas contribuir a este proyecto, por favor haz un fork del repositorio y crea un pull request con las modificaciones que consideres necesarias.
