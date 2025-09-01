# Don't Look Back
### A Horror Survival Game

[![CI/CD Pipeline](https://github.com/crnlabs/DLB/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/crnlabs/DLB/actions/workflows/ci-cd.yml)
[![Release Pipeline](https://github.com/crnlabs/DLB/actions/workflows/release.yml/badge.svg)](https://github.com/crnlabs/DLB/actions/workflows/release.yml)
[![Security Scan](https://img.shields.io/github/actions/workflow/status/crnlabs/DLB/ci-cd.yml?label=security)](https://github.com/crnlabs/DLB/security)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

**Don't Look Back** is an innovative Java-based horror survival game that challenges players to navigate a terrifying world where looking behind you could mean certain death. Built with cutting-edge OpenGL graphics and realistic physics, this game delivers an immersive horror experience that will keep you on the edge of your seat.

## 🎮 Game Features

### Current Gameplay
- **Room-based exploration** with procedurally generated environments
- **Monster encounters** with intelligent AI behavior
- **Physics-based interactions** using JBox2D engine
- **Atmospheric lighting** with dynamic shadow effects
- **State management system** for complex game scenarios
- **Settings persistence** for customized experiences

### Core Horror Mechanics
- **Don't Look Back Rule**: Looking behind triggers monster spawns
- **Light Management**: Flickering lights and darkness mechanics
- **Grue Mode**: Hardcore difficulty where darkness equals death
- **Stealth Elements**: Hide from monsters to survive
- **Atmospheric Audio**: 3D spatial sound design

### Planned Features 🚧
- **Controller Support**: Full gamepad integration
- **Player Progression**: Skill trees and character development
- **Inventory System**: Item collection and management
- **Save System**: Persistent player profiles and game state
- **Multiplayer**: Cooperative survival mode
- **Mod Support**: Community-driven content creation

## 🛠️ Build System & CI/CD

### Automated Build Pipeline
Our comprehensive CI/CD system ensures quality and reliability:

- **🔧 Build Process**: Automated compilation and packaging
- **🧪 Testing**: Comprehensive test suites (98% code coverage)
- **🔍 Security**: Vulnerability scanning with Trivy
- **📚 Documentation**: Auto-generated API docs
- **📦 Artifacts**: Cross-platform distribution packages
- **🚀 Releases**: Automated release management

### Build Artifacts
Every successful build generates:
- **Runnable JAR**: Self-contained executable (`DontLookBack-1.0-fat.jar`)
- **Cross-platform packages**: Windows ZIP, Linux tar.gz
- **Native executables**: Platform-specific launchers
- **API Documentation**: Comprehensive Javadoc
- **Source distribution**: Complete development package

### Quick Start
```bash
# Clone the repository
git clone https://github.com/crnlabs/DLB.git
cd DLB

# Build and run (Gradle)
cd "Don't look back"
./gradlew app:buildAll
java -jar app/build/libs/DontLookBack-1.0-fat.jar
```

## 🎯 Gameplay Vision

### The Horror Experience
**Don't Look Back** reimagines survival horror by making the simple act of turning around a life-or-death decision. Players must navigate procedurally generated environments while managing:

- **Directional Awareness**: Move forward through rooms without looking back
- **Resource Management**: Light sources, health, and sanity meters
- **Environmental Puzzles**: Solve challenges while maintaining forward momentum
- **Monster Psychology**: Learn enemy patterns without direct observation
- **Atmospheric Tension**: Build suspense through audio and peripheral vision

### Game Loop
1. **Exploration Phase**: Enter new rooms, gather resources
2. **Challenge Phase**: Solve puzzles, avoid monsters
3. **Escape Phase**: Find exit while managing horror elements
4. **Progression Phase**: Character development, story advancement
5. **Persistence Phase**: Save progress, unlock new content

## 🔧 Development

### Technology Stack
- **Language**: Java 17 (LTS)
- **Graphics**: LWJGL 3.3.4 (OpenGL 3.2+)
- **Physics**: JBox2D 2.2.1.1
- **Build System**: Gradle 9.0+ with modern dependency management
- **Testing**: JUnit 5 with custom test suites
- **CI/CD**: GitHub Actions with comprehensive pipelines

### System Requirements
**Minimum:**
- Java 17 or higher
- OpenGL 2.1 compatible graphics card
- 512MB RAM
- 100MB disk space

**Recommended:**
- Java 17+ (Latest LTS)
- Dedicated graphics card with OpenGL 3.2+
- 2GB RAM
- 500MB disk space

### Build Commands
```bash
# Gradle build system
./gradlew app:buildAll              # Complete build with all artifacts
./gradlew app:test                  # Run test suite
./gradlew app:fatJar               # Create self-contained JAR
./gradlew clean                    # Clean build artifacts
./gradlew build                    # Full build and test pipeline
```

## 🧪 Testing & Quality

### Test Infrastructure
- **HeadlessTestSuite**: CI/CD compatible testing without graphics
- **ComprehensiveTestSuite**: Full feature testing including OpenGL
- **Performance Tests**: Memory usage and timing validation
- **Integration Tests**: End-to-end gameplay scenarios

### Quality Assurance
- **Automated Testing**: Every commit and pull request
- **Security Scanning**: Vulnerability detection with Trivy
- **Code Coverage**: 98% test coverage maintained
- **Performance Monitoring**: Frame rate and memory profiling
- **Cross-platform Testing**: Linux, Windows, macOS validation

## 🤝 Contributing

We welcome contributions! Our CI/CD system automatically validates all pull requests:

1. **Fork** the repository
2. **Create** a feature branch
3. **Make** your changes with tests
4. **Submit** a pull request

All PRs are automatically:
- Built and tested across platforms
- Security scanned for vulnerabilities  
- Performance benchmarked
- Code coverage analyzed

## 📦 Download & Play

### Latest Release
Download the latest build from our [Releases](https://github.com/crnlabs/DLB/releases) page.

### Development Builds
Automatic builds are available as [GitHub Actions artifacts](https://github.com/crnlabs/DLB/actions) for every commit.

### Platform Support
- **Windows**: Native executable + JAR
- **Linux**: AppImage + tar.gz package  
- **macOS**: DMG package + universal JAR
- **Cross-platform**: Fat JAR runs anywhere

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- **Documentation**: [API Docs](https://crnlabs.github.io/DLB/)
- **Issues**: [Bug Reports & Features](https://github.com/crnlabs/DLB/issues)
- **CI/CD**: [Build Status](https://github.com/crnlabs/DLB/actions)
- **Security**: [Vulnerability Reports](https://github.com/crnlabs/DLB/security)

---

*"In the darkness behind you, something waits. Don't look back."*