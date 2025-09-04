package dontlookback.demo;

/**
 * Enhanced demonstration of the room generator system
 * Shows room generation, content creation, and cleanup in action
 * 
 * @author Room Generator System
 */
public class RoomGeneratorDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Room Generator Demonstration ===\n");
        
        demonstrateBasicGeneration();
        demonstrateViewBasedGeneration();
        demonstrateCleanupSystem();
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    private static void demonstrateBasicGeneration() {
        System.out.println("--- Basic Room Generation ---");
        
        RoomGenerator generator = new RoomGenerator();
        System.out.println("Created room generator. Active rooms: " + generator.getActiveRoomCount());
        
        // Simulate player looking in different directions
        float[] playerPos = {0f, 0f, 0f};
        float[] lookForward = {0f, 0f, 1f};
        float[] lookRight = {1f, 0f, 0f};
        
        // Look forward - should generate rooms
        generator.update(playerPos, lookForward);
        System.out.println("After looking forward: " + generator.getActiveRoomCount() + " active rooms");
        
        // Look right - should generate more rooms
        generator.update(playerPos, lookRight);
        System.out.println("After looking right: " + generator.getActiveRoomCount() + " active rooms");
        
        // List some rooms
        int count = 0;
        for (Room room : generator.getActiveRooms()) {
            if (count < 3) { // Show first 3 rooms
                float[] center = room.getCenter();
                System.out.println("  Room " + room.getRoomId() + ": " + room.getType().name() + 
                                  " at [" + center[0] + ", " + center[1] + ", " + center[2] + "]");
                count++;
            }
        }
        
        System.out.println();
    }
    
    private static void demonstrateViewBasedGeneration() {
        System.out.println("--- View-Based Room Generation ---");
        
        RoomGenerator generator = new RoomGenerator();
        
        // Test positions at different distances and angles
        float[][] testPositions = {
            {0f, 0f, 0f},      // Origin
            {10f, 0f, 10f},    // Close
            {30f, 0f, 30f},    // Medium distance
            {100f, 0f, 100f}   // Far (should not generate)
        };
        
        float[] lookDiagonal = {0.707f, 0f, 0.707f}; // 45 degree angle
        
        for (float[] pos : testPositions) {
            int beforeCount = generator.getActiveRoomCount();
            generator.update(pos, lookDiagonal);
            int afterCount = generator.getActiveRoomCount();
            
            System.out.println("From position [" + pos[0] + ", " + pos[1] + ", " + pos[2] + "]: " +
                              (afterCount - beforeCount) + " new rooms generated");
        }
        
        System.out.println("Total rooms after view test: " + generator.getActiveRoomCount());
        System.out.println();
    }
    
    private static void demonstrateCleanupSystem() {
        System.out.println("--- Cleanup System Demonstration ---");
        
        // Create a room and show its lifecycle
        SimpleRoom testRoom = new SimpleRoom(999, RoomType.SMALL_ROOM, new float[]{5f, 0f, 5f});
        
        System.out.println("Created test room " + testRoom.getRoomId());
        System.out.println("Room is empty: " + testRoom.isEmpty());
        System.out.println("Should deconstruct: " + testRoom.shouldDeconstruct());
        
        // Generate content
        testRoom.generateContent();
        System.out.println("After generating content:");
        System.out.println("  Room is empty: " + testRoom.isEmpty());
        System.out.println("  Content count: " + testRoom.getContentCount());
        System.out.println("  Furniture: " + testRoom.getFurniture());
        System.out.println("  Monsters: " + testRoom.getMonsters());
        System.out.println("  Should deconstruct: " + testRoom.shouldDeconstruct());
        
        // Simulate time passing
        try {
            System.out.println("Waiting 1 second...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        System.out.println("After 1 second:");
        System.out.println("  Should deconstruct: " + testRoom.shouldDeconstruct());
        
        // Update view time
        testRoom.updateLastViewedTime();
        System.out.println("After updating view time:");
        System.out.println("  Should deconstruct: " + testRoom.shouldDeconstruct());
        
        // Test room positioning
        float[] testPos = {5f, 0f, 5f}; // Center of room
        float[] dims = testRoom.getDimensions();
        boolean inRoom = isPositionInRoom(testPos, testRoom.getCenter(), dims);
        System.out.println("Position [5, 0, 5] is in room: " + inRoom);
        
        testPos = new float[]{20f, 0f, 20f}; // Outside room
        inRoom = isPositionInRoom(testPos, testRoom.getCenter(), dims);
        System.out.println("Position [20, 0, 20] is in room: " + inRoom);
        
        // Clean up
        testRoom.clearContent();
        System.out.println("After cleanup - Room is empty: " + testRoom.isEmpty());
        System.out.println();
    }
    
    /**
     * Helper method to check if a position is within room bounds
     */
    private static boolean isPositionInRoom(float[] position, float[] roomCenter, float[] roomDims) {
        float halfWidth = roomDims[0] / 2f;
        float halfLength = roomDims[1] / 2f;
        
        return (position[0] >= roomCenter[0] - halfWidth &&
                position[0] <= roomCenter[0] + halfWidth &&
                position[2] >= roomCenter[2] - halfLength &&
                position[2] <= roomCenter[2] + halfLength);
    }
}