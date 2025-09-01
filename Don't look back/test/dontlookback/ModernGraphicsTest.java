package dontlookback;

import dontlookback.modern.ModernGraphics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test for the modern graphics system
 */
public class ModernGraphicsTest {
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        // Capture system output for testing
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        // Set headless mode for CI testing
        System.setProperty("java.awt.headless", "true");
    }
    
    void tearDown() {
        // Restore original output
        System.setOut(originalOut);
    }
    
    @Test
    @Timeout(10) // 10 second timeout
    void testModernGraphicsInitialization() {
        try {
            // Test basic initialization without full window creation
            System.out.println("Testing modern graphics initialization...");
            
            // Since we can't create OpenGL context in headless mode,
            // we'll test that the class exists and imports work
            Class<?> modernGraphicsClass = Class.forName("dontlookback.modern.ModernGraphics");
            assertNotNull(modernGraphicsClass, "ModernGraphics class should exist");
            
            Class<?> modernMainClass = Class.forName("dontlookback.modern.ModernDontLookBack");
            assertNotNull(modernMainClass, "ModernDontLookBack class should exist");
            
            // Verify LWJGL 3.x classes are available
            Class.forName("org.lwjgl.glfw.GLFW");
            Class.forName("org.lwjgl.opengl.GL11");
            Class.forName("org.lwjgl.Version");
            
            System.out.println("Modern graphics classes loaded successfully");
            
        } catch (ClassNotFoundException e) {
            fail("Required modern graphics classes not found: " + e.getMessage());
        } finally {
            tearDown();
        }
    }
    
    @Test
    void testLWJGL3DependenciesAvailable() {
        try {
            // Test that LWJGL 3.x dependencies are properly loaded
            Class.forName("org.lwjgl.glfw.GLFW");
            Class.forName("org.lwjgl.opengl.GL11");
            Class.forName("org.lwjgl.opengl.GL20");
            Class.forName("org.lwjgl.system.MemoryStack");
            Class.forName("org.lwjgl.Version");
            
            // Test that we can access version info
            String version = org.lwjgl.Version.getVersion();
            assertNotNull(version, "LWJGL version should be available");
            assertTrue(version.startsWith("3."), "Should be using LWJGL 3.x, got: " + version);
            
            System.out.println("LWJGL 3.x version: " + version);
            
        } catch (ClassNotFoundException e) {
            fail("LWJGL 3.x classes not found: " + e.getMessage());
        } finally {
            tearDown();
        }
    }
    
    @Test
    void testModernPhysicsDependenciesAvailable() {
        try {
            // Test that modern physics engine is available
            Class.forName("org.jbox2d.dynamics.World");
            Class.forName("org.jbox2d.common.Vec2");
            
            System.out.println("Modern physics engine (JBox2D) available");
            
        } catch (ClassNotFoundException e) {
            fail("Modern physics engine classes not found: " + e.getMessage());
        } finally {
            tearDown();
        }
    }
    
    @Test
    void testBackwardCompatibility() {
        try {
            // Ensure legacy classes still exist for compatibility
            Class.forName("dontlookback.DontLookBack");
            Class.forName("dontlookback.DLB_Graphics");
            Class.forName("dontlookback.Settings");
            
            System.out.println("Legacy classes still available for compatibility");
            
        } catch (ClassNotFoundException e) {
            fail("Legacy compatibility classes not found: " + e.getMessage());
        } finally {
            tearDown();
        }
    }
}