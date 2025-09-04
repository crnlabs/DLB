package dontlookback.systems;

/**
 * Player Survival Mechanics for Don't Look Back
 * 
 * Manages the player's survival stats that are affected by horror elements:
 * - Health: Physical wellbeing, reduced by monster attacks
 * - Sanity: Mental state, reduced by horror events and darkness
 * - Stamina: Physical energy for running and interactions
 * - Fear: Accumulated terror that affects all other stats
 * 
 * These systems work together to create mounting tension and challenge.
 * 
 * @author DLB Team
 * @version 1.0
 */
public class PlayerSurvivalSystem {
    
    // === Survival Constants ===
    
    /** Maximum health points */
    public static final float MAX_HEALTH = 100.0f;
    
    /** Maximum sanity points */
    public static final float MAX_SANITY = 100.0f;
    
    /** Maximum stamina points */
    public static final float MAX_STAMINA = 100.0f;
    
    /** Maximum fear level */
    public static final float MAX_FEAR = 100.0f;
    
    /** Critical thresholds */
    private static final float CRITICAL_HEALTH = 20.0f;
    private static final float CRITICAL_SANITY = 25.0f;
    private static final float CRITICAL_STAMINA = 15.0f;
    private static final float HIGH_FEAR = 75.0f;
    
    /** Regeneration rates per second */
    private static final float HEALTH_REGEN_RATE = 1.0f;    // Slow natural healing
    private static final float SANITY_REGEN_RATE = 2.0f;    // Faster in safe areas
    private static final float STAMINA_REGEN_RATE = 10.0f;  // Quick stamina recovery
    private static final float FEAR_DECAY_RATE = 0.5f;      // Very slow fear reduction
    
    /** Consumption rates per second */
    private static final float STAMINA_WALK_RATE = 2.0f;    // Walking stamina cost
    private static final float STAMINA_RUN_RATE = 15.0f;    // Running stamina cost
    private static final float SANITY_DARKNESS_RATE = 5.0f; // Sanity loss in darkness
    private static final float SANITY_FEAR_RATE = 3.0f;     // Sanity loss from high fear
    
    // === Current Stats ===
    
    private float currentHealth;
    private float currentSanity;
    private float currentStamina;
    private float currentFear;
    
    // === State Tracking ===
    
    private boolean isInDarkness;
    private boolean isBeingChased;
    private boolean isHiding;
    private boolean isInSafeArea;
    private boolean isExhausted;
    private boolean isPanicking;
    private boolean isInjured;
    
    // === Activity Tracking ===
    
    private PlayerActivity currentActivity;
    private long lastActivityTime;
    private double timeInCurrentActivity;
    private double totalDarknessTime;
    private double totalFearTime;
    
    /** Player activities that affect survival stats */
    public enum PlayerActivity {
        IDLE(0.0f, 0.0f, 0.0f),           // Standing still
        WALKING(STAMINA_WALK_RATE, 0.0f, 0.0f),     // Normal movement
        RUNNING(STAMINA_RUN_RATE, 0.0f, 1.0f),      // Fast movement
        HIDING(0.0f, -1.0f, -2.0f),       // Hiding (sanity and fear reduction)
        EXAMINING(5.0f, -0.5f, 0.0f),     // Examining objects (stamina cost)
        INTERACTING(3.0f, 0.0f, 0.0f);    // Using items/doors
        
        private final float staminaCost;
        private final float sanityChange;
        private final float fearChange;
        
        PlayerActivity(float staminaCost, float sanityChange, float fearChange) {
            this.staminaCost = staminaCost;
            this.sanityChange = sanityChange;
            this.fearChange = fearChange;
        }
        
        public float getStaminaCost() { return staminaCost; }
        public float getSanityChange() { return sanityChange; }
        public float getFearChange() { return fearChange; }
    }
    
    // === Horror Events ===
    
    /** Horror events that affect player stats */
    public enum HorrorEvent {
        MONSTER_SPOTTED(0.0f, -15.0f, 20.0f),        // See a monster
        MONSTER_CLOSE(0.0f, -10.0f, 15.0f),          // Monster nearby
        MONSTER_ATTACK(-25.0f, -20.0f, 30.0f),       // Monster attacks
        GRUE_APPROACHING(0.0f, -25.0f, 35.0f),       // Grue is coming
        LIGHT_FAILURE(0.0f, -8.0f, 12.0f),           // Light goes out
        STRANGE_SOUND(0.0f, -5.0f, 8.0f),            // Unexplained noise
        DOOR_SLAM(0.0f, -3.0f, 5.0f),                // Door slams shut
        ROOM_CHANGED(0.0f, -10.0f, 15.0f),           // Room regenerated
        DARKNESS_EXTENDED(0.0f, -5.0f, 8.0f),        // Long time in darkness
        ITEM_DISAPPEARED(0.0f, -5.0f, 10.0f);        // Expected item gone
        
        private final float healthChange;
        private final float sanityChange;
        private final float fearChange;
        
        HorrorEvent(float healthChange, float sanityChange, float fearChange) {
            this.healthChange = healthChange;
            this.sanityChange = sanityChange;
            this.fearChange = fearChange;
        }
        
        public float getHealthChange() { return healthChange; }
        public float getSanityChange() { return sanityChange; }
        public float getFearChange() { return fearChange; }
    }
    
    // === Beneficial Events ===
    
    /** Positive events that restore player stats */
    public enum BeneficialEvent {
        FOUND_LIGHT(0.0f, 10.0f, -5.0f),             // Found light source
        ENTERED_SAFE_ROOM(0.0f, 20.0f, -15.0f),      // Safe room discovered
        USED_BANDAGE(15.0f, 0.0f, 0.0f),             // Health item used
        RESTED_SAFELY(5.0f, 15.0f, -10.0f),          // Rest in safe area
        ESCAPED_MONSTER(0.0f, 5.0f, -20.0f),         // Successfully escaped
        SOLVED_PUZZLE(0.0f, 8.0f, -5.0f),            // Puzzle completed
        FOUND_DOCUMENT(0.0f, 3.0f, -2.0f),           // Information discovered
        LIGHT_RESTORED(0.0f, 15.0f, -10.0f);         // Power/light restored
        
        private final float healthChange;
        private final float sanityChange;
        private final float fearChange;
        
        BeneficialEvent(float healthChange, float sanityChange, float fearChange) {
            this.healthChange = healthChange;
            this.sanityChange = sanityChange;
            this.fearChange = fearChange;
        }
        
        public float getHealthChange() { return healthChange; }
        public float getSanityChange() { return sanityChange; }
        public float getFearChange() { return fearChange; }
    }
    
    // === Effects and Conditions ===
    
    /** Status effects that modify player state */
    public enum StatusEffect {
        BLEEDING(-2.0f, 0.0f, 0.0f, 0.0f),           // Health drain
        EXHAUSTED(0.0f, 0.0f, -50.0f, 0.0f),         // Stamina penalty
        TERRIFIED(0.0f, -3.0f, 0.0f, 2.0f),          // Sanity drain, fear gain
        PARANOID(0.0f, -1.0f, 0.0f, 1.0f),           // Mild sanity drain
        ADRENALINE(0.0f, 0.0f, 25.0f, 0.0f),         // Stamina boost
        CALM(0.0f, 2.0f, 0.0f, -1.0f),               // Sanity boost, fear reduction
        FOCUSED(0.0f, 1.0f, 5.0f, 0.0f);             // Slight boosts
        
        private final float healthChange;
        private final float sanityChange;
        private final float staminaChange;
        private final float fearChange;
        
        StatusEffect(float healthChange, float sanityChange, float staminaChange, float fearChange) {
            this.healthChange = healthChange;
            this.sanityChange = sanityChange;
            this.staminaChange = staminaChange;
            this.fearChange = fearChange;
        }
        
        public float getHealthChange() { return healthChange; }
        public float getSanityChange() { return sanityChange; }
        public float getStaminaChange() { return staminaChange; }
        public float getFearChange() { return fearChange; }
    }
    
    // === Status Effect Management ===
    
    private final java.util.Map<StatusEffect, Double> activeEffects;
    private final java.util.Map<StatusEffect, Double> effectDurations;
    
    /**
     * Initialize survival system with full stats
     */
    public PlayerSurvivalSystem() {
        // Start with full stats
        this.currentHealth = MAX_HEALTH;
        this.currentSanity = MAX_SANITY;
        this.currentStamina = MAX_STAMINA;
        this.currentFear = 0.0f;
        
        // Initialize state
        this.isInDarkness = false;
        this.isBeingChased = false;
        this.isHiding = false;
        this.isInSafeArea = false;
        this.isExhausted = false;
        this.isPanicking = false;
        this.isInjured = false;
        
        // Initialize activity tracking
        this.currentActivity = PlayerActivity.IDLE;
        this.lastActivityTime = System.currentTimeMillis();
        this.timeInCurrentActivity = 0.0;
        this.totalDarknessTime = 0.0;
        this.totalFearTime = 0.0;
        
        // Initialize effects
        this.activeEffects = new java.util.HashMap<>();
        this.effectDurations = new java.util.HashMap<>();
        
        System.out.println("Player Survival System initialized - all stats at maximum");
    }
    
    // === Core Update Method ===
    
    /**
     * Update all survival stats based on current conditions
     * @param deltaTime Time since last update in seconds
     */
    public void update(double deltaTime) {
        // Update activity time tracking
        timeInCurrentActivity += deltaTime;
        
        // Update total time tracking
        if (isInDarkness) {
            totalDarknessTime += deltaTime;
        }
        if (currentFear > HIGH_FEAR) {
            totalFearTime += deltaTime;
        }
        
        // Apply activity effects
        applyActivityEffects(deltaTime);
        
        // Apply environmental effects
        applyEnvironmentalEffects(deltaTime);
        
        // Apply status effects
        applyStatusEffects(deltaTime);
        
        // Apply natural regeneration
        applyNaturalRegeneration(deltaTime);
        
        // Update derived states
        updateDerivedStates();
        
        // Process status effect durations
        updateStatusEffectDurations(deltaTime);
        
        // Check for automatic events
        checkAutomaticEvents(deltaTime);
    }
    
    /**
     * Apply effects from current player activity
     */
    private void applyActivityEffects(double deltaTime) {
        float staminaCost = currentActivity.getStaminaCost() * (float)deltaTime;
        float sanityChange = currentActivity.getSanityChange() * (float)deltaTime;
        float fearChange = currentActivity.getFearChange() * (float)deltaTime;
        
        // Apply stamina cost
        if (staminaCost > 0) {
            modifyStamina(-staminaCost);
        }
        
        // Apply sanity change
        if (sanityChange != 0) {
            modifySanity(sanityChange);
        }
        
        // Apply fear change
        if (fearChange != 0) {
            modifyFear(fearChange);
        }
    }
    
    /**
     * Apply effects from environmental conditions
     */
    private void applyEnvironmentalEffects(double deltaTime) {
        // Darkness effects
        if (isInDarkness && !isInSafeArea) {
            modifySanity(-SANITY_DARKNESS_RATE * (float)deltaTime);
            
            // Escalating fear in darkness
            float fearMultiplier = 1.0f + (float)(totalDarknessTime / 10.0); // More fear over time
            modifyFear(3.0f * fearMultiplier * (float)deltaTime);
        }
        
        // Being chased effects
        if (isBeingChased) {
            modifyFear(10.0f * (float)deltaTime);
            modifySanity(-8.0f * (float)deltaTime);
        }
        
        // High fear effects
        if (currentFear > HIGH_FEAR) {
            modifySanity(-SANITY_FEAR_RATE * (float)deltaTime);
        }
        
        // Safe area benefits
        if (isInSafeArea && !isBeingChased) {
            modifySanity(5.0f * (float)deltaTime);
            modifyFear(-3.0f * (float)deltaTime);
        }
        
        // Hiding benefits
        if (isHiding && !isBeingChased) {
            modifyFear(-8.0f * (float)deltaTime);
            modifySanity(2.0f * (float)deltaTime);
        }
    }
    
    /**
     * Apply active status effects
     */
    private void applyStatusEffects(double deltaTime) {
        for (java.util.Map.Entry<StatusEffect, Double> entry : activeEffects.entrySet()) {
            StatusEffect effect = entry.getKey();
            
            modifyHealth(effect.getHealthChange() * (float)deltaTime);
            modifySanity(effect.getSanityChange() * (float)deltaTime);
            modifyStamina(effect.getStaminaChange() * (float)deltaTime);
            modifyFear(effect.getFearChange() * (float)deltaTime);
        }
    }
    
    /**
     * Apply natural regeneration
     */
    private void applyNaturalRegeneration(double deltaTime) {
        // Health regeneration (only when not injured and well-fed)
        if (!isInjured && currentHealth < MAX_HEALTH && currentStamina > 50.0f) {
            modifyHealth(HEALTH_REGEN_RATE * (float)deltaTime);
        }
        
        // Sanity regeneration (only in safe conditions)
        if (isInSafeArea && !isInDarkness && currentFear < 50.0f) {
            modifySanity(SANITY_REGEN_RATE * (float)deltaTime);
        }
        
        // Stamina regeneration (when not running)
        if (currentActivity != PlayerActivity.RUNNING && 
            currentActivity != PlayerActivity.EXAMINING) {
            float regenRate = STAMINA_REGEN_RATE;
            
            // Faster regen when resting
            if (currentActivity == PlayerActivity.IDLE || isHiding) {
                regenRate *= 1.5f;
            }
            
            modifyStamina(regenRate * (float)deltaTime);
        }
        
        // Fear decay (very slow)
        if (!isInDarkness && !isBeingChased) {
            modifyFear(-FEAR_DECAY_RATE * (float)deltaTime);
        }
    }
    
    /**
     * Update derived states based on current stats
     */
    private void updateDerivedStates() {
        // Update exhaustion state
        boolean wasExhausted = isExhausted;
        isExhausted = currentStamina < CRITICAL_STAMINA;
        
        if (isExhausted && !wasExhausted) {
            System.out.println("💪 Player is exhausted - movement will be slower");
            addStatusEffect(StatusEffect.EXHAUSTED, 30.0); // 30 seconds
        }
        
        // Update panic state
        boolean wasPanicking = isPanicking;
        isPanicking = currentSanity < CRITICAL_SANITY || currentFear > HIGH_FEAR;
        
        if (isPanicking && !wasPanicking) {
            System.out.println("😰 Player is panicking - performance degraded");
            addStatusEffect(StatusEffect.TERRIFIED, 20.0); // 20 seconds
        } else if (!isPanicking && wasPanicking) {
            removeStatusEffect(StatusEffect.TERRIFIED);
        }
        
        // Update injured state
        isInjured = currentHealth < CRITICAL_HEALTH;
        
        if (isInjured) {
            addStatusEffect(StatusEffect.BLEEDING, 5.0); // 5 second intervals
        }
    }
    
    /**
     * Update status effect durations
     */
    private void updateStatusEffectDurations(double deltaTime) {
        java.util.Iterator<java.util.Map.Entry<StatusEffect, Double>> iterator = 
            effectDurations.entrySet().iterator();
        
        while (iterator.hasNext()) {
            java.util.Map.Entry<StatusEffect, Double> entry = iterator.next();
            StatusEffect effect = entry.getKey();
            double remainingTime = entry.getValue() - deltaTime;
            
            if (remainingTime <= 0) {
                // Effect expired
                iterator.remove();
                activeEffects.remove(effect);
                System.out.println("Status effect expired: " + effect);
            } else {
                entry.setValue(remainingTime);
            }
        }
    }
    
    /**
     * Check for automatic events based on conditions
     */
    private void checkAutomaticEvents(double deltaTime) {
        // Extended darkness event
        if (isInDarkness && totalDarknessTime > 30.0) {
            if (Math.random() < 0.1 * deltaTime) { // 10% chance per second
                triggerHorrorEvent(HorrorEvent.DARKNESS_EXTENDED);
            }
        }
        
        // Random strange sounds in darkness
        if (isInDarkness && Math.random() < 0.05 * deltaTime) { // 5% chance per second
            triggerHorrorEvent(HorrorEvent.STRANGE_SOUND);
        }
        
        // Automatic recovery in safe areas
        if (isInSafeArea && timeInCurrentActivity > 10.0) {
            if (Math.random() < 0.2 * deltaTime) { // 20% chance per second
                triggerBeneficialEvent(BeneficialEvent.RESTED_SAFELY);
            }
        }
    }
    
    // === Stat Modification Methods ===
    
    /**
     * Modify health with bounds checking
     */
    private void modifyHealth(float change) {
        float oldHealth = currentHealth;
        currentHealth = Math.max(0.0f, Math.min(MAX_HEALTH, currentHealth + change));
        
        if (change < 0 && currentHealth != oldHealth) {
            System.out.println("❤️ Health: " + String.format("%.1f", currentHealth) + "/" + MAX_HEALTH + 
                             " (" + String.format("%.1f", change) + ")");
        }
    }
    
    /**
     * Modify sanity with bounds checking
     */
    private void modifySanity(float change) {
        float oldSanity = currentSanity;
        currentSanity = Math.max(0.0f, Math.min(MAX_SANITY, currentSanity + change));
        
        if (Math.abs(change) > 1.0f && currentSanity != oldSanity) {
            System.out.println("🧠 Sanity: " + String.format("%.1f", currentSanity) + "/" + MAX_SANITY + 
                             " (" + String.format("%.1f", change) + ")");
        }
    }
    
    /**
     * Modify stamina with bounds checking
     */
    private void modifyStamina(float change) {
        currentStamina = Math.max(0.0f, Math.min(MAX_STAMINA, currentStamina + change));
    }
    
    /**
     * Modify fear with bounds checking
     */
    private void modifyFear(float change) {
        float oldFear = currentFear;
        currentFear = Math.max(0.0f, Math.min(MAX_FEAR, currentFear + change));
        
        if (change > 2.0f && currentFear != oldFear) {
            System.out.println("😨 Fear: " + String.format("%.1f", currentFear) + "/" + MAX_FEAR + 
                             " (" + String.format("%.1f", change) + ")");
        }
    }
    
    // === Event Triggers ===
    
    /**
     * Trigger a horror event
     * @param event Horror event that occurred
     */
    public void triggerHorrorEvent(HorrorEvent event) {
        modifyHealth(event.getHealthChange());
        modifySanity(event.getSanityChange());
        modifyFear(event.getFearChange());
        
        System.out.println("💀 Horror Event: " + event + 
                          " (Health: " + String.format("%.1f", event.getHealthChange()) + 
                          ", Sanity: " + String.format("%.1f", event.getSanityChange()) + 
                          ", Fear: " + String.format("%.1f", event.getFearChange()) + ")");
        
        // Special effects for certain events
        switch (event) {
            case MONSTER_ATTACK:
                addStatusEffect(StatusEffect.BLEEDING, 15.0);
                addStatusEffect(StatusEffect.TERRIFIED, 30.0);
                break;
            case GRUE_APPROACHING:
                addStatusEffect(StatusEffect.TERRIFIED, 45.0);
                break;
            case ROOM_CHANGED:
                addStatusEffect(StatusEffect.PARANOID, 60.0);
                break;
        }
    }
    
    /**
     * Trigger a beneficial event
     * @param event Beneficial event that occurred
     */
    public void triggerBeneficialEvent(BeneficialEvent event) {
        modifyHealth(event.getHealthChange());
        modifySanity(event.getSanityChange());
        modifyFear(event.getFearChange());
        
        System.out.println("✨ Beneficial Event: " + event + 
                          " (Health: " + String.format("%.1f", event.getHealthChange()) + 
                          ", Sanity: " + String.format("%.1f", event.getSanityChange()) + 
                          ", Fear: " + String.format("%.1f", event.getFearChange()) + ")");
        
        // Special effects for certain events
        switch (event) {
            case ENTERED_SAFE_ROOM:
                addStatusEffect(StatusEffect.CALM, 120.0);
                removeStatusEffect(StatusEffect.TERRIFIED);
                break;
            case USED_BANDAGE:
                removeStatusEffect(StatusEffect.BLEEDING);
                break;
            case ESCAPED_MONSTER:
                addStatusEffect(StatusEffect.ADRENALINE, 10.0);
                break;
            case SOLVED_PUZZLE:
                addStatusEffect(StatusEffect.FOCUSED, 30.0);
                break;
        }
    }
    
    // === Status Effect Management ===
    
    /**
     * Add a status effect with duration
     * @param effect Status effect to add
     * @param duration Duration in seconds
     */
    public void addStatusEffect(StatusEffect effect, double duration) {
        activeEffects.put(effect, duration);
        effectDurations.put(effect, duration);
        System.out.println("Status effect added: " + effect + " (" + duration + "s)");
    }
    
    /**
     * Remove a status effect
     * @param effect Status effect to remove
     */
    public void removeStatusEffect(StatusEffect effect) {
        if (activeEffects.remove(effect) != null) {
            effectDurations.remove(effect);
            System.out.println("Status effect removed: " + effect);
        }
    }
    
    /**
     * Check if player has a specific status effect
     * @param effect Status effect to check
     * @return true if player has the effect
     */
    public boolean hasStatusEffect(StatusEffect effect) {
        return activeEffects.containsKey(effect);
    }
    
    // === State Setters ===
    
    /**
     * Set environmental conditions
     */
    public void setInDarkness(boolean inDarkness) {
        if (this.isInDarkness != inDarkness) {
            this.isInDarkness = inDarkness;
            if (!inDarkness) {
                totalDarknessTime = 0.0; // Reset darkness timer
                triggerBeneficialEvent(BeneficialEvent.LIGHT_RESTORED);
            }
        }
    }
    
    public void setBeingChased(boolean beingChased) {
        if (this.isBeingChased != beingChased) {
            this.isBeingChased = beingChased;
            if (!beingChased) {
                triggerBeneficialEvent(BeneficialEvent.ESCAPED_MONSTER);
            }
        }
    }
    
    public void setHiding(boolean hiding) { this.isHiding = hiding; }
    public void setInSafeArea(boolean inSafeArea) { this.isInSafeArea = inSafeArea; }
    
    /**
     * Set player activity
     * @param activity New activity
     */
    public void setActivity(PlayerActivity activity) {
        if (this.currentActivity != activity) {
            this.currentActivity = activity;
            this.timeInCurrentActivity = 0.0;
            this.lastActivityTime = System.currentTimeMillis();
        }
    }
    
    // === Getters ===
    
    public float getCurrentHealth() { return currentHealth; }
    public float getCurrentSanity() { return currentSanity; }
    public float getCurrentStamina() { return currentStamina; }
    public float getCurrentFear() { return currentFear; }
    
    public float getHealthPercentage() { return currentHealth / MAX_HEALTH; }
    public float getSanityPercentage() { return currentSanity / MAX_SANITY; }
    public float getStaminaPercentage() { return currentStamina / MAX_STAMINA; }
    public float getFearPercentage() { return currentFear / MAX_FEAR; }
    
    public boolean isInDarkness() { return isInDarkness; }
    public boolean isBeingChased() { return isBeingChased; }
    public boolean isHiding() { return isHiding; }
    public boolean isInSafeArea() { return isInSafeArea; }
    public boolean isExhausted() { return isExhausted; }
    public boolean isPanicking() { return isPanicking; }
    public boolean isInjured() { return isInjured; }
    public boolean isDead() { return currentHealth <= 0.0f; }
    public boolean isInsane() { return currentSanity <= 0.0f; }
    
    public PlayerActivity getCurrentActivity() { return currentActivity; }
    public double getTimeInCurrentActivity() { return timeInCurrentActivity; }
    public double getTotalDarknessTime() { return totalDarknessTime; }
    public double getTotalFearTime() { return totalFearTime; }
    
    // === Utility Methods ===
    
    /**
     * Get overall condition assessment
     * @return Overall player condition
     */
    public String getOverallCondition() {
        if (isDead()) return "DEAD";
        if (isInsane()) return "INSANE";
        
        float averageCondition = (getHealthPercentage() + getSanityPercentage() + 
                                 getStaminaPercentage() + (1.0f - getFearPercentage())) / 4.0f;
        
        if (averageCondition > 0.8f) return "EXCELLENT";
        if (averageCondition > 0.6f) return "GOOD";
        if (averageCondition > 0.4f) return "FAIR";
        if (averageCondition > 0.2f) return "POOR";
        return "CRITICAL";
    }
    
    /**
     * Get detailed status report
     * @return Comprehensive status string
     */
    public String getStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PLAYER SURVIVAL STATUS ===\n");
        sb.append(String.format("Health:  %.1f/%.1f (%.0f%%) %s\n", 
                               currentHealth, MAX_HEALTH, getHealthPercentage() * 100,
                               isInjured ? "[INJURED]" : ""));
        sb.append(String.format("Sanity:  %.1f/%.1f (%.0f%%) %s\n", 
                               currentSanity, MAX_SANITY, getSanityPercentage() * 100,
                               isPanicking ? "[PANICKING]" : ""));
        sb.append(String.format("Stamina: %.1f/%.1f (%.0f%%) %s\n", 
                               currentStamina, MAX_STAMINA, getStaminaPercentage() * 100,
                               isExhausted ? "[EXHAUSTED]" : ""));
        sb.append(String.format("Fear:    %.1f/%.1f (%.0f%%)\n", 
                               currentFear, MAX_FEAR, getFearPercentage() * 100));
        
        sb.append("\nCondition: ").append(getOverallCondition()).append("\n");
        sb.append("Activity: ").append(currentActivity).append("\n");
        
        sb.append("\nEnvironment:\n");
        sb.append("  In Darkness: ").append(isInDarkness).append("\n");
        sb.append("  Being Chased: ").append(isBeingChased).append("\n");
        sb.append("  Hiding: ").append(isHiding).append("\n");
        sb.append("  Safe Area: ").append(isInSafeArea).append("\n");
        
        if (!activeEffects.isEmpty()) {
            sb.append("\nActive Effects:\n");
            for (java.util.Map.Entry<StatusEffect, Double> entry : effectDurations.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(" (")
                  .append(String.format("%.1f", entry.getValue())).append("s)\n");
            }
        }
        
        sb.append("\nTime Tracking:\n");
        sb.append("  Total Darkness Time: ").append(String.format("%.1f", totalDarknessTime)).append("s\n");
        sb.append("  Total Fear Time: ").append(String.format("%.1f", totalFearTime)).append("s\n");
        
        return sb.toString();
    }
    
    /**
     * Reset all stats to maximum (for testing/new game)
     */
    public void reset() {
        currentHealth = MAX_HEALTH;
        currentSanity = MAX_SANITY;
        currentStamina = MAX_STAMINA;
        currentFear = 0.0f;
        
        isInDarkness = false;
        isBeingChased = false;
        isHiding = false;
        isInSafeArea = false;
        isExhausted = false;
        isPanicking = false;
        isInjured = false;
        
        currentActivity = PlayerActivity.IDLE;
        timeInCurrentActivity = 0.0;
        totalDarknessTime = 0.0;
        totalFearTime = 0.0;
        
        activeEffects.clear();
        effectDurations.clear();
        
        System.out.println("Player survival stats reset to maximum");
    }
    
    @Override
    public String toString() {
        return String.format("PlayerSurvival{health=%.1f, sanity=%.1f, stamina=%.1f, fear=%.1f, condition=%s}", 
                           currentHealth, currentSanity, currentStamina, currentFear, getOverallCondition());
    }
}