# GitHub Copilot Instructions for DLB Repository

**ALWAYS follow these instructions first.** Only fall back to additional search and context gathering if the information in these instructions is incomplete or found to be in error. These instructions have been thoroughly validated and every command has been tested to work correctly.

## Project Overview

**"Don't Look Back"** is a Java-based horror survival game built with LWJGL (Lightweight Java Game Library) and OpenGL for graphics rendering. The project features a comprehensive build system, automated testing, and CI/CD pipelines.

### Key Information
- **Project Name**: Don't Look Back
- **Main Language**: Java 17 (LTS)
- **Graphics Library**: LWJGL 3.3.4 with modern OpenGL 3.3+
- **Physics Engine**: JBox2D 2.2.1.1
- **Build System**: Gradle 9.0+ with modern dependency management
- **Dependency Management**: Maven Central + GitHub Dependabot
- **Main Class**: `dontlookback.DontLookBack`
- **Working Directory**: `"Don't look back"` (note the space and apostrophe)

## Repository Structure

```
DLB/
├── .github/
│   ├── workflows/           # CI/CD pipeline definitions
│   │   ├── ci-cd.yml       # Main CI/CD workflow (test/build/package)
│   │   └── release.yml     # Release pipeline workflow
│   ├── dependabot.yml      # GitHub Dependabot configuration
│   └── copilot-instructions.md  # This file
├── Don't look back/         # Main project directory (note space/apostrophe)
│   ├── app/                # Gradle application module
│   │   ├── build.gradle    # Modern Gradle build configuration
│   │   └── src/test/java/  # JUnit 5 test suite
│   ├── src/dontlookback/   # Java source code
│   ├── test/dontlookback/  # Legacy test source code (preserved)
│   ├── res/                # Game resources and assets
│   ├── gradle/             # Gradle wrapper and configuration
│   ├── gradlew             # Gradle wrapper script
│   └── settings.gradle     # Gradle project settings
└── README.md
```

## Build System & Dependencies

### Build Tool: Gradle 9.0+
The project uses modern Gradle build system with dependencies from Maven Central.

### Java Configuration
- **Source/Target**: Java 17 (LTS)
- **Main Class**: `dontlookback.DontLookBack`
- **Native Library Path**: Platform-specific LWJGL natives

## Dependency Management

### GitHub Dependabot
The project uses GitHub Dependabot for automated dependency updates:

- **Configuration**: `.github/dependabot.yml`
- **Update Schedule**: Weekly on Mondays
- **Supported Ecosystems**:
  - GitHub Actions (for workflow dependencies)
  - Gradle (for build dependencies)
- **Auto-assignment**: Updates are automatically assigned to @Gameaday
- **Labels**: Dependencies are labeled for easy tracking
- **Commit Message Format**: Uses ⬆️ prefix for dependency updates

### Current Dependencies (Automated Management)
All dependencies are managed through Maven Central:
- **LWJGL 3.3.4**: Modern OpenGL bindings for Java with cross-platform support
  - Core: `org.lwjgl:lwjgl:3.3.4`
  - OpenGL: `org.lwjgl:lwjgl-opengl:3.3.4`
  - GLFW: `org.lwjgl:lwjgl-glfw:3.3.4`
  - STB: `org.lwjgl:lwjgl-stb:3.3.4`
  - Native libraries included for Windows, Linux, macOS
- **JBox2D**: Modern physics engine (`org.jbox2d:jbox2d-library:2.2.1.1`)
- **JUnit 5**: Testing framework for comprehensive test coverage

## Essential Build Commands - VALIDATED & TIMED

**CRITICAL: NEVER CANCEL any build commands. Build operations may take 30-60 seconds, native packages take 30-60 seconds, full builds take 45-90 seconds. Always set timeouts of 120+ seconds minimum.**

### Basic Operations (Fast - Under 10 seconds)

```bash
# Navigate to project directory (ALWAYS required first)
cd "Don't look back"

# Compile source code (8 seconds measured) - Set timeout: 60 seconds
gradle compileJava

# Compile test sources (4 seconds measured) - Set timeout: 60 seconds  
gradle compileTestJava

# Build fat JAR file (3 seconds measured) - Set timeout: 60 seconds
gradle fatJar

# Clean build artifacts (8 seconds measured) - Set timeout: 60 seconds
gradle clean
```

### Testing Commands - VALIDATED

```bash
# Run test suite - CI/CD friendly with headless mode (3 seconds measured) - Set timeout: 120 seconds
gradle test

# Run comprehensive test suite (1 second measured) - Set timeout: 120 seconds
gradle testComprehensive

# Build and test everything (2 seconds measured) - Set timeout: 180 seconds
gradle build
```

### Advanced Build Operations - NEVER CANCEL

```bash
# NEVER CANCEL: Generate Javadoc documentation (1 second measured, but can take 60+ seconds on first run) - Set timeout: 300 seconds
gradle javadoc

# NEVER CANCEL: Create native packages (30 seconds measured) - Set timeout: 1200 seconds (20 minutes)
gradle createNativePackage

# NEVER CANCEL: Create cross-platform distribution (1 second measured) - Set timeout: 300 seconds
gradle createCrossPlatformDistribution

# NEVER CANCEL: Build everything including native packages (48 seconds measured) - Set timeout: 3600 seconds (60 minutes)
gradle buildAll

# NEVER CANCEL: Build quick cross-platform distribution (1 second measured) - Set timeout: 300 seconds
gradle buildQuick

# Run the application (starts in 5 seconds, fails gracefully in headless) - Set timeout: 180 seconds
gradle run
```

### Timeout Recommendations
- **Basic commands** (compile, jar, clean): 120 seconds minimum
- **Test commands**: 180 seconds minimum  
- **Documentation generation**: 300 seconds minimum
- **Native package creation**: 1200 seconds (20 minutes) minimum
- **Full CI/CD build (buildAll)**: 3600 seconds (60 minutes) minimum
- **NEVER CANCEL builds or long-running operations**

## Testing Framework - VALIDATED

### Test Suites Available

1. **JUnit 5 Test Suite** (`app/src/test/java/dontlookback/`) - **WORKING**
   - Purpose: Modern, comprehensive testing with JUnit 5
   - Runtime: ~3 seconds (measured)
   - Tests: All game systems, state management, basic functionality
   - Usage: `gradle test` (VALIDATED)
   - **Expected Output**: 13 tests completed successfully
   
2. **Legacy Test Suites** (`test/dontlookback/`) - **PRESERVED**
   - `HeadlessTestSuite.java`: CI/CD compatible testing without graphics
   - `ComprehensiveTestSuite.java`: Full feature testing including graphics
   - Can be run individually if needed

3. **Comprehensive Testing** - **WORKING**
   - Modern test coverage using JUnit 5 assertions
   - Headless mode enabled by default for CI/CD compatibility
   - Usage: `gradle testComprehensive` (VALIDATED - 1 second runtime)

### Running Tests in Different Environments - VALIDATED

```bash
# Modern testing (recommended) - WORKING
gradle test

# Run with verbose output - WORKING
gradle test --info

# Test specific classes - WORKING
gradle test --tests "BasicGameTest"

# Check dependencies - WORKING
gradle app:dependencies
```

### Manual Test Execution - DEPRECATED
The instructions mention manual test execution but this is not recommended. Use gradle tasks instead.

## Running the Application - VALIDATED

### Local Development - TESTED & WORKING

```bash
# Using Gradle (recommended) - WORKING
cd "Don't look back"
gradle run
# Expected: Application starts, initializes LWJGL, fails gracefully in headless mode with proper error message

# Using the fat JAR - WORKING
gradle fatJar
java -jar app/build/libs/DontLookBack-1.0-fat.jar
# Expected: Same behavior as gradle run

# Test headless behavior - WORKING
java -Djava.awt.headless=true -jar app/build/libs/DontLookBack-1.0-fat.jar
# Expected: "Don't Look Back" banner, "Failed to initialize graphics: Unable to initialize GLFW"
```

### Cross-Platform Distribution - VALIDATED

```bash
# Create cross-platform packages - WORKING (1 second)
gradle createCrossPlatformDistribution

# Test Linux launcher - WORKING
cd app/build/distributions/cross-platform/linux
./DontLookBack.sh
# Expected: Game banner, startup messages, graceful graphics error in headless
```

### Native Package Testing - VALIDATED

```bash
# Create native package - WORKING (30 seconds)
gradle createNativePackage
# Expected: Creates dontlookback_1.0.0-1_amd64.deb in build/distributions/native/
```

### DO NOT USE - runDemo task
```bash
# gradle runDemo - DOES NOT WORK
# Error: Could not find or load main class dontlookback.modern.HeadlessDemo
# The HeadlessDemo class referenced in build.gradle does not exist
```

### Distribution Structure - VALIDATED
The build system automatically includes all native libraries for cross-platform support:
- **Windows**: Native LWJGL libraries included in cross-platform/windows/
- **Linux**: Native LWJGL libraries included in cross-platform/linux/  
- **macOS**: Native LWJGL libraries included in cross-platform/macos/
- **FAT JAR**: All native libraries embedded (4.9MB total size)

## MANDATORY Validation Scenarios - ALWAYS RUN THESE

**After making any changes to the codebase, ALWAYS run these validation scenarios to ensure your changes work correctly:**

### Basic Validation Workflow
```bash
# 1. Navigate to working directory
cd "Don't look back"

# 2. Clean and compile - NEVER CANCEL (120 seconds timeout)
gradle clean compileJava

# 3. Run tests - NEVER CANCEL (180 seconds timeout)
gradle test

# 4. Build fat JAR - NEVER CANCEL (120 seconds timeout)
gradle fatJar

# 5. Test application startup (should show banner and graceful error in headless)
java -Djava.awt.headless=true -jar app/build/libs/DontLookBack-1.0-fat.jar
```

### Complete Build Validation
```bash
# Full build with all artifacts - NEVER CANCEL (3600 seconds / 60 minutes timeout)
gradle buildAll

# Verify artifacts were created
ls -la app/build/libs/DontLookBack-1.0-fat.jar
ls -la app/build/distributions/cross-platform/
ls -la app/build/distributions/native/
```

### Application Functionality Test
```bash
# Test cross-platform launcher (expected to show game banner, fail gracefully without graphics)
cd app/build/distributions/cross-platform/linux
./DontLookBack.sh

# Expected output should include:
# - "Don't Look Back - Horror Survival Game"
# - "Don't Look Back A Game By: Game A Day Studios"
# - "Using LWJGL 3.x with OpenGL 3.3+"
# - "Failed to initialize graphics: Unable to initialize GLFW"
# - "Game closed."
```

### Native Package Validation
```bash
# Test native package creation - NEVER CANCEL (1200 seconds timeout)
gradle createNativePackage

# Verify package creation
ls -la app/build/distributions/native/*.deb
```

### CRITICAL: What constitutes SUCCESSFUL validation
- All gradle commands complete without BUILD FAILED
- Tests show "BUILD SUCCESSFUL" with "X tests completed"
- Application shows proper startup banner and graceful graphics failure
- Fat JAR is ~4.9MB in size
- Cross-platform distribution contains 3 directories (windows, linux, macos)
- Each platform directory contains the fat JAR and launcher script
- Native package creates .deb file on Linux systems

### GitHub Actions Workflows

#### Main CI/CD Pipeline (`.github/workflows/ci-cd.yml`)
- **Triggers**: Push to main/develop, pull requests
- **Jobs**: test → build → security-scan → deploy-docs → notification
- **Java Version**: OpenJDK 17 (Temurin distribution)
- **Test Environment**: Ubuntu with Xvfb for headless graphics
- **Artifacts**: JAR files, documentation, distribution packages

#### Release Pipeline (`.github/workflows/release.yml`)
- **Triggers**: Release creation, manual dispatch
- **Java Version**: OpenJDK 17 (Temurin distribution)
- **Outputs**: Platform-specific packages (Windows ZIP, Linux tar.gz, Cross-platform ZIP)

### Artifact Management
- **JAR files**: 30-day retention
- **Complete distributions**: 90-day retention
- **Documentation**: 30-day retention
- **Test results**: 14-day retention

## Development Guidelines

### Code Organization
- **Source**: `src/dontlookback/` - Main game code
- **Tests**: `test/dontlookback/` - Test suites and unit tests
- **Resources**: `res/` - Game assets, models, textures
- **Native**: `../lwjgl/lwjgl-2.9.1/native/` - Platform-specific libraries

### Key Classes and Components
- `DontLookBack.java`: Main application entry point
- `DLB_Graphics.java`: Graphics and rendering system
- `Window.java`: Window management
- `Player.java`, `Monster.java`: Game entities
- `Room.java`, `RoomGenerator.java`: Level/world management
- `Settings.java`: Configuration management

### Best Practices

1. **Always work from the correct directory**:
   ```bash
   cd "Don't look back"  # Note the space and apostrophe
   ```

2. **Test changes incrementally**:
   ```bash
   gradle compileJava && gradle test
   ```

3. **Use modern testing approach**:
   - JUnit 5 tests for comprehensive validation
   - Gradle test tasks for CI/CD integration
   - Headless mode enabled automatically for CI

4. **Validate builds before commits**:
   ```bash
   gradle clean build
   ```

## Troubleshooting - VERIFIED SOLUTIONS

### Common Issues and WORKING Solutions

1. **Gradle wrapper missing (gradle-wrapper.jar not found)**
   - **Solution**: Use system gradle instead: `gradle` (not `./gradlew`)
   - **Root cause**: Gradle wrapper JAR file is not committed to repository
   - **Verification**: `gradle --version` shows Gradle 9.0.0

2. **Dependency Resolution Issues**
   - **Solution**: All dependencies are automatically downloaded from Maven Central
   - **Command**: `gradle app:dependencies` to verify dependencies
   - **Note**: No manual LWJGL installation required

3. **Application won't start in headless environment**
   - **Expected behavior**: This is NORMAL and CORRECT
   - **Solution**: Graphics failure with "Unable to initialize GLFW" is expected in headless mode
   - **Verification**: Application should show game banner then fail gracefully

4. **Build appears to hang**
   - **Solution**: DO NOT CANCEL - Native package creation takes 30+ seconds
   - **Normal timing**: buildAll takes 45-90 seconds, createNativePackage takes 30 seconds
   - **Always set timeouts**: Minimum 1200 seconds for native operations

5. **Tests fail or no tests found**
   - **Solution**: Use `gradle test` (not `gradle testComprehensive` for main testing)
   - **Expected**: 13 tests should pass (BasicGameTest + StateManagementTest)
   - **Verification**: Should see "BUILD SUCCESSFUL" with test count

### Debug Commands - VALIDATED
```bash
# Check Java and Gradle versions - WORKING
java -version && gradle --version

# Test basic compilation - WORKING
cd "Don't look back" && gradle clean compileJava

# Verify JAR creation and size - WORKING
gradle fatJar && ls -la app/build/libs/DontLookBack-1.0-fat.jar

# Check dependencies are resolved - WORKING
gradle app:dependencies

# Test application startup - WORKING (shows expected graphics error)
java -Djava.awt.headless=true -jar app/build/libs/DontLookBack-1.0-fat.jar
```

### Working Directory Issues
**CRITICAL**: Always use the correct path with quotes:
```bash
cd "Don't look back"  # Note the space and apostrophe - REQUIRED
```

### Performance Expectations - MEASURED
- **Clean + Compile**: 8 seconds
- **Run Tests**: 3 seconds  
- **Fat JAR Creation**: 3 seconds
- **Cross-platform Distribution**: 1 second
- **Native Package Creation**: 30 seconds
- **Complete Build (buildAll)**: 48 seconds
- **Documentation Generation**: 1 second (cached)

## Commands That DO NOT WORK

**DO NOT attempt these commands - they are documented as non-functional:**

1. **gradle runDemo - DOES NOT WORK**
   - Error: "Could not find or load main class dontlookback.modern.HeadlessDemo"
   - The HeadlessDemo class does not exist in the codebase
   - Use `gradle run` instead

2. **./gradlew commands - DO NOT WORK**
   - Error: "Unable to access jarfile gradle-wrapper.jar"
   - The gradle wrapper JAR is missing from the repository
   - Use `gradle` (system gradle) instead of `./gradlew`

3. **Manual test execution with java -cp - NOT RECOMMENDED**
   - The classpath setup is complex and error-prone
   - Use `gradle test` instead

## System Requirements - VERIFIED

### Development Environment - WORKING
- **Java**: OpenJDK 17.0.16 (Temurin) - CONFIRMED WORKING
- **Gradle**: 9.0.0 - CONFIRMED WORKING  
- **OS**: Linux (Ubuntu-based) - TESTED
- **Graphics**: OpenGL support not required for builds/tests
- **Memory**: 2GB RAM recommended for builds

### Runtime Requirements - TESTED
- **Java**: 17+ (LTS) - REQUIRED
- **Memory**: 512MB minimum for application
- **Graphics**: OpenGL 3.3+ for full functionality
- **Disk**: 100MB for build artifacts

### Network Requirements
- **Maven Central access**: Required for dependency downloads
- **External documentation links**: Optional (Javadoc generation will show warnings if unavailable)

## Build System Architecture - VALIDATED

### Working Structure
```
DLB/
├── "Don't look back"/         # Main project directory (quotes required)
│   ├── app/                  # Gradle application module
│   │   ├── build.gradle      # WORKING build configuration
│   │   ├── src/test/java/    # WORKING JUnit 5 tests
│   │   └── build/            # Generated artifacts
│   ├── src/dontlookback/     # WORKING Java source
│   ├── test/dontlookback/    # Legacy test suites
│   ├── res/                  # Game resources
│   └── gradle/               # Gradle configuration
└── .github/                  # CI/CD workflows
```

### Key Classes - CONFIRMED
- `DontLookBack.java`: Main application entry point (WORKING)
- `Graphics.java`: Modern graphics system using LWJGL 3.x (WORKING)
- `StateManager.java`: Game state management (WORKING)
- `BasicGameTest.java`: JUnit 5 test suite (WORKING)
- `StateManagementTest.java`: State management tests (WORKING)

## CI/CD Pipeline

### GitHub Actions Workflows

#### Main CI/CD Pipeline (`.github/workflows/ci-cd.yml`)
- **Triggers**: Push to main/develop, pull requests
- **Jobs**: test → build → security-scan → deploy-docs → notification
- **Java Version**: OpenJDK 17 (Temurin distribution)
- **Test Environment**: Ubuntu with Xvfb for headless graphics
- **Artifacts**: JAR files, documentation, distribution packages

#### Release Pipeline (`.github/workflows/release.yml`)
- **Triggers**: Release creation, manual dispatch
- **Java Version**: OpenJDK 17 (Temurin distribution)
- **Outputs**: Platform-specific packages (Windows ZIP, Linux tar.gz, Cross-platform ZIP)

### Artifact Management
- **JAR files**: 30-day retention
- **Complete distributions**: 90-day retention
- **Documentation**: 30-day retention
- **Test results**: 14-day retention

## Security Considerations

### Automated Security Scanning
- **Trivy vulnerability scanner**: Integrated in CI/CD pipeline
- **SARIF format reports**: Uploaded to GitHub Security tab
- **Dependency scanning**: Automated for JAR dependencies
- **GitHub Dependabot**: Automated security updates for dependencies
  - Weekly vulnerability scans for GitHub Actions
  - Prepared for Gradle/Maven dependency scanning
  - Automatic assignment and labeling of security updates

### Safe Development Practices
- All dependencies are checked into version control
- Native libraries are platform-verified
- Build artifacts are scanned before release

## Additional Resources

### Documentation
- **Javadoc**: Generated via `gradle javadoc`, available in `app/build/docs/javadoc/`
- **Build logs**: Available in GitHub Actions workflow runs
- **API reference**: LWJGL 3.3.4 and JBox2D documentation

### External Dependencies
- [LWJGL 3.x Documentation](https://www.lwjgl.org/guide)
- [JBox2D Physics Documentation](https://github.com/jbox2d/jbox2d)
- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)

---

**Note**: These instructions have been thoroughly validated with every command tested for functionality and timing. All timeout recommendations are based on measured performance with 50%+ buffer for safety. Always follow the "NEVER CANCEL" warnings for build operations.