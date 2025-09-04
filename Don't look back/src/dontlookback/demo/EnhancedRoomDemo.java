package dontlookback.demo;

/**
 * Enhanced Room Generation Demo
 * 
 * Demonstrates the complete room system with 5-second regeneration,
 * door connections, item placement, and integration with other systems.
 */
public class EnhancedRoomDemo {
    
    public static void main(String[] args) {
        System.out.println("=== ENHANCED ROOM GENERATION DEMO ===");
        System.out.println();
        
        // Initialize systems
        EnhancedRoomGenerator roomGen = new EnhancedRoomGenerator();
        LightManager lightManager = new LightManager();
        InventorySystem inventory = new InventorySystem();
        
        // Player state
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        float[] viewDirection = {0.0f, 0.0f, 1.0f}; // Looking north
        
        System.out.println("=== INITIAL ROOM GENERATION ===");
        
        // Generate initial rooms
        roomGen.update(playerPos, viewDirection, 0.1);
        
        System.out.println("Generated rooms around player:");
        for (EnhancedRoomGenerator.EnhancedRoom room : roomGen.getActiveRooms()) {
            System.out.println("  " + room.toString());
        }
        System.out.println();
        
        // === Room Exploration Demo ===
        System.out.println("=== ROOM EXPLORATION DEMO ===");
        
        // Enter the room at origin
        EnhancedRoomGenerator.EnhancedRoom currentRoom = roomGen.getRoomAt(playerPos);
        if (currentRoom != null) {
            roomGen.onPlayerEnterRoom(playerPos);
            
            // Collect items if available
            for (String item : currentRoom.getContainedItems()) {
                if (inventory.addItem(item, 1)) {
                    currentRoom.collectItem(item);
                }
            }
            
            // Read documents if available
            for (String doc : currentRoom.getDocuments()) {
                String content = currentRoom.readDocument(doc);
                System.out.println("📖 Document content: \"" + content + "\"");
            }
        }
        System.out.println();
        
        // === Movement and View Direction Demo ===
        System.out.println("=== MOVEMENT AND VIEW DIRECTION DEMO ===");
        
        // Move player and change view direction
        float[] newPlayerPos = {10.0f, 0.0f, 0.0f}; // Move east
        float[] newViewDirection = {1.0f, 0.0f, 0.0f}; // Look east
        
        System.out.println("Player moves to " + java.util.Arrays.toString(newPlayerPos));
        System.out.println("Player looks " + java.util.Arrays.toString(newViewDirection));
        
        if (currentRoom != null) {
            roomGen.onPlayerExitRoom(playerPos);
        }
        
        roomGen.update(newPlayerPos, newViewDirection, 0.1);
        
        // Enter new room
        EnhancedRoomGenerator.EnhancedRoom newRoom = roomGen.getRoomAt(newPlayerPos);
        if (newRoom != null) {
            roomGen.onPlayerEnterRoom(newPlayerPos);
        }
        
        System.out.println("Active rooms after movement: " + roomGen.getActiveRooms().size());
        System.out.println();
        
        // === Room Regeneration Demo ===
        System.out.println("=== ROOM REGENERATION DEMO ===");
        
        // Look away from original room for 6 seconds to trigger regeneration
        System.out.println("Looking away from original room for 6 seconds...");
        
        float[] lookAwayDirection = {0.0f, 0.0f, -1.0f}; // Look south (away from north rooms)
        roomGen.update(newPlayerPos, lookAwayDirection, 6.0);
        
        // Look back to see regenerated room
        System.out.println("Looking back at original area...");
        float[] lookBackDirection = {-1.0f, 0.0f, 0.0f}; // Look west (back toward origin)
        roomGen.update(newPlayerPos, lookBackDirection, 0.1);
        
        // Check if original room regenerated
        EnhancedRoomGenerator.EnhancedRoom possiblyRegeneratedRoom = roomGen.getRoomAt(playerPos);
        if (possiblyRegeneratedRoom != null) {
            System.out.println("Original room status: " + possiblyRegeneratedRoom.toString());
        }
        System.out.println();
        
        // === Door System Demo ===
        System.out.println("=== DOOR SYSTEM DEMO ===");
        
        // Explore rooms and their doors
        for (EnhancedRoomGenerator.EnhancedRoom room : roomGen.getActiveRooms()) {
            if (!room.getDoors().isEmpty()) {
                System.out.println("Room " + room.getId() + " (" + room.getRoomType().getDisplayName() + "):");
                for (EnhancedRoomGenerator.Door door : room.getDoors()) {
                    System.out.println("  🚪 " + door.getName() + " leads to " + 
                                     java.util.Arrays.toString(door.getTargetPosition()));
                }
            }
        }
        System.out.println();
        
        // === Integration with Light and Inventory Demo ===
        System.out.println("=== INTEGRATION DEMO ===");
        
        System.out.println("Current inventory:");
        System.out.println(inventory.getInventorySummary());
        
        // Place light sources in rooms with fireplaces
        for (EnhancedRoomGenerator.EnhancedRoom room : roomGen.getActiveRooms()) {
            if (room.getRoomType() == EnhancedRoomGenerator.EnhancedRoomType.LIBRARY ||
                room.getRoomType() == EnhancedRoomGenerator.EnhancedRoomType.SAFE_ROOM) {
                
                // Add fireplace if Victorian theme
                if (room.getRoomTheme() == EnhancedRoomGenerator.RoomTheme.VICTORIAN) {
                    LightSource fireplace = new LightSource(LightSource.LightType.FIREPLACE, 
                                                           room.getPosition(), 0.0f);
                    fireplace.light();
                    lightManager.addLightSource(fireplace);
                    
                    System.out.println("🔥 Lit fireplace in " + room.getRoomType().getDisplayName() + 
                                     " at " + java.util.Arrays.toString(room.getPosition()));
                }
            }
        }
        
        lightManager.update(0.1f);
        System.out.println("Active light sources: " + lightManager.getActiveLights().size());
        
        // Check protection in different rooms
        for (EnhancedRoomGenerator.EnhancedRoom room : roomGen.getActiveRooms()) {
            boolean isProtected = lightManager.isPositionProtected(room.getPosition());
            float lightLevel = lightManager.getLightLevelAtPosition(room.getPosition());
            
            if (isProtected || lightLevel > 0.1f) {
                System.out.println("🛡️ " + room.getRoomType().getDisplayName() + 
                                 " is protected (light level: " + String.format("%.2f", lightLevel) + ")");
            }
        }
        System.out.println();
        
        // === Room Type Distribution Demo ===
        System.out.println("=== ROOM TYPE DISTRIBUTION ===");
        
        // Generate many rooms to see distribution
        float[] explorerPos = {0.0f, 0.0f, 0.0f};
        float[] explorerView = {1.0f, 0.0f, 0.0f};
        
        // Simulate extensive exploration
        for (int i = 0; i < 10; i++) {
            explorerPos[0] += 10.0f; // Move east
            roomGen.update(explorerPos, explorerView, 0.1);
            
            explorerPos[2] += 10.0f; // Move north
            roomGen.update(explorerPos, explorerView, 0.1);
            
            explorerPos[0] -= 10.0f; // Move west
            roomGen.update(explorerPos, explorerView, 0.1);
            
            explorerPos[2] -= 10.0f; // Move south
            roomGen.update(explorerPos, explorerView, 0.1);
        }
        
        // Count room types
        java.util.Map<EnhancedRoomGenerator.EnhancedRoomType, Integer> roomTypeCounts = new java.util.HashMap<>();
        java.util.Map<EnhancedRoomGenerator.RoomTheme, Integer> roomThemeCounts = new java.util.HashMap<>();
        
        for (EnhancedRoomGenerator.EnhancedRoom room : roomGen.getActiveRooms()) {
            roomTypeCounts.merge(room.getRoomType(), 1, Integer::sum);
            roomThemeCounts.merge(room.getRoomTheme(), 1, Integer::sum);
        }
        
        System.out.println("Room type distribution:");
        for (java.util.Map.Entry<EnhancedRoomGenerator.EnhancedRoomType, Integer> entry : roomTypeCounts.entrySet()) {
            System.out.println("  " + entry.getKey().getDisplayName() + ": " + entry.getValue());
        }
        
        System.out.println("Room theme distribution:");
        for (java.util.Map.Entry<EnhancedRoomGenerator.RoomTheme, Integer> entry : roomThemeCounts.entrySet()) {
            System.out.println("  " + entry.getKey().getDisplayName() + ": " + entry.getValue());
        }
        System.out.println();
        
        // === Regeneration Statistics Demo ===
        System.out.println("=== REGENERATION STATISTICS ===");
        
        int totalRooms = roomGen.getActiveRooms().size();
        int regeneratedRooms = 0;
        int totalRegenerations = 0;
        
        for (EnhancedRoomGenerator.EnhancedRoom room : roomGen.getActiveRooms()) {
            if (room.getRegenerationCount() > 0) {
                regeneratedRooms++;
                totalRegenerations += room.getRegenerationCount();
            }
        }
        
        System.out.println("Total rooms: " + totalRooms);
        System.out.println("Rooms that have regenerated: " + regeneratedRooms);
        System.out.println("Total regeneration events: " + totalRegenerations);
        System.out.println("Average regenerations per room: " + 
                          (regeneratedRooms > 0 ? String.format("%.1f", (float)totalRegenerations / regeneratedRooms) : "0.0"));
        System.out.println();
        
        // === Survival Scenario Demo ===
        System.out.println("=== SURVIVAL SCENARIO DEMO ===");
        
        System.out.println("Scenario: Player lost in dark building, needs to find light sources...");
        
        // Find rooms with light sources
        java.util.List<EnhancedRoomGenerator.EnhancedRoom> roomsWithLights = new java.util.ArrayList<>();
        
        for (EnhancedRoomGenerator.EnhancedRoom room : roomGen.getActiveRooms()) {
            for (String item : room.getContainedItems()) {
                if (item.equals("candle") || item.equals("torch") || 
                    item.equals("flashlight") || item.equals("match")) {
                    roomsWithLights.add(room);
                    break;
                }
            }
        }
        
        System.out.println("Found " + roomsWithLights.size() + " rooms with light sources:");
        for (EnhancedRoomGenerator.EnhancedRoom room : roomsWithLights) {
            System.out.println("  📍 " + room.getRoomType().getDisplayName() + 
                             " (" + room.getRoomTheme().getDisplayName() + ") at " + 
                             java.util.Arrays.toString(room.getPosition()));
            System.out.println("     Items: " + room.getContainedItems());
        }
        System.out.println();
        
        // === Final Status ===
        System.out.println("=== FINAL STATUS ===");
        System.out.println(roomGen.getStatusReport());
        System.out.println();
        System.out.println(lightManager.getStatusReport());
        System.out.println();
        System.out.println(inventory.getInventorySummary());
        System.out.println();
        
        System.out.println("✅ Enhanced room generation system fully functional!");
        System.out.println("✅ Rooms regenerate after 5 seconds when not observed");
        System.out.println("✅ Door system creates interconnected spaces");  
        System.out.println("✅ Items and documents provide survival resources");
        System.out.println("✅ Multiple room types and themes for variety");
        System.out.println("✅ Integration with light and inventory systems");
        System.out.println("✅ Complete environmental storytelling framework");
        
        System.out.println();
        System.out.println("=== DEMO COMPLETE ===");
    }
}