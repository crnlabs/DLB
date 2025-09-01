# GitHub Copilot Instructions for DLB Repository

## Project Overview

**"Don't Look Back"** is a Java-based horror survival game built with LWJGL (Lightweight Java Game Library) and OpenGL for graphics rendering. The project features a comprehensive build system, automated testing, and CI/CD pipelines.

### Key Information
- **Project Name**: Don't Look Back
- **Main Language**: Java 17 (LTS)
- **Graphics Library**: LWJGL 2.9.1 with OpenGL
- **Physics Engine**: JBullet
- **Build System**: Apache Ant with custom targets
- **Dependency Management**: GitHub Dependabot
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
│   ├── src/dontlookback/   # Java source code
│   ├── test/dontlookback/  # Test source code
│   ├── res/                # Game resources and assets
│   ├── build.xml           # Apache Ant build configuration
│   ├── manifest.mf         # JAR manifest file
│   └── nbproject/          # NetBeans project configuration
├── lwjgl/                  # LWJGL dependencies
│   ├── lwjgl-2.9.1/       # LWJGL 2.9.1 library files
│   └── jbullet.jar        # JBullet physics library
└── README.md
```

## Build System & Dependencies

### Build Tool: Apache Ant
The project uses Apache Ant with a custom `build.xml` containing 74+ custom targets.

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
  - Gradle (for future migration from Ant)
  - Maven (for future migration from Ant)
- **Auto-assignment**: Updates are automatically assigned to @Gameaday
- **Labels**: Dependencies are labeled for easy tracking
- **Commit Message Format**: Uses ⬆️ prefix for dependency updates

### Current Dependencies (Manual Management)
- **LWJGL 2.9.1**: OpenGL bindings for Java
  - Location: `../lwjgl/lwjgl-2.9.1/jar/lwjgl.jar`
  - Utilities: `../lwjgl/lwjgl-2.9.1/jar/lwjgl_util.jar`
  - Native libraries: `../lwjgl/lwjgl-2.9.1/native/[platform]/`
- **JBullet**: Physics engine (`../lwjgl/jbullet.jar`)
- **PNGDecoder**: Image loading (`../lwjgl/lwjgl-2.9.1/native/windows/PNGDecoder.jar`)

### Migration Path
To fully leverage automated dependency management, consider migrating from Apache Ant to:
- **Gradle**: Modern build tool with excellent dependency management
- **Maven**: Traditional but robust build and dependency management

Both build tools are pre-configured in Dependabot for when migration occurs.

## Essential Build Commands

### Basic Operations (Fast - Under 10 seconds)

```bash
# Navigate to project directory (ALWAYS required first)
cd "Don't look back"

# Compile source code (≈5 seconds)
ant compile

# Compile test sources (≈2 seconds)  
ant compile-custom-tests

# Build JAR file (≈1 second)
ant jar

# Clean build artifacts (≈1 second)
ant clean
```

### Testing Commands

```bash
# Run headless test suite - CI/CD friendly (≈2 seconds)
ant test-headless

# Run comprehensive test suite with graphics (≈3-5 seconds)
ant test-comprehensive

# Run all existing individual tests (≈3-5 seconds)
ant test-existing

# Run complete test suite (≈5-10 seconds)
ant test-all
```

### Advanced Build Operations

```bash
# Generate Javadoc documentation (≈10-15 seconds)
ant javadoc

# Create distribution package (≈15-20 seconds)
ant dist-package

# Full CI/CD build pipeline (≈30-45 seconds)
ant ci-build
```

### Timeout Recommendations
- **Basic commands** (compile, jar): 30 seconds
- **Test commands**: 60 seconds  
- **Documentation generation**: 120 seconds
- **Full CI/CD build**: 180 seconds
- **Release packaging**: 300 seconds

## Testing Framework

### Test Suites Available

1. **HeadlessTestSuite** (`test/dontlookback/HeadlessTestSuite.java`)
   - Purpose: CI/CD compatible testing without graphics
   - Runtime: ~0.01 seconds
   - Tests: Core logic, settings, room structure
   - Usage: `ant test-headless`

2. **ComprehensiveTestSuite** (`test/dontlookback/ComprehensiveTestSuite.java`)
   - Purpose: Full feature testing including graphics
   - Runtime: ~3-5 seconds
   - Tests: Graphics, physics, game mechanics
   - Usage: `ant test-comprehensive`

3. **Individual Test Files**
   - `SimpleRoomTest.java`: Basic room functionality
   - `RoomGeneratorTest.java`: Room generation algorithms
   - Usage: `ant test-existing`

### Running Tests in Different Environments

```bash
# Headless environment (CI/CD)
export DISPLAY=:99.0
Xvfb :99 -screen 0 1024x768x24 > /dev/null 2>&1 &
ant test-headless

# Local development with graphics
ant test-comprehensive

# Manual test execution
java -cp "build/classes:$CLASSPATH" -Djava.awt.headless=true dontlookback.HeadlessTestSuite
```

## Running the Application

### Local Development
```bash
# Using Ant (recommended)
cd "Don't look back"
ant run

# Manual execution
java -Djava.library.path="../lwjgl/lwjgl-2.9.1/native/linux" \
     -cp "dist/Don_t_look_back.jar:../lwjgl/lwjgl-2.9.1/jar/*:../lwjgl/jbullet.jar" \
     dontlookback.DontLookBack
```

### Platform-Specific Native Paths
- **Linux**: `../lwjgl/lwjgl-2.9.1/native/linux`
- **Windows**: `../lwjgl/lwjgl-2.9.1/native/windows`
- **macOS**: `../lwjgl/lwjgl-2.9.1/native/macosx`

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
   ant compile && ant test-headless
   ```

3. **Use appropriate test suites**:
   - HeadlessTestSuite for CI/CD and quick validation
   - ComprehensiveTestSuite for full feature testing

4. **Validate builds before commits**:
   ```bash
   ant clean compile test-all jar
   ```

## Troubleshooting

### Common Issues

1. **Native Library Path Errors**
   - Ensure correct platform-specific path in `-Djava.library.path`
   - Verify LWJGL natives exist for your platform

2. **Compilation Warnings**
   - "system modules path not set" warning is expected with Java 11
   - Does not affect functionality

3. **OpenGL/Graphics Issues**
   - Use headless test suite for CI environments
   - Ensure Xvfb is running for headless graphics testing

4. **Build Path Issues**
   - Always use the full path with quotes: `"Don't look back"`
   - Relative paths in build.xml assume parent directory context

### Debug Commands
```bash
# Check Java version and classpath
java -version
echo $CLASSPATH

# Verify LWJGL natives
ls -la "../lwjgl/lwjgl-2.9.1/native/"

# Test basic compilation
ant clean compile

# Verify JAR creation
ant jar && ls -la dist/
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
- **Javadoc**: Generated via `ant javadoc`, available in `dist/javadoc/`
- **Build logs**: Available in GitHub Actions workflow runs
- **API reference**: LWJGL 2.9.1 documentation included

### External Dependencies
- [LWJGL 2.9.1 Documentation](https://legacy.lwjgl.org/wiki/index.php)
- [JBullet Physics Documentation](http://jbullet.advel.cz/)
- [Apache Ant Manual](https://ant.apache.org/manual/)

---

**Note**: This file is automatically maintained and updated through CI/CD processes. Manual changes should be synchronized with the development team.