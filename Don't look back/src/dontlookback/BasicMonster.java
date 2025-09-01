package dontlookback;

/**
 * Basic Monster Implementation
 * 
 * A concrete implementation of the Monster interface for collision detection
 * and basic game mechanics. This monster has position, dimensions, and 
 * behavior suitable for collision testing.
 * 
 * @author DLB Team
 * @version 1.0
 */
public class BasicMonster implements Monster {
    
    // === Position and Dimensions ===
    private float posX = 0.0f;
    private float posY = 0.0f;
    private float posZ = 0.0f;
    
    private float rotationX = 0.0f;
    private float rotationY = 0.0f;
    private float rotationZ = 0.0f;
    
    // === Physical Attributes ===
    private static final short DEFAULT_LEG_LENGTH = 50; // 50cm legs
    private static final double DEFAULT_SCALE = 1.0;
    private static final double DEFAULT_WEIGHT = 60.0; // 60kg
    private static final float DEFAULT_MOVEMENT_SPEED = 0.001f;
    private static final int DEFAULT_THREAT_LEVEL = 3;
    private static final int DEFAULT_INTELLIGENCE = 2;
    private static final int DEFAULT_REACTION_TIME = 500; // 500ms
    
    // === Behavior Attributes ===
    private boolean hostile = true;
    private double lastSeenTime = -1.0; // Negative means waiting
    private float orientation = 0.0f;
    
    // === Dimensions for collision detection ===
    private static final int[] DEFAULT_DIMENSIONS = {60, 180, 40}; // width, height, depth in cm
    
    /**
     * Create a new BasicMonster at the origin
     */
    public BasicMonster() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    /**
     * Create a new BasicMonster at specified position
     */
    public BasicMonster(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }
    
    // === Monster Interface Implementation ===
    
    @Override
    public short legLength() {
        return DEFAULT_LEG_LENGTH;
    }
    
    @Override
    public boolean hostile() {
        return hostile;
    }
    
    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }
    
    @Override
    public double lastSeen() {
        return lastSeenTime;
    }
    
    public void setLastSeen(double time) {
        this.lastSeenTime = time;
    }
    
    @Override
    public double scale() {
        return DEFAULT_SCALE;
    }
    
    @Override
    public float orientation() {
        return orientation;
    }
    
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }
    
    // === Enemy Interface Implementation ===
    
    @Override
    public int reactionTime() {
        return DEFAULT_REACTION_TIME;
    }
    
    @Override
    public int threatLevel() {
        return DEFAULT_THREAT_LEVEL;
    }
    
    @Override
    public int Cost() {
        return 10; // Basic monster costs 10 points
    }
    
    @Override
    public float movementSpeed() {
        return DEFAULT_MOVEMENT_SPEED;
    }
    
    @Override
    public int intellegence() {
        return DEFAULT_INTELLIGENCE;
    }
    
    @Override
    public double weight() {
        return DEFAULT_WEIGHT;
    }
    
    @Override
    public boolean jump() {
        return false; // Basic monsters can't jump
    }
    
    @Override
    public int jumpHeight() {
        return 0;
    }
    
    @Override
    public int[] dimensions() {
        return DEFAULT_DIMENSIONS.clone();
    }
    
    // === NPC Interface Implementation ===
    
    @Override
    public boolean Interactive() {
        return false; // Basic monsters are not interactive
    }
    
    @Override
    public boolean Quest() {
        return false; // Basic monsters don't have quests
    }
    
    @Override
    public int movementPattern() {
        return 1; // Simple movement pattern
    }
    
    @Override
    public int state() {
        return hostile ? 2 : 1; // 2 = hostile, 1 = active
    }
    
    // === Position Methods ===
    
    public float positionX() {
        return posX;
    }
    
    public float positionY() {
        return posY;
    }
    
    public float positionZ() {
        return posZ;
    }
    
    public void setPosition(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }
    
    // === Movement Methods ===
    
    /**
     * Move the monster towards a target position
     */
    public void moveTowards(float targetX, float targetY, float targetZ) {
        float dx = targetX - posX;
        float dy = targetY - posY;
        float dz = targetZ - posZ;
        
        // Normalize movement vector and apply speed
        float distance = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (distance > 0) {
            posX += (dx / distance) * movementSpeed();
            posY += (dy / distance) * movementSpeed();
            posZ += (dz / distance) * movementSpeed();
        }
    }
    
    /**
     * Get the monster's bounding box for collision detection
     * Returns {minX, minY, minZ, maxX, maxY, maxZ}
     */
    public float[] getBoundingBox() {
        int[] dims = dimensions();
        float halfWidth = dims[0] / 200.0f;  // Convert cm to game units
        float height = dims[1] / 100.0f;
        float halfDepth = dims[2] / 200.0f;
        
        return new float[] {
            posX - halfWidth,  // minX
            posY,              // minY (ground level)
            posZ - halfDepth,  // minZ
            posX + halfWidth,  // maxX
            posY + height,     // maxY
            posZ + halfDepth   // maxZ
        };
    }
}