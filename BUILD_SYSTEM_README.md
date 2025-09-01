# Don't Look Back - Modern Build System

This project has been completely modernized with a Gradle-based build system using LWJGL 3.3.4 and JBox2D from Maven Central.

## Build Requirements

- Java 17 or higher (LTS)  
- Gradle 9.0+ (included via wrapper)
- **No external dependencies** - everything is downloaded from Maven Central

## Quick Start

### Build Everything
```bash
./gradlew app:buildAll
```

This will create:
- A fat JAR with all dependencies included
- Cross-platform executable scripts (.bat for Windows, .sh for Unix)
- Instructions for creating native .EXE files

### Available Build Tasks

#### Core Tasks
- `./gradlew app:compileJava` - Compile the Java source code
- `./gradlew app:test` - Run the test suite
- `./gradlew app:fatJar` - Create a runnable JAR with all dependencies

#### Distribution Tasks
- `./gradlew app:createExecutable` - Create wrapper scripts (.bat/.sh)
- `./gradlew app:createWindowsEXE` - Create .EXE launcher instructions
- `./gradlew app:buildAll` - Build everything

#### Testing Tasks
- `./gradlew app:test` - Run basic unit tests
- `./gradlew app:testComprehensive` - Run comprehensive test suite

## Generated Artifacts

After building, you'll find the following in `app/build/distributions/`:

### Runnable JAR
- `DontLookBack-1.0-fat.jar` - Self-contained JAR with all dependencies

### Executable Scripts
- `DontLookBack.bat` - Windows batch file launcher
- `DontLookBack.sh` - Unix shell script launcher

### Native Executable Support
- `create_exe.bat` - Instructions for creating Windows .EXE files

## Running the Game

### Using the Fat JAR
```bash
java -Djava.library.path=natives/linux -jar DontLookBack-1.0-fat.jar
```

### Using the Scripts
**Windows:**
```cmd
DontLookBack.bat
```

**Linux/Mac:**
```bash
./DontLookBack.sh
```

## Creating Native Executables

### Windows .EXE
1. Download launch4j from http://launch4j.sourceforge.net/
2. Use the provided fat JAR and follow the instructions in `create_exe.bat`
3. Configure launch4j to wrap the JAR in a native .EXE

### Advanced: jpackage (Java 17+)
Since the project now targets Java 17+, you can use jpackage:
```bash
jpackage --input app/build/distributions \
         --name DontLookBack \
         --main-jar DontLookBack-1.0-fat.jar \
         --main-class dontlookback.DontLookBack \
         --type app-image
```

## Features

### ✅ Completed Features
- [x] Runnable fat JAR with embedded dependencies
- [x] Cross-platform executable scripts
- [x] Modern Gradle build system
- [x] Comprehensive testing framework
- [x] Native library bundling (LWJGL)
- [x] CI/CD ready build process

### 🎯 Advanced Features
- Instructions for .EXE creation using launch4j
- Support for jpackage-based native executables
- Automated testing with headless mode
- Resource bundling and management

## Project Structure

```
Don't look back/
├── app/                    # Gradle application module
│   ├── build.gradle       # Modern build configuration
│   └── src/
│       ├── main/java/     # (references ../src via sourceSets)
│       └── test/java/     # New comprehensive tests
├── src/                   # Original Java source files
├── res/                   # Game resources
├── lwjgl/                 # LWJGL native libraries
└── build.xml             # Legacy Ant build (preserved)
```

## Legacy Support

The original Ant build system (`build.xml`) is preserved and still functional:
```bash
ant clean compile jar
```

However, the Gradle build system provides modern features like:
- Dependency management
- Fat JAR creation
- Cross-platform support
- Automated testing
- Native executable generation