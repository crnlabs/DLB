# Native Build System Guide

Don't Look Back now features a comprehensive native build system that generates platform-specific packages and installers, moving beyond simple JAR distribution to provide truly native application experiences.

## Build System Overview

The enhanced build system provides multiple packaging options:

### 🚀 Quick Development Builds
```bash
cd "Don't look back"
gradle buildQuick
```
**Output**: Cross-platform distribution with platform-specific launchers
- `windows/` - Windows .bat launcher + JAR
- `linux/` - Linux .sh launcher + JAR  
- `macos/` - macOS .command launcher + JAR

### 📦 Native Packages
```bash
gradle createNativePackage
```
**Output**: Platform-native self-contained applications
- **Linux**: `.deb` package (39MB, includes JRE)
- **Windows**: `.exe` installer (when run on Windows)
- **macOS**: `.app` bundle (when run on macOS)

### 🏗️ Platform-Specific Installers
```bash
gradle buildInstallers
```
**Output**: Professional installer packages
- **Linux**: `.deb` and `.rpm` packages
- **Windows**: `.msi` and `.exe` installers
- **macOS**: `.dmg` and `.pkg` installers

### 🎯 Complete Build
```bash
gradle buildAll
```
**Output**: Everything - native packages, cross-platform distribution, and documentation

## Key Improvements Over JAR-Only Approach

### ✅ Benefits of Native Packaging

| Feature | JAR-Only | Native Packages |
|---------|----------|-----------------|
| **Java Installation Required** | ❌ Yes | ✅ No (JRE included) |
| **Platform Integration** | ❌ Limited | ✅ Full (shortcuts, menus, file associations) |
| **Installation Experience** | ❌ Manual | ✅ Professional installers |
| **Startup Performance** | ❌ Slower | ✅ Optimized |
| **Distribution Size** | ✅ Small (~5MB) | ⚠️ Larger (~39MB) |
| **Security** | ❌ Requires Java | ✅ Self-contained |

### 🎯 Target Use Cases

**Cross-Platform Distribution** (buildQuick):
- Development builds
- Cross-platform compatibility testing
- Users with Java already installed

**Native Packages** (createNativePackage):
- End-user distribution
- App store submissions
- Professional deployment

**Installer Packages** (buildInstallers):
- Enterprise deployment
- Software distribution platforms
- Professional releases

## Technical Implementation

### Native Package Contents
```
DontLookBack/
├── bin/
│   └── DontLookBack              # Native launcher
├── lib/
│   ├── app/
│   │   └── DontLookBack-1.0-fat.jar
│   └── runtime/                  # Embedded JRE
│       ├── bin/
│       ├── lib/
│       └── ...
└── share/
    └── applications/
        └── DontLookBack.desktop  # Linux desktop integration
```

### Cross-Platform Distribution Structure
```
cross-platform/
├── windows/
│   ├── DontLookBack-1.0-fat.jar
│   ├── DontLookBack.bat          # Enhanced Windows launcher
│   └── README.txt
├── linux/
│   ├── DontLookBack-1.0-fat.jar
│   ├── DontLookBack.sh           # Enhanced Linux launcher
│   └── README.txt
└── macos/
    ├── DontLookBack-1.0-fat.jar
    ├── DontLookBack.command      # Enhanced macOS launcher
    └── README.txt
```

## Platform-Specific Features

### Linux (.deb/.rpm)
- Desktop shortcuts and menu entries
- Proper file associations
- System integration via package manager
- Uninstall support

### Windows (.msi/.exe)
- Start Menu integration
- Desktop shortcuts
- Windows registry integration
- Add/Remove Programs support

### macOS (.dmg/.pkg)
- Applications folder integration
- Launchpad support
- macOS-style app bundle
- Notarization ready

## CI/CD Integration

The build system is fully integrated with GitHub Actions:

```yaml
- name: Build with native packages
  run: |
    cd "Don't look back"
    gradle buildAll --no-daemon
    gradle buildInstallers --no-daemon
```

### Artifact Organization
- **Native Packages**: 90-day retention
- **Cross-Platform**: 30-day retention  
- **JAR Files**: 30-day retention
- **Documentation**: 30-day retention

## Development Workflow

### For Developers
```bash
# Quick iterative development
gradle buildQuick

# Test native packaging
gradle createNativePackage

# Full release preparation
gradle buildAll
```

### For End Users

**Option 1: Native Package (Recommended)**
1. Download platform-specific package (.deb, .msi, .dmg)
2. Install using system package manager
3. Launch from desktop/menu

**Option 2: Cross-Platform**
1. Download platform folder (windows/, linux/, macos/)
2. Run platform-specific launcher script
3. Java 17+ required

**Option 3: Direct JAR**
1. Download `DontLookBack-1.0-fat.jar`
2. Run: `java -jar DontLookBack-1.0-fat.jar`
3. Java 17+ required

## System Requirements

### Native Packages (Self-Contained)
- **RAM**: 2GB minimum
- **Disk**: 100MB (JRE included)
- **Graphics**: OpenGL 3.3+ compatible
- **OS**: Windows 10+, Linux (modern distro), macOS 10.14+

### JAR Distribution
- **Java**: 17 or later required
- **RAM**: 512MB minimum
- **Disk**: 50MB (excluding Java)
- **Graphics**: OpenGL 3.3+ compatible

## Build Performance

| Task | Time | Output Size | Use Case |
|------|------|-------------|----------|
| `buildQuick` | ~8s | 15MB total | Development |
| `createNativePackage` | ~43s | 39MB | End users |
| `buildInstallers` | ~60s | 40-45MB | Professional |
| `buildAll` | ~90s | 60MB total | Release |

## Future Enhancements

### Planned Improvements
- **Windows Code Signing**: Enhanced security
- **macOS Notarization**: App Store compatibility  
- **Linux AppImage**: Universal Linux packages
- **Modular JRE**: Reduced package sizes
- **Auto-Update**: Built-in update mechanism

### Advanced Features
- **Custom JRE**: Minimal runtime for smaller packages
- **Platform Icons**: OS-specific application icons
- **File Associations**: Game file format support
- **Multi-Platform CI**: Build all platforms in parallel

---

## Quick Reference

```bash
# Development
gradle buildQuick              # Fast cross-platform build

# Native packages  
gradle createNativePackage    # Self-contained app
gradle buildInstallers        # Professional installers

# Complete build
gradle buildAll               # Everything + documentation

# Legacy JAR-only
gradle fatJar                 # Single JAR file
```

The build system is designed to be platform-agnostic while providing platform-native experiences, offering the best of both worlds for developers and end users.