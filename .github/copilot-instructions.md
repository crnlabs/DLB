# GitHub Copilot Instructions for DLB Repository

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

## Essential Build Commands

### Basic Operations (Fast - Under 10 seconds)

```bash
# Navigate to project directory (ALWAYS required first)
cd "Don't look back"

# Compile source code (≈5 seconds)
gradle compileJava

# Compile test sources (≈2 seconds)  
gradle compileTestJava

# Build fat JAR file (≈3 seconds)
gradle fatJar

# Clean build artifacts (≈1 second)
gradle clean
```

### Testing Commands

```bash
# Run test suite - CI/CD friendly with headless mode (≈3 seconds)
gradle test

# Run comprehensive test suite (≈3-5 seconds)
gradle testComprehensive

# Build and test everything (≈5-10 seconds)
gradle build
```

### Advanced Build Operations

```bash
# Generate Javadoc documentation (≈10-15 seconds)
gradle javadoc

# Create distribution package with executables (≈15-20 seconds)
gradle buildAll

# Run the application (≈5 seconds startup)
gradle run
```

### Timeout Recommendations
- **Basic commands** (compile, jar): 30 seconds
- **Test commands**: 60 seconds  
- **Documentation generation**: 120 seconds
- **Full CI/CD build**: 180 seconds
- **Release packaging**: 300 seconds

## Testing Framework

### Test Suites Available

1. **JUnit 5 Test Suite** (`app/src/test/java/dontlookback/`)
   - Purpose: Modern, comprehensive testing with JUnit 5
   - Runtime: ~3-5 seconds
   - Tests: All game systems, state management, basic functionality
   - Usage: `gradle test`

2. **Legacy Test Suites** (`test/dontlookback/`) - Preserved for compatibility
   - `HeadlessTestSuite.java`: CI/CD compatible testing without graphics
   - `ComprehensiveTestSuite.java`: Full feature testing including graphics
   - Can be run individually if needed

3. **Comprehensive Testing**
   - Modern test coverage using JUnit 5 assertions
   - Headless mode enabled by default for CI/CD compatibility
   - Usage: `gradle testComprehensive`

### Running Tests in Different Environments

```bash
# Modern testing (recommended)
gradle test

# Run with verbose output
gradle test --info

# Test specific classes
gradle test --tests "BasicGameTest"

# Manual test execution
java -cp "build/classes:$CLASSPATH" -Djava.awt.headless=true dontlookback.HeadlessTestSuite
```

## Running the Application

### Local Development
```bash
# Using Gradle (recommended)
cd "Don't look back"
gradle run

# Using the fat JAR
gradle fatJar
java -jar app/build/libs/DontLookBack-1.0-fat.jar

# Run demo to verify modern dependencies
gradle runDemo
```

### Distribution
The modern build system automatically includes all native libraries for cross-platform support:
- **Windows**: Native LWJGL libraries included
- **Linux**: Native LWJGL libraries included  
- **macOS**: Native LWJGL libraries included

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

## Troubleshooting

### Common Issues

1. **Dependency Resolution Issues**
   - All dependencies are automatically downloaded from Maven Central
   - No manual LWJGL installation required

2. **Compilation Warnings**
   - Modern Java 17 LTS provides optimal performance and compatibility
   - Does not affect functionality

3. **OpenGL/Graphics Issues**
   - Tests automatically run in headless mode for CI compatibility
   - Modern LWJGL 3.x provides better compatibility

4. **Build Path Issues**
   - Always use the full path with quotes: `"Don't look back"`
   - Gradle handles all dependency paths automatically

### Debug Commands
```bash
# Check Java version and Gradle
java -version
gradle --version

# Test basic compilation
gradle clean compileJava

# Verify JAR creation
gradle fatJar && ls -la app/build/libs/

# Check dependencies
gradle dependencies
```

## Performance Expectations

### Build Times (Approximate)
- **Clean + Compile**: 6-8 seconds
- **Run Tests**: 5-10 seconds
- **Generate Documentation**: 10-15 seconds
- **Complete CI Build**: 30-45 seconds
- **Full Release Package**: 2-3 minutes

### Resource Requirements
- **RAM**: 512MB minimum, 1GB recommended
- **Disk Space**: 100MB for build artifacts
- **Java Heap**: Default settings sufficient for development

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

**Note**: This file is automatically maintained and updated through CI/CD processes. Manual changes should be synchronized with the development team.