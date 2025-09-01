package dontlookback;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify the modernization setup works
 */
public class ModernizationBasicTest {
    
    @Test
    void testJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion, "Java version should be available");
        // Accept Java 11+ (the Gradle build specifies Java 11 as minimum)
        assertTrue(javaVersion.startsWith("17.") || javaVersion.startsWith("11.") || javaVersion.startsWith("21."), 
                  "Should be using Java 11+ LTS version, got: " + javaVersion);
        System.out.println("Java version: " + javaVersion);
    }
    
    @Test
    void testLWJGL3Available() {
        try {
            // Test that LWJGL 3.x is available
            Class.forName("org.lwjgl.Version");
            String version = org.lwjgl.Version.getVersion();
            assertNotNull(version, "LWJGL version should be available");
            assertTrue(version.startsWith("3."), "Should be using LWJGL 3.x, got: " + version);
            System.out.println("LWJGL version: " + version);
        } catch (Exception e) {
            fail("LWJGL 3.x should be available: " + e.getMessage());
        }
    }
    
    @Test
    void testModernClassesExist() {
        try {
            Class.forName("dontlookback.modern.ModernDontLookBack");
            Class.forName("dontlookback.modern.ModernGraphics");
            System.out.println("Modern classes available");
        } catch (ClassNotFoundException e) {
            fail("Modern classes should exist: " + e.getMessage());
        }
    }
    
    @Test
    void testJBox2DAvailable() {
        try {
            Class.forName("org.jbox2d.dynamics.World");
            System.out.println("JBox2D physics engine available");
        } catch (ClassNotFoundException e) {
            fail("JBox2D should be available: " + e.getMessage());
        }
    }
}