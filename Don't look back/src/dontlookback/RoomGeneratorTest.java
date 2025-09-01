package dontlookback;

/**
 * Simple test class to validate room generator functionality
 * Tests basic room creation, content generation, and cleanup
 * 
 * @author Room Generator System
 */
public class RoomGeneratorTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Room Generator Test...");
        
        testRoomTypeEnum();
        testRoomCreation();
        testRoomGenerator();
        
        System.out.println("Room Generator Test completed successfully!");
    }
    
    private static void testRoomTypeEnum() {
        System.out.println("\n--- Testing RoomType Enum ---");
        
        // Test all room types
        for (RoomType type : RoomType.values()) {
            System.out.println("Room Type: " + type.name() + 
                              " - ID: " + type.getTypeId() + 
                              " - Dimensions: " + type.getWidth() + "x" + 
                              type.getLength() + "x" + type.getHeight());
        }
        
        // Test random generation
        System.out.println("\nRandom room types:");
        for (int i = 0; i < 5; i++) {
            System.out.println("Random: " + RoomType.getRandomType().name());
            System.out.println("Weighted: " + RoomType.getWeightedRandomType().name());
        }
    }
    
    private static void testRoomCreation() {
        System.out.println("\n--- Testing Room Creation ---");
        
        // Create a test room
        float[] center = {10f, 0f, 10f};
        Room testRoom = new Room(1, RoomType.MEDIUM_ROOM, center);
        
        System.out.println("Created room with ID: " + testRoom.getRoomId());
        System.out.println("Room type: " + testRoom.getType().name());
        System.out.println("Room center: [" + testRoom.getCenter()[0] + ", " + 
                          testRoom.getCenter()[1] + ", " + testRoom.getCenter()[2] + "]");
        System.out.println("Room is empty: " + testRoom.isEmpty());
        
        // Generate content
        testRoom.generateContent();
        System.out.println("After generating content - Room is empty: " + testRoom.isEmpty());
        System.out.println("Room contents count: " + testRoom.getRoomContents().size());
        
        // Test deconstruction timing
        System.out.println("Should deconstruct: " + testRoom.shouldDeconstruct());
        
        // Wait a moment to test timing (simulated)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Clear content
        testRoom.clearContent();
        System.out.println("After clearing - Room is empty: " + testRoom.isEmpty());
    }
    
    private static void testRoomGenerator() {
        System.out.println("\n--- Testing RoomGenerator ---");
        
        RoomGenerator generator = new RoomGenerator();
        System.out.println("Created room generator");
        System.out.println("Initial active rooms: " + generator.getActiveRoomCount());
        
        // Simulate player position and look direction
        float[] playerPos = {0f, 0f, 0f};
        float[] lookDir = {0f, 0f, 1f}; // Looking forward
        
        // Update generator (this should create rooms in view)
        generator.update(playerPos, lookDir);
        System.out.println("After first update - Active rooms: " + generator.getActiveRoomCount());
        
        // Create a specific room
        Room specificRoom = generator.generateSpecificRoom(
            new float[]{20f, 0f, 20f}, RoomType.LARGE_ROOM);
        System.out.println("Created specific room with ID: " + specificRoom.getRoomId());
        System.out.println("Total active rooms: " + generator.getActiveRoomCount());
        
        // Test room lookup
        Room foundRoom = generator.getRoomAtPosition(new float[]{20f, 0f, 20f});
        if (foundRoom != null) {
            System.out.println("Found room at position with ID: " + foundRoom.getRoomId());
        } else {
            System.out.println("No room found at test position");
        }
        
        // Clear all rooms
        generator.clearAllRooms();
        System.out.println("After clearing all - Active rooms: " + generator.getActiveRoomCount());
    }
}