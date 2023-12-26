# Exercises

A collection of various exercises

## How to

### Build the project

```bash
./gradlew build
```

### Publish the artifacts to Maven local

```bash
./gradlew build publishToMavenLocal
```

### Upgrade Gradle (example version)

```bash
./gradlew wrapper --gradle-version 8.5 --distribution-type alls
```

### Update all dependencies if more recent versions exist, and remove unused ones (it will update `gradle/libs.versions.toml`)

```bash
./gradlew versionCatalogUpdate
```