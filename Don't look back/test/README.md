# Testing Infrastructure

This directory contains the comprehensive testing suite for the Don't Look Back game.

## Test Files

### ComprehensiveTestSuite.java
A full-featured test suite that covers all game systems:
- Room generation and management
- Entity creation and behavior
- Game mechanics and physics
- Graphics and rendering systems
- Memory management and cleanup
- Performance and stability

**Note:** This test suite requires OpenGL/LWJGL libraries and may not run in headless CI environments.

### HeadlessTestSuite.java
A lightweight test suite designed for CI/CD pipelines:
- Tests core game logic without graphics dependencies
- Validates room system structure and behavior
- Checks basic game mechanics and calculations
- Suitable for automated testing in headless environments

## Running Tests

### Local Development
```bash
# Compile all tests
gradle compileTestJava

# Run all tests (headless mode for CI compatibility)
gradle test

# Run comprehensive test suite
gradle testComprehensive

# Clean and rebuild with tests
gradle clean build

# Build complete distribution
gradle buildAll
```

### Individual Test Execution
```bash
# Run specific test class
gradle test --tests "BasicGameTest"

# Run specific test method
gradle test --tests "StateManagementTest.testInitialState"

# Run tests with verbose output
gradle test --info
```

## CI/CD Integration

The testing infrastructure is integrated with GitHub Actions:

- **Automated Testing**: All tests run on every commit and pull request
- **Artifact Generation**: Build artifacts are uploaded after successful builds
- **Documentation**: API documentation is generated and deployed
- **Security Scanning**: Code is scanned for vulnerabilities

### GitHub Actions Workflows

- `.github/workflows/ci-cd.yml` - Main CI/CD pipeline
- `.github/workflows/release.yml` - Release artifact generation

## Test Coverage

The test suites cover:

1. **Core Systems**
   - Room type enumeration and properties
   - Settings configuration
   - Basic object creation and management
   - Memory management and garbage collection

2. **Room Generation**
   - Room creation and initialization
   - Content generation (furniture, monsters)
   - Room positioning and bounds checking
   - Cleanup and deconstruction logic

3. **Game Mechanics**
   - View-based room generation
   - Timing mechanics (5-second cleanup)
   - Player movement and positioning
   - Entity management and collision

4. **Performance**
   - Room generation performance
   - Memory usage monitoring
   - Concurrent operations safety

5. **Integration**
   - Full gameplay scenarios
   - System integration testing

## Adding New Tests

To add new tests:

1. Create new test classes in `app/src/test/java/dontlookback/`
2. Follow JUnit 5 conventions with `@Test` annotations
3. Use standard JUnit assertions (`assertEquals`, `assertTrue`, etc.)
4. Test both modern and legacy functionality as needed
5. Recompile and test with `gradle test`

## Troubleshooting

### OpenGL/LWJGL Issues
If tests fail with OpenGL/LWJGL errors:
- Use `HeadlessTestSuite` instead of `ComprehensiveTestSuite`
- Ensure native libraries are in the correct path
- Set `-Djava.awt.headless=true` JVM argument

### Build Failures
If compilation fails:
- Check that all dependencies are in the classpath
- Verify LWJGL libraries are properly referenced in `project.properties`
- Ensure test directory structure is correct

### CI/CD Issues
If GitHub Actions fail:
- Check workflow logs for specific error messages
- Verify environment setup steps complete successfully
- Ensure headless test suite is being used in CI environment