#!/bin/bash
# Dependabot Configuration Validator
# Validates Dependabot configuration and provides setup assistance

set -e

echo "🔍 Dependabot Configuration Validator"
echo "======================================"

# Check if we're in the correct directory
if [ ! -f ".github/dependabot.yml" ]; then
    echo "❌ Error: .github/dependabot.yml not found"
    echo "   Please run this script from the repository root"
    exit 1
fi

echo ""
echo "✅ Found .github/dependabot.yml"

# Validate YAML syntax
echo ""
echo "🔍 Validating YAML syntax..."
if command -v yamllint &> /dev/null; then
    if yamllint .github/dependabot.yml; then
        echo "✅ YAML syntax is valid"
    else
        echo "❌ YAML syntax errors found"
        exit 1
    fi
else
    echo "⚠️  yamllint not available, skipping syntax validation"
    echo "   Install with: pip install yamllint"
fi

# Check directory structure
echo ""
echo "🔍 Validating directory structure..."

if [ -d "Don't look back" ]; then
    echo "✅ Found 'Don't look back' directory"
else
    echo "❌ 'Don't look back' directory not found"
    echo "   Dependabot is configured to monitor this directory"
    exit 1
fi

if [ -f "Don't look back/app/build.gradle" ]; then
    echo "✅ Found Gradle build file"
else
    echo "❌ Gradle build file not found at 'Don't look back/app/build.gradle'"
    exit 1
fi

if [ -f "Don't look back/gradle/libs.versions.toml" ]; then
    echo "✅ Found version catalog"
else
    echo "❌ Version catalog not found at 'Don't look back/gradle/libs.versions.toml'"
    exit 1
fi

# Check GitHub Actions workflows
echo ""
echo "🔍 Checking GitHub Actions workflows..."

WORKFLOW_DIR=".github/workflows"
if [ -d "$WORKFLOW_DIR" ]; then
    echo "✅ Found GitHub Actions workflows directory"
    
    WORKFLOW_COUNT=$(find "$WORKFLOW_DIR" -name "*.yml" -o -name "*.yaml" | wc -l)
    echo "📊 Found $WORKFLOW_COUNT workflow files"
    
    if [ "$WORKFLOW_COUNT" -gt 0 ]; then
        echo "   Dependabot will monitor these for action updates"
    fi
else
    echo "⚠️  No GitHub Actions workflows found"
    echo "   GitHub Actions updates will not be applicable"
fi

# Test dependency resolution
echo ""
echo "🔍 Testing dependency resolution..."

cd "Don't look back"

if gradle app:dependencies --configuration compileClasspath --quiet > /dev/null 2>&1; then
    echo "✅ Gradle dependencies resolve successfully"
else
    echo "❌ Gradle dependency resolution failed"
    echo "   This may prevent Dependabot from analyzing dependencies"
    cd ..
    exit 1
fi

cd ..

# Check for common issues
echo ""
echo "🔍 Checking for common configuration issues..."

# Check for old/problematic labels
if grep -q "dependency-update" .github/dependabot.yml; then
    echo "⚠️  Found 'dependency-update' label reference"
    echo "   This label may not exist and could cause labeling failures"
    echo "   Consider using standard labels like 'dependencies' instead"
fi

# Check for proper grouping
if grep -q "groups:" .github/dependabot.yml; then
    echo "✅ Found dependency grouping configuration"
else
    echo "⚠️  No dependency grouping found"
    echo "   Consider adding groups to reduce PR noise"
fi

# Summary
echo ""
echo "📋 Configuration Summary"
echo "========================"

# Count ecosystems
ECOSYSTEM_COUNT=$(grep -c "package-ecosystem:" .github/dependabot.yml)
echo "📦 Monitoring $ECOSYSTEM_COUNT package ecosystems"

# List ecosystems
echo "🔍 Configured ecosystems:"
grep "package-ecosystem:" .github/dependabot.yml | sed 's/.*: "/  - /' | sed 's/"//'

# Check update frequency
if grep -q "weekly" .github/dependabot.yml; then
    echo "⏰ Update frequency: Weekly (recommended)"
else
    echo "⏰ Update frequency: $(grep "interval:" .github/dependabot.yml | head -1 | cut -d'"' -f2)"
fi

# Check review settings
if grep -q "reviewers:" .github/dependabot.yml; then
    echo "👥 Reviewers: Configured"
else
    echo "👥 Reviewers: Not configured"
fi

if grep -q "assignees:" .github/dependabot.yml; then
    echo "📝 Assignees: Configured"
else
    echo "📝 Assignees: Not configured"
fi

echo ""
echo "🎉 Dependabot configuration validation complete!"
echo ""
echo "📚 Next steps:"
echo "   1. Ensure repository labels exist (see .github/dependabot-labels.yml)"
echo "   2. Test configuration by creating a test dependency update"
echo "   3. Monitor Dependabot PRs and adjust configuration as needed"
echo "   4. Review DEPENDABOT_GUIDE.md for best practices"
echo ""
echo "🔗 Useful links:"
echo "   - Repository settings: https://github.com/$(git config --get remote.origin.url | sed 's/.*github.com[:/]//' | sed 's/.git$//')/settings"
echo "   - Dependabot documentation: https://docs.github.com/en/code-security/dependabot"
echo "   - Configuration reference: https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file"