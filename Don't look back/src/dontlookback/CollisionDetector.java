package dontlookback;

/**
 * Collision Detection System
 * 
 * Provides collision detection functionality for the Don't Look Back game.
 * Uses Axis-Aligned Bounding Box (AABB) collision detection for efficient
 * and reliable collision checking between game entities.
 * 
 * @author DLB Team
 * @version 1.0
 */
public class CollisionDetector {
    
    /**
     * Check collision between player and monster using AABB collision detection
     * 
     * @param player The player to check
     * @param monster The monster to check against
     * @return true if collision detected, false otherwise
     */
    public static boolean checkPlayerMonsterCollision(Player player, BasicMonster monster) {
        float[] playerBox = getPlayerBoundingBox(player);
        float[] monsterBox = monster.getBoundingBox();
        
        return checkAABBCollision(playerBox, monsterBox);
    }
    
    /**
     * Check collision between player and a wall/solid object
     * 
     * @param player The player to check
     * @param wallBox Bounding box of the wall {minX, minY, minZ, maxX, maxY, maxZ}
     * @return true if collision detected, false otherwise
     */
    public static boolean checkPlayerWallCollision(Player player, float[] wallBox) {
        float[] playerBox = getPlayerBoundingBox(player);
        return checkAABBCollision(playerBox, wallBox);
    }
    
    /**
     * Check collision between player and an item for pickup/interaction
     * 
     * @param player The player to check
     * @param itemX Item X position
     * @param itemY Item Y position
     * @param itemZ Item Z position
     * @param itemSize Size of the item for collision detection
     * @return true if within interaction range, false otherwise
     */
    public static boolean checkPlayerItemInteraction(Player player, float itemX, float itemY, float itemZ, float itemSize) {
        float playerX = player.positionX();
        float playerY = player.positionY();
        float playerZ = player.positionZ();
        
        // Calculate distance to item
        float dx = playerX - itemX;
        float dy = playerY - itemY;
        float dz = playerZ - itemZ;
        float distance = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        
        // Check if within interaction range (item size + small buffer)
        float interactionRange = itemSize + 0.5f;
        return distance <= interactionRange;
    }
    
    /**
     * Check if a movement would cause a collision and return safe position
     * 
     * @param player The player attempting to move
     * @param newX Proposed new X position
     * @param newY Proposed new Y position
     * @param newZ Proposed new Z position
     * @param obstacles Array of obstacle bounding boxes to check against
     * @return Array with safe position {x, y, z} or null if movement is safe
     */
    public static float[] getSafeMovementPosition(Player player, float newX, float newY, float newZ, float[][] obstacles) {
        // Create temporary bounding box for proposed position
        float[] proposedBox = getPlayerBoundingBoxAt(player, newX, newY, newZ);
        
        // Check collision with each obstacle
        for (float[] obstacle : obstacles) {
            if (checkAABBCollision(proposedBox, obstacle)) {
                // Collision detected, return current safe position
                return new float[] {player.positionX(), player.positionY(), player.positionZ()};
            }
        }
        
        // No collision, movement is safe
        return null;
    }
    
    /**
     * Get player's bounding box for collision detection
     * 
     * @param player The player
     * @return Bounding box {minX, minY, minZ, maxX, maxY, maxZ}
     */
    public static float[] getPlayerBoundingBox(Player player) {
        return getPlayerBoundingBoxAt(player, player.positionX(), player.positionY(), player.positionZ());
    }
    
    /**
     * Get player's bounding box at a specific position
     * 
     * @param player The player
     * @param x X position
     * @param y Y position  
     * @param z Z position
     * @return Bounding box {minX, minY, minZ, maxX, maxY, maxZ}
     */
    private static float[] getPlayerBoundingBoxAt(Player player, float x, float y, float z) {
        // Convert player dimensions from cm to game units
        float halfWidth = player.width() / 200.0f;  // Half width in game units
        float height = player.height() / 100.0f;    // Height in game units
        float halfDepth = player.depth() / 200.0f;  // Half depth in game units
        
        return new float[] {
            x - halfWidth,    // minX
            y,                // minY (ground level)
            z - halfDepth,    // minZ
            x + halfWidth,    // maxX
            y + height,       // maxY
            z + halfDepth     // maxZ
        };
    }
    
    /**
     * Check collision between two AABB (Axis-Aligned Bounding Box) objects
     * 
     * @param box1 First bounding box {minX, minY, minZ, maxX, maxY, maxZ}
     * @param box2 Second bounding box {minX, minY, minZ, maxX, maxY, maxZ}
     * @return true if boxes overlap, false otherwise
     */
    private static boolean checkAABBCollision(float[] box1, float[] box2) {
        // Check for separation on each axis
        // If separated on any axis, no collision
        
        // X axis check
        if (box1[3] < box2[0] || box1[0] > box2[3]) return false;
        
        // Y axis check
        if (box1[4] < box2[1] || box1[1] > box2[4]) return false;
        
        // Z axis check
        if (box1[5] < box2[2] || box1[2] > box2[5]) return false;
        
        // No separation found, collision detected
        return true;
    }
    
    /**
     * Create a wall bounding box for collision detection
     * 
     * @param centerX Wall center X position
     * @param centerY Wall center Y position
     * @param centerZ Wall center Z position
     * @param width Wall width
     * @param height Wall height
     * @param depth Wall depth
     * @return Wall bounding box {minX, minY, minZ, maxX, maxY, maxZ}
     */
    public static float[] createWallBoundingBox(float centerX, float centerY, float centerZ, 
                                               float width, float height, float depth) {
        float halfWidth = width / 2.0f;
        float halfDepth = depth / 2.0f;
        
        return new float[] {
            centerX - halfWidth,    // minX
            centerY,                // minY (base level)
            centerZ - halfDepth,    // minZ
            centerX + halfWidth,    // maxX
            centerY + height,       // maxY
            centerZ + halfDepth     // maxZ
        };
    }
    
    /**
     * Create room boundary walls for collision detection
     * 
     * @param room The room to create boundaries for
     * @return Array of wall bounding boxes for the room perimeter
     */
    public static float[][] createRoomWalls(Room room) {
        // This would integrate with the Room class to create wall boundaries
        // For now, return empty array as Room integration requires more analysis
        return new float[0][];
    }
}