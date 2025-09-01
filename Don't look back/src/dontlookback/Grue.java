package dontlookback;

/**
 * The Grue - Darkness Monster
 * 
 * A special monster that appears when there is no light, implementing
 * the core "grue" mechanic from the notes.txt vision. The grue kills
 * instantly if the player is caught in darkness without a light source.
 * 
 * Key behaviors:
 * - Only spawns in complete darkness
 * - Instantly kills player on contact
 * - Cannot be seen or fought, only avoided with light
 * - Represents ultimate fear of the unknown
 * - Makes camping in corners impossible
 * 
 * @author DLB Team
 * @version 1.0
 */
public class Grue extends BasicMonster {
    
    // === Grue Constants ===
    
    /** Time in darkness before grue becomes active (seconds) */
    private static final float DARKNESS_ACTIVATION_TIME = 5.0f;
    
    /** Maximum distance grue can be from player when spawning */
    private static final float MAX_SPAWN_DISTANCE = 15.0f;
    
    /** Minimum distance grue must maintain from light sources */
    private static final float MIN_LIGHT_DISTANCE = 2.0f;
    
    /** Speed at which grue approaches player */
    private static final float GRUE_SPEED = 0.5f;
    
    /** Warning distance - player gets audio cues when grue is this close */
    private static final float WARNING_DISTANCE = 5.0f;
    
    // === Grue State ===
    
    /** How long the area has been in darkness */
    private float darknessTime;
    
    /** Whether the grue is currently active */
    private boolean isActive;
    
    /** Target position (usually player position) */
    private float[] targetPosition;
    
    /** Time of last warning sound */
    private long lastWarningTime;
    
    /** Whether the grue has killed the player */
    private boolean hasKilled;
    
    /** Reference to light manager for darkness detection */
    private LightManager lightManager;
    
    /** Audio cues for grue presence */
    private boolean isGrowling;
    private long lastGrowlTime;
    
    /**
     * Create a grue instance
     * @param lightManager Reference to light management system
     */
    public Grue(LightManager lightManager) {
        super();
        this.lightManager = lightManager;
        this.darknessTime = 0.0f;
        this.isActive = false;
        this.targetPosition = new float[]{0.0f, 0.0f, 0.0f};
        this.lastWarningTime = 0;
        this.hasKilled = false;
        this.isGrowling = false;
        this.lastGrowlTime = 0;
        
        // Grue is invisible and intangible until active
        setVisible(false);
        setHostile(false);
        
        // Set grue-specific properties
        setThreatLevel(10); // Maximum threat
        setIntelligence(1);  // Simple AI - just approaches
        setReactionTime(100); // Instant reaction
        
        System.out.println("Grue initialized - lurking in the darkness...");
    }
    
    /**
     * Update grue behavior based on lighting conditions
     * @param playerPosition Current player position [x, y, z]
     * @param deltaTime Time since last update in seconds
     */
    public void update(float[] playerPosition, float deltaTime) {
        // Store target position
        System.arraycopy(playerPosition, 0, targetPosition, 0, 3);
        
        // Check if player is in darkness
        boolean playerInDarkness = !lightManager.isPositionProtected(playerPosition);
        
        if (playerInDarkness) {
            // Accumulate darkness time
            darknessTime += deltaTime;
            
            // Activate grue after sufficient darkness time
            if (darknessTime >= DARKNESS_ACTIVATION_TIME && !isActive) {
                activateGrue();
            }
            
            // Update active grue behavior
            if (isActive) {
                updateActiveGrue(deltaTime);
            }
        } else {
            // Player is in light - deactivate grue
            if (isActive || darknessTime > 0) {
                deactivateGrue();
            }
        }
    }
    
    /**
     * Activate the grue when darkness conditions are met
     */
    private void activateGrue() {
        isActive = true;
        setVisible(false); // Grue remains invisible
        setHostile(true);
        
        // Spawn near player but not too close
        spawnNearPlayer();
        
        System.out.println("The grue awakens in the darkness...");
        
        // Audio cue for grue activation
        playGrueSound("activate");
    }
    
    /**
     * Deactivate the grue when light is restored
     */
    private void deactivateGrue() {
        if (isActive) {
            System.out.println("The grue retreats from the light...");
            playGrueSound("retreat");
        }
        
        isActive = false;
        setVisible(false);
        setHostile(false);
        darknessTime = 0.0f;
        isGrowling = false;
        
        // Move grue away from play area
        setPosition(-1000.0f, -1000.0f, -1000.0f);
    }
    
    /**
     * Spawn the grue near the player
     */
    private void spawnNearPlayer() {
        // Choose a random position near the player but outside light radius
        float angle = (float) (Math.random() * 2 * Math.PI);
        float distance = 8.0f + (float) (Math.random() * (MAX_SPAWN_DISTANCE - 8.0f));
        
        float spawnX = targetPosition[0] + (float) Math.cos(angle) * distance;
        float spawnY = targetPosition[1]; // Same Y level as player
        float spawnZ = targetPosition[2] + (float) Math.sin(angle) * distance;
        
        // Ensure spawn position is not too close to any light source
        float[] spawnPos = {spawnX, spawnY, spawnZ};
        if (lightManager.isPositionProtected(spawnPos)) {
            // Try opposite direction
            spawnX = targetPosition[0] - (float) Math.cos(angle) * distance;
            spawnZ = targetPosition[2] - (float) Math.sin(angle) * distance;
        }
        
        setPosition(spawnX, spawnY, spawnZ);
        
        System.out.println("Grue spawned at: " + java.util.Arrays.toString(new float[]{spawnX, spawnY, spawnZ}));
    }
    
    /**
     * Update active grue behavior
     * @param deltaTime Time since last update
     */
    private void updateActiveGrue(float deltaTime) {
        // Calculate distance to player
        float distance = calculateDistanceToPlayer();
        
        // Move towards player
        moveTowardsPlayer(deltaTime);
        
        // Check for kill condition
        if (distance < 1.0f) { // Very close to player
            killPlayer();
            return;
        }
        
        // Generate audio cues based on distance
        updateAudioCues(distance);
        
        // Prevent grue from getting too close to light sources
        avoidLightSources();
    }
    
    /**
     * Move grue towards player
     * @param deltaTime Time since last update
     */
    private void moveTowardsPlayer(float deltaTime) {
        float dx = targetPosition[0] - positionX();
        float dy = targetPosition[1] - positionY();
        float dz = targetPosition[2] - positionZ();
        
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance > 0.1f) {
            // Normalize direction
            dx /= distance;
            dy /= distance;
            dz /= distance;
            
            // Move towards player
            float moveDistance = GRUE_SPEED * deltaTime;
            setPosition(
                positionX() + dx * moveDistance,
                positionY() + dy * moveDistance,
                positionZ() + dz * moveDistance
            );
        }
    }
    
    /**
     * Calculate distance from grue to player
     * @return Distance in world units
     */
    private float calculateDistanceToPlayer() {
        float dx = targetPosition[0] - positionX();
        float dy = targetPosition[1] - positionY();
        float dz = targetPosition[2] - positionZ();
        
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Update audio cues based on grue proximity
     * @param distance Distance to player
     */
    private void updateAudioCues(float distance) {
        long currentTime = System.currentTimeMillis();
        
        // Warning sounds when grue is close
        if (distance < WARNING_DISTANCE) {
            if (currentTime - lastWarningTime > 2000) { // Every 2 seconds
                playGrueSound("approach");
                lastWarningTime = currentTime;
            }
        }
        
        // Growling sounds when very close
        if (distance < 3.0f) {
            if (currentTime - lastGrowlTime > 1000) { // Every second
                playGrueSound("growl");
                lastGrowlTime = currentTime;
                isGrowling = true;
            }
        } else {
            isGrowling = false;
        }
        
        // Breathing/presence sounds for atmosphere
        if (distance < 8.0f && Math.random() < 0.01) { // 1% chance per frame
            playGrueSound("presence");
        }
    }
    
    /**
     * Make grue avoid getting too close to light sources
     */
    private void avoidLightSources() {
        // Check all light sources and avoid getting too close
        for (LightSource light : lightManager.getActiveLights()) {
            if (!light.isLit()) continue;
            
            float[] lightPos = light.getCenter();
            float dx = positionX() - lightPos[0];
            float dy = positionY() - lightPos[1];
            float dz = positionZ() - lightPos[2];
            
            float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            if (distance < MIN_LIGHT_DISTANCE) {
                // Move away from light
                if (distance > 0.1f) {
                    float moveDistance = MIN_LIGHT_DISTANCE - distance;
                    dx /= distance;
                    dy /= distance;
                    dz /= distance;
                    
                    setPosition(
                        positionX() + dx * moveDistance,
                        positionY() + dy * moveDistance,
                        positionZ() + dz * moveDistance
                    );
                }
            }
        }
    }
    
    /**
     * Kill the player (grue victory condition)
     */
    private void killPlayer() {
        if (hasKilled) return;
        
        hasKilled = true;
        System.out.println("=== THE GRUE HAS CAUGHT YOU ===");
        System.out.println("You have been eaten by a grue!");
        System.out.println("The darkness has claimed another victim...");
        
        playGrueSound("kill");
        
        // Trigger game over state
        // This would integrate with the state management system
        // For now, we'll just mark the kill
    }
    
    /**
     * Play grue sound effect
     * @param soundType Type of sound to play
     */
    private void playGrueSound(String soundType) {
        // This would integrate with the audio system
        // For now, we'll use console output
        switch (soundType) {
            case "activate":
                System.out.println("♪ [Grue awakens - ominous whisper] ♪");
                break;
            case "retreat":
                System.out.println("♪ [Grue retreats - fading growl] ♪");
                break;
            case "approach":
                System.out.println("♪ [Footsteps in darkness - getting closer] ♪");
                break;
            case "growl":
                System.out.println("♪ [Low, menacing growl nearby] ♪");
                break;
            case "presence":
                System.out.println("♪ [Unsettling breathing in the dark] ♪");
                break;
            case "kill":
                System.out.println("♪ [TERRIFYING ROAR - GAME OVER] ♪");
                break;
        }
    }
    
    // === Public Interface ===
    
    /**
     * Check if the grue is currently active
     * @return true if grue is hunting
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Check if the grue has killed the player
     * @return true if player is dead
     */
    public boolean hasKilledPlayer() {
        return hasKilled;
    }
    
    /**
     * Get how long the area has been in darkness
     * @return Darkness time in seconds
     */
    public float getDarknessTime() {
        return darknessTime;
    }
    
    /**
     * Get distance to player
     * @return Distance in world units, or -1 if not active
     */
    public float getDistanceToPlayer() {
        if (!isActive) return -1.0f;
        return calculateDistanceToPlayer();
    }
    
    /**
     * Check if grue is making threatening sounds
     * @return true if growling or making noise
     */
    public boolean isThreatening() {
        return isActive && (isGrowling || calculateDistanceToPlayer() < WARNING_DISTANCE);
    }
    
    /**
     * Get time until grue activation (if in darkness)
     * @return Seconds until activation, or -1 if not in darkness
     */
    public float getTimeUntilActivation() {
        if (isActive) return 0.0f;
        if (darknessTime == 0.0f) return -1.0f;
        return Math.max(0.0f, DARKNESS_ACTIVATION_TIME - darknessTime);
    }
    
    // === Monster Interface Overrides ===
    
    @Override
    public boolean Interactive() {
        return false; // Cannot interact with grue
    }
    
    @Override
    public boolean Quest() {
        return false; // No quests with grue
    }
    
    @Override
    public int movementPattern() {
        return isActive ? 3 : 0; // Aggressive when active, dormant otherwise
    }
    
    @Override
    public int state() {
        if (hasKilled) return 5;     // Victory state
        if (isActive) return 4;      // Hunting state
        if (darknessTime > 0) return 2; // Awakening state
        return 1;                    // Dormant state
    }
    
    // === Utility Methods ===
    
    /**
     * Reset grue to initial state
     */
    public void reset() {
        deactivateGrue();
        hasKilled = false;
        darknessTime = 0.0f;
        setPosition(-1000.0f, -1000.0f, -1000.0f);
        System.out.println("Grue reset to dormant state");
    }
    
    /**
     * Force activate grue for testing
     */
    public void forceActivate() {
        darknessTime = DARKNESS_ACTIVATION_TIME;
        activateGrue();
    }
    
    @Override
    public String toString() {
        return "Grue{" +
                "active=" + isActive +
                ", darknessTime=" + String.format("%.1f", darknessTime) +
                ", distanceToPlayer=" + (isActive ? String.format("%.1f", calculateDistanceToPlayer()) : "N/A") +
                ", hasKilled=" + hasKilled +
                ", threatening=" + isThreatening() +
                '}';
    }
}