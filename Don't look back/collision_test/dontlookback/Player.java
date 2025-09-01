package dontlookback;

/**
 * Player Character Controller
 * 
 * Manages the player character's attributes, movement, and interactions
 * within the game world. This class implements the Human interface
 * and provides core player functionality for the horror survival game.
 * 
 * Key Features:
 * - Character movement and speed control
 * - Physical attributes (weight, gender)  
 * - Item interaction system
 * - Player state management
 * 
 * The Player class serves as the central point for all player-related
 * game mechanics and provides the interface between user input and
 * game world interactions.
 * 
 * @author DLB Team
 * @version 1.0
 * @see Human interface for inherited behavior
 */
public class Player implements Human {

    // === Physical Attributes ===
    
    /** Player gender (true = male, false = female) */
    private boolean gender = true;
    
    /** Player weight in kilograms (affects movement and physics) */
    private double weight = 35; // Default: 30-40kg for a small child
    
    /** Movement speed in units/frame (calibrated for game balance) */
    private float speed = .001666f; // Default walking speed
    
    // === Position and Dimensions ===
    
    /** Player position coordinates in world space */
    private float posX = 0.0f;
    private float posY = 0.0f; 
    private float posZ = 0.0f;
    
    /** Player rotation values in degrees */
    private float rotationX = 0.0f;
    private float rotationY = 0.0f;
    private float rotationZ = 0.0f;
    
    /** Player physical dimensions for collision detection */
    private static final int DEFAULT_HEIGHT = 175; // 1.75m in cm
    private static final int DEFAULT_WIDTH = 40;   // 40cm width
    private static final int DEFAULT_DEPTH = 30;   // 30cm depth
    
    // === Movement System ===

    /**
     * Get the player's current movement speed
     * @return current speed value in units per frame
     */
    public float speed() {
        return this.speed;
    }

    /**
     * Set the player's movement speed
     * @param speed new speed value (higher = faster movement)
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // === Interaction System ===
    
    /**
     * Attempt to pick up an item from the environment
     * Currently placeholder implementation - to be enhanced with inventory system
     */
    public void pickUpItem() {
        System.out.println("there is nothing to pickup dumbass");
    }

    // === Physical Attributes Accessors ===
    
    /**
     * Get the player's weight
     * @return weight in kilograms
     */
    public double weight() {
        return this.weight;
    }

    /**
     * Set the player's weight (affects movement physics)
     * @param weight new weight in kilograms
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Get the player's gender
     * @return true for male, false for female
     */
    public boolean gender() {
        return this.gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public boolean jump() {
        System.out.println("this isn't one of those games!");
        System.out.println("besides you need legs to jump!");
        return false;
    }

    public int jumpHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int height() {
        return DEFAULT_HEIGHT;
    }

    public int width() {
        return DEFAULT_WIDTH;
    }

    public int depth() {
        return DEFAULT_DEPTH;
    }

    /**
     * Get the player's inventory contents
     * @return array of Objects in inventory (to be implemented)
     * @throws UnsupportedOperationException inventory system not yet implemented
     */
    public Objects[] inventory() {
        throw new UnsupportedOperationException("Inventory system not yet implemented");
    }
    
    // === Status Effects System (To Be Implemented) ===
    
    /**
     * Get active debuffs affecting the player
     * @return array of debuff IDs (to be implemented)
     * @throws UnsupportedOperationException status effect system not yet implemented
     */
    public int[] deBuffs() {
        throw new UnsupportedOperationException("Status effect system not yet implemented");
    }

    /**
     * Get the currently held item
     * @return the Objects instance being held (to be implemented)
     * @throws UnsupportedOperationException item holding system not yet implemented
     */
    public Objects heldItem() {
        throw new UnsupportedOperationException("Item holding system not yet implemented");
    }

    /**
     * Check if player is currently holding an item
     * @return true if holding an item, false otherwise (to be implemented)
     * @throws UnsupportedOperationException item holding system not yet implemented
     */
    public boolean isHolding() {
        throw new UnsupportedOperationException("Item holding system not yet implemented");
    }

    /**
     * Get the player's reaction time stat
     * @return reaction time in milliseconds (to be implemented)
     * @throws UnsupportedOperationException stat system not yet implemented
     */
    public int reactionTime() {
        throw new UnsupportedOperationException("Stat system not yet implemented");
    }

    // === Position and Rotation System (To Be Implemented) ===
    
    /**
     * Get player's X position in world space
     * @return X coordinate
     */
    public float positionX() {
        return posX;
    }

    /**
     * Get player's Y position in world space
     * @return Y coordinate
     */
    public float positionY() {
        return posY;
    }

    /**
     * Get player's Z position in world space
     * @return Z coordinate
     */
    public float positionZ() {
        return posZ;
    }
    
    /**
     * Set player position in world space
     * @param x X coordinate
     * @param y Y coordinate  
     * @param z Z coordinate
     */
    public void setPosition(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    /**
     * Get player's rotation around X axis (pitch)
     * @return X rotation in degrees
     */
    public float rotX() {
        return rotationX;
    }

    /**
     * Get player's rotation around Y axis (yaw) 
     * @return Y rotation in degrees
     */
    public float rotY() {
        return rotationY;
    }

    /**
     * Get player's rotation around Z axis (roll)
     * @return Z rotation in degrees
     */
    public float rotZ() {
        return rotationZ;
    }
    
    /**
     * Set player rotation
     * @param rotX X rotation in degrees
     * @param rotY Y rotation in degrees
     * @param rotZ Z rotation in degrees
     */
    public void setRotation(float rotX, float rotY, float rotZ) {
        this.rotationX = rotX;
        this.rotationY = rotY;
        this.rotationZ = rotZ;
    }

    // === Movement Controls ===
    
    /**
     * Move player character to the left
     * Updates position and provides console feedback for debugging
     */
    public void moveToLeft() {
        posX -= speed;
        System.out.println("moving left to (" + posX + ", " + posY + ", " + posZ + ")");
    }

    /**
     * Move player character to the right  
     * Updates position and provides console feedback for debugging
     */
    public void moveToRight() {
        posX += speed;
        System.out.println("moving right to (" + posX + ", " + posY + ", " + posZ + ")");
    }

    /**
     * Move player character forward
     * Updates position and provides console feedback for debugging
     */
    public void moveToFront() {
        posZ -= speed;
        System.out.println("moving forward to (" + posX + ", " + posY + ", " + posZ + ")");
    }

    /**
     * Move player character backward
     * Updates position and provides console feedback for debugging
     */
    public void moveToBack() {
        posZ += speed;
        System.out.println("moving backward to (" + posX + ", " + posY + ", " + posZ + ")");
    }
    
    // === Collision-Aware Movement ===
    
    /**
     * Attempt to move left with collision detection
     * @param obstacles Array of obstacle bounding boxes to check against
     * @return true if movement succeeded, false if blocked by collision
     */
    public boolean moveLeftSafe(float[][] obstacles) {
        float newX = posX - speed;
        float[] safePos = CollisionDetector.getSafeMovementPosition(this, newX, posY, posZ, obstacles);
        
        if (safePos == null) {
            // Movement is safe
            posX = newX;
            System.out.println("moving left to (" + posX + ", " + posY + ", " + posZ + ")");
            return true;
        } else {
            // Collision detected, movement blocked
            System.out.println("movement left blocked by collision");
            return false;
        }
    }
    
    /**
     * Attempt to move right with collision detection
     * @param obstacles Array of obstacle bounding boxes to check against
     * @return true if movement succeeded, false if blocked by collision
     */
    public boolean moveRightSafe(float[][] obstacles) {
        float newX = posX + speed;
        float[] safePos = CollisionDetector.getSafeMovementPosition(this, newX, posY, posZ, obstacles);
        
        if (safePos == null) {
            // Movement is safe
            posX = newX;
            System.out.println("moving right to (" + posX + ", " + posY + ", " + posZ + ")");
            return true;
        } else {
            // Collision detected, movement blocked
            System.out.println("movement right blocked by collision");
            return false;
        }
    }
    
    /**
     * Attempt to move forward with collision detection
     * @param obstacles Array of obstacle bounding boxes to check against
     * @return true if movement succeeded, false if blocked by collision
     */
    public boolean moveForwardSafe(float[][] obstacles) {
        float newZ = posZ - speed;
        float[] safePos = CollisionDetector.getSafeMovementPosition(this, posX, posY, newZ, obstacles);
        
        if (safePos == null) {
            // Movement is safe
            posZ = newZ;
            System.out.println("moving forward to (" + posX + ", " + posY + ", " + posZ + ")");
            return true;
        } else {
            // Collision detected, movement blocked
            System.out.println("movement forward blocked by collision");
            return false;
        }
    }
    
    /**
     * Attempt to move backward with collision detection
     * @param obstacles Array of obstacle bounding boxes to check against
     * @return true if movement succeeded, false if blocked by collision
     */
    public boolean moveBackSafe(float[][] obstacles) {
        float newZ = posZ + speed;
        float[] safePos = CollisionDetector.getSafeMovementPosition(this, posX, posY, newZ, obstacles);
        
        if (safePos == null) {
            // Movement is safe
            posZ = newZ;
            System.out.println("moving backward to (" + posX + ", " + posY + ", " + posZ + ")");
            return true;
        } else {
            // Collision detected, movement blocked
            System.out.println("movement backward blocked by collision");
            return false;
        }
    }
    
    /**
     * Get the player's bounding box for collision detection
     * @return Bounding box {minX, minY, minZ, maxX, maxY, maxZ}
     */
    public float[] getBoundingBox() {
        return CollisionDetector.getPlayerBoundingBox(this);
    }

}