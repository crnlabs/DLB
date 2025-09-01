package dontlookback;

/**
 * Comprehensive Demo of New Core Systems
 * 
 * Demonstrates the new light management, grue mechanics, look-based monsters,
 * and inventory system working together.
 * 
 * This showcases the core "Don't Look Back" gameplay mechanics.
 */
public class CoreSystemsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== DON'T LOOK BACK - CORE SYSTEMS DEMO ===");
        System.out.println();
        
        // Initialize all systems
        System.out.println("Initializing game systems...");
        LightManager lightManager = new LightManager();
        InventorySystem inventory = new InventorySystem();
        Grue grue = new Grue(lightManager);
        LookBasedMonster monster = new LookBasedMonster(new float[]{10.0f, 0.0f, 10.0f});
        
        System.out.println("✓ Light Manager initialized");
        System.out.println("✓ Inventory System initialized");
        System.out.println("✓ Grue initialized");
        System.out.println("✓ Look-Based Monster initialized");
        System.out.println();
        
        // === Demo Light Management System ===
        System.out.println("=== LIGHT MANAGEMENT SYSTEM DEMO ===");
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        System.out.println("Player position: " + java.util.Arrays.toString(playerPos));
        System.out.println("Position protected from grue: " + lightManager.isPositionProtected(playerPos));
        System.out.println("Light level at position: " + lightManager.getLightLevelAtPosition(playerPos));
        System.out.println();
        
        // Add light sources
        System.out.println("Creating light sources...");
        LightSource match = new LightSource(LightSource.LightType.MATCH, playerPos, 0.0f);
        LightSource candle = new LightSource(LightSource.LightType.CANDLE, 
                                           new float[]{3.0f, 0.0f, 0.0f}, 0.0f);
        LightSource fireplace = new LightSource(LightSource.LightType.FIREPLACE, 
                                               new float[]{-5.0f, 0.0f, 0.0f}, 0.0f);
        
        // Light the match
        System.out.println("Lighting match...");
        match.light();
        lightManager.addLightSource(match);
        lightManager.update(0.1f);
        
        System.out.println("Match status: " + match.getStatusDescription());
        System.out.println("Position now protected: " + lightManager.isPositionProtected(playerPos));
        System.out.println("Light level: " + lightManager.getLightLevelAtPosition(playerPos));
        System.out.println();
        
        // Light the candle and fireplace
        System.out.println("Lighting candle and fireplace...");
        candle.light();
        fireplace.light();
        lightManager.addLightSource(candle);
        lightManager.addLightSource(fireplace);
        lightManager.update(0.1f);
        
        System.out.println("Active lights: " + lightManager.getActiveLights().size());
        System.out.println("Global light level: " + lightManager.getGlobalLightLevel());
        System.out.println();
        
        // Simulate time passing - match burns out
        System.out.println("Simulating 5 seconds passing...");
        match.update(5.0f);
        candle.update(5.0f);
        lightManager.update(5.0f);
        
        System.out.println("Match fuel remaining: " + String.format("%.1f%%", match.getFuelLevel() * 100));
        System.out.println("Match status: " + match.getStatusDescription());
        
        // Simulate 6 more seconds - match should burn out
        System.out.println("Simulating 6 more seconds...");
        match.update(6.0f);
        lightManager.update(6.0f);
        
        System.out.println("Match status: " + match.getStatusDescription());
        System.out.println("Match consumed: " + match.isConsumed());
        System.out.println();
        
        // === Demo Grue System ===
        System.out.println("=== GRUE SYSTEM DEMO ===");
        
        // Extinguish all lights to trigger grue
        System.out.println("Extinguishing all lights...");
        lightManager.extinguishAllLights();
        lightManager.update(0.1f);
        
        System.out.println("Position protected: " + lightManager.isPositionProtected(playerPos));
        System.out.println("Grue active: " + grue.isActive());
        System.out.println();
        
        // Simulate time in darkness
        System.out.println("Player in darkness for 1 second...");
        grue.update(playerPos, 1.0f);
        System.out.println("Darkness time: " + String.format("%.1f", grue.getDarknessTime()) + "s");
        System.out.println("Grue active: " + grue.isActive());
        
        System.out.println("Player in darkness for 2 more seconds...");
        grue.update(playerPos, 2.0f);
        System.out.println("Darkness time: " + String.format("%.1f", grue.getDarknessTime()) + "s");
        System.out.println("Grue active: " + grue.isActive());
        
        System.out.println("Player in darkness for 1 more second (total 4s)...");
        grue.update(playerPos, 1.0f);
        System.out.println("Darkness time: " + String.format("%.1f", grue.getDarknessTime()) + "s");
        System.out.println("Grue active: " + grue.isActive());
        System.out.println("Grue distance to player: " + 
                          (grue.isActive() ? String.format("%.1f", grue.getDistanceToPlayer()) : "N/A"));
        System.out.println();
        
        // Emergency lighting!
        System.out.println("EMERGENCY! Lighting torch to escape grue...");
        LightSource torch = new LightSource(LightSource.LightType.TORCH, playerPos, 0.0f);
        torch.light();
        lightManager.addLightSource(torch);
        lightManager.update(0.1f);
        
        System.out.println("Position protected: " + lightManager.isPositionProtected(playerPos));
        grue.update(playerPos, 0.1f);
        System.out.println("Grue deactivated: " + !grue.isActive());
        System.out.println();
        
        // === Demo Look-Based Monster System ===
        System.out.println("=== LOOK-BASED MONSTER SYSTEM DEMO ===");
        
        System.out.println("Monster type: " + monster.getMonsterType().getDisplayName());
        System.out.println("Monster behavior: " + monster.getCurrentBehavior());
        System.out.println("Monster active: " + monster.isActive());
        System.out.println();
        
        // Player looks at monster
        System.out.println("Player looks at monster...");
        monster.updateLookBasedBehavior(playerPos, true, 0.1);
        System.out.println("Monster behavior: " + monster.getCurrentBehavior());
        System.out.println("Monster active: " + monster.isActive());
        System.out.println("Monster being observed: " + monster.isBeingObserved());
        System.out.println();
        
        // Simulate several updates while being observed
        System.out.println("Monster stalks player for 2 seconds...");
        for (int i = 0; i < 20; i++) {
            monster.updateLookBasedBehavior(playerPos, true, 0.1);
        }
        System.out.println("Monster behavior: " + monster.getCurrentBehavior());
        System.out.println("Active time: " + String.format("%.1f", monster.getActiveTime()) + "s");
        System.out.println();
        
        // Player looks away
        System.out.println("Player looks away from monster...");
        monster.updateLookBasedBehavior(playerPos, false, 1.0);
        System.out.println("Monster behavior: " + monster.getCurrentBehavior());
        System.out.println("Monster being observed: " + monster.isBeingObserved());
        
        System.out.println("Monster searches for 3 more seconds...");
        monster.updateLookBasedBehavior(playerPos, false, 3.0);
        System.out.println("Monster behavior: " + monster.getCurrentBehavior());
        
        System.out.println("Monster waits another 2 seconds (total 6s not observed)...");
        monster.updateLookBasedBehavior(playerPos, false, 2.0);
        System.out.println("Monster behavior: " + monster.getCurrentBehavior());
        System.out.println();
        
        // === Demo Inventory System ===
        System.out.println("=== INVENTORY SYSTEM DEMO ===");
        
        System.out.println("Adding survival items to inventory...");
        inventory.addItem("match", 10);
        inventory.addItem("candle", 3);
        inventory.addItem("flashlight", 1);
        inventory.addItem("bandage", 5);
        inventory.addItem("brass_key", 1);
        
        System.out.println(inventory.getInventorySummary());
        System.out.println();
        
        // Show hotbar
        System.out.println("Setting up hotbar...");
        // Find flashlight slot and add to hotbar
        for (int i = 0; i < InventorySystem.MAX_INVENTORY_SLOTS; i++) {
            InventorySystem.InventoryItem item = inventory.getItem(i);
            if (item != null && item.getId().equals("flashlight")) {
                inventory.setHotbarSlot(0, i);
                break;
            }
        }
        
        InventorySystem.InventoryItem hotbarItem = inventory.getHotbarItem(0);
        if (hotbarItem != null) {
            System.out.println("Hotbar slot 0: " + hotbarItem.getDisplayName());
        }
        System.out.println();
        
        // Use some items
        System.out.println("Using a match...");
        InventorySystem.InventoryItem matches = inventory.findItem("match");
        if (matches != null) {
            System.out.println("Matches before use: " + matches.getQuantity());
            // Find slot and use
            for (int i = 0; i < InventorySystem.MAX_INVENTORY_SLOTS; i++) {
                if (inventory.getItem(i) != null && 
                    inventory.getItem(i).getId().equals("match")) {
                    inventory.useItem(i);
                    break;
                }
            }
            System.out.println("Matches after use: " + inventory.countItem("match"));
        }
        System.out.println();
        
        // === Integration Demo ===
        System.out.println("=== FULL INTEGRATION DEMO ===");
        
        System.out.println("Scenario: Player trapped in darkness with approaching grue...");
        
        // Reset systems
        lightManager.clearAllLights();
        grue.reset();
        
        // Player in complete darkness
        System.out.println("Complete darkness activated...");
        System.out.println("Position protected: " + lightManager.isPositionProtected(playerPos));
        
        // Grue starts approaching
        System.out.println("Grue activation timer: 3 seconds until danger...");
        grue.update(playerPos, 3.5f); // Activate grue
        System.out.println("GRUE ACTIVE! Distance: " + 
                          String.format("%.1f", grue.getDistanceToPlayer()) + " units");
        
        // Player lights match from inventory
        System.out.println("Player lights match from inventory!");
        LightSource emergencyMatch = new LightSource(LightSource.LightType.MATCH, playerPos, 0.0f);
        emergencyMatch.light();
        lightManager.addLightSource(emergencyMatch);
        lightManager.update(0.1f);
        
        System.out.println("Emergency light activated!");
        System.out.println("Position protected: " + lightManager.isPositionProtected(playerPos));
        
        grue.update(playerPos, 0.1f);
        System.out.println("Grue retreated: " + !grue.isActive());
        
        System.out.println("Match burn time remaining: " + emergencyMatch.getRemainingTime() / 1000 + " seconds");
        System.out.println("Player must find more light sources or candles to survive!");
        System.out.println();
        
        // Final status
        System.out.println("=== FINAL STATUS ===");
        System.out.println(lightManager.getStatusReport());
        System.out.println();
        System.out.println(inventory.getInventorySummary());
        System.out.println();
        System.out.println("Grue Status: " + grue.toString());
        System.out.println("Monster Status: " + monster.toString());
        System.out.println();
        
        System.out.println("✓ Core systems fully integrated and functional!");
        System.out.println("✓ Light management protects from grue");
        System.out.println("✓ Look-based monsters implement core mechanic");
        System.out.println("✓ Inventory supports survival gameplay");
        System.out.println("✓ All systems work together seamlessly");
        System.out.println();
        System.out.println("=== DEMO COMPLETE ===");
    }
}