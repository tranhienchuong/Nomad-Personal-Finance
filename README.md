# Nomad

Nomad is a learning project for a native Android personal-finance and expense-tracking app.

## Sprint 1 scope

This repository currently contains project foundation only: multi-module structure, build tooling,
Compose design-system scaffolding, static placeholder navigation, Hilt, Firebase wiring, Room, and
DataStore. It intentionally contains no feature logic, authentication flow, persistence logic, or
network client.

The Room module contains only an empty `AppDatabase` shell and builder configuration. Its concrete
schema is intentionally deferred because this sprint prohibits entities and DAOs.

## Modules

```text
app
core:common
core:designsystem
core:database
core:datastore
core:ui
feature:auth
feature:home
feature:transaction
feature:budget
feature:statistics
feature:profile
build-logic
```

## Technology

Kotlin, Jetpack Compose, Material 3, Navigation Compose, Hilt, Firebase BoM (Authentication,
Firestore, Crashlytics), Room, DataStore, Gradle Kotlin DSL, Version Catalog, and focused Gradle
convention plugins.

## Build and run

From the repository root, run:

```powershell
.\gradlew.bat build
.\gradlew.bat installDebug
```

The static demo flow starts at Splash. Tap the centered text to continue through Onboarding and
Auth, then switch between the five bottom-navigation destinations.

## Firebase

Add your Firebase configuration file as `app/google-services.json`. It is intentionally ignored by
Git and is not created by this project. The Google Services plugin is applied automatically when
that file is present.
