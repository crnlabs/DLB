package dontlookback.entities;

import dontlookback.interfaces.Human;
import dontlookback.PlayerProfile;
import dontlookback.systems.InputManager;

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
    
    // === Inventory and Items ===
    
    /** Basic inventory system - array of held objects */
    private Objects[] inventory;
    
    /** Currently held item (quick access) */
    private Objects heldItem;
    
    /** Maximum inventory size */
    private static final int MAX_INVENTORY_SIZE = 10;
    
    /** Player stats */
    private int reactionTime = 250; // milliseconds
    
    /** Active debuffs affecting the player */
    private int[] activeDebuffs = new int[0]; // Empty array by default
    
    /** Player profile for persistence */
    private PlayerProfile profile;
    
    /** Input manager for controller support */
    private InputManager inputManager;
    
    // === Constructor ===
    
    /**
     * Create a new Player instance with default settings
     */
    public Player() {
        this.inventory = new Objects[MAX_INVENTORY_SIZE];
        this.heldItem = null;
        this.profile = new PlayerProfile();
        this.inputManager = new InputManager();
        
        // Try to load existing profile
        profile.loadProfile();
    }
    
    /**
     * Create a new Player instance with specified profile name
     * @param playerName the player's name
     */
    public Player(String playerName) {
        this.inventory = new Objects[MAX_INVENTORY_SIZE];
        this.heldItem = null;
        this.profile = new PlayerProfile(playerName);
        this.inputManager = new InputManager();
        
        // Try to load existing profile
        profile.loadProfile();
    }
    
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
     * @return array of Objects in inventory
     */
    public Objects[] inventory() {
        // Return a copy to prevent external modification
        Objects[] inventoryCopy = new Objects[MAX_INVENTORY_SIZE];
        System.arraycopy(inventory, 0, inventoryCopy, 0, MAX_INVENTORY_SIZE);
        return inventoryCopy;
    }
    
    /**
     * Add an item to the player's inventory
     * @param item the item to add
     * @return true if successfully added, false if inventory is full
     */
    public boolean addToInventory(Objects item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = item;
                return true;
            }
        }
        return false; // Inventory full
    }
    
    /**
     * Remove an item from the player's inventory
     * @param item the item to remove
     * @return true if successfully removed, false if not found
     */
    public boolean removeFromInventory(Objects item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == item) {
                inventory[i] = null;
                if (heldItem == item) {
                    heldItem = null;
                }
                return true;
            }
        }
        return false;
    }
    
    // === Status Effects System (To Be Implemented) ===
    
    /**
     * Get active debuffs affecting the player
     * @return array of debuff IDs
     */
    public int[] deBuffs() {
        // Return a copy to prevent external modification
        int[] debuffsCopy = new int[activeDebuffs.length];
        System.arraycopy(activeDebuffs, 0, debuffsCopy, 0, activeDebuffs.length);
        return debuffsCopy;
    }
    
    /**
     * Add a debuff to the player
     * @param debuffId the ID of the debuff to add
     */
    public void addDebuff(int debuffId) {
        // Check if debuff already exists
        for (int debuff : activeDebuffs) {
            if (debuff == debuffId) {
                return; // Already has this debuff
            }
        }
        
        // Add new debuff
        int[] newDebuffs = new int[activeDebuffs.length + 1];
        System.arraycopy(activeDebuffs, 0, newDebuffs, 0, activeDebuffs.length);
        newDebuffs[activeDebuffs.length] = debuffId;
        activeDebuffs = newDebuffs;
    }
    
    /**
     * Remove a debuff from the player
     * @param debuffId the ID of the debuff to remove
     */
    public void removeDebuff(int debuffId) {
        for (int i = 0; i < activeDebuffs.length; i++) {
            if (activeDebuffs[i] == debuffId) {
                // Remove debuff by creating new array without it
                int[] newDebuffs = new int[activeDebuffs.length - 1];
                System.arraycopy(activeDebuffs, 0, newDebuffs, 0, i);
                System.arraycopy(activeDebuffs, i + 1, newDebuffs, i, activeDebuffs.length - i - 1);
                activeDebuffs = newDebuffs;
                return;
            }
        }
    }

    /**
     * Get the currently held item
     * @return the Objects instance being held, or null if nothing is held
     */
    public Objects heldItem() {
        return heldItem;
    }
    
    /**
     * Set the currently held item
     * @param item the item to hold (must be in inventory)
     * @return true if successfully set, false if item not in inventory
     */
    public boolean setHeldItem(Objects item) {
        if (item == null) {
            heldItem = null;
            return true;
        }
        
        // Check if item is in inventory
        for (Objects inventoryItem : inventory) {
            if (inventoryItem == item) {
                heldItem = item;
                return true;
            }
        }
        return false; // Item not in inventory
    }

    /**
     * Check if player is currently holding an item
     * @return true if holding an item, false otherwise
     */
    public boolean isHolding() {
        return heldItem != null;
    }

    /**
     * Get the player's reaction time stat
     * @return reaction time in milliseconds
     */
    public int reactionTime() {
        return reactionTime;
    }
    
    /**
     * Set the player's reaction time
     * @param reactionTime new reaction time in milliseconds
     */
    public void setReactionTime(int reactionTime) {
        this.reactionTime = Math.max(50, reactionTime); // Minimum 50ms
    }

    // === Position and Rotation System ===
    
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
     * Update player based on input (call each frame)
     */
    public void update() {
        if (inputManager != null) {
            inputManager.update();
            
            // Check for dangerous look back action
            if (inputManager.isLookBackTriggered()) {
                handleLookBack();
            }
            
            // Handle movement input
            float[] movement = inputManager.getMovementVector();
            if (movement[0] != 0.0f || movement[1] != 0.0f) {
                moveWithVector(movement[0], movement[1]);
            }
        }
    }
    
    /**
     * Move player with a direction vector
     * @param deltaX movement in X direction (-1.0 to 1.0)
     * @param deltaY movement in Y direction (-1.0 to 1.0)
     */
    public void moveWithVector(float deltaX, float deltaY) {
        float oldX = posX;
        float oldZ = posZ;
        
        posX += deltaX * speed;
        posZ += deltaY * speed;
        
        // Calculate distance moved for statistics
        float distance = (float) Math.sqrt((posX - oldX) * (posX - oldX) + (posZ - oldZ) * (posZ - oldZ));
        if (profile != null) {
            profile.addDistance(distance);
        }
        
        System.out.println("moving to (" + posX + ", " + posY + ", " + posZ + ")");
    }
    
    /**
     * Handle the dangerous look back action
     * This is the core horror mechanic!
     */
    private void handleLookBack() {
        if (profile != null) {
            profile.recordLookBack();
        }
        
        System.out.println("*** WARNING: YOU LOOKED BACK! ***");
        System.out.println("Something stirs in the darkness behind you...");
        
        // TODO: Trigger monster spawn or other horror effects
        // This would integrate with the monster/room systems
    }
    
    /**
     * Move player character to the left
     * Updates position and provides console feedback for debugging
     */
    public void moveToLeft() {
        float oldX = posX;
        posX -= speed;
        trackMovement(oldX, posX, 0, 0);
        System.out.println("moving left to (" + posX + ", " + posY + ", " + posZ + ")");
    }

    /**
     * Move player character to the right  
     * Updates position and provides console feedback for debugging
     */
    public void moveToRight() {
        float oldX = posX;
        posX += speed;
        trackMovement(oldX, posX, 0, 0);
        System.out.println("moving right to (" + posX + ", " + posY + ", " + posZ + ")");
    }

    /**
     * Move player character forward
     * Updates position and provides console feedback for debugging
     */
    public void moveToFront() {
        float oldZ = posZ;
        posZ -= speed;
        trackMovement(0, 0, oldZ, posZ);
        System.out.println("moving forward to (" + posX + ", " + posY + ", " + posZ + ")");
    }

    /**
     * Move player character backward
     * Updates position and provides console feedback for debugging
     */
    public void moveToBack() {
        float oldZ = posZ;
        posZ += speed;
        trackMovement(0, 0, oldZ, posZ);
        System.out.println("moving backward to (" + posX + ", " + posY + ", " + posZ + ")");
    }
    
    /**
     * Helper method to track movement distance
     * @param oldX previous X position
     * @param newX new X position  
     * @param oldZ previous Z position
     * @param newZ new Z position
     */
    private void trackMovement(float oldX, float newX, float oldZ, float newZ) {
        if (profile != null) {
            float distance = (float) Math.sqrt((newX - oldX) * (newX - oldX) + (newZ - oldZ) * (newZ - oldZ));
            profile.addDistance(distance);
        }
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
    
    // === System Access ===
    
    /**
     * Get the player's profile for persistence operations
     * @return player profile instance
     */
    public PlayerProfile getProfile() {
        return profile;
    }
    
    /**
     * Get the input manager for controller operations
     * @return input manager instance
     */
    public InputManager getInputManager() {
        return inputManager;
    }
    
    /**
     * Save player profile to disk
     * @return true if successfully saved
     */
    public boolean saveProfile() {
        return profile != null && profile.saveProfile();
    }
    
    /**
     * Complete a game session (updates statistics)
     */
    public void completeGame() {
        if (profile != null) {
            profile.recordGameCompletion();
            profile.saveProfile();
        }
    }
    
    /**
     * Update the highest room reached
     * @param roomNumber the room number reached
     */
    public void reachedRoom(int roomNumber) {
        if (profile != null) {
            profile.updateHighestRoom(roomNumber);
        }
    }

}