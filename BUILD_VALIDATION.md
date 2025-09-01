# Build Validation Guide

This document describes the comprehensive build validation system for Don't Look Back, designed to ensure code quality and build integrity before PR merging.

## Overview

The build validation system provides multiple layers of validation to ensure that:
1. Code compiles correctly (both main and test code)
2. JAR artifacts are created properly and contain expected content
3. Applications start correctly and handle errors gracefully
4. Native dependencies are properly bundled
5. Build artifacts meet size and structure requirements

## Automated CI/CD Validation

### Build Validation Job

The CI/CD pipeline includes a dedicated `build-validation` job that runs on every pull request and push. This job performs:

1. **Test Compilation Validation**
   - Ensures test code compiles without errors
   - Independent of test execution results

2. **Main Code Compilation Validation**
   - Validates all source code compiles correctly
   - Catches compilation errors early

3. **JAR Creation and Size Validation**
   - Creates fat JAR with all dependencies
   - Validates JAR size is reasonable (3-20MB range)
   - Ensures JAR creation doesn't fail

4. **JAR Manifest and Structure Validation**
   - Validates main class is correctly set in manifest
   - Ensures main class exists in JAR
   - Verifies LWJGL native libraries are bundled

5. **Application Startup Validation**
   - Tests application startup in headless mode
   - Validates proper game banner display
   - Ensures graceful graphics error handling

### Job Dependencies

The build validation is integrated into the CI/CD workflow:
- `test` job: Runs unit tests (may have some failures)
- `build-validation` job: Validates build integrity (must pass)
- `build` job: Creates full build artifacts (depends on validation)
- `security-scan` job: Performs security scanning
- `deploy-docs` job: Deploys documentation

**PR merging is blocked if build validation fails.**

## Local Validation

### Quick Validation Script

Use the provided validation script to check your build locally before pushing:

```bash
./validate-build.sh
```

This script performs the same validation checks as the CI/CD pipeline and provides immediate feedback.

### Manual Validation Steps

You can also run validation steps manually:

```bash
cd "Don't look back"

# 1. Test compilation
gradle compileTestJava --no-daemon

# 2. Main compilation  
gradle compileJava --no-daemon

# 3. JAR creation
gradle fatJar --no-daemon

# 4. Test application startup
java -Djava.awt.headless=true -jar app/build/libs/DontLookBack-1.0-fat.jar
```

## Validation Criteria

### JAR File Requirements
- **Size**: Between 3MB and 20MB
- **Main Class**: `dontlookback.DontLookBack`
- **Native Libraries**: LWJGL natives for Linux, Windows, macOS
- **Structure**: Valid ZIP structure with correct manifest

### Application Startup Requirements
- **Game Banner**: Must display "Don't Look Back"
- **Studio Banner**: Must display "A Game By: Game A Day Studios"
- **Graphics Initialization**: Must attempt LWJGL/OpenGL initialization
- **Graceful Failure**: Must handle headless environment gracefully

### Compilation Requirements
- **Zero Compilation Errors**: Both main and test code must compile
- **Warning Treatment**: Warnings are treated as errors (`-Werror`)
- **Modern Java**: Must compile with Java 17 features

## Troubleshooting Build Validation

### Common Issues

#### Compilation Failures
```bash
# Check for syntax errors, missing imports, deprecated APIs
gradle compileJava --info
```

#### JAR Size Issues
- **Too Small**: Missing dependencies or compilation issues
- **Too Large**: Unnecessary dependencies or resource duplication

#### Manifest Issues
- Main class not found: Check `application.mainClass` in build.gradle
- Missing natives: Verify LWJGL dependencies in build.gradle

#### Startup Issues
- Missing main class: Compilation or packaging problem
- Graphics errors: Expected in headless mode
- Unexpected crashes: Check for static initialization issues

### Debug Commands

```bash
# Check JAR contents
unzip -l app/build/libs/DontLookBack-1.0-fat.jar | head -20

# Verify main class in manifest
unzip -p app/build/libs/DontLookBack-1.0-fat.jar META-INF/MANIFEST.MF

# Test startup with verbose output
java -Djava.awt.headless=true -verbose:class -jar app/build/libs/DontLookBack-1.0-fat.jar
```

## Integration with Development Workflow

### Pre-Push Checklist
1. Run `./validate-build.sh` to ensure build validates locally
2. Optionally run `gradle test` to check test status
3. Commit and push changes
4. Monitor CI/CD pipeline for validation results

### PR Review Process
1. Build validation must pass (automated check)
2. Code review for functionality and style
3. Test results reviewed (failures may be acceptable for certain test types)
4. Security scan results reviewed
5. PR approved and merged

## Build Validation vs. Testing

**Build Validation** focuses on:
- Code compilation and basic functionality
- Artifact creation and integrity
- Essential runtime requirements
- Cross-platform compatibility

**Testing** focuses on:
- Game logic correctness
- Feature functionality
- Performance characteristics
- Edge case handling

Build validation ensures the codebase can be built and deployed, while testing ensures the game works correctly. Both are important, but build validation is required for PR merging while test failures may be acceptable in certain circumstances.