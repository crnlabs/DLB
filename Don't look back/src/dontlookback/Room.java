package dontlookback;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single room instance in the game world
 * Manages room content, visibility state, and lifecycle
 * 
 * @author Room Generator System
 */
public class Room {
    
    private final int roomId;
    private final RoomType type;
    private final float[] center;
    private final float[] dimensions;
    
    private StaticList roomContents;
    private List<Objects> furniture;
    private List<Objects> monsters;
    
    private long lastViewedTime;
    private boolean isActive;
    private boolean isEmpty;
    
    // Room content limits based on size
    private int maxFurniture;
    private int maxMonsters;
    
    /**
     * Creates a new room instance
     */
    public Room(int roomId, RoomType type, float[] center) {
        this.roomId = roomId;
        this.type = type;
        this.center = center.clone();
        this.dimensions = type.getDimensions();
        
        this.roomContents = new StaticList();
        this.furniture = new ArrayList<>();
        this.monsters = new ArrayList<>();
        
        this.lastViewedTime = System.currentTimeMillis();
        this.isActive = false;
        this.isEmpty = true;
        
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
        
        for (int i = 0; i < furnitureCount; i++) {
            try {
                // Create furniture as colored cubes for now
                Cube furnitureItem = new Cube();
                
                // Position randomly within room bounds
                float[] pos = getRandomPositionInRoom();
                furnitureItem.setCenter(pos);
                
                // Set color to indicate it's furniture
                furnitureItem.setRGB(new float[]{0.6f, 0.4f, 0.2f}); // Brown-ish
                
                // Only set up VBO if not in headless mode
                if (!isHeadlessMode()) {
                    furnitureItem.setUpVBO();
                }
                
                furniture.add(furnitureItem);
                roomContents.add(furnitureItem);
            } catch (Exception e) {
                // Handle headless mode gracefully - skip VBO setup but keep furniture data
                System.out.println("Warning: Skipping furniture VBO setup (headless mode): " + e.getMessage());
            }
        }
    }
    
    /**
     * Generates random monsters for the room  
     */
    private void generateMonsters() {
        int monsterCount = (int) (Math.random() * maxMonsters) + 1;
        
        for (int i = 0; i < monsterCount; i++) {
            try {
                // Create monsters as dark cubes for now
                Cube monster = new Cube();
                
                // Position randomly within room bounds
                float[] pos = getRandomPositionInRoom();
                monster.setCenter(pos);
                
                // Set color to indicate it's a monster
                monster.setRGB(new float[]{0.1f, 0.1f, 0.1f}); // Dark
                
                // Only set up VBO if not in headless mode
                if (!isHeadlessMode()) {
                    monster.setUpVBO();
                }
                
                monsters.add(monster);
                roomContents.add(monster);
            } catch (Exception e) {
                // Handle headless mode gracefully - skip VBO setup but keep monster data
                System.out.println("Warning: Skipping monster VBO setup (headless mode): " + e.getMessage());
            }
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
        // Delete VBOs for proper cleanup (only if not in headless mode)
        if (!isHeadlessMode()) {
            for (Objects obj : furniture) {
                try {
                    obj.delete();
                } catch (Exception e) {
                    System.out.println("Warning: Skipping VBO deletion (headless mode): " + e.getMessage());
                }
            }
            for (Objects obj : monsters) {
                try {
                    obj.delete();
                } catch (Exception e) {
                    System.out.println("Warning: Skipping VBO deletion (headless mode): " + e.getMessage());
                }
            }
        }
        
        furniture.clear();
        monsters.clear();
        roomContents = new StaticList();
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
    
    /**
     * Renders the room and its contents
     */
    public void render() {
        if (!isActive || isEmpty) {
            return;
        }
        
        // Skip rendering in headless mode
        if (isHeadlessMode()) {
            return;
        }
        
        // Render room structure (walls, floor, ceiling)
        renderRoomStructure();
        
        // Render room contents
        roomContents.render();
    }
    
    /**
     * Renders the basic room structure using Shapes class
     */
    private void renderRoomStructure() {
        // Use the existing Shapes.renderRoom method
        Shapes.renderRoom(center, type.getTypeId());
    }
    
    /**
     * Updates room contents behavior
     */
    public void update() {
        if (!isActive || isEmpty) {
            return;
        }
        
        // Skip updating room contents in headless mode to avoid OpenGL calls
        if (!isHeadlessMode()) {
            roomContents.update();
        }
    }
    
    // Getters
    public int getRoomId() { return roomId; }
    public RoomType getType() { return type; }
    public float[] getCenter() { return center.clone(); }
    public float[] getDimensions() { return dimensions.clone(); }
    public boolean isActive() { return isActive; }
    public boolean isEmpty() { return isEmpty; }
    public long getLastViewedTime() { return lastViewedTime; }
    public StaticList getRoomContents() { return roomContents; }
    
    // State management
    public void setActive(boolean active) { this.isActive = active; }
    
    /**
     * Checks if running in headless mode (without graphics support)
     */
    private boolean isHeadlessMode() {
        // Check if headless system property is set
        boolean headlessProperty = Boolean.getBoolean("java.awt.headless");
        
        // Check if DISPLAY environment variable is available (Linux/Unix)
        String display = System.getenv("DISPLAY");
        boolean noDisplay = (display == null || display.trim().isEmpty());
        
        // Check if we're in a typical CI environment
        boolean ciMode = System.getenv("CI") != null || 
                        System.getenv("GITHUB_ACTIONS") != null ||
                        System.getenv("JENKINS_URL") != null;
        
        return headlessProperty || (noDisplay && ciMode);
    }
}