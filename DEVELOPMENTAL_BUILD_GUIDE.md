# Developmental Build System Guide

## Overview

The Don't Look Back project now features a comprehensive developmental build system that automatically creates platform-specific packages whenever code is merged to the main branch. This guide explains how to use and understand the developmental build system.

## Automatic Developmental Builds

### Trigger Conditions
Developmental builds are automatically triggered when:
- Code is successfully merged to the `main` branch via pull request
- All tests pass and build validation succeeds
- The CI/CD pipeline completes successfully

### What Gets Built
Each developmental build includes:

#### 🖥️ Windows Native (No Java Required)
- **Self-contained .exe** with embedded JRE
- **File size**: ~57MB (includes everything needed)
- **Installation**: Simply run `DontLookBack.exe`
- **Artifact**: `dont-look-back-dev-{build#}-windows-native`

#### 🐧 Linux Native (No Java Required)  
- **Native .deb package** with system integration
- **File size**: ~39MB (includes JRE)
- **Installation**: `sudo dpkg -i dontlookback_1.0.0-1_amd64.deb`
- **Features**: Desktop shortcuts, menu integration, uninstall support
- **Artifact**: `dont-look-back-dev-{build#}-linux-native`

#### 🌐 Cross-Platform (Java 17+ Required)
- **Fat JAR** with all dependencies (5MB)
- **Platform launchers** for Windows (.bat), Linux (.sh), macOS (.command)
- **Installation**: Run platform-specific launcher script
- **Artifact**: `dont-look-back-dev-{build#}-crossplatform-complete`

#### 👨‍💻 Developer Package
- **Complete source code** for debugging
- **Build configuration** files (Gradle, version catalogs)
- **Test source code** and build scripts
- **API documentation** (Javadoc)
- **Game resources** and assets

## Accessing Developmental Builds

### From GitHub Actions
1. Go to the [Actions tab](https://github.com/crnlabs/DLB/actions)
2. Find the latest main branch build
3. Scroll down to "Artifacts" section
4. Download the appropriate package for your platform

### Artifact Retention
- **Native packages**: 90 days
- **Cross-platform**: 90 days  
- **Quick access files**: 90 days
- **Complete builds**: 90 days

## Local Development Builds

### Quick Commands
```bash
# Navigate to project directory
cd "Don't look back"

# Create developmental build with all platforms
gradle buildDevelopmental --no-configuration-cache

# Create just Windows executable
gradle createWindowsExecutable --no-configuration-cache

# Create just Linux native package
gradle createNativePackage --no-configuration-cache

# Quick cross-platform build
gradle buildQuick
```

### Build Outputs
After running `gradle buildDevelopmental`, you'll find:

```
app/build/
├── developmental/                    # Complete developmental package
│   ├── BUILD_INFO.txt               # Build information and instructions
│   ├── artifacts/                   # All build artifacts
│   │   ├── DontLookBack-1.0-fat.jar # Self-contained JAR
│   │   ├── cross-platform/          # Platform launchers
│   │   ├── native/                  # Linux .deb package
│   │   ├── windows-native/          # Windows .exe (self-contained)
│   │   └── javadoc/                 # API documentation
│   └── source/                      # Complete source code
│       ├── src/                     # Main source
│       ├── test/                    # Test source
│       ├── res/                     # Game resources
│       └── build-config/            # Build configuration
└── distributions/
    ├── DontLookBack-Windows-SelfContained.zip
    ├── cross-platform/
    ├── native/
    └── windows-native/
```

## Installation Instructions

### Windows (No Java Required)
1. Download `dont-look-back-dev-{build#}-windows-native`
2. Extract the ZIP file
3. Navigate to `DontLookBack/` folder
4. Run `DontLookBack.exe`

### Linux (No Java Required)
1. Download `dont-look-back-dev-{build#}-linux-native`
2. Extract the package
3. Install: `sudo dpkg -i dontlookback_1.0.0-1_amd64.deb`
4. Launch from applications menu or run `dontlookback`

### Cross-Platform (Java 17+ Required)
1. Download `dont-look-back-dev-{build#}-crossplatform-complete`
2. Extract the package
3. Navigate to `artifacts/cross-platform/[your-platform]/`
4. Run the appropriate launcher:
   - Windows: `DontLookBack.bat`
   - Linux: `./DontLookBack.sh`
   - macOS: `./DontLookBack.command`

### Direct JAR (Java 17+ Required)
1. Download any package containing the JAR
2. Run: `java -jar DontLookBack-1.0-fat.jar`

## System Requirements

### Windows Native
- **Operating System**: Windows 10 or later
- **Memory**: 2GB RAM minimum
- **Graphics**: OpenGL 3.3+ compatible
- **Java**: Not required (embedded)

### Linux Native
- **Operating System**: Ubuntu 18.04+ or equivalent
- **Memory**: 2GB RAM minimum  
- **Graphics**: OpenGL 3.3+ compatible
- **Java**: Not required (embedded)

### Cross-Platform
- **Operating System**: Windows 10+, Linux, macOS 10.14+
- **Java**: 17 or later (LTS recommended)
- **Memory**: 2GB RAM minimum
- **Graphics**: OpenGL 3.3+ compatible

## Troubleshooting

### Windows Issues
- **"Windows protected your PC"**: Right-click → "Run anyway" (first run only)
- **Graphics errors**: Update graphics drivers
- **Performance issues**: Close other applications

### Linux Issues
- **Package conflicts**: `sudo apt --fix-broken install`
- **Permission errors**: Ensure proper installation with `sudo`
- **Graphics errors**: Install OpenGL drivers

### Cross-Platform Issues
- **Java not found**: Install Java 17+ or set JAVA_HOME
- **Permission denied**: Make scripts executable with `chmod +x`
- **Graphics errors**: Check Java and graphics driver compatibility

## Development Notes

### For Contributors
- Developmental builds are created automatically on main branch merges
- No manual intervention required for package creation
- Source code is always included for debugging
- Build artifacts are retained for 90 days

### For Users
- Developmental builds contain the latest features
- May be less stable than official releases
- Native packages provide the best user experience
- Cross-platform packages offer maximum compatibility

### Build Performance
- **Developmental build**: ~54 seconds
- **Windows .exe creation**: ~15 seconds
- **Linux .deb creation**: ~30 seconds
- **Cross-platform distribution**: ~2 seconds

## Related Documentation
- [Build System README](BUILD_SYSTEM_README.md)
- [Native Build Guide](NATIVE_BUILD_GUIDE.md)
- [Release Workflow](RELEASE_WORKFLOW.md)
- [GitHub Actions Workflows](.github/workflows/)

---

**Contact**: For issues or questions, visit [GitHub Issues](https://github.com/crnlabs/DLB/issues)