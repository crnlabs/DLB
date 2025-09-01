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
    @DisplayName("Test simple room functionality")
    void testSimpleRoom() {
        assertDoesNotThrow(() -> {
            // Create a simple room with required parameters
            float[] center = {0.0f, 0.0f, 0.0f};
            SimpleRoom room = new SimpleRoom(1, RoomType.SMALL_ROOM, center);
            assertNotNull(room);
            assertEquals(1, room.getRoomId());
        });
    }
}