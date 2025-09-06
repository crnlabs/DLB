# Android Build Configuration Guide

## Overview

This guide explains how to resolve Android build issues and set up Android support for the Don't Look Back project. The version catalog has been configured to handle Kotlin/Compose compatibility issues.

## Resolved Issues

### Compose Compiler Compatibility Issue

**Issue**: `This version (1.5.4) of the Compose Compiler requires Kotlin version 1.9.20 but you appear to be using Kotlin version 1.9.22 which is not known to be compatible.`

**Solution**: Updated version catalog with compatible versions:

- **Kotlin**: 1.9.24 (newer version with better compatibility)
- **Compose Compiler**: 1.5.15 (compatible with Kotlin 1.9.24)
- **Android Gradle Plugin**: 8.7.2 (latest stable)
- **Compose BOM**: 2024.12.01 (latest stable Compose libraries)

## Version Catalog Configuration

The `gradle/libs.versions.toml` file now includes:

```toml
[versions]
kotlin = "1.9.24"
android-gradle-plugin = "8.7.2"
compose-bom = "2024.12.01"
compose-compiler = "1.5.15"

[bundles]
compose = ["compose-ui", "compose-ui-tooling-preview", "compose-material3", "compose-activity"]
compose-testing = ["compose-ui-test-junit4", "compose-ui-tooling", "compose-ui-test-manifest"]

[plugins]
android-application = { id = "com.android.application", version.ref = "android-gradle-plugin" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

## Adding Android Module (When Needed)

When you're ready to add Android support to the project:

### 1. Create Android Module

```bash
cd "Don't look back"
mkdir android
cd android
```

### 2. Android Module Build Configuration

Create `android/build.gradle`:

```gradle
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace 'com.crnlabs.dontlookback'
    compileSdk 35

    defaultConfig {
        applicationId "com.crnlabs.dontlookback"
        minSdk 24
        targetSdk 35
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = '17'
    }
    
    buildFeatures {
        compose true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion libs.versions.compose.compiler.get()
    }
    
    packaging {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
}

dependencies {
    // Compose BOM for version alignment
    implementation platform(libs.compose.bom)
    
    // Compose dependencies
    implementation libs.bundles.compose
    
    // Testing
    testImplementation libs.bundles.testing
    androidTestImplementation libs.bundles.compose.testing
    
    // Shared game logic (reference to desktop module)
    implementation project(':app')
}
```

### 3. Update Settings Gradle

Add to `settings.gradle`:

```gradle
include ':android'
```

## Compatibility Matrix

| Component | Version | Compatibility |
|-----------|---------|---------------|
| Kotlin | 1.9.24 | ✅ Compatible with Compose Compiler 1.5.15 |
| Compose Compiler | 1.5.15 | ✅ Compatible with Kotlin 1.9.24 |
| Android Gradle Plugin | 8.7.2 | ✅ Latest stable |
| Compose BOM | 2024.12.01 | ✅ Latest stable Compose libraries |

## Build Commands

### Desktop Build (Current)
```bash
cd "Don't look back"
gradle clean build
```

### Android Build (When Module Added)
```bash
cd "Don't look back"
gradle android:assembleDebug
```

### Multi-Platform Build
```bash
cd "Don't look back"
gradle clean build android:assembleDebug
```

## Troubleshooting

### Issue: Compose Compiler Version Conflicts

**Symptoms**: Build fails with Kotlin/Compose version incompatibility

**Solution**: 
1. Check `gradle/libs.versions.toml` for version consistency
2. Ensure Kotlin and Compose Compiler versions are compatible
3. Update to newer versions as shown in this guide

### Issue: Android Gradle Plugin Conflicts

**Symptoms**: Build fails with AGP version issues

**Solution**:
1. Update AGP to latest stable version (8.7.2)
2. Ensure Gradle version supports the AGP version
3. Update Gradle wrapper if needed

### Issue: Dependency Resolution Problems

**Symptoms**: Dependencies cannot be resolved

**Solution**:
1. Clean build cache: `gradle clean`
2. Refresh dependencies: `gradle --refresh-dependencies`
3. Check version catalog syntax in `gradle/libs.versions.toml`

## Migration Path

1. **Phase 1** (Current): Desktop Java game with version catalog prepared for Android
2. **Phase 2**: Add Android module with shared game logic
3. **Phase 3**: Implement Android-specific features (touch controls, mobile UI)
4. **Phase 4**: Cross-platform build system with shared game engine

## References

- [Compose Kotlin Compatibility Map](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
- [Android Gradle Plugin Release Notes](https://developer.android.com/studio/releases/gradle-plugin)
- [Kotlin Release Notes](https://kotlinlang.org/docs/releases.html)
- [Version Catalog Documentation](https://docs.gradle.org/current/userguide/platforms.html)

## Contact

For issues related to Android build configuration:
- GitHub Issues: https://github.com/crnlabs/DLB/issues
- Build System Documentation: [BUILD_SYSTEM_README.md](BUILD_SYSTEM_README.md)