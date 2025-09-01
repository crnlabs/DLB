package dontlookback;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Manages procedural room generation and lifecycle
 * Creates rooms when viewed, destroys when not seen for 5+ seconds
 * 
 * @author Room Generator System
 */
public class RoomGenerator {
    
    private List<Room> activeRooms;
    private int nextRoomId;
    private float[] playerPosition;
    private float[] playerLookDirection;
    private float viewDistance;
    private float viewAngle;
    
    // Generation parameters
    private final float ROOM_SPACING = 25f; // Distance between room centers
    private final long DECONSTRUCT_TIME = 5000; // 5 seconds in milliseconds
    
    public RoomGenerator() {
        this.activeRooms = new ArrayList<>();
        this.nextRoomId = 1;
        this.playerPosition = new float[]{0f, 0f, 0f};
        this.playerLookDirection = new float[]{0f, 0f, 1f}; // Looking forward
        this.viewDistance = 50f; // How far player can see
        this.viewAngle = 60f; // Field of view in degrees
    }
    
    /**
     * Updates the room generator system each frame
     * Handles room generation and destruction based on player view
     */
    public void update(float[] playerPos, float[] lookDir) {
        updatePlayerState(playerPos, lookDir);
        
        // Generate new rooms if needed
        generateRoomsInView();
        
        // Update existing rooms
        updateActiveRooms();
        
        // Remove rooms that should be deconstructed
        deconstructOldRooms();
    }
    
    /**
     * Updates player position and look direction
     */
    private void updatePlayerState(float[] playerPos, float[] lookDir) {
        if (playerPos != null) {
            this.playerPosition = playerPos.clone();
        }
        if (lookDir != null) {
            this.playerLookDirection = lookDir.clone();
        }
    }
    
    /**
     * Generates rooms in the player's field of view
     */
    private void generateRoomsInView() {
        // Generate potential room positions in a grid around player
        List<float[]> potentialPositions = generatePotentialRoomPositions();
        
        for (float[] position : potentialPositions) {
            if (isPositionInView(position) && !hasRoomAtPosition(position)) {
                createRoomAtPosition(position);
            }
        }
    }
    
    /**
     * Generates a grid of potential room positions around the player
     */
    private List<float[]> generatePotentialRoomPositions() {
        List<float[]> positions = new ArrayList<>();
        
        int gridRadius = (int) (viewDistance / ROOM_SPACING) + 1;
        
        for (int x = -gridRadius; x <= gridRadius; x++) {
            for (int z = -gridRadius; z <= gridRadius; z++) {
                float roomX = playerPosition[0] + (x * ROOM_SPACING);
                float roomY = playerPosition[1]; // Same Y level as player
                float roomZ = playerPosition[2] + (z * ROOM_SPACING);
                
                positions.add(new float[]{roomX, roomY, roomZ});
            }
        }
        
        return positions;
    }
    
    /**
     * Checks if a position is within the player's field of view
     */
    private boolean isPositionInView(float[] position) {
        // Calculate distance from player
        float dx = position[0] - playerPosition[0];
        float dz = position[2] - playerPosition[2];
        float distance = (float) Math.sqrt(dx * dx + dz * dz);
        
        if (distance > viewDistance) {
            return false; // Too far away
        }
        
        // Calculate angle between look direction and position
        float[] toPosition = {dx, 0, dz};
        float dotProduct = playerLookDirection[0] * toPosition[0] + 
                          playerLookDirection[2] * toPosition[2];
        float angle = (float) Math.acos(dotProduct / (distance * 
                     Math.sqrt(playerLookDirection[0] * playerLookDirection[0] + 
                              playerLookDirection[2] * playerLookDirection[2])));
        
        // Convert to degrees
        angle = (float) Math.toDegrees(angle);
        
        return angle <= (viewAngle / 2f); // Within field of view
    }
    
    /**
     * Checks if there's already a room at or near the given position
     */
    private boolean hasRoomAtPosition(float[] position) {
        float minDistance = ROOM_SPACING * 0.8f; // Allow some overlap
        
        for (Room room : activeRooms) {
            float[] roomCenter = room.getCenter();
            float distance = calculateDistance(position, roomCenter);
            
            if (distance < minDistance) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Creates a new room at the specified position
     */
    private void createRoomAtPosition(float[] position) {
        // Randomly determine if this position should have a room
        if (Math.random() < 0.7) { // 70% chance of generating a room
            RoomType roomType = RoomType.getWeightedRandomType();
            Room newRoom = new Room(nextRoomId++, roomType, position);
            
            // Generate content immediately since it's in view
            newRoom.generateContent();
            
            activeRooms.add(newRoom);
        }
    }
    
    /**
     * Updates all active rooms
     */
    private void updateActiveRooms() {
        for (Room room : activeRooms) {
            if (isRoomInView(room)) {
                room.updateLastViewedTime();
                
                // Generate content if room is empty and now in view
                if (room.isEmpty()) {
                    room.generateContent();
                }
            }
            
            room.update();
        }
    }
    
    /**
     * Checks if a room is currently in the player's view
     */
    private boolean isRoomInView(Room room) {
        return isPositionInView(room.getCenter());
    }
    
    /**
     * Removes rooms that haven't been viewed for the deconstruct time
     */
    private void deconstructOldRooms() {
        Iterator<Room> iterator = activeRooms.iterator();
        
        while (iterator.hasNext()) {
            Room room = iterator.next();
            
            if (room.shouldDeconstruct()) {
                room.clearContent();
                iterator.remove();
            }
        }
    }
    
    /**
     * Renders all active rooms
     */
    public void renderRooms() {
        for (Room room : activeRooms) {
            room.render();
        }
    }
    
    /**
     * Forces generation of a room at a specific position with specific type
     * Useful for doors and predetermined room layouts
     */
    public Room generateSpecificRoom(float[] position, RoomType roomType) {
        Room newRoom = new Room(nextRoomId++, roomType, position);
        newRoom.generateContent();
        activeRooms.add(newRoom);
        return newRoom;
    }
    
    /**
     * Clears all rooms (useful for scene transitions)
     */
    public void clearAllRooms() {
        for (Room room : activeRooms) {
            room.clearContent();
        }
        activeRooms.clear();
    }
    
    /**
     * Calculates distance between two 3D points
     */
    private float calculateDistance(float[] pos1, float[] pos2) {
        float dx = pos1[0] - pos2[0];
        float dy = pos1[1] - pos2[1];
        float dz = pos1[2] - pos2[2];
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Gets the room at a specific position (if any)
     */
    public Room getRoomAtPosition(float[] position) {
        for (Room room : activeRooms) {
            float[] roomCenter = room.getCenter();
            float[] roomDims = room.getDimensions();
            
            // Check if position is within room bounds
            if (position[0] >= roomCenter[0] - roomDims[0]/2 &&
                position[0] <= roomCenter[0] + roomDims[0]/2 &&
                position[2] >= roomCenter[2] - roomDims[1]/2 &&
                position[2] <= roomCenter[2] + roomDims[1]/2) {
                return room;
            }
        }
        return null;
    }
    
    // Getters and configuration methods
    public List<Room> getActiveRooms() { return new ArrayList<>(activeRooms); }
    public int getActiveRoomCount() { return activeRooms.size(); }
    public void setViewDistance(float distance) { this.viewDistance = distance; }
    public void setViewAngle(float angle) { this.viewAngle = angle; }
    public float getViewDistance() { return viewDistance; }
    public float getViewAngle() { return viewAngle; }
}