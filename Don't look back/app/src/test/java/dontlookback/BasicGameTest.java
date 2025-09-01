package dontlookback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for the game functionality
 */
public class BasicGameTest {

    @BeforeEach
    void setUp() {
        // Set headless mode for testing
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    @DisplayName("Test basic game classes can be instantiated")
    void testBasicClasses() {
        // Test that basic game classes exist and can be instantiated
        assertDoesNotThrow(() -> {
            // These should not throw exceptions in headless mode
            new Player();
            new Light();
            new StaticList();
        });
    }

    @Test
    @DisplayName("Test cube array functionality")
    void testCubeArray() {
        assertDoesNotThrow(() -> {
            cubeArray cubes = new cubeArray();
            assertNotNull(cubes);
        });
    }

    @Test
    @DisplayName("Test entities system")
    void testEntities() {
        assertDoesNotThrow(() -> {
            Entities entities = new Entities();
            assertNotNull(entities);
        });
    }

    @Test
    @DisplayName("Test quadruped entity")
    void testQuadruped() {
        assertDoesNotThrow(() -> {
            Quadraped quad = new Quadraped();
            assertNotNull(quad);
        });
    }

    @Test
    @DisplayName("Test simple room functionality")
    void testSimpleRoom() {
        assertDoesNotThrow(() -> {
            SimpleRoom room = new SimpleRoom();
            assertNotNull(room);
        });
    }
}