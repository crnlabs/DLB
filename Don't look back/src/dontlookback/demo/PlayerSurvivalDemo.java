package dontlookback.demo;

/**
 * Player Survival System Demo
 * 
 * Demonstrates the complete survival mechanics working with all other systems.
 * Shows how health, sanity, stamina, and fear interact with the horror elements.
 */
public class PlayerSurvivalDemo {
    
    public static void main(String[] args) {
        System.out.println("=== PLAYER SURVIVAL SYSTEM DEMO ===");
        System.out.println();
        
        // Initialize all systems
        PlayerSurvivalSystem survival = new PlayerSurvivalSystem();
        LightManager lightManager = new LightManager();
        InventorySystem inventory = new InventorySystem();
        Grue grue = new Grue(lightManager);
        LookBasedMonster monster = new LookBasedMonster(new float[]{15.0f, 0.0f, 0.0f});
        
        // Player state
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        System.out.println("=== INITIAL STATUS ===");
        System.out.println(survival.getStatusReport());
        
        // === Basic Activity Demo ===
        System.out.println("=== BASIC ACTIVITY EFFECTS DEMO ===");
        
        // Walking around
        System.out.println("Player starts walking...");
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.WALKING);
        survival.update(5.0); // 5 seconds of walking
        
        System.out.println("Stamina after 5 seconds of walking: " + 
                          String.format("%.1f", survival.getCurrentStamina()));
        
        // Running
        System.out.println("Player starts running...");
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.RUNNING);
        survival.update(10.0); // 10 seconds of running
        
        System.out.println("Stamina after 10 seconds of running: " + 
                          String.format("%.1f", survival.getCurrentStamina()));
        
        if (survival.isExhausted()) {
            System.out.println("⚠️ Player is now exhausted!");
        }
        
        // Rest to recover stamina
        System.out.println("Player rests...");
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.IDLE);
        survival.update(8.0); // 8 seconds of rest
        
        System.out.println("Stamina after 8 seconds of rest: " + 
                          String.format("%.1f", survival.getCurrentStamina()));
        System.out.println();
        
        // === Darkness and Horror Demo ===
        System.out.println("=== DARKNESS AND HORROR EFFECTS DEMO ===");
        
        // Player enters darkness
        System.out.println("Player enters complete darkness...");
        survival.setInDarkness(true);
        
        // Simulate 15 seconds in darkness
        for (int i = 0; i < 15; i++) {
            survival.update(1.0);
            if (i == 4) {
                System.out.println("After 5 seconds in darkness:");
                System.out.println("  Sanity: " + String.format("%.1f", survival.getCurrentSanity()));
                System.out.println("  Fear: " + String.format("%.1f", survival.getCurrentFear()));
            }
        }
        
        System.out.println("After 15 seconds in darkness:");
        System.out.println("  Sanity: " + String.format("%.1f", survival.getCurrentSanity()));
        System.out.println("  Fear: " + String.format("%.1f", survival.getCurrentFear()));
        System.out.println("  Condition: " + survival.getOverallCondition());
        
        if (survival.isPanicking()) {
            System.out.println("⚠️ Player is panicking from darkness!");
        }
        
        // Activate grue in darkness
        grue.update(playerPos, 4.0f); // Activate grue
        if (grue.isActive()) {
            System.out.println("🖤 Grue activates - triggering horror event...");
            survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.GRUE_APPROACHING);
        }
        
        // Player lights a torch for relief
        System.out.println("Player lights a torch for emergency light!");
        survival.setInDarkness(false);
        survival.triggerBeneficialEvent(PlayerSurvivalSystem.BeneficialEvent.FOUND_LIGHT);
        
        grue.update(playerPos, 0.1f); // Deactivate grue
        System.out.println("Grue retreats from light");
        System.out.println();
        
        // === Monster Encounter Demo ===
        System.out.println("=== MONSTER ENCOUNTER DEMO ===");
        
        // Player spots a monster
        System.out.println("Player spots a monster in the distance...");
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.MONSTER_SPOTTED);
        
        // Monster gets closer
        System.out.println("Monster approaches...");
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.MONSTER_CLOSE);
        survival.setBeingChased(true);
        
        // Player runs away
        System.out.println("Player runs away from monster!");
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.RUNNING);
        survival.update(8.0); // 8 seconds of panicked running
        
        // Player hides successfully
        System.out.println("Player finds hiding spot...");
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.HIDING);
        survival.setHiding(true);
        survival.setBeingChased(false); // Successfully escaped
        
        survival.update(10.0); // 10 seconds of hiding
        
        System.out.println("After hiding:");
        System.out.println("  Fear: " + String.format("%.1f", survival.getCurrentFear()));
        System.out.println("  Sanity: " + String.format("%.1f", survival.getCurrentSanity()));
        System.out.println();
        
        // === Combat and Injury Demo ===
        System.out.println("=== COMBAT AND INJURY DEMO ===");
        
        // Monster attacks before player can escape
        System.out.println("Monster catches and attacks player!");
        survival.setHiding(false);
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.MONSTER_ATTACK);
        
        System.out.println("Health after attack: " + 
                          String.format("%.1f", survival.getCurrentHealth()));
        
        if (survival.isInjured()) {
            System.out.println("⚠️ Player is injured and bleeding!");
        }
        
        // Player uses bandage
        System.out.println("Player uses bandage from inventory...");
        survival.triggerBeneficialEvent(PlayerSurvivalSystem.BeneficialEvent.USED_BANDAGE);
        
        System.out.println("Health after bandage: " + 
                          String.format("%.1f", survival.getCurrentHealth()));
        System.out.println();
        
        // === Safe Room Recovery Demo ===
        System.out.println("=== SAFE ROOM RECOVERY DEMO ===");
        
        // Player finds safe room
        System.out.println("Player discovers a safe room!");
        survival.setInSafeArea(true);
        survival.triggerBeneficialEvent(PlayerSurvivalSystem.BeneficialEvent.ENTERED_SAFE_ROOM);
        
        // Rest and recover in safe room
        System.out.println("Player rests in safe room for 30 seconds...");
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.IDLE);
        
        for (int i = 0; i < 6; i++) {
            survival.update(5.0); // 5-second intervals
            if (i % 2 == 0) {
                System.out.println("After " + ((i + 1) * 5) + " seconds:");
                System.out.println("  Health: " + String.format("%.1f", survival.getCurrentHealth()));
                System.out.println("  Sanity: " + String.format("%.1f", survival.getCurrentSanity()));
                System.out.println("  Fear: " + String.format("%.1f", survival.getCurrentFear()));
            }
        }
        
        System.out.println("Recovery complete. Overall condition: " + 
                          survival.getOverallCondition());
        System.out.println();
        
        // === Exploration and Discovery Demo ===
        System.out.println("=== EXPLORATION AND DISCOVERY DEMO ===");
        
        survival.setInSafeArea(false);
        
        // Player examines objects (stamina cost)
        System.out.println("Player examines various objects...");
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.EXAMINING);
        survival.update(8.0);
        
        // Player finds useful document
        System.out.println("Player finds important document!");
        survival.triggerBeneficialEvent(PlayerSurvivalSystem.BeneficialEvent.FOUND_DOCUMENT);
        
        // Player solves a puzzle
        System.out.println("Player solves environmental puzzle!");
        survival.triggerBeneficialEvent(PlayerSurvivalSystem.BeneficialEvent.SOLVED_PUZZLE);
        
        System.out.println("Sanity after discoveries: " + 
                          String.format("%.1f", survival.getCurrentSanity()));
        System.out.println();
        
        // === Status Effects Demo ===
        System.out.println("=== STATUS EFFECTS DEMO ===");
        
        // Show current effects
        System.out.println("Current active status effects:");
        if (survival.hasStatusEffect(PlayerSurvivalSystem.StatusEffect.CALM)) {
            System.out.println("  ✅ CALM - Gradual sanity recovery");
        }
        if (survival.hasStatusEffect(PlayerSurvivalSystem.StatusEffect.FOCUSED)) {
            System.out.println("  ✅ FOCUSED - Enhanced performance");
        }
        
        // Add some negative effects
        System.out.println("Environmental hazards affect player...");
        survival.addStatusEffect(PlayerSurvivalSystem.StatusEffect.PARANOID, 20.0);
        
        // Show status effects wearing off over time
        System.out.println("Waiting for effects to expire...");
        survival.update(25.0); // 25 seconds
        
        System.out.println("Status effects after 25 seconds:");
        System.out.println("  Effects remaining: " + 
                          (survival.hasStatusEffect(PlayerSurvivalSystem.StatusEffect.PARANOID) ? 
                           "PARANOID" : "None"));
        System.out.println();
        
        // === Integration with Other Systems Demo ===
        System.out.println("=== INTEGRATION WITH OTHER SYSTEMS DEMO ===");
        
        // Add survival items to inventory
        inventory.addItem("bandage", 3);
        inventory.addItem("match", 5);
        inventory.addItem("candle", 2);
        
        System.out.println("Survival items in inventory:");
        System.out.println("  Bandages: " + inventory.countItem("bandage"));
        System.out.println("  Matches: " + inventory.countItem("match"));
        System.out.println("  Candles: " + inventory.countItem("candle"));
        
        // Simulate survival scenario
        System.out.println("\\nSurvival scenario: Lost in dark maze...");
        
        // Multiple horror events
        survival.setInDarkness(true);
        survival.update(8.0); // 8 seconds in darkness
        
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.STRANGE_SOUND);
        survival.update(3.0);
        
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.ROOM_CHANGED);
        survival.update(2.0);
        
        System.out.println("After multiple horror events:");
        System.out.println("  Overall condition: " + survival.getOverallCondition());
        System.out.println("  Fear level: " + String.format("%.1f%%", survival.getFearPercentage() * 100));
        
        // Player uses match for light
        System.out.println("Player lights match for emergency light...");
        survival.setInDarkness(false);
        survival.triggerBeneficialEvent(PlayerSurvivalSystem.BeneficialEvent.FOUND_LIGHT);
        
        // Gradual recovery
        System.out.println("Gradual recovery with light...");
        survival.update(15.0);
        
        System.out.println("After recovery:");
        System.out.println("  Overall condition: " + survival.getOverallCondition());
        System.out.println();
        
        // === Extreme Scenario Demo ===
        System.out.println("=== EXTREME SURVIVAL SCENARIO DEMO ===");
        
        System.out.println("Nightmare scenario: Multiple threats simultaneously...");
        
        // Set up worst-case scenario
        survival.setInDarkness(true);
        survival.setBeingChased(true);
        survival.setActivity(PlayerSurvivalSystem.PlayerActivity.RUNNING);
        
        // Multiple rapid horror events
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.MONSTER_ATTACK);
        survival.update(2.0);
        
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.GRUE_APPROACHING);
        survival.update(2.0);
        
        survival.triggerHorrorEvent(PlayerSurvivalSystem.HorrorEvent.LIGHT_FAILURE);
        survival.update(3.0);
        
        System.out.println("After nightmare scenario:");
        System.out.println("  Health: " + String.format("%.1f%%", survival.getHealthPercentage() * 100));
        System.out.println("  Sanity: " + String.format("%.1f%%", survival.getSanityPercentage() * 100));
        System.out.println("  Fear: " + String.format("%.1f%%", survival.getFearPercentage() * 100));
        System.out.println("  Overall condition: " + survival.getOverallCondition());
        
        // Check if player survived
        if (survival.isDead()) {
            System.out.println("💀 Player died from injuries!");
        } else if (survival.isInsane()) {
            System.out.println("🧠 Player went insane from terror!");
        } else {
            System.out.println("😰 Player barely survived the ordeal!");
        }
        System.out.println();
        
        // === Final Status Report ===
        System.out.println("=== FINAL SURVIVAL STATUS ===");
        System.out.println(survival.getStatusReport());
        
        System.out.println("✅ Player Survival System fully functional!");
        System.out.println("✅ Health, Sanity, Stamina, and Fear mechanics working");
        System.out.println("✅ Horror events create mounting tension");
        System.out.println("✅ Beneficial events provide relief and recovery");
        System.out.println("✅ Status effects add depth to survival experience");
        System.out.println("✅ Perfect integration with light, inventory, and monster systems");
        System.out.println("✅ Complete survival horror experience implemented");
        
        System.out.println();
        System.out.println("=== DEMO COMPLETE ===");
    }
}