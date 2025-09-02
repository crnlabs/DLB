# Dependabot Troubleshooting Guide

This document provides solutions for common Dependabot issues in the Don't Look Back project.

## 🚨 Common Issues

### 1. "Label could not be found" Error

**Error Message:**
```
The following labels could not be found: dependency-update
Please create it before Dependabot can add it to a pull request.
```

**Solution:**
This error occurred because the old configuration referenced a non-existent label. The configuration has been updated to use standard labels:

- ✅ `dependencies` - Standard GitHub label (exists by default)
- ✅ `github-actions` - Created automatically by Dependabot
- ✅ `java` - Created automatically by Dependabot

**Manual Label Creation (if needed):**
```bash
# Via GitHub CLI
gh label create dependencies --color "0366d6" --description "Pull requests that update a dependency file"
gh label create github-actions --color "000000" --description "GitHub Actions workflow updates"
gh label create java --color "b07219" --description "Java/Gradle dependency updates"
gh label create security --color "ee0701" --description "Security vulnerability fixes"
gh label create automerge --color "ededed" --description "PRs safe for automated merging"
```

### 2. Dependabot Not Creating PRs

**Check Configuration:**
```bash
# Validate configuration
./validate-dependabot.sh

# Check YAML syntax
yamllint .github/dependabot.yml

# Verify directory structure
ls -la "Don't look back"
```

**Common Causes:**
- ❌ **Invalid YAML syntax** - Use yamllint to validate
- ❌ **Missing directories** - Ensure `/Don't look back` exists
- ❌ **Repository permissions** - Check Dependabot has write access
- ❌ **Rate limiting** - GitHub may temporarily limit Dependabot

**Force Dependabot Check:**
1. Go to repository **Insights** → **Dependency graph** → **Dependabot**
2. Click **Last checked: X time ago** → **Check for updates**

### 3. Build Failures After Updates

#### LWJGL Updates
```bash
# Test in headless environment
cd "Don't look back"
gradle clean test

# Check OpenGL context
java -Djava.awt.headless=true -jar app/build/libs/DontLookBack-1.0-fat.jar
```

**Common LWJGL Issues:**
- 🔴 **Native library mismatch** - Update all LWJGL natives together
- 🔴 **OpenGL context failure** - Expected in headless CI environment
- 🔴 **Memory allocation** - LWJGL 3.x uses different memory model

#### JUnit Updates
```bash
# Run full test suite
gradle build

# Check for deprecated annotations
grep -r "@Test\|@Before\|@After" app/src/test/
```

**Common JUnit Issues:**
- 🔴 **Deprecated annotations** - Update `@Before` to `@BeforeEach`
- 🔴 **Assertion imports** - Update to `org.junit.jupiter.api.Assertions`
- 🔴 **Test runner** - JUnit 5 uses different test engine

### 4. Version Catalog Issues

**Check Version Catalog:**
```bash
# Validate version catalog syntax
cd "Don't look back"
gradle --no-daemon help

# Check catalog dependencies
gradle app:dependencies --configuration compileClasspath
```

**Common Catalog Issues:**
- 🔴 **Invalid TOML syntax** - Check `gradle/libs.versions.toml`
- 🔴 **Missing version references** - Ensure all versions are defined
- 🔴 **Bundle conflicts** - Check bundle definitions match usage

### 5. Security Update Issues

**Enable Security Updates:**
1. Repository **Settings** → **Code security and analysis**
2. Enable **Dependabot security updates**
3. Enable **Dependency graph**
4. Enable **Vulnerable dependency alerts**

**Check Security Alerts:**
```bash
# Via GitHub CLI
gh api repos/crnlabs/DLB/vulnerability-alerts

# Check for security advisories
curl -s "https://api.github.com/repos/crnlabs/DLB/security-advisories"
```

### 6. Auto-merge Not Working

**Requirements for Auto-merge:**
- ✅ Branch protection rules configured
- ✅ Required status checks pass
- ✅ PR has `automerge` label
- ✅ PR is from Dependabot bot

**Debug Auto-merge:**
```bash
# Check branch protection
gh api repos/crnlabs/DLB/branches/main/protection

# Check PR merge status
gh pr view [PR_NUMBER] --json mergeable,mergeStateStatus
```

## 🛠️ Advanced Troubleshooting

### Dependabot Logs

**Access Logs:**
1. Go to repository **Insights** → **Dependency graph** → **Dependabot**
2. Click on individual ecosystem for detailed logs
3. Check for error messages or warnings

### Manual Trigger

**Force Update Check:**
```bash
# Via GitHub API
curl -X POST \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/crnlabs/DLB/dependabot/updates

# Via GitHub CLI
gh api -X POST repos/crnlabs/DLB/dependabot/updates
```

### Reset Configuration

**Clean Reset:**
```bash
# Backup current config
cp .github/dependabot.yml .github/dependabot.yml.backup

# Reset to minimal config for testing
cat > .github/dependabot.yml << 'EOF'
version: 2
updates:
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "daily"
EOF

# Test minimal config, then restore full config
```

## 📞 Getting Help

### Internal Resources
- **Configuration**: `.github/dependabot.yml`
- **Validation Script**: `./validate-dependabot.sh`
- **Documentation**: `DEPENDABOT_GUIDE.md`

### External Resources
- [GitHub Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [Configuration Reference](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file)
- [Gradle Version Catalogs](https://docs.gradle.org/current/userguide/platforms.html)

### Support Channels
- **Repository Issues**: Report bugs or feature requests
- **GitHub Community**: [GitHub Community Forum](https://github.community/)
- **Stack Overflow**: Tag questions with `dependabot` and `gradle`

## 🔄 Preventive Measures

### Regular Maintenance
- **Weekly**: Review Dependabot PRs and merge safe updates
- **Monthly**: Run `./validate-dependabot.sh` to check configuration
- **Quarterly**: Review ignore rules and update policies

### Monitoring
- **Enable notifications** for Dependabot PRs
- **Set up alerts** for security vulnerabilities
- **Monitor build status** after dependency updates
- **Review dependency licenses** periodically

### Best Practices
- ✅ **Test major updates** in staging environment
- ✅ **Group related dependencies** together
- ✅ **Keep documentation updated** when configuration changes
- ✅ **Use semantic versioning** for ignore rules
- ✅ **Enable auto-merge** for low-risk updates only