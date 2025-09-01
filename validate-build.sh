#!/bin/bash
# Build Validation Script
# Run this script locally to validate your build before pushing to PR

set -e

echo "🔍 Starting local build validation..."
echo "=================================="

# Change to project directory
cd "Don't look back"

echo ""
echo "🔍 Step 1: Validating test compilation..."
gradle compileTestJava --no-daemon
echo "✅ Test code compilation successful"

echo ""
echo "🔍 Step 2: Validating main code compilation..."
gradle compileJava --no-daemon
echo "✅ Main code compilation successful"

echo ""
echo "🔍 Step 3: Validating JAR creation..."
gradle fatJar --no-daemon

# Check that JAR file was created
if [ ! -f "app/build/libs/DontLookBack-1.0-fat.jar" ]; then
  echo "❌ Fat JAR not created"
  exit 1
fi

# Check JAR size is reasonable (should be ~5MB with dependencies)
JAR_SIZE=$(stat -c%s "app/build/libs/DontLookBack-1.0-fat.jar")
if [ $JAR_SIZE -lt 3000000 ]; then
  echo "❌ JAR file too small: $JAR_SIZE bytes (expected >3MB)"
  exit 1
fi

if [ $JAR_SIZE -gt 20000000 ]; then
  echo "❌ JAR file too large: $JAR_SIZE bytes (expected <20MB)"
  exit 1
fi

echo "✅ JAR created successfully (${JAR_SIZE} bytes)"

echo ""
echo "🔍 Step 4: Validating JAR manifest and structure..."

# Check JAR manifest contains main class
MAIN_CLASS=$(unzip -p app/build/libs/DontLookBack-1.0-fat.jar META-INF/MANIFEST.MF | grep "Main-Class" | cut -d' ' -f2 | tr -d '\r')
if [ "$MAIN_CLASS" != "dontlookback.DontLookBack" ]; then
  echo "❌ Invalid main class in manifest: '$MAIN_CLASS'"
  exit 1
fi

# Check that main class exists in JAR
if ! unzip -l app/build/libs/DontLookBack-1.0-fat.jar | grep -q "dontlookback/DontLookBack.class"; then
  echo "❌ Main class not found in JAR"
  exit 1
fi

# Check that LWJGL natives are included
if ! unzip -l app/build/libs/DontLookBack-1.0-fat.jar | grep -q "META-INF/linux"; then
  echo "❌ LWJGL native libraries not found in JAR"
  exit 1
fi

echo "✅ JAR manifest and structure validation passed"

echo ""
echo "🔍 Step 5: Validating application startup..."

# Test application startup (should fail gracefully with graphics error)
OUTPUT=$(timeout 30s java -Djava.awt.headless=true -jar app/build/libs/DontLookBack-1.0-fat.jar 2>&1 || true)

# Check that the application shows expected startup messages
if ! echo "$OUTPUT" | grep -q "Don't Look Back"; then
  echo "❌ Application startup validation failed - no game banner found"
  echo "Output was: $OUTPUT"
  exit 1
fi

if ! echo "$OUTPUT" | grep -q "A Game By: Game A Day Studios"; then
  echo "❌ Application startup validation failed - no studio banner found"
  echo "Output was: $OUTPUT"
  exit 1
fi

# Check that it attempts to initialize graphics (expected to fail in headless)
if ! echo "$OUTPUT" | grep -q "LWJGL\|Graphics\|OpenGL"; then
  echo "❌ Application startup validation failed - no graphics initialization attempt"
  echo "Output was: $OUTPUT"
  exit 1
fi

echo "✅ Application startup validation passed"

echo ""
echo "🎉 All build validation checks completed successfully!"
echo "=================================="
echo "Your build is ready for PR submission."
echo ""
echo "Next steps:"
echo "  1. Run 'gradle test' to check if tests pass (optional)"
echo "  2. Commit your changes"
echo "  3. Push to your PR branch"
echo ""