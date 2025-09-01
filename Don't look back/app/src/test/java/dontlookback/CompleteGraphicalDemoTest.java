package dontlookback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive test suite for the Complete Graphical Demo
 * 
 * Tests both headless fallback functionality and graphics integration
 * when available. Validates that all survival systems work correctly
 * in both graphical and headless modes.
 */
public class CompleteGraphicalDemoTest {
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        // Capture console output for testing
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        // Ensure headless mode for consistent testing
        System.setProperty("java.awt.headless", "true");
    }
    
    @Test
    @DisplayName("Complete Graphical Demo should run successfully in headless mode")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testCompleteGraphicalDemoHeadless() {
        assertDoesNotThrow(() -> {
            CompleteGraphicalDemo.main(new String[]{});
        }, "Complete Graphical Demo should run without throwing exceptions");
        
        String output = outputStream.toString();
        
        // Verify demo initialization
        assertTrue(output.contains("DON'T LOOK BACK - COMPLETE GRAPHICAL DEMO"), 
                   "Demo should display proper title");
        assertTrue(output.contains("Detecting graphics environment"), 
                   "Demo should attempt graphics detection");
        assertTrue(output.contains("Running in headless mode"), 
                   "Demo should detect headless environment");
        
        // Verify all systems are initialized
        assertTrue(output.contains("Initializing complete game systems"), 
                   "Demo should initialize game systems");
        assertTrue(output.contains("Light Manager initialized"), 
                   "Light management system should be initialized");
        assertTrue(output.contains("Enhanced Room Generator initialized"), 
                   "Room generation system should be initialized");
        assertTrue(output.contains("Inventory system initialized"), 
                   "Inventory system should be initialized");
        assertTrue(output.contains("Player Survival System initialized"), 
                   "Survival system should be initialized");
        
        // Verify horror elements are created
        assertTrue(output.contains("Grue initialized"), 
                   "Grue should be initialized");
        assertTrue(output.contains("Look-based monster created"), 
                   "Look-based monsters should be created");
        
        // Verify chapter system runs
        assertTrue(output.contains("CHAPTER 1: FIRST STEPS INTO DARKNESS"), 
                   "Chapter 1 should execute");
        assertTrue(output.contains("CHAPTER 2: SOMETHING IN THE SHADOWS"), 
                   "Chapter 2 should execute");
        assertTrue(output.contains("CHAPTER 3: LIGHT AND DARKNESS BATTLE"), 
                   "Chapter 3 should execute");
        
        // Verify survival mechanics work
        assertTrue(output.contains("Health:") && output.contains("Sanity:") && 
                   output.contains("Stamina:") && output.contains("Fear:"), 
                   "Survival stats should be displayed");
        
        // Verify final report
        assertTrue(output.contains("FINAL STATUS REPORT"), 
                   "Final status report should be generated");
        assertTrue(output.contains("DEMONSTRATED FEATURES"), 
                   "Feature summary should be displayed");
        assertTrue(output.contains("HORROR EXPERIENCE GOALS ACHIEVED"), 
                   "Horror goals summary should be displayed");
        
        // Verify demo completion
        assertTrue(output.contains("VALIDATION SUCCESSFUL"), 
                   "Demo should complete successfully");
    }
    
    @Test
    @DisplayName("Demo should handle graphics environment detection gracefully")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testGraphicsEnvironmentDetection() {
        // Clear headless property to test detection logic
        System.clearProperty("java.awt.headless");
        
        assertDoesNotThrow(() -> {
            CompleteGraphicalDemo.main(new String[]{});
        }, "Demo should handle graphics detection without crashing");
        
        String output = outputStream.toString();
        
        // Should either succeed with graphics or fallback to headless
        assertTrue(
            output.contains("Graphics environment detected") || 
            output.contains("Graphics initialization failed"),
            "Demo should attempt graphics initialization and report result"
        );
        
        assertTrue(
            output.contains("FULL GRAPHICAL DEMO") || 
            output.contains("HEADLESS DEMO"),
            "Demo should run in either graphical or headless mode"
        );
    }
    
    @Test
    @DisplayName("All survival systems should be properly initialized")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testSurvivalSystemsInitialization() {
        CompleteGraphicalDemo.main(new String[]{});
        String output = outputStream.toString();
        
        // Test individual system initialization
        assertTrue(output.contains("Light Manager initialized"), 
                   "Light Manager should initialize");
        assertTrue(output.contains("Enhanced Room Generator initialized"), 
                   "Enhanced Room Generator should initialize");
        assertTrue(output.contains("Inventory system initialized"), 
                   "Inventory system should initialize");
        assertTrue(output.contains("Player Survival System initialized"), 
                   "Player Survival System should initialize");
        
        // Test horror elements
        assertTrue(output.contains("Grue initialized"), 
                   "Grue should initialize");
        assertTrue(output.contains("Look-based monster created"), 
                   "Look-based monsters should be created");
        
        // Test starting equipment
        assertTrue(output.contains("Matches: 8"), 
                   "Starting matches should be set");
        assertTrue(output.contains("Candles: 3"), 
                   "Starting candles should be set");
        assertTrue(output.contains("Bandages: 2"), 
                   "Starting bandages should be set");
        assertTrue(output.contains("Flashlight: 1"), 
                   "Starting flashlight should be set");
        assertTrue(output.contains("Brass Key: 1"), 
                   "Starting brass key should be set");
    }
    
    @Test
    @DisplayName("Horror chapter system should execute all chapters")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testHorrorChapterSystem() {
        CompleteGraphicalDemo.main(new String[]{});
        String output = outputStream.toString();
        
        // Verify all chapters execute
        assertTrue(output.contains("CHAPTER 1: FIRST STEPS INTO DARKNESS"), 
                   "Chapter 1 should execute");
        assertTrue(output.contains("CHAPTER 2: SOMETHING IN THE SHADOWS"), 
                   "Chapter 2 should execute");
        assertTrue(output.contains("CHAPTER 3: LIGHT AND DARKNESS BATTLE"), 
                   "Chapter 3 should execute");
        assertTrue(output.contains("CHAPTER 4: THE PREDATORS AWAKEN"), 
                   "Chapter 4 should execute");
        assertTrue(output.contains("CHAPTER 5: BLOOD AND TERROR"), 
                   "Chapter 5 should execute");
        assertTrue(output.contains("CHAPTER 6: SANCTUARY IN THE STORM"), 
                   "Chapter 6 should execute");
        assertTrue(output.contains("CHAPTER 7: DESCENT INTO MADNESS"), 
                   "Chapter 7 should execute");
        
        // Verify chapter progression makes sense
        assertTrue(output.indexOf("CHAPTER 1") < output.indexOf("CHAPTER 2"), 
                   "Chapters should execute in order");
        assertTrue(output.indexOf("CHAPTER 2") < output.indexOf("CHAPTER 3"), 
                   "Chapters should execute in order");
        assertTrue(output.indexOf("CHAPTER 6") < output.indexOf("CHAPTER 7"), 
                   "Chapters should execute in order");
    }
    
    @Test
    @DisplayName("Survival stats should change appropriately throughout demo")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testSurvivalStatsProgression() {
        CompleteGraphicalDemo.main(new String[]{});
        String output = outputStream.toString();
        
        // Find all health percentages in the output
        String[] lines = output.split("\n");
        boolean foundHealthDecrease = false;
        boolean foundSanityDecrease = false;
        boolean foundFearIncrease = false;
        
        for (String line : lines) {
            if (line.contains("Health:") && line.contains("%")) {
                // Look for health values less than 100%
                if (line.contains("Health:") && !line.contains("100%")) {
                    foundHealthDecrease = true;
                }
            }
            if (line.contains("Sanity:") && line.contains("%")) {
                // Look for sanity values less than 100%
                if (line.contains("Sanity:") && !line.contains("100%")) {
                    foundSanityDecrease = true;
                }
            }
            if (line.contains("Fear:") && line.contains("%")) {
                // Look for fear values greater than 0%
                if (line.contains("Fear:") && !line.contains("0%")) {
                    foundFearIncrease = true;
                }
            }
        }
        
        assertTrue(foundHealthDecrease || foundSanityDecrease || foundFearIncrease, 
                   "Player stats should change during the horror experience");
        
        // Verify final outcome is reported
        assertTrue(
            output.contains("Player perished") || 
            output.contains("mind shattered") || 
            output.contains("survived") || 
            output.contains("forever changed"),
            "Demo should report a final outcome for the player"
        );
    }
    
    @Test
    @DisplayName("Demo should demonstrate all core features")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testFeatureDemonstration() {
        CompleteGraphicalDemo.main(new String[]{});
        String output = outputStream.toString();
        
        // Verify all major features are demonstrated
        assertTrue(output.contains("Health System"), 
                   "Health system should be demonstrated");
        assertTrue(output.contains("Sanity System"), 
                   "Sanity system should be demonstrated");
        assertTrue(output.contains("Stamina System"), 
                   "Stamina system should be demonstrated");
        assertTrue(output.contains("Fear System"), 
                   "Fear system should be demonstrated");
        assertTrue(output.contains("Status Effects"), 
                   "Status effects should be demonstrated");
        assertTrue(output.contains("Activity System"), 
                   "Activity system should be demonstrated");
        assertTrue(output.contains("Environmental Integration"), 
                   "Environmental integration should be demonstrated");
        assertTrue(output.contains("Horror Events"), 
                   "Horror events should be demonstrated");
        assertTrue(output.contains("Recovery Events"), 
                   "Recovery events should be demonstrated");
        assertTrue(output.contains("Light Management"), 
                   "Light management should be demonstrated");
        assertTrue(output.contains("Look-Based AI"), 
                   "Look-based AI should be demonstrated");
        assertTrue(output.contains("Room Generation"), 
                   "Room generation should be demonstrated");
        assertTrue(output.contains("Inventory System"), 
                   "Inventory system should be demonstrated");
        
        // Verify horror experience goals
        assertTrue(output.contains("Mounting tension"), 
                   "Mounting tension should be achieved");
        assertTrue(output.contains("Multiple failure conditions"), 
                   "Multiple failure conditions should be demonstrated");
        assertTrue(output.contains("Strategic resource management"), 
                   "Resource management should be demonstrated");
        assertTrue(output.contains("Risk/reward balance"), 
                   "Risk/reward balance should be demonstrated");
        assertTrue(output.contains("Emergent storytelling"), 
                   "Emergent storytelling should be demonstrated");
        assertTrue(output.contains("Real stakes"), 
                   "Real stakes should be demonstrated");
    }
    
    @Test
    @DisplayName("Demo should complete within reasonable time")
    @Timeout(value = 25, unit = TimeUnit.SECONDS)
    void testPerformance() {
        long startTime = System.currentTimeMillis();
        
        CompleteGraphicalDemo.main(new String[]{});
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 20000, 
                   "Demo should complete within 20 seconds (actual: " + duration + "ms)");
        
        String output = outputStream.toString();
        assertTrue(output.contains("VALIDATION SUCCESSFUL"), 
                   "Demo should complete successfully within time limit");
    }
    
    @Test
    @DisplayName("Output should be properly formatted and readable")
    void testOutputFormatting() {
        CompleteGraphicalDemo.main(new String[]{});
        String output = outputStream.toString();
        
        // Check for proper formatting elements
        assertTrue(output.contains("╔══════════════════════════════════════════════════════════╗"), 
                   "Output should contain formatted headers");
        assertTrue(output.contains("✅"), 
                   "Output should contain success indicators");
        assertTrue(output.contains("🎮"), 
                   "Output should contain game-related emojis");
        
        // Check that output is not empty
        assertTrue(output.length() > 1000, 
                   "Output should be substantial (at least 1000 characters)");
        
        // Check for proper line structure
        String[] lines = output.split("\n");
        assertTrue(lines.length > 50, 
                   "Output should have multiple lines of content");
    }
    
    void tearDown() {
        // Restore original output stream
        System.setOut(originalOut);
        
        // Clear system properties
        System.clearProperty("java.awt.headless");
    }
}