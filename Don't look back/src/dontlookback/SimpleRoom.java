package dontlookback;

import java.util.ArrayList;
import java.util.List;

/**
 * Simplified Room class for testing without OpenGL dependencies
 * Simulates room content without actual rendering
 * 
 * @author Room Generator System
 */
public class SimpleRoom {
    
    private final int roomId;
    private final RoomType type;
    private final float[] center;
    private final float[] dimensions;
    
    private List<String> furniture;
    private List<String> monsters;
    
    private long lastViewedTime;
    private boolean isActive;
    private boolean isEmpty;
    
    // Room content limits based on size
    private int maxFurniture;
    private int maxMonsters;
    private int contentCount;
    
    /**
     * Creates a new room instance
     */
    public SimpleRoom(int roomId, RoomType type, float[] center) {
        this.roomId = roomId;
        this.type = type;
        this.center = center.clone();
        this.dimensions = type.getDimensions();
        
        this.furniture = new ArrayList<>();
        this.monsters = new ArrayList<>();
        
        this.lastViewedTime = System.currentTimeMillis();
        this.isActive = false;
        this.isEmpty = true;
        this.contentCount = 0;
        
        // Set content limits based on room size
        setContentLimits();
    }
    
    /**
     * Sets the maximum number of objects based on room type
     */
    private void setContentLimits() {
        switch (type) {
            case CLOSET:
                maxFurniture = 1;
                maxMonsters = 1;
                break;
            case HALLWAY:
                maxFurniture = 2;
                maxMonsters = 1;
                break;
            case SMALL_ROOM:
                maxFurniture = 4;
                maxMonsters = 2;
                break;
            case MEDIUM_ROOM:
                maxFurniture = 6;
                maxMonsters = 2;
                break;
            case LARGE_ROOM:
                maxFurniture = 10;
                maxMonsters = 3;
                break;
            case BASEMENT:
                maxFurniture = 8;
                maxMonsters = 4;
                break;
            default:
                maxFurniture = 3;
                maxMonsters = 1;
        }
    }
    
    /**
     * Generates room content when first viewed
     */
    public void generateContent() {
        if (!isEmpty) {
            return; // Content already generated
        }
        
        clearContent();
        
        // Generate furniture
        generateFurniture();
        
        // Generate monsters with lower probability
        if (Math.random() < 0.3) { // 30% chance of monsters
            generateMonsters();
        }
        
        isEmpty = false;
        updateLastViewedTime();
    }
    
    /**
     * Generates random furniture for the room
     */
    private void generateFurniture() {
        int furnitureCount = (int) (Math.random() * maxFurniture);
        String[] furnitureTypes = {"chair", "table", "bookshelf", "lamp", "cabinet", "desk"};
        
        for (int i = 0; i < furnitureCount; i++) {
            String furnitureType = furnitureTypes[(int) (Math.random() * furnitureTypes.length)];
            float[] pos = getRandomPositionInRoom();
            furniture.add(furnitureType + "_at_[" + pos[0] + "," + pos[1] + "," + pos[2] + "]");
            contentCount++;
        }
    }
    
    /**
     * Generates random monsters for the room  
     */
    private void generateMonsters() {
        int monsterCount = (int) (Math.random() * maxMonsters) + 1;
        String[] monsterTypes = {"shadow", "ghoul", "specter", "demon"};
        
        for (int i = 0; i < monsterCount; i++) {
            String monsterType = monsterTypes[(int) (Math.random() * monsterTypes.length)];
            float[] pos = getRandomPositionInRoom();
            monsters.add(monsterType + "_at_[" + pos[0] + "," + pos[1] + "," + pos[2] + "]");
            contentCount++;
        }
    }
    
    /**
     * Returns a random position within the room bounds
     */
    private float[] getRandomPositionInRoom() {
        float halfWidth = dimensions[0] / 2f;
        float halfLength = dimensions[1] / 2f;
        
        float x = center[0] + (float) (Math.random() * dimensions[0] - halfWidth);
        float y = center[1]; // Keep on floor level
        float z = center[2] + (float) (Math.random() * dimensions[1] - halfLength);
        
        return new float[]{x, y, z};
    }
    
    /**
     * Clears all room content
     */
    public void clearContent() {
        furniture.clear();
        monsters.clear();
        contentCount = 0;
        isEmpty = true;
    }
    
    /**
     * Checks if room should be deconstructed (not viewed for 5+ seconds)
     */
    public boolean shouldDeconstruct() {
        long currentTime = System.currentTimeMillis();
        return isActive && (currentTime - lastViewedTime) > 5000; // 5 seconds
    }
    
    /**
     * Updates the last viewed timestamp
     */
    public void updateLastViewedTime() {
        this.lastViewedTime = System.currentTimeMillis();
        this.isActive = true;
    }
    
    // Getters
    public int getRoomId() { return roomId; }
    public RoomType getType() { return type; }
    public float[] getCenter() { return center.clone(); }
    public float[] getDimensions() { return dimensions.clone(); }
    public boolean isActive() { return isActive; }
    public boolean isEmpty() { return isEmpty; }
    public long getLastViewedTime() { return lastViewedTime; }
    public int getContentCount() { return contentCount; }
    public List<String> getFurniture() { return new ArrayList<>(furniture); }
    public List<String> getMonsters() { return new ArrayList<>(monsters); }
    
    // State management
    public void setActive(boolean active) { this.isActive = active; }
    
    /**
     * Returns a string representation of the room
     */
    @Override
    public String toString() {
        return "Room " + roomId + " (" + type.name() + ") at [" + 
               center[0] + "," + center[1] + "," + center[2] + "] - " + 
               contentCount + " items";
    }
}