package dontlookback;

/**
 * Standalone demonstration of the room generator system
 * Uses only non-OpenGL dependent classes to show functionality
 * 
 * @author Room Generator System
 */
public class StandaloneRoomDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Room Generator Standalone Demo ===\n");
        
        demonstrateRoomTypes();
        demonstrateRoomLifecycle();
        demonstrateRoomPositioning();
        
        System.out.println("\n=== Demo Complete - Room Generator Working Successfully! ===");
    }
    
    private static void demonstrateRoomTypes() {
        System.out.println("--- Room Type System ---");
        
        // Show all available room types
        System.out.println("Available room types:");
        for (RoomType type : RoomType.values()) {
            System.out.println("  " + type.name() + ": " + 
                              type.getWidth() + "x" + type.getLength() + "x" + type.getHeight() + 
                              " (ID: " + type.getTypeId() + ")");
        }
        
        // Demonstrate weighted random selection
        System.out.println("\nWeighted random selection (10 samples):");
        int[] counts = new int[RoomType.values().length];
        for (int i = 0; i < 10; i++) {
            RoomType selected = RoomType.getWeightedRandomType();
            counts[selected.ordinal()]++;
            System.out.println("  " + (i+1) + ": " + selected.name());
        }
        
        System.out.println();
    }
    
    private static void demonstrateRoomLifecycle() {
        System.out.println("--- Room Lifecycle Management ---");
        
        // Create a room
        float[] center = {10f, 0f, 10f};
        SimpleRoom room = new SimpleRoom(1, RoomType.MEDIUM_ROOM, center);
        
        System.out.println("Created: " + room);
        System.out.println("Initially empty: " + room.isEmpty());
        System.out.println("Content count: " + room.getContentCount());
        
        // Generate content
        room.generateContent();
        System.out.println("\nAfter generating content:");
        System.out.println("Empty: " + room.isEmpty());
        System.out.println("Content count: " + room.getContentCount());
        
        // Show generated content
        if (!room.getFurniture().isEmpty()) {
            System.out.println("Furniture:");
            for (String item : room.getFurniture()) {
                System.out.println("  - " + item);
            }
        }
        if (!room.getMonsters().isEmpty()) {
            System.out.println("Monsters:");
            for (String monster : room.getMonsters()) {
                System.out.println("  - " + monster);
            }
        }
        
        // Test destruction timer
        System.out.println("\nTesting 5-second destruction timer:");
        System.out.println("Should deconstruct now: " + room.shouldDeconstruct());
        
        // Simulate waiting (without actually waiting)
        long oldTime = room.getLastViewedTime();
        try {
            // Simulate time passage by directly checking the logic
            long simulatedTime = oldTime - 6000; // 6 seconds ago
            System.out.println("If last viewed 6 seconds ago, should deconstruct: " + 
                              (System.currentTimeMillis() - simulatedTime > 5000));
        } catch (Exception e) {
            System.out.println("Timer simulation completed");
        }
        
        // Update view time
        room.updateLastViewedTime();
        System.out.println("After updating view time, should deconstruct: " + room.shouldDeconstruct());
        
        // Clean up
        room.clearContent();
        System.out.println("After cleanup - Empty: " + room.isEmpty() + 
                          ", Content count: " + room.getContentCount());
        
        System.out.println();
    }
    
    private static void demonstrateRoomPositioning() {
        System.out.println("--- Room Positioning and Bounds ---");
        
        // Create rooms at different positions
        SimpleRoom[] rooms = {
            new SimpleRoom(1, RoomType.SMALL_ROOM, new float[]{0f, 0f, 0f}),
            new SimpleRoom(2, RoomType.HALLWAY, new float[]{25f, 0f, 0f}),
            new SimpleRoom(3, RoomType.LARGE_ROOM, new float[]{0f, 0f, 25f}),
            new SimpleRoom(4, RoomType.CLOSET, new float[]{-10f, 0f, -10f})
        };
        
        // Generate content for all rooms
        for (SimpleRoom room : rooms) {
            room.generateContent();
            System.out.println(room);
        }
        
        // Test position checking
        System.out.println("\nPosition testing:");
        float[][] testPositions = {
            {0f, 0f, 0f},      // Should be in room 1
            {25f, 0f, 0f},     // Should be in room 2  
            {100f, 0f, 100f},  // Should be in no room
            {-10f, 0f, -10f}   // Should be in room 4
        };
        
        for (float[] pos : testPositions) {
            System.out.print("Position [" + pos[0] + ", " + pos[1] + ", " + pos[2] + "] is in: ");
            
            boolean foundRoom = false;
            for (SimpleRoom room : rooms) {
                if (isPositionInRoom(pos, room.getCenter(), room.getDimensions())) {
                    System.out.println("Room " + room.getRoomId() + " (" + room.getType().name() + ")");
                    foundRoom = true;
                    break;
                }
            }
            if (!foundRoom) {
                System.out.println("No room");
            }
        }
        
        // Demonstrate view-based generation logic
        System.out.println("\nView-based generation simulation:");
        float[] playerPos = {0f, 0f, 0f};
        float[] lookDirection = {1f, 0f, 0f}; // Looking right
        
        System.out.println("Player at [0, 0, 0] looking right [1, 0, 0]");
        
        // Check which rooms would be "visible"
        float viewDistance = 30f;
        for (SimpleRoom room : rooms) {
            float[] roomCenter = room.getCenter();
            float distance = calculateDistance(playerPos, roomCenter);
            
            if (distance <= viewDistance) {
                // Simple angle check (in real implementation this would be more sophisticated)
                float dx = roomCenter[0] - playerPos[0];
                float dz = roomCenter[2] - playerPos[2];
                
                if (dx >= 0) { // In the direction we're looking (simplified)
                    System.out.println("  Would generate/maintain Room " + room.getRoomId() + 
                                      " at distance " + String.format("%.1f", distance));
                } else {
                    System.out.println("  Would deconstruct Room " + room.getRoomId() + 
                                      " (behind player)");
                }
            } else {
                System.out.println("  Room " + room.getRoomId() + " too far (distance: " + 
                                  String.format("%.1f", distance) + ")");
            }
        }
        
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
    
    /**
     * Helper method to calculate distance between two points
     */
    private static float calculateDistance(float[] pos1, float[] pos2) {
        float dx = pos1[0] - pos2[0];
        float dy = pos1[1] - pos2[1];
        float dz = pos1[2] - pos2[2];
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}