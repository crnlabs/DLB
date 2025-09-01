package dontlookback.modern;

import org.lwjgl.Version;

/**
 * Simple headless demo to verify LWJGL 3.x integration
 * This can be run in CI environments without a display
 */
public class HeadlessDemo {
    
    public static void main(String[] args) {
        System.out.println("Don't Look Back - Modern Headless Demo");
        System.out.println("=====================================");
        
        try {
            // Test LWJGL 3.x availability
            String lwjglVersion = Version.getVersion();
            System.out.println("✓ LWJGL version: " + lwjglVersion);
            
            // Verify it's version 3.x
            if (lwjglVersion.startsWith("3.")) {
                System.out.println("✓ Modern LWJGL 3.x detected");
            } else {
                System.err.println("✗ Expected LWJGL 3.x, got: " + lwjglVersion);
                System.exit(1);
            }
            
            // Test JBox2D availability
            try {
                Class.forName("org.jbox2d.dynamics.World");
                System.out.println("✓ JBox2D physics engine available");
            } catch (ClassNotFoundException e) {
                System.err.println("✗ JBox2D not found: " + e.getMessage());
                System.exit(1);
            }
            
            // Basic success
            System.out.println("✓ All modern dependencies verified");
            System.out.println("✓ Modernization successful!");
            
        } catch (Exception e) {
            System.err.println("✗ Error during demo: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}