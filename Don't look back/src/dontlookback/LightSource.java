package dontlookback;

import java.awt.Color;
import dontlookback.interfaces.Light;

/**
 * Light Source Implementation for Don't Look Back
 * 
 * Represents various light sources that protect players from the grue:
 * - Matches (temporary, consumable)
 * - Candles (longer duration, placeable)
 * - Torches (portable, medium duration)
 * - Fireplace (permanent, stationary)
 * - Flashlight (rechargeable, limited battery)
 * 
 * Core to the game's light/darkness survival mechanic.
 * 
 * @author DLB Team
 * @version 1.0
 */
public class LightSource extends Objects implements Light {
    
    // === Light Source Types ===
    
    public enum LightType {
        MATCH("Match", 10.0f, 0.5f, 30.0f, true, true),           // 10 seconds, small radius
        CANDLE("Candle", 300.0f, 2.0f, 120.0f, true, false),     // 5 minutes, medium radius
        TORCH("Torch", 180.0f, 3.0f, 150.0f, false, true),       // 3 minutes, large radius
        FIREPLACE("Fireplace", -1.0f, 5.0f, 300.0f, false, false), // Permanent, huge radius
        FLASHLIGHT("Flashlight", 600.0f, 4.0f, 200.0f, false, true), // 10 minutes, focused beam
        LANTERN("Lantern", 900.0f, 3.5f, 180.0f, true, true);    // 15 minutes, steady light
        
        private final String displayName;
        private final float maxDuration;  // -1 for permanent
        private final float lightRadius;
        private final float intensity;
        private final boolean consumable;
        private final boolean portable;
        
        LightType(String displayName, float maxDuration, float lightRadius, 
                 float intensity, boolean consumable, boolean portable) {
            this.displayName = displayName;
            this.maxDuration = maxDuration;
            this.lightRadius = lightRadius;
            this.intensity = intensity;
            this.consumable = consumable;
            this.portable = portable;
        }
        
        public String getDisplayName() { return displayName; }
        public float getMaxDuration() { return maxDuration; }
        public float getLightRadius() { return lightRadius; }
        public float getIntensity() { return intensity; }
        public boolean isConsumable() { return consumable; }
        public boolean isPortable() { return portable; }
        public boolean isPermanent() { return maxDuration < 0; }
    }
    
    // === Light Source Properties ===
    
    /** Type of light source */
    private final LightType lightType;
    
    /** Current fuel/battery level (0.0 to 1.0) */
    private float fuelLevel;
    
    /** Whether the light is currently active/lit */
    private boolean isLit;
    
    /** Time when the light was turned on (for duration tracking) */
    private long lightStartTime;
    
    /** Current light intensity (0.0 to max intensity) */
    private float currentIntensity;
    
    /** Light color based on type and condition */
    private Color lightColor;
    
    /** Whether this light source has been used/consumed */
    private boolean isConsumed;
    
    /** Flickering state for atmospheric effect */
    private boolean isFlickering;
    private long lastFlickerTime;
    
    /** Light quality degrades over time for realism */
    private float qualityDegradation;
    
    /**
     * Create a new light source
     * @param lightType Type of light source
     * @param position Initial position [x, y, z]
     * @param orientation Initial orientation
     */
    public LightSource(LightType lightType, float[] position, float orientation) {
        super(position, orientation);
        this.lightType = lightType;
        this.fuelLevel = 1.0f;
        this.isLit = false;
        this.lightStartTime = 0;
        this.currentIntensity = 0.0f;
        this.isConsumed = false;
        this.isFlickering = false;
        this.qualityDegradation = 0.0f;
        
        // Set initial color based on light type
        updateLightColor();
        
        // Set visual appearance
        setAppearanceForType();
    }
    
    /**
     * Create a light source at origin
     * @param lightType Type of light source
     */
    public LightSource(LightType lightType) {
        this(lightType, new float[]{0.0f, 0.0f, 0.0f}, 0.0f);
    }
    
    // === Core Light Management ===
    
    /**
     * Light/ignite this light source
     * @return true if successfully lit, false if cannot be lit
     */
    public boolean light() {
        if (isConsumed || fuelLevel <= 0.0f) {
            return false;
        }
        
        if (!isLit) {
            isLit = true;
            lightStartTime = System.currentTimeMillis();
            currentIntensity = lightType.getIntensity();
            updateLightColor();
            
            System.out.println(lightType.getDisplayName() + " lit at position " + 
                              java.util.Arrays.toString(getCenter()));
            return true;
        }
        
        return false; // Already lit
    }
    
    /**
     * Extinguish this light source
     */
    public void extinguish() {
        if (isLit) {
            isLit = false;
            currentIntensity = 0.0f;
            
            // For consumable lights, mark as consumed when extinguished
            if (lightType.isConsumable()) {
                isConsumed = true;
                fuelLevel = 0.0f;
            }
            
            System.out.println(lightType.getDisplayName() + " extinguished");
        }
    }
    
    /**
     * Update light source state (call every frame)
     * @param deltaTime Time since last update in seconds
     */
    public void update(float deltaTime) {
        if (!isLit || lightType.isPermanent()) {
            return;
        }
        
        // Consume fuel over time
        float fuelConsumptionRate = 1.0f / lightType.getMaxDuration();
        fuelLevel -= fuelConsumptionRate * deltaTime;
        
        // Handle fuel depletion
        if (fuelLevel <= 0.0f) {
            fuelLevel = 0.0f;
            extinguish();
            isConsumed = true;
            return;
        }
        
        // Update intensity based on fuel level and quality
        float baseIntensity = lightType.getIntensity();
        float fuelMultiplier = Math.max(0.1f, fuelLevel); // Never completely dark until extinguished
        float qualityMultiplier = Math.max(0.3f, 1.0f - qualityDegradation);
        
        currentIntensity = baseIntensity * fuelMultiplier * qualityMultiplier;
        
        // Handle flickering for atmospheric effect
        updateFlickering(deltaTime);
        
        // Gradual quality degradation
        if (lightType != LightType.FIREPLACE) {
            qualityDegradation += deltaTime * 0.001f; // Very slow degradation
            qualityDegradation = Math.min(0.7f, qualityDegradation); // Maximum degradation
        }
        
        // Update color based on current state
        updateLightColor();
    }
    
    /**
     * Handle flickering effects
     * @param deltaTime Time since last update
     */
    private void updateFlickering(float deltaTime) {
        long currentTime = System.currentTimeMillis();
        
        // Different flickering patterns based on fuel level and light type
        if (fuelLevel < 0.2f) {
            // Critical fuel - frequent flickering
            if (currentTime - lastFlickerTime > 100 + Math.random() * 200) {
                isFlickering = !isFlickering;
                lastFlickerTime = currentTime;
            }
        } else if (fuelLevel < 0.5f) {
            // Low fuel - occasional flickering
            if (currentTime - lastFlickerTime > 500 + Math.random() * 1000) {
                isFlickering = !isFlickering;
                lastFlickerTime = currentTime;
            }
        } else {
            // Normal operation - rare flickering for atmosphere
            if (lightType == LightType.CANDLE || lightType == LightType.TORCH) {
                if (currentTime - lastFlickerTime > 2000 + Math.random() * 3000) {
                    isFlickering = !isFlickering;
                    lastFlickerTime = currentTime;
                }
            } else {
                isFlickering = false;
            }
        }
        
        // Apply flickering to intensity
        if (isFlickering) {
            currentIntensity *= 0.7f + Math.random() * 0.3f; // Flicker between 70-100%
        }
    }
    
    /**
     * Update light color based on type and condition
     */
    private void updateLightColor() {
        float red, green, blue;
        
        switch (lightType) {
            case MATCH:
                // Bright yellow-orange flame
                red = 1.0f;
                green = 0.8f * fuelLevel + 0.2f;
                blue = 0.3f * fuelLevel;
                break;
                
            case CANDLE:
                // Warm yellow glow
                red = 1.0f;
                green = 0.9f * fuelLevel + 0.1f;
                blue = 0.4f * fuelLevel + 0.1f;
                break;
                
            case TORCH:
                // Orange-red flame
                red = 1.0f;
                green = 0.6f * fuelLevel + 0.2f;
                blue = 0.2f * fuelLevel;
                break;
                
            case FIREPLACE:
                // Deep orange-red with yellow highlights
                red = 1.0f;
                green = 0.7f + (float)Math.random() * 0.2f;
                blue = 0.2f + (float)Math.random() * 0.2f;
                break;
                
            case FLASHLIGHT:
                // Cool white light
                red = 0.9f + 0.1f * fuelLevel;
                green = 0.9f + 0.1f * fuelLevel;
                blue = 1.0f;
                break;
                
            case LANTERN:
                // Steady white-yellow
                red = 1.0f;
                green = 1.0f;
                blue = 0.8f + 0.2f * fuelLevel;
                break;
                
            default:
                red = green = blue = 1.0f;
                break;
        }
        
        // Apply quality degradation
        float quality = 1.0f - qualityDegradation;
        red *= quality;
        green *= quality;
        blue *= quality;
        
        // Apply flickering
        if (isFlickering) {
            float flicker = 0.8f + (float)Math.random() * 0.2f;
            red *= flicker;
            green *= flicker;
            blue *= flicker;
        }
        
        lightColor = new Color(
            Math.max(0.0f, Math.min(1.0f, red)),
            Math.max(0.0f, Math.min(1.0f, green)),
            Math.max(0.0f, Math.min(1.0f, blue))
        );
        
        // Update object RGB for rendering
        setRGB(red, green, blue);
    }
    
    /**
     * Set visual appearance based on light type
     */
    private void setAppearanceForType() {
        switch (lightType) {
            case MATCH:
                setRGB(0.9f, 0.7f, 0.3f);
                break;
            case CANDLE:
                setRGB(0.8f, 0.8f, 0.6f);
                break;
            case TORCH:
                setRGB(0.7f, 0.4f, 0.2f);
                break;
            case FIREPLACE:
                setRGB(0.5f, 0.2f, 0.1f);
                break;
            case FLASHLIGHT:
                setRGB(0.3f, 0.3f, 0.3f);
                break;
            case LANTERN:
                setRGB(0.6f, 0.6f, 0.4f);
                break;
        }
    }
    
    // === Distance and Protection Calculations ===
    
    /**
     * Check if a position is within the protection radius of this light
     * @param position Position to check [x, y, z]
     * @return true if position is protected from grue
     */
    public boolean isPositionProtected(float[] position) {
        if (!isLit || isConsumed) {
            return false;
        }
        
        float distance = calculateDistance(position);
        float effectiveRadius = getCurrentRadius();
        
        return distance <= effectiveRadius;
    }
    
    /**
     * Calculate distance to a position
     * @param position Target position [x, y, z]
     * @return Distance in world units
     */
    public float calculateDistance(float[] position) {
        float dx = position[0] - getX();
        float dy = position[1] - getY();
        float dz = position[2] - getZ();
        
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Get current effective light radius
     * @return Current light radius considering all factors
     */
    public float getCurrentRadius() {
        if (!isLit || isConsumed) {
            return 0.0f;
        }
        
        float baseRadius = lightType.getLightRadius();
        float intensityMultiplier = currentIntensity / lightType.getIntensity();
        float qualityMultiplier = 1.0f - qualityDegradation * 0.5f;
        
        return baseRadius * intensityMultiplier * qualityMultiplier;
    }
    
    // === Light Interface Implementation ===
    
    @Override
    public int getIntensity() {
        return Math.round(currentIntensity);
    }
    
    @Override
    public double[] getLightOrientation() {
        return new double[]{(double)super.getOrientation(), 0.0, 0.0}; // Only Y rotation for now
    }
    
    @Override
    public float spread() {
        switch (lightType) {
            case FLASHLIGHT:
                return 30.0f; // Focused beam
            case TORCH:
                return 120.0f; // Wide spread
            default:
                return 360.0f; // Omnidirectional
        }
    }
    
    @Override
    public Color getLightColor() {
        return lightColor;
    }
    
    // === Intangibles Interface Implementation ===
    
    @Override
    public boolean interactable() {
        return lightType.isPortable() && !isConsumed;
    }
    
    @Override
    public boolean visable() {
        return !isConsumed;
    }
    
    @Override
    public boolean field() {
        return isLit; // Creates light field when lit
    }
    
    @Override
    public Object emitter() {
        return isLit ? lightColor : null;
    }
    
    @Override
    public double scale() {
        return isLit ? getCurrentRadius() / lightType.getLightRadius() : 0.0;
    }
    
    @Override
    public boolean hazard() {
        return false; // Light sources are not hazardous
    }
    
    @Override
    public int lifespan() {
        if (lightType.isPermanent()) {
            return -1;
        }
        return Math.round(fuelLevel * lightType.getMaxDuration());
    }
    
    @Override
    public boolean glow() {
        return isLit;
    }
    
    // === Objects Abstract Methods ===
    
    @Override
    public void render() {
        if (isConsumed && lightType.isConsumable()) {
            return; // Don't render consumed items
        }
        
        // Render the light source object
        // This would integrate with the rendering system
        // For now, we'll set color based on state
        if (isLit) {
            float intensity = currentIntensity / lightType.getIntensity();
            setRGB(getRGB()[0] * intensity, getRGB()[1] * intensity, getRGB()[2] * intensity);
        } else {
            // Dim appearance when not lit
            setRGB(getRGB()[0] * 0.3f, getRGB()[1] * 0.3f, getRGB()[2] * 0.3f);
        }
    }
    
    @Override
    public void setColor() {
        updateLightColor();
    }
    
    @Override
    public void setColor(float[] color) {
        setRGB(color);
    }
    
    @Override
    public void setUpVBO() {
        // VBO setup would go here for OpenGL rendering
        // Implementation depends on specific rendering system
    }
    
    @Override
    public void delete() {
        extinguish();
        isConsumed = true;
    }
    
    @Override
    public void behavior() {
        // Light-specific behaviors
        if (isLit && lightType == LightType.CANDLE) {
            // Candles flicker more in drafts
            if (Math.random() < 0.001) {
                isFlickering = true;
            }
        }
    }
    
    @Override
    public void update() {
        update(0.016f); // Default 60 FPS update
    }
    
    // === Getter Methods ===
    
    public LightType getLightType() { return lightType; }
    public float getFuelLevel() { return fuelLevel; }
    public boolean isLit() { return isLit; }
    public boolean isConsumed() { return isConsumed; }
    public boolean isFlickering() { return isFlickering; }
    public float getCurrentIntensity() { return currentIntensity; }
    public long getRemainingTime() {
        if (lightType.isPermanent() || !isLit) {
            return -1;
        }
        return Math.round(fuelLevel * lightType.getMaxDuration() * 1000);
    }
    
    // === Utility Methods ===
    
    /**
     * Add fuel to this light source (if applicable)
     * @param amount Fuel amount (0.0 to 1.0)
     * @return true if fuel was added
     */
    public boolean addFuel(float amount) {
        if (lightType == LightType.FLASHLIGHT && !isConsumed) {
            fuelLevel = Math.min(1.0f, fuelLevel + amount);
            return true;
        }
        return false;
    }
    
    /**
     * Check if this light source needs fuel
     * @return true if fuel level is low
     */
    public boolean needsFuel() {
        return fuelLevel < 0.3f && !lightType.isPermanent();
    }
    
    /**
     * Get status description for UI
     * @return Human-readable status
     */
    public String getStatusDescription() {
        if (isConsumed) {
            return lightType.getDisplayName() + " (Consumed)";
        }
        
        if (!isLit) {
            return lightType.getDisplayName() + " (Unlit)";
        }
        
        if (lightType.isPermanent()) {
            return lightType.getDisplayName() + " (Active)";
        }
        
        int remainingSeconds = Math.round(fuelLevel * lightType.getMaxDuration());
        return lightType.getDisplayName() + " (" + remainingSeconds + "s remaining)";
    }
    
    @Override
    public String toString() {
        return "LightSource{" +
                "type=" + lightType +
                ", fuel=" + String.format("%.1f%%", fuelLevel * 100) +
                ", lit=" + isLit +
                ", intensity=" + String.format("%.1f", currentIntensity) +
                ", position=" + java.util.Arrays.toString(getCenter()) +
                '}';
    }
}