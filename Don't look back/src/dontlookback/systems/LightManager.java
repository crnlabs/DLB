package dontlookback.systems;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Light Management System for Don't Look Back
 * 
 * Manages all light sources in the game world and determines whether
 * positions are protected from the grue. Handles dynamic lighting,
 * light source lifecycle, and environmental lighting effects.
 * 
 * Features:
 * - Global light source registry
 * - Position protection calculation
 * - Environmental lighting simulation
 * - Light flickering and failure events
 * - Power management systems
 * - Atmospheric lighting effects
 * 
 * @author DLB Team
 * @version 1.0
 */
public class LightManager {
    
    // === Light Management Constants ===
    
    /** Maximum number of active light sources */
    private static final int MAX_ACTIVE_LIGHTS = 50;
    
    /** Ambient light level (never completely dark for gameplay) */
    private static final float AMBIENT_LIGHT_LEVEL = 0.05f;
    
    /** Distance for light source combination effects */
    private static final float LIGHT_COMBINATION_DISTANCE = 3.0f;
    
    /** Frequency of environmental light events (seconds) */
    private static final float EVENT_CHECK_INTERVAL = 5.0f;
    
    // === Light Source Management ===
    
    /** All active light sources in the world */
    private final List<LightSource> activeLights;
    
    /** Light sources pending addition (thread-safe) */
    private final Queue<LightSource> pendingAdditions;
    
    /** Light sources pending removal (thread-safe) */
    private final Queue<LightSource> pendingRemovals;
    
    /** Environmental light sources (fixed installations) */
    private final List<LightSource> environmentalLights;
    
    /** Player-owned light sources */
    private final List<LightSource> playerLights;
    
    // === Environmental Lighting ===
    
    /** Global light level (0.0 = pitch black, 1.0 = full light) */
    private float globalLightLevel;
    
    /** Whether power is available (affects electrical lights) */
    private boolean powerAvailable;
    
    /** Time until next environmental event */
    private float timeToNextEvent;
    
    /** Random generator for environmental effects */
    private final Random random;
    
    // === State Tracking ===
    
    /** Last update time for delta calculations */
    private long lastUpdateTime;
    
    /** Statistics for debugging */
    private int totalLightSources;
    private int activeLightSources;
    private float averageLightLevel;
    
    /** Settings for light management */
    private boolean enableFlickering;
    private boolean enablePowerFailures;
    private float lightFailureChance;
    
    /**
     * Create a new Light Manager
     */
    public LightManager() {
        this.activeLights = new CopyOnWriteArrayList<>();
        this.pendingAdditions = new ArrayDeque<>();
        this.pendingRemovals = new ArrayDeque<>();
        this.environmentalLights = new ArrayList<>();
        this.playerLights = new ArrayList<>();
        this.random = new Random();
        
        // Initialize environmental settings
        this.globalLightLevel = AMBIENT_LIGHT_LEVEL;
        this.powerAvailable = true;
        this.timeToNextEvent = EVENT_CHECK_INTERVAL;
        this.lastUpdateTime = System.currentTimeMillis();
        
        // Initialize settings
        this.enableFlickering = true;
        this.enablePowerFailures = true;
        this.lightFailureChance = 0.001f; // 0.1% chance per frame
        
        System.out.println("Light Manager initialized - tracking darkness and light sources");
    }
    
    // === Light Source Registration ===
    
    /**
     * Add a light source to the world
     * @param lightSource Light source to add
     * @return true if successfully added
     */
    public boolean addLightSource(LightSource lightSource) {
        if (lightSource == null) {
            return false;
        }
        
        synchronized (pendingAdditions) {
            if (activeLights.size() + pendingAdditions.size() >= MAX_ACTIVE_LIGHTS) {
                System.out.println("Warning: Maximum light sources reached, cannot add: " + 
                                 lightSource.getLightType());
                return false;
            }
            
            pendingAdditions.offer(lightSource);
            System.out.println("Light source queued for addition: " + lightSource.getLightType() + 
                             " at " + Arrays.toString(lightSource.getCenter()));
            return true;
        }
    }
    
    /**
     * Remove a light source from the world
     * @param lightSource Light source to remove
     * @return true if successfully queued for removal
     */
    public boolean removeLightSource(LightSource lightSource) {
        if (lightSource == null) {
            return false;
        }
        
        synchronized (pendingRemovals) {
            pendingRemovals.offer(lightSource);
            System.out.println("Light source queued for removal: " + lightSource.getLightType());
            return true;
        }
    }
    
    /**
     * Process pending light source additions and removals
     */
    private void processPendingChanges() {
        // Process additions
        synchronized (pendingAdditions) {
            while (!pendingAdditions.isEmpty() && activeLights.size() < MAX_ACTIVE_LIGHTS) {
                LightSource light = pendingAdditions.poll();
                activeLights.add(light);
                
                // Categorize light source
                if (light.getLightType() == LightSource.LightType.FIREPLACE) {
                    environmentalLights.add(light);
                } else if (light.getLightType().isPortable()) {
                    playerLights.add(light);
                } else {
                    environmentalLights.add(light);
                }
                
                System.out.println("Light source added: " + light.getLightType());
            }
        }
        
        // Process removals
        synchronized (pendingRemovals) {
            while (!pendingRemovals.isEmpty()) {
                LightSource light = pendingRemovals.poll();
                activeLights.remove(light);
                environmentalLights.remove(light);
                playerLights.remove(light);
                
                System.out.println("Light source removed: " + light.getLightType());
            }
        }
    }
    
    // === Position Protection Calculation ===
    
    /**
     * Check if a position is protected from the grue by any light source
     * @param position Position to check [x, y, z]
     * @return true if position is safe from grue
     */
    public boolean isPositionProtected(float[] position) {
        if (position == null || position.length != 3) {
            return false;
        }
        
        // Check global light level first
        if (globalLightLevel > 0.3f) {
            return true; // Sufficient ambient light
        }
        
        // Check each active light source
        for (LightSource light : activeLights) {
            if (light.isPositionProtected(position)) {
                return true;
            }
        }
        
        return false; // Position is in darkness
    }
    
    /**
     * Get the combined light level at a position
     * @param position Position to check [x, y, z]
     * @return Light level from 0.0 (dark) to 1.0+ (very bright)
     */
    public float getLightLevelAtPosition(float[] position) {
        if (position == null || position.length != 3) {
            return 0.0f;
        }
        
        float totalLight = globalLightLevel;
        
        // Add light contributions from all sources
        for (LightSource light : activeLights) {
            if (!light.isLit()) continue;
            
            float distance = light.calculateDistance(position);
            float radius = light.getCurrentRadius();
            
            if (distance <= radius) {
                // Calculate light falloff (inverse square law with minimum)
                float falloff = Math.max(0.1f, 1.0f - (distance * distance) / (radius * radius));
                float contribution = (light.getCurrentIntensity() / 100.0f) * falloff;
                totalLight += contribution;
            }
        }
        
        return Math.min(2.0f, totalLight); // Cap at 2x for bright areas
    }
    
    /**
     * Find the nearest light source to a position
     * @param position Position to check [x, y, z]
     * @return Nearest light source, or null if none found
     */
    public LightSource getNearestLightSource(float[] position) {
        if (position == null || activeLights.isEmpty()) {
            return null;
        }
        
        LightSource nearest = null;
        float nearestDistance = Float.MAX_VALUE;
        
        for (LightSource light : activeLights) {
            if (!light.isLit()) continue;
            
            float distance = light.calculateDistance(position);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = light;
            }
        }
        
        return nearest;
    }
    
    /**
     * Get all light sources within a radius of a position
     * @param position Center position [x, y, z]
     * @param radius Search radius
     * @return List of light sources within radius
     */
    public List<LightSource> getLightSourcesInRadius(float[] position, float radius) {
        List<LightSource> nearbyLights = new ArrayList<>();
        
        if (position == null) {
            return nearbyLights;
        }
        
        for (LightSource light : activeLights) {
            if (light.calculateDistance(position) <= radius) {
                nearbyLights.add(light);
            }
        }
        
        return nearbyLights;
    }
    
    // === Environmental Effects ===
    
    /**
     * Update all light sources and environmental effects
     * @param deltaTime Time since last update in seconds
     */
    public void update(float deltaTime) {
        long currentTime = System.currentTimeMillis();
        lastUpdateTime = currentTime;
        
        // Process pending changes
        processPendingChanges();
        
        // Update all light sources
        updateLightSources(deltaTime);
        
        // Update environmental effects
        updateEnvironmentalEffects(deltaTime);
        
        // Update statistics
        updateStatistics();
        
        // Handle random environmental events
        handleEnvironmentalEvents(deltaTime);
    }
    
    /**
     * Update all individual light sources
     * @param deltaTime Time since last update
     */
    private void updateLightSources(float deltaTime) {
        List<LightSource> toRemove = new ArrayList<>();
        
        for (LightSource light : activeLights) {
            // Update light source
            light.update(deltaTime);
            
            // Mark consumed light sources for removal
            if (light.isConsumed() && light.getLightType().isConsumable()) {
                toRemove.add(light);
            }
        }
        
        // Remove consumed light sources
        for (LightSource light : toRemove) {
            activeLights.remove(light);
            environmentalLights.remove(light);
            playerLights.remove(light);
            System.out.println("Consumed light source removed: " + light.getLightType());
        }
    }
    
    /**
     * Update environmental lighting effects
     * @param deltaTime Time since last update
     */
    private void updateEnvironmentalEffects(float deltaTime) {
        // Gradually adjust global light level based on active lights
        int activeLightCount = (int) activeLights.stream().mapToLong(light -> light.isLit() ? 1 : 0).sum();
        
        float targetGlobalLight = AMBIENT_LIGHT_LEVEL;
        if (activeLightCount > 10) {
            targetGlobalLight += 0.1f; // Slight ambient boost with many lights
        }
        
        // Smooth transition to target light level
        float lightChangeRate = 0.5f * deltaTime;
        if (globalLightLevel < targetGlobalLight) {
            globalLightLevel = Math.min(targetGlobalLight, globalLightLevel + lightChangeRate);
        } else if (globalLightLevel > targetGlobalLight) {
            globalLightLevel = Math.max(targetGlobalLight, globalLightLevel - lightChangeRate);
        }
    }
    
    /**
     * Handle random environmental events
     * @param deltaTime Time since last update
     */
    private void handleEnvironmentalEvents(float deltaTime) {
        timeToNextEvent -= deltaTime;
        
        if (timeToNextEvent <= 0.0f) {
            timeToNextEvent = EVENT_CHECK_INTERVAL + random.nextFloat() * EVENT_CHECK_INTERVAL;
            
            // Random events
            if (enablePowerFailures && random.nextFloat() < 0.1f) {
                triggerPowerFlicker();
            }
            
            if (enableFlickering && random.nextFloat() < 0.3f) {
                triggerRandomFlickering();
            }
        }
        
        // Continuous random light failures
        if (random.nextFloat() < lightFailureChance) {
            causeRandomLightFailure();
        }
    }
    
    /**
     * Trigger a power flicker event
     */
    private void triggerPowerFlicker() {
        System.out.println("⚡ Power flicker! ⚡");
        
        // Briefly affect electrical lights
        for (LightSource light : activeLights) {
            if (light.getLightType() == LightSource.LightType.FLASHLIGHT && 
                light.isLit() && random.nextFloat() < 0.5f) {
                
                // Create temporary flicker effect
                System.out.println(light.getLightType() + " flickers from power surge");
            }
        }
        
        // Brief global light reduction
        globalLightLevel *= 0.8f;
    }
    
    /**
     * Trigger random flickering in candles and torches
     */
    private void triggerRandomFlickering() {
        for (LightSource light : activeLights) {
            if ((light.getLightType() == LightSource.LightType.CANDLE || 
                 light.getLightType() == LightSource.LightType.TORCH) && 
                light.isLit() && random.nextFloat() < 0.2f) {
                
                System.out.println(light.getLightType() + " flickers in the draft");
            }
        }
    }
    
    /**
     * Cause a random light to fail
     */
    private void causeRandomLightFailure() {
        List<LightSource> activeLitLights = new ArrayList<>();
        for (LightSource light : activeLights) {
            if (light.isLit() && !light.getLightType().isPermanent()) {
                activeLitLights.add(light);
            }
        }
        
        if (!activeLitLights.isEmpty()) {
            LightSource failingLight = activeLitLights.get(random.nextInt(activeLitLights.size()));
            
            // Different failure types based on light type
            switch (failingLight.getLightType()) {
                case MATCH:
                    if (random.nextFloat() < 0.1f) {
                        failingLight.extinguish();
                        System.out.println("💨 Match burned out unexpectedly!");
                    }
                    break;
                    
                case CANDLE:
                    if (random.nextFloat() < 0.05f) {
                        failingLight.extinguish();
                        System.out.println("🕯️ Candle was blown out by a draft!");
                    }
                    break;
                    
                case FLASHLIGHT:
                    if (random.nextFloat() < 0.02f) {
                        failingLight.extinguish();
                        System.out.println("🔦 Flashlight batteries died!");
                    }
                    break;
            }
        }
    }
    
    /**
     * Update internal statistics
     */
    private void updateStatistics() {
        totalLightSources = activeLights.size();
        activeLightSources = (int) activeLights.stream().mapToLong(light -> light.isLit() ? 1 : 0).sum();
        
        if (activeLightSources > 0) {
            float totalIntensity = 0.0f;
            for (LightSource light : activeLights) {
                if (light.isLit()) {
                    totalIntensity += light.getCurrentIntensity();
                }
            }
            averageLightLevel = totalIntensity / activeLightSources;
        } else {
            averageLightLevel = 0.0f;
        }
    }
    
    // === Public Interface ===
    
    /**
     * Get all active light sources
     * @return Immutable list of active lights
     */
    public List<LightSource> getActiveLights() {
        return Collections.unmodifiableList(activeLights);
    }
    
    /**
     * Get player-owned light sources
     * @return List of portable light sources
     */
    public List<LightSource> getPlayerLights() {
        return Collections.unmodifiableList(playerLights);
    }
    
    /**
     * Get environmental light sources
     * @return List of fixed light sources
     */
    public List<LightSource> getEnvironmentalLights() {
        return Collections.unmodifiableList(environmentalLights);
    }
    
    /**
     * Get global light level
     * @return Current global light level (0.0 to 1.0)
     */
    public float getGlobalLightLevel() {
        return globalLightLevel;
    }
    
    /**
     * Check if power is available for electrical devices
     * @return true if power is available
     */
    public boolean isPowerAvailable() {
        return powerAvailable;
    }
    
    /**
     * Set power availability
     * @param available Whether power should be available
     */
    public void setPowerAvailable(boolean available) {
        if (powerAvailable != available) {
            powerAvailable = available;
            System.out.println("Power " + (available ? "restored" : "failed"));
            
            // Affect electrical lights
            if (!available) {
                for (LightSource light : activeLights) {
                    if (light.getLightType() == LightSource.LightType.FLASHLIGHT && light.isLit()) {
                        light.extinguish();
                        System.out.println("Flashlight disabled due to power failure");
                    }
                }
            }
        }
    }
    
    // === Settings and Configuration ===
    
    /**
     * Enable/disable light flickering effects
     * @param enabled Whether flickering should occur
     */
    public void setFlickeringEnabled(boolean enabled) {
        this.enableFlickering = enabled;
    }
    
    /**
     * Enable/disable power failure events
     * @param enabled Whether power failures should occur
     */
    public void setPowerFailuresEnabled(boolean enabled) {
        this.enablePowerFailures = enabled;
    }
    
    /**
     * Set the chance of random light failures
     * @param chance Failure chance per frame (0.0 to 1.0)
     */
    public void setLightFailureChance(float chance) {
        this.lightFailureChance = Math.max(0.0f, Math.min(1.0f, chance));
    }
    
    // === Utility Methods ===
    
    /**
     * Emergency light all candles and torches (panic button)
     */
    public void emergencyLightAll() {
        System.out.println("🔥 EMERGENCY LIGHTING ACTIVATED 🔥");
        
        int lightsLit = 0;
        for (LightSource light : activeLights) {
            if (!light.isLit() && !light.isConsumed() && 
                (light.getLightType() == LightSource.LightType.CANDLE || 
                 light.getLightType() == LightSource.LightType.TORCH)) {
                
                if (light.light()) {
                    lightsLit++;
                }
            }
        }
        
        System.out.println("Emergency lighting lit " + lightsLit + " sources");
    }
    
    /**
     * Extinguish all lights (for testing or special events)
     */
    public void extinguishAllLights() {
        System.out.println("💨 Extinguishing all lights...");
        
        for (LightSource light : activeLights) {
            if (light.isLit() && !light.getLightType().isPermanent()) {
                light.extinguish();
            }
        }
        
        System.out.println("All non-permanent lights extinguished");
    }
    
    /**
     * Get detailed status information
     * @return Status string for debugging
     */
    public String getStatusReport() {
        return String.format(
            "LightManager Status:\n" +
            "  Total Lights: %d\n" +
            "  Active Lights: %d\n" +
            "  Global Light Level: %.2f\n" +
            "  Average Intensity: %.1f\n" +
            "  Power Available: %s\n" +
            "  Environmental Lights: %d\n" +
            "  Player Lights: %d",
            totalLightSources,
            activeLightSources,
            globalLightLevel,
            averageLightLevel,
            powerAvailable ? "Yes" : "No",
            environmentalLights.size(),
            playerLights.size()
        );
    }
    
    /**
     * Clear all light sources (for level transitions)
     */
    public void clearAllLights() {
        activeLights.clear();
        environmentalLights.clear();
        playerLights.clear();
        pendingAdditions.clear();
        pendingRemovals.clear();
        
        globalLightLevel = AMBIENT_LIGHT_LEVEL;
        System.out.println("All light sources cleared");
    }
    
    @Override
    public String toString() {
        return String.format("LightManager{lights=%d, active=%d, globalLevel=%.2f}", 
                           totalLightSources, activeLightSources, globalLightLevel);
    }
}