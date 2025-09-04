package dontlookback.entities;

/**
 * Look-Based Monster Implementation
 * 
 * Implements the core "Don't Look Back" mechanic where monsters:
 * - Only become active when looked at by the player
 * - Chase the player when seen
 * - Despawn after 5 seconds of not being observed
 * - Can regenerate in different locations
 * 
 * This is the central monster type that drives the unique horror experience.
 * 
 * @author DLB Team
 * @version 1.0
 */
public class LookBasedMonster extends BasicMonster {
    
    // === Look-Based Mechanics Constants ===
    
    /** Time in seconds before monster despawns if not seen */
    private static final double DESPAWN_TIME = 5.0;
    
    /** Time monster remains aggressive after losing sight */
    private static final double AGGRESSION_COOLDOWN = 2.0;
    
    /** Distance at which monster starts chasing */
    private static final float CHASE_DISTANCE = 15.0f;
    
    /** Distance at which monster attacks */
    private static final float ATTACK_DISTANCE = 1.5f;
    
    /** Speed when chasing player */
    private static final float CHASE_SPEED = 2.0f;
    
    /** Speed when patrolling */
    private static final float PATROL_SPEED = 0.5f;
    
    /** Maximum distance monster can spawn from player */
    private static final float MAX_SPAWN_DISTANCE = 25.0f;
    
    /** Minimum distance monster must spawn from player */
    private static final float MIN_SPAWN_DISTANCE = 8.0f;
    
    // === Monster State ===
    
    /** Accumulated simulation time for testing */
    private double simulationTime;
    
    /** Time when monster was last seen by player (-1 if never seen) */
    private double lastSeenTime;
    
    /** Current behavior state */
    private MonsterBehavior currentBehavior;
    
    /** Time when current behavior started */
    private double behaviorStartTime;
    
    /** Target position for movement */
    private float[] targetPosition;
    
    /** Player position (updated externally) */
    private float[] playerPosition;
    
    /** Whether monster is currently visible to player */
    private boolean isBeingObserved;
    
    /** Time since monster became active */
    private double activeTime;
    
    /** Monster type/appearance */
    private MonsterType monsterType;
    
    /** Spawn location for respawning */
    private float[] originalSpawnLocation;
    
    /** Whether monster has been killed/defeated */
    private boolean isDefeated;
    
    /** Audio state for atmospheric effects */
    private long lastSoundTime;
    private boolean isMakingNoise;
    
    /**
     * Monster behavior states
     */
    public enum MonsterBehavior {
        DORMANT,      // Not active, waiting to be seen
        SPAWNING,     // Just became active from being looked at
        STALKING,     // Following player but not aggressive
        CHASING,      // Actively pursuing player
        ATTACKING,    // In combat with player
        SEARCHING,    // Lost sight of player, searching
        DESPAWNING    // About to disappear
    }
    
    /**
     * Monster appearance types
     */
    public enum MonsterType {
        SHADOW_FIGURE("Shadowy Figure", 1.8f, 2),
        CRAWLING_HORROR("Crawling Horror", 0.8f, 4),
        TALL_STALKER("Tall Stalker", 2.5f, 3),
        FACELESS_WALKER("Faceless Walker", 1.6f, 1),
        CHILD_MIMIC("Child Mimic", 1.2f, 2);
        
        private final String displayName;
        private final float height;
        private final int threatLevel;
        
        MonsterType(String displayName, float height, int threatLevel) {
            this.displayName = displayName;
            this.height = height;
            this.threatLevel = threatLevel;
        }
        
        public String getDisplayName() { return displayName; }
        public float getHeight() { return height; }
        public int getThreatLevel() { return threatLevel; }
    }
    
    /**
     * Create a look-based monster
     * @param monsterType Type of monster
     * @param spawnPosition Initial spawn position [x, y, z]
     */
    public LookBasedMonster(MonsterType monsterType, float[] spawnPosition) {
        super(spawnPosition[0], spawnPosition[1], spawnPosition[2]);
        
        this.monsterType = monsterType;
        this.simulationTime = 0.0;
        this.lastSeenTime = -1.0; // Never seen
        this.currentBehavior = MonsterBehavior.DORMANT;
        this.behaviorStartTime = getCurrentTime();
        this.targetPosition = new float[]{spawnPosition[0], spawnPosition[1], spawnPosition[2]};
        this.playerPosition = new float[]{0.0f, 0.0f, 0.0f};
        this.isBeingObserved = false;
        this.activeTime = 0.0;
        this.originalSpawnLocation = spawnPosition.clone();
        this.isDefeated = false;
        this.lastSoundTime = 0;
        this.isMakingNoise = false;
        
        // Set monster properties based on type
        configureMosnterForType();
        
        // Start as dormant and invisible
        setVisible(false);
        setHostile(false);
        
        System.out.println("Look-based monster created: " + monsterType.getDisplayName() + 
                          " at " + java.util.Arrays.toString(spawnPosition));
    }
    
    /**
     * Create a random monster type
     * @param spawnPosition Spawn position
     */
    public LookBasedMonster(float[] spawnPosition) {
        this(MonsterType.values()[(int)(Math.random() * MonsterType.values().length)], spawnPosition);
    }
    
    /**
     * Configure monster properties based on type
     */
    private void configureMosnterForType() {
        setThreatLevel(monsterType.getThreatLevel());
        
        switch (monsterType) {
            case SHADOW_FIGURE:
                setIntelligence(2);
                setReactionTime(300);
                setRGB(0.1f, 0.1f, 0.1f); // Very dark
                break;
                
            case CRAWLING_HORROR:
                setIntelligence(1);
                setReactionTime(200);
                setRGB(0.3f, 0.1f, 0.1f); // Dark red
                break;
                
            case TALL_STALKER:
                setIntelligence(3);
                setReactionTime(400);
                setRGB(0.2f, 0.2f, 0.1f); // Dark yellow-green
                break;
                
            case FACELESS_WALKER:
                setIntelligence(2);
                setReactionTime(250);
                setRGB(0.2f, 0.2f, 0.2f); // Gray
                break;
                
            case CHILD_MIMIC:
                setIntelligence(4);
                setReactionTime(150);
                setRGB(0.3f, 0.2f, 0.3f); // Pale purple
                break;
        }
    }
    
    // === Core Look-Based Mechanics ===
    
    /**
     * Update monster behavior based on observation status
     * @param playerPos Current player position [x, y, z]
     * @param isObserved Whether player is currently looking at this monster
     * @param deltaTime Time since last update in seconds
     */
    public void updateLookBasedBehavior(float[] playerPos, boolean isObserved, double deltaTime) {
        // Update simulation time for testing
        simulationTime += deltaTime;
        
        // Update player position
        System.arraycopy(playerPos, 0, playerPosition, 0, 3);
        
        // Update observation status
        boolean wasObserved = isBeingObserved;
        isBeingObserved = isObserved;
        
        // Handle first time being seen
        if (isObserved && !wasObserved && currentBehavior == MonsterBehavior.DORMANT) {
            activateMonster();
        }
        
        // Update last seen time
        if (isObserved) {
            lastSeenTime = getCurrentTime();
        }
        
        // Update active time if monster is active
        if (currentBehavior != MonsterBehavior.DORMANT) {
            activeTime += deltaTime;
        }
        
        // Update behavior based on current state
        updateBehavior(deltaTime);
        
        // Update movement
        updateMovement(deltaTime);
        
        // Update audio effects
        updateAudioEffects();
        
        // Check for despawn condition
        checkDespawnCondition();
    }
    
    /**
     * Activate monster when first seen
     */
    private void activateMonster() {
        System.out.println("⚠️ " + monsterType.getDisplayName() + " has been spotted! ⚠️");
        
        lastSeenTime = getCurrentTime();
        currentBehavior = MonsterBehavior.SPAWNING;
        behaviorStartTime = getCurrentTime();
        
        setVisible(true);
        setHostile(true);
        
        // Play activation sound
        playMonsterSound("spotted");
    }
    
    /**
     * Update monster behavior state machine
     * @param deltaTime Time since last update
     */
    private void updateBehavior(double deltaTime) {
        double currentTime = getCurrentTime();
        double timeSinceLastSeen = currentTime - lastSeenTime;
        double behaviorDuration = currentTime - behaviorStartTime;
        float distanceToPlayer = calculateDistanceToPlayer();
        
        switch (currentBehavior) {
            case DORMANT:
                // Waiting to be observed
                break;
                
            case SPAWNING:
                // Brief spawn animation/delay
                if (behaviorDuration > 0.5) {
                    changeBehavior(MonsterBehavior.STALKING);
                }
                break;
                
            case STALKING:
                // Following player but not aggressive
                if (distanceToPlayer < CHASE_DISTANCE) {
                    changeBehavior(MonsterBehavior.CHASING);
                } else if (timeSinceLastSeen > 1.0) {
                    changeBehavior(MonsterBehavior.SEARCHING);
                }
                break;
                
            case CHASING:
                // Actively pursuing player
                if (distanceToPlayer < ATTACK_DISTANCE) {
                    changeBehavior(MonsterBehavior.ATTACKING);
                } else if (timeSinceLastSeen > AGGRESSION_COOLDOWN) {
                    changeBehavior(MonsterBehavior.SEARCHING);
                } else if (distanceToPlayer > CHASE_DISTANCE * 1.5f) {
                    changeBehavior(MonsterBehavior.STALKING);
                }
                break;
                
            case ATTACKING:
                // In combat with player
                if (distanceToPlayer > ATTACK_DISTANCE * 2) {
                    changeBehavior(MonsterBehavior.CHASING);
                } else if (timeSinceLastSeen > 0.5) {
                    changeBehavior(MonsterBehavior.SEARCHING);
                }
                // Handle attack logic here
                performAttack();
                break;
                
            case SEARCHING:
                // Lost sight of player, searching
                if (isBeingObserved) {
                    changeBehavior(MonsterBehavior.CHASING);
                } else if (timeSinceLastSeen > DESPAWN_TIME) {
                    changeBehavior(MonsterBehavior.DESPAWNING);
                } else if (distanceToPlayer < CHASE_DISTANCE / 2) {
                    changeBehavior(MonsterBehavior.CHASING);
                }
                break;
                
            case DESPAWNING:
                // About to disappear
                if (behaviorDuration > 1.0) {
                    despawnMonster();
                }
                break;
        }
    }
    
    /**
     * Change monster behavior state
     * @param newBehavior New behavior to switch to
     */
    private void changeBehavior(MonsterBehavior newBehavior) {
        if (currentBehavior != newBehavior) {
            System.out.println(monsterType.getDisplayName() + " behavior: " + 
                             currentBehavior + " -> " + newBehavior);
            
            currentBehavior = newBehavior;
            behaviorStartTime = getCurrentTime();
            
            // Play appropriate sound for behavior change
            switch (newBehavior) {
                case CHASING:
                    playMonsterSound("chase");
                    break;
                case ATTACKING:
                    playMonsterSound("attack");
                    break;
                case SEARCHING:
                    playMonsterSound("search");
                    break;
                case DESPAWNING:
                    playMonsterSound("despawn");
                    break;
            }
        }
    }
    
    /**
     * Update monster movement based on current behavior
     * @param deltaTime Time since last update
     */
    private void updateMovement(double deltaTime) {
        float moveSpeed = 0.0f;
        
        switch (currentBehavior) {
            case DORMANT:
            case SPAWNING:
                // No movement
                return;
                
            case STALKING:
                moveSpeed = PATROL_SPEED;
                updateStalkingMovement();
                break;
                
            case CHASING:
            case ATTACKING:
                moveSpeed = CHASE_SPEED;
                updateChaseMovement();
                break;
                
            case SEARCHING:
                moveSpeed = PATROL_SPEED * 0.7f;
                updateSearchMovement();
                break;
                
            case DESPAWNING:
                // Slow, erratic movement
                moveSpeed = PATROL_SPEED * 0.3f;
                updateDespawnMovement();
                break;
        }
        
        // Apply movement
        if (moveSpeed > 0.0f) {
            moveTowardsTarget(moveSpeed, deltaTime);
        }
    }
    
    /**
     * Update target position for stalking behavior
     */
    private void updateStalkingMovement() {
        // Follow player at a distance
        float[] offsetPosition = calculateOffsetPosition(8.0f);
        System.arraycopy(offsetPosition, 0, targetPosition, 0, 3);
    }
    
    /**
     * Update target position for chase behavior
     */
    private void updateChaseMovement() {
        // Move directly towards player
        System.arraycopy(playerPosition, 0, targetPosition, 0, 3);
    }
    
    /**
     * Update target position for search behavior
     */
    private void updateSearchMovement() {
        // Search in area where player was last seen
        if (Math.random() < 0.1) { // Change direction occasionally
            float searchRadius = 5.0f;
            targetPosition[0] = playerPosition[0] + (float)(Math.random() - 0.5) * searchRadius * 2;
            targetPosition[2] = playerPosition[2] + (float)(Math.random() - 0.5) * searchRadius * 2;
        }
    }
    
    /**
     * Update target position for despawn behavior
     */
    private void updateDespawnMovement() {
        // Erratic, fade-away movement
        if (Math.random() < 0.2) {
            targetPosition[0] += (float)(Math.random() - 0.5) * 2.0f;
            targetPosition[2] += (float)(Math.random() - 0.5) * 2.0f;
        }
    }
    
    /**
     * Move monster towards target position
     * @param speed Movement speed
     * @param deltaTime Time since last update
     */
    private void moveTowardsTarget(float speed, double deltaTime) {
        float dx = targetPosition[0] - positionX();
        float dy = targetPosition[1] - positionY();
        float dz = targetPosition[2] - positionZ();
        
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance > 0.1f) {
            // Normalize direction
            dx /= distance;
            dy /= distance;
            dz /= distance;
            
            // Move towards target
            float moveDistance = speed * (float)deltaTime;
            setPosition(
                positionX() + dx * moveDistance,
                positionY() + dy * moveDistance,
                positionZ() + dz * moveDistance
            );
        }
    }
    
    /**
     * Calculate an offset position relative to player
     * @param distance Distance from player
     * @return Offset position [x, y, z]
     */
    private float[] calculateOffsetPosition(float distance) {
        float angle = (float) (Math.random() * 2 * Math.PI);
        
        return new float[]{
            playerPosition[0] + (float) Math.cos(angle) * distance,
            playerPosition[1],
            playerPosition[2] + (float) Math.sin(angle) * distance
        };
    }
    
    /**
     * Calculate distance to player
     * @return Distance in world units
     */
    private float calculateDistanceToPlayer() {
        float dx = playerPosition[0] - positionX();
        float dy = playerPosition[1] - positionY();
        float dz = playerPosition[2] - positionZ();
        
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Perform attack on player
     */
    private void performAttack() {
        // Attack logic - for now just sound and message
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSoundTime > 1000) { // Attack every second
            System.out.println("💀 " + monsterType.getDisplayName() + " attacks! 💀");
            playMonsterSound("attack_hit");
            lastSoundTime = currentTime;
            
            // This would integrate with player health system
            // player.takeDamage(monsterType.getThreatLevel());
        }
    }
    
    /**
     * Check if monster should despawn
     */
    private void checkDespawnCondition() {
        double timeSinceLastSeen = getCurrentTime() - lastSeenTime;
        
        if (currentBehavior != MonsterBehavior.DORMANT && 
            currentBehavior != MonsterBehavior.DESPAWNING &&
            timeSinceLastSeen > DESPAWN_TIME) {
            
            changeBehavior(MonsterBehavior.DESPAWNING);
        }
    }
    
    /**
     * Despawn the monster
     */
    private void despawnMonster() {
        System.out.println("👻 " + monsterType.getDisplayName() + " fades back into the shadows...");
        
        currentBehavior = MonsterBehavior.DORMANT;
        lastSeenTime = -1.0;
        activeTime = 0.0;
        
        setVisible(false);
        setHostile(false);
        
        // Respawn in new location
        respawnInNewLocation();
        
        playMonsterSound("despawned");
    }
    
    /**
     * Respawn monster in a new random location
     */
    private void respawnInNewLocation() {
        // Choose new spawn location away from player
        float angle = (float) (Math.random() * 2 * Math.PI);
        float distance = MIN_SPAWN_DISTANCE + 
                        (float) Math.random() * (MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE);
        
        float newX = playerPosition[0] + (float) Math.cos(angle) * distance;
        float newY = playerPosition[1]; // Same Y level
        float newZ = playerPosition[2] + (float) Math.sin(angle) * distance;
        
        setPosition(newX, newY, newZ);
        
        // Update spawn location
        originalSpawnLocation = new float[]{newX, newY, newZ};
        
        System.out.println("Monster respawned at: " + java.util.Arrays.toString(originalSpawnLocation));
    }
    
    /**
     * Update audio effects based on monster state
     */
    private void updateAudioEffects() {
        long currentTime = System.currentTimeMillis();
        float distanceToPlayer = calculateDistanceToPlayer();
        
        // Generate audio cues based on behavior and distance
        switch (currentBehavior) {
            case STALKING:
                if (distanceToPlayer < 10.0f && currentTime - lastSoundTime > 3000) {
                    playMonsterSound("stalk");
                    lastSoundTime = currentTime;
                }
                break;
                
            case CHASING:
                if (currentTime - lastSoundTime > 1500) {
                    playMonsterSound("chase_footsteps");
                    lastSoundTime = currentTime;
                }
                break;
                
            case SEARCHING:
                if (currentTime - lastSoundTime > 4000) {
                    playMonsterSound("search");
                    lastSoundTime = currentTime;
                }
                break;
        }
    }
    
    /**
     * Play monster sound effect
     * @param soundType Type of sound to play
     */
    private void playMonsterSound(String soundType) {
        // This would integrate with the audio system
        String prefix = "♪ [" + monsterType.getDisplayName() + "] ";
        
        switch (soundType) {
            case "spotted":
                System.out.println(prefix + "Sinister presence awakens ♪");
                break;
            case "chase":
                System.out.println(prefix + "Pursuing footsteps ♪");
                break;
            case "attack":
                System.out.println(prefix + "Menacing growl ♪");
                break;
            case "attack_hit":
                System.out.println(prefix + "Violent contact! ♪");
                break;
            case "search":
                System.out.println(prefix + "Searching sounds ♪");
                break;
            case "despawn":
                System.out.println(prefix + "Fading into darkness ♪");
                break;
            case "despawned":
                System.out.println(prefix + "Gone... for now ♪");
                break;
            case "stalk":
                System.out.println(prefix + "Distant following ♪");
                break;
            case "chase_footsteps":
                System.out.println(prefix + "Running footsteps approaching! ♪");
                break;
        }
    }
    
    /**
     * Get current time in seconds
     * @return Current time
     */
    private double getCurrentTime() {
        // Use simulation time if we're being tested (deltaTime updates accumulate)
        // Otherwise use real-world time for actual gameplay
        return simulationTime > 0 ? simulationTime : System.currentTimeMillis() / 1000.0;
    }
    
    // === Monster Interface Implementation ===
    
    @Override
    public double lastSeen() {
        if (lastSeenTime < 0) {
            return -1.0; // Never seen
        }
        
        double timeSinceLastSeen = getCurrentTime() - lastSeenTime;
        if (timeSinceLastSeen > DESPAWN_TIME) {
            return 0.0; // Should be destroyed
        }
        
        return timeSinceLastSeen;
    }
    
    @Override
    public boolean hostile() {
        return currentBehavior == MonsterBehavior.CHASING || 
               currentBehavior == MonsterBehavior.ATTACKING;
    }
    
    @Override
    public double scale() {
        // Scale based on distance and behavior
        float distance = calculateDistanceToPlayer();
        double baseScale = monsterType.getHeight() / 1.8; // Normalize to average human height
        
        // Monsters appear larger when close
        if (distance < 5.0f) {
            baseScale *= 1.2;
        }
        
        // Despawning monsters shrink
        if (currentBehavior == MonsterBehavior.DESPAWNING) {
            double despawnProgress = (getCurrentTime() - behaviorStartTime) / 1.0;
            baseScale *= (1.0 - despawnProgress * 0.5);
        }
        
        return baseScale;
    }
    
    @Override
    public float orientation() {
        // Face towards player when active
        if (currentBehavior != MonsterBehavior.DORMANT) {
            float dx = playerPosition[0] - positionX();
            float dz = playerPosition[2] - positionZ();
            return (float) Math.atan2(dz, dx);
        }
        
        return orientation();
    }
    
    // === Public Interface ===
    
    /**
     * Get current monster behavior
     * @return Current behavior state
     */
    public MonsterBehavior getCurrentBehavior() {
        return currentBehavior;
    }
    
    /**
     * Get monster type
     * @return Monster type
     */
    public MonsterType getMonsterType() {
        return monsterType;
    }
    
    /**
     * Check if monster is currently active
     * @return true if monster is active
     */
    public boolean isActive() {
        return currentBehavior != MonsterBehavior.DORMANT;
    }
    
    /**
     * Check if monster is being observed
     * @return true if player is looking at monster
     */
    public boolean isBeingObserved() {
        return isBeingObserved;
    }
    
    /**
     * Get time since monster became active
     * @return Active time in seconds
     */
    public double getActiveTime() {
        return activeTime;
    }
    
    /**
     * Force despawn monster (for testing or special events)
     */
    public void forceDespawn() {
        changeBehavior(MonsterBehavior.DESPAWNING);
    }
    
    /**
     * Reset monster to dormant state
     */
    public void reset() {
        currentBehavior = MonsterBehavior.DORMANT;
        lastSeenTime = -1.0;
        activeTime = 0.0;
        isBeingObserved = false;
        isDefeated = false;
        
        setVisible(false);
        setHostile(false);
        
        // Return to original spawn location
        setPosition(originalSpawnLocation[0], originalSpawnLocation[1], originalSpawnLocation[2]);
        
        System.out.println(monsterType.getDisplayName() + " reset to dormant state");
    }
    
    @Override
    public String toString() {
        return String.format("%s{behavior=%s, lastSeen=%.1fs, distance=%.1f, active=%.1fs}", 
                           monsterType.getDisplayName(),
                           currentBehavior,
                           lastSeen(),
                           calculateDistanceToPlayer(),
                           activeTime);
    }
}