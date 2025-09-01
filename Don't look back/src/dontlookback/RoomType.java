package dontlookback;

/**
 * Enumeration defining different types of rooms that can be generated
 * Each type has different dimensions and characteristics
 * 
 * @author Room Generator System
 */
public enum RoomType {
    HALLWAY(1, 10f, 3f, 2f),        // narrow passage
    SMALL_ROOM(2, 8f, 8f, 3f),      // small square room
    MEDIUM_ROOM(3, 12f, 12f, 3f),   // medium square room  
    LARGE_ROOM(4, 20f, 15f, 4f),    // large rectangular room
    CLOSET(5, 3f, 3f, 2.5f),        // tiny closet space
    BASEMENT(6, 15f, 10f, 2f);       // low ceiling basement

    private final int typeId;
    private final float width;
    private final float length; 
    private final float height;

    RoomType(int typeId, float width, float length, float height) {
        this.typeId = typeId;
        this.width = width;
        this.length = length;
        this.height = height;
    }

    public int getTypeId() {
        return typeId;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public float getHeight() {
        return height;
    }

    public float[] getDimensions() {
        return new float[]{width, length, height};
    }

    /**
     * Returns a random room type
     */
    public static RoomType getRandomType() {
        RoomType[] types = RoomType.values();
        return types[(int) (Math.random() * types.length)];
    }

    /**
     * Returns a room type weighted towards certain types for gameplay
     */
    public static RoomType getWeightedRandomType() {
        double rand = Math.random();
        
        // Weight distribution for better gameplay
        if (rand < 0.3) return SMALL_ROOM;       // 30% chance
        else if (rand < 0.5) return MEDIUM_ROOM; // 20% chance  
        else if (rand < 0.7) return HALLWAY;     // 20% chance
        else if (rand < 0.85) return LARGE_ROOM; // 15% chance
        else if (rand < 0.95) return CLOSET;     // 10% chance
        else return BASEMENT;                    // 5% chance
    }
}