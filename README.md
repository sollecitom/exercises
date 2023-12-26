# Sandpit Pricing Model

A model allowing to calculate pricing for sandpits.

## How to

### Build the project

```bash
./gradlew build
```

### Publish the artefacts to Maven local

```bash
./gradlew build publishToMavenLocal
```

### Upgrade Gradle (example version)

```bash
./gradlew wrapper --gradle-version 8.4 --distribution-type alls
```

### Update all dependencies if more recent versions exist, and remove unused ones (it will update `gradle/libs.versions.toml`)

```bash
./gradlew versionCatalogUpdate
```