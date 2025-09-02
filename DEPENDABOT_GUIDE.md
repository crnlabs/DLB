# Dependabot Configuration Guide

This document describes the Dependabot configuration for the Don't Look Back project and provides guidance for managing automated dependency updates.

## Overview

Dependabot is configured to automatically monitor and update dependencies across multiple ecosystems:

- **GitHub Actions**: Workflow dependencies and actions
- **Gradle**: Java dependencies managed through our modern Gradle build system

## Configuration Features

### 🏷️ Smart Labeling
- **Dependencies**: General dependency updates
- **GitHub Actions**: Workflow and CI/CD related updates  
- **Java**: Gradle and Java ecosystem updates

### 📦 Dependency Grouping
Dependencies are grouped logically to reduce PR noise and ensure related updates happen together:

**LWJGL Group**: All LWJGL graphics libraries updated together
- `org.lwjgl:*` (core, OpenGL, GLFW, STB modules)
- Ensures graphics stack compatibility

**Testing Group**: Test framework dependencies
- JUnit Jupiter and platform launcher
- Maintains test framework consistency

**Physics Group**: Physics engine dependencies  
- JBox2D libraries
- Ensures physics simulation compatibility

**Actions Group**: GitHub Actions
- `actions/*` and `github/*` actions
- Keeps CI/CD pipeline actions aligned

### 🛡️ Safety Controls

**Major Version Protection**: Major updates are ignored for critical dependencies:
- **LWJGL**: Major updates can introduce breaking API changes requiring code refactoring
- **JUnit**: Major updates may require test rewriting

**Update Limits**: 
- GitHub Actions: 5 concurrent PRs maximum
- Gradle: 10 concurrent PRs maximum  

**Review Requirements**:
- All PRs automatically assigned to @Gameaday
- Manual review required before merging

### ⏰ Update Scheduling
- **Day**: Mondays (start of work week)
- **Time**: Staggered times to avoid conflicts
  - GitHub Actions: 09:00 UTC
  - Gradle: 10:00 UTC

## Version Catalog Integration

The project uses Gradle version catalogs (`gradle/libs.versions.toml`) for centralized dependency management:

```toml
[versions]
lwjgl = "3.3.4"
jbox2d = "2.2.1.1"
junit-jupiter = "5.12.1"

[bundles]
lwjgl-core = ["lwjgl-core", "lwjgl-opengl", "lwjgl-glfw", "lwjgl-stb"]
testing = ["junit-jupiter", "junit-platform-launcher"]
```

**Benefits**:
- ✅ Dependabot can update versions in the catalog
- ✅ All modules using the same dependency stay in sync
- ✅ Easier to review and manage dependency versions
- ✅ Gradle bundles group related dependencies together

## Automated PR Features

### Commit Message Format
```
⬆️(deps): Bump actions/checkout from 4 to 5
⬆️(deps): Bump org.lwjgl:lwjgl from 3.3.3 to 3.3.4
```

### PR Content
Each Dependabot PR includes:
- 📋 **Detailed changelog** links from dependency maintainers
- 🔗 **Compatibility badges** showing update safety
- 📊 **Release notes** and important changes
- 🛠️ **Commands** for manual Dependabot control

## Manual Dependabot Commands

You can control Dependabot behavior by commenting on PRs:

```bash
@dependabot rebase          # Rebase the PR with latest changes
@dependabot recreate        # Recreate the PR from scratch
@dependabot merge           # Merge after CI passes
@dependabot squash and merge # Squash and merge after CI passes
@dependabot cancel merge    # Cancel auto-merge
@dependabot ignore this dependency  # Ignore this dependency permanently
@dependabot ignore this major version  # Ignore this major version
```

## Security Features

### Vulnerability Alerts
- **Direct dependencies**: Monitored for known security issues
- **Indirect dependencies**: Transitive dependencies also monitored
- **Automatic security updates**: Critical vulnerabilities trigger immediate PRs

### Update Types Allowed
- ✅ **Security updates**: Always allowed for all dependencies
- ✅ **Minor updates**: Allowed for most dependencies
- ✅ **Patch updates**: Allowed for all dependencies
- ⚠️ **Major updates**: Blocked for critical dependencies (LWJGL, JUnit)

## Troubleshooting

### Missing Labels Error
If you see "dependency-update label could not be found":

1. **Solution**: The configuration now uses standard labels:
   - `dependencies` (should exist by default)
   - `github-actions` and `java` (will be created if needed)

2. **Alternative**: Create missing labels manually:
   ```bash
   # Via GitHub CLI
   gh label create dependencies --color "0366d6" --description "Pull requests that update a dependency file"
   gh label create github-actions --color "000000" --description "GitHub Actions workflow updates"  
   gh label create java --color "b07219" --description "Java/Gradle dependency updates"
   ```

### Dependabot Not Creating PRs

**Check Configuration**:
```bash
# Validate YAML syntax
yamllint .github/dependabot.yml

# Check file permissions
ls -la .github/dependabot.yml
```

**Common Issues**:
- **Invalid YAML**: Use a YAML validator to check syntax
- **Wrong directory**: Ensure `/Don't look back` directory exists
- **Repository permissions**: Dependabot needs write access

### Build Failures After Updates

**LWJGL Updates**:
- Test in headless environment: `gradle test`
- Verify native library compatibility
- Check OpenGL context creation

**JUnit Updates**:
- Run full test suite: `gradle build`
- Check for deprecated test annotations
- Verify test execution compatibility

## Best Practices

### 🔄 Regular Maintenance
1. **Weekly Review**: Check Dependabot PRs every Monday
2. **Batch Testing**: Test related updates together before merging
3. **Release Planning**: Coordinate dependency updates with releases

### 🧪 Testing Strategy
1. **Automated Tests**: All PRs must pass CI/CD pipeline
2. **Manual Testing**: Test critical graphics/physics updates manually
3. **Staging Deployment**: Test major updates in staging environment

### 📝 Documentation
1. **Update Changelog**: Document major dependency changes
2. **Breaking Changes**: Note any API changes in release notes
3. **Compatibility**: Update system requirements if needed

## Configuration Files

- **Primary Config**: `.github/dependabot.yml`
- **Version Catalog**: `Don't look back/gradle/libs.versions.toml`
- **Build Config**: `Don't look back/app/build.gradle`
- **This Guide**: `DEPENDABOT_GUIDE.md`

## Related Documentation

- [GitHub Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [Gradle Version Catalogs](https://docs.gradle.org/current/userguide/platforms.html)
- [LWJGL Documentation](https://www.lwjgl.org/guide)
- [Project Build Guide](BUILD_VALIDATION.md)