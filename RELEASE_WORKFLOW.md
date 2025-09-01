# Don't Look Back - Release Workflow Documentation

## Overview

This document describes the release and CI/CD pipeline for the Don't Look Back game project.

## CI/CD Pipeline (`ci-cd.yml`)

### Triggers
- Push to `main`, `develop`, or feature branches
- Pull requests to `main`, `develop`  
- Manual workflow dispatch

### Jobs
1. **Test Suite** - Runs comprehensive tests including JUnit 5 tests
2. **Build and Package** - Creates release packages for all platforms
3. **Security Scan** - Trivy vulnerability scanning  
4. **Deploy Docs** - GitHub Pages deployment (main branch only)
5. **Notification** - Build status summary

### Artifacts Created (Every Build)
- **JAR Files**: `dont-look-back-jar-{build#}` (30 days retention)
- **Release Packages**: `dont-look-back-packages-{build#}` (90 days retention)
  - `DontLookBack-build{#}-Windows.zip`
  - `DontLookBack-build{#}-Linux.tar.gz` 
  - `DontLookBack-build{#}-CrossPlatform.zip`
- **Legacy Distribution**: `dont-look-back-distribution-{build#}` (90 days retention)
- **Javadoc**: `dont-look-back-javadoc-{build#}` (30 days retention)
- **Test Results**: `test-results-{build#}` (14 days retention)

## Release Pipeline (`release.yml`)

### Triggers
- GitHub Release creation
- Manual workflow dispatch (with version parameter)

### Release Creation Process

#### Manual Release (Recommended)
1. Go to GitHub Actions → Release Pipeline → Run workflow
2. Enter version (e.g., `1.0.0`)
3. Pipeline automatically:
   - Builds all artifacts
   - Creates GitHub Release with tag `v{version}`
   - Attaches all platform packages to the release
   - Generates comprehensive release notes

#### GitHub Release Event
- When a release is created manually via GitHub UI
- Pipeline builds and attaches artifacts to existing release

### Release Package Contents

Each release includes 3 platform-specific packages:

#### Windows Package (`DontLookBack-{version}-Windows.zip`)
- `DontLookBack-1.0-fat.jar` - Self-contained game executable
- `run.bat` - Windows startup script
- `res/` - Game resources (textures, shaders, models)
- `javadoc/` - Complete API documentation
- `RELEASE_INFO.txt` - Release information

#### Linux Package (`DontLookBack-{version}-Linux.tar.gz`) 
- `DontLookBack-1.0-fat.jar` - Self-contained game executable
- `run.sh` - Linux/Mac startup script (executable)
- `res/` - Game resources (textures, shaders, models)
- `javadoc/` - Complete API documentation
- `RELEASE_INFO.txt` - Release information

#### Cross-Platform Package (`DontLookBack-{version}-CrossPlatform.zip`)
- Same contents as Windows package
- Works on any platform with Java 17+

## System Requirements

- **Java 17 or higher** (LTS)
- **OpenGL 2.1** compatible graphics card
- **512MB RAM** minimum
- **100MB disk space**

## Quick Start for End Users

### Windows
1. Download `DontLookBack-{version}-Windows.zip`
2. Extract to a folder
3. Double-click `run.bat` or run `java -jar DontLookBack-1.0-fat.jar`

### Linux/Mac
1. Download `DontLookBack-{version}-Linux.tar.gz`
2. Extract: `tar -xzf DontLookBack-{version}-Linux.tar.gz`
3. Run: `./run.sh` or `java -jar DontLookBack-1.0-fat.jar`

### Cross-Platform
1. Download `DontLookBack-{version}-CrossPlatform.zip`
2. Extract and run: `java -jar DontLookBack-1.0-fat.jar`

## Developer Workflow

### Creating a Release
1. **Manual Release** (Recommended):
   ```bash
   # Go to GitHub Actions → Release Pipeline → Run workflow
   # Enter version: 1.0.0
   # Pipeline creates release automatically
   ```

2. **GitHub UI Release**:
   ```bash
   # Create release via GitHub UI with tag v1.0.0
   # Pipeline will attach artifacts automatically
   ```

### Local Development
```bash
cd "Don't look back"

# Build game
./gradlew app:build app:fatJar

# Run tests
./gradlew app:test

# Generate documentation
./gradlew app:javadoc

# Run game locally
./gradlew app:run
```

### Build Artifacts Locally
```bash
cd "Don't look back"

# Create fat JAR
./gradlew app:fatJar

# Find artifacts
ls -la app/build/libs/
# Output: DontLookBack-1.0-fat.jar (self-contained, ~5MB)
#         app.jar (regular JAR)
```

## Technical Details

### Build System
- **Gradle 9.0+** with modern dependency management
- **LWJGL 3.3.4** for cross-platform graphics
- **JBox2D 2.2.1.1** for physics simulation
- **JUnit 5** for comprehensive testing

### Dependencies
All dependencies downloaded from Maven Central:
- No manual library management required
- Native libraries embedded in fat JAR
- Cross-platform compatibility built-in

### Security
- **Trivy vulnerability scanning** on every build
- **Dependabot** for automated dependency updates
- **SARIF reports** uploaded to GitHub Security tab

### Performance
- **Build times**: ~30-60 seconds typical
- **Fat JAR size**: ~5MB including all dependencies
- **Test execution**: ~10-15 seconds
- **Package creation**: ~10-20 seconds

## Troubleshooting

### Common Issues

#### Build Failures
```bash
# Clean and rebuild
./gradlew clean app:build

# Check Java version
java -version  # Should be 17+

# Re-download dependencies
./gradlew --refresh-dependencies app:build
```

#### Missing Artifacts
- Check GitHub Actions → Artifacts tab
- Artifacts are retained 30-90 days depending on type
- Use manual release trigger to regenerate

#### Release Not Created
- Ensure version format is correct (e.g., `1.0.0`)
- Check GitHub Actions logs for errors
- Verify GitHub token permissions

### Getting Help
- Check GitHub Actions logs for detailed error messages
- Review build reports in `build/reports/`
- Ensure all required dependencies are available

## Release History

Releases are available at: https://github.com/crnlabs/DLB/releases

Each release includes:
- Release notes with features and changes
- System requirements
- Download instructions  
- Platform-specific packages
- Changelog and technical details