package dontlookback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the new core game systems:
 * - Light Management System with Grue mechanics
 * - Look-Based Monster AI
 * - Inventory System
 * 
 * These tests validate the core "Don't Look Back" gameplay mechanics.
 */
public class CoreSystemsTest {
    
    private LightManager lightManager;
    private InventorySystem inventory;
    private Grue grue;
    private LookBasedMonster monster;
    
    @BeforeEach
    void setUp() {
        lightManager = new LightManager();
        inventory = new InventorySystem();
        grue = new Grue(lightManager);
        monster = new LookBasedMonster(new float[]{10.0f, 0.0f, 10.0f});
    }
    
    // === Light Management System Tests ===
    
    @Test
    @DisplayName("Light sources provide protection from grue")
    void testLightProtection() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Initially no protection
        assertFalse(lightManager.isPositionProtected(playerPos));
        
        // Add a lit candle
        LightSource candle = new LightSource(LightSource.LightType.CANDLE, playerPos, 0.0f);
        lightManager.addLightSource(candle);
        candle.light();
        
        // Update light manager to process additions
        lightManager.update(0.1f);
        
        // Now position should be protected
        assertTrue(lightManager.isPositionProtected(playerPos));
        
        // Extinguish candle
        candle.extinguish();
        lightManager.update(0.1f);
        
        // No longer protected
        assertFalse(lightManager.isPositionProtected(playerPos));
    }
    
    @Test
    @DisplayName("Light sources consume fuel over time")
    void testLightFuelConsumption() {
        LightSource match = new LightSource(LightSource.LightType.MATCH);
        
        // Light the match
        assertTrue(match.light());
        assertTrue(match.isLit());
        assertEquals(1.0f, match.getFuelLevel(), 0.01f);
        
        // Simulate 5 seconds (half the match duration)
        match.update(5.0f);
        assertTrue(match.isLit());
        assertEquals(0.5f, match.getFuelLevel(), 0.1f);
        
        // Simulate another 5 seconds (match should be consumed)
        match.update(5.0f);
        assertFalse(match.isLit());
        assertTrue(match.isConsumed());
        assertEquals(0.0f, match.getFuelLevel(), 0.01f);
    }
    
    @Test
    @DisplayName("Different light types have different characteristics")
    void testLightTypeCharacteristics() {
        LightSource match = new LightSource(LightSource.LightType.MATCH);
        LightSource fireplace = new LightSource(LightSource.LightType.FIREPLACE);
        LightSource flashlight = new LightSource(LightSource.LightType.FLASHLIGHT);
        
        // Test duration characteristics
        assertTrue(match.getLightType().isConsumable());
        assertFalse(fireplace.getLightType().isConsumable());
        assertTrue(fireplace.getLightType().isPermanent());
        
        // Test portability
        assertTrue(match.getLightType().isPortable());
        assertFalse(fireplace.getLightType().isPortable());
        assertTrue(flashlight.getLightType().isPortable());
        
        // Test light radius differences
        assertTrue(fireplace.getLightType().getLightRadius() > match.getLightType().getLightRadius());
        assertTrue(flashlight.getLightType().getLightRadius() > match.getLightType().getLightRadius());
    }
    
    // === Grue System Tests ===
    
    @Test
    @DisplayName("Grue activates after time in darkness")
    void testGrueActivation() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Initially grue is dormant
        assertFalse(grue.isActive());
        
        // Simulate 2 seconds in darkness (not enough to activate)
        grue.update(playerPos, 2.0f);
        assertFalse(grue.isActive());
        
        // Simulate another 2 seconds (total 4 seconds, still not enough)
        grue.update(playerPos, 2.0f);
        assertFalse(grue.isActive());
        
        // Simulate 1 more second (total 5 seconds, should activate)
        grue.update(playerPos, 1.0f);
        assertTrue(grue.isActive());
    }
    
    @Test
    @DisplayName("Grue deactivates when light is restored")
    void testGrueDeactivation() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Force activate grue
        grue.forceActivate();
        assertTrue(grue.isActive());
        
        // Add light source and update
        LightSource torch = new LightSource(LightSource.LightType.TORCH, playerPos, 0.0f);
        lightManager.addLightSource(torch);
        torch.light();
        lightManager.update(0.1f);
        
        // Update grue - should deactivate due to light
        grue.update(playerPos, 0.1f);
        assertFalse(grue.isActive());
    }
    
    @Test
    @DisplayName("Grue tracks distance to player")
    void testGrueDistanceTracking() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Activate grue
        grue.forceActivate();
        
        // Initial distance
        float initialDistance = grue.getDistanceToPlayer();
        assertTrue(initialDistance > 0);
        
        // Update several times - grue should move closer
        for (int i = 0; i < 10; i++) {
            grue.update(playerPos, 0.1f);
        }
        
        float finalDistance = grue.getDistanceToPlayer();
        assertTrue(finalDistance < initialDistance, "Grue should move closer to player");
    }
    
    // === Look-Based Monster Tests ===
    
    @Test
    @DisplayName("Monster activates when observed")
    void testMonsterActivation() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Initially dormant
        assertEquals(LookBasedMonster.MonsterBehavior.DORMANT, monster.getCurrentBehavior());
        assertFalse(monster.isActive());
        
        // Observe monster
        monster.updateLookBasedBehavior(playerPos, true, 0.1);
        
        // Should activate
        assertTrue(monster.isActive());
        assertNotEquals(LookBasedMonster.MonsterBehavior.DORMANT, monster.getCurrentBehavior());
    }
    
    @Test
    @DisplayName("Monster despawns after not being observed")
    void testMonsterDespawn() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Activate monster
        monster.updateLookBasedBehavior(playerPos, true, 0.1);
        assertTrue(monster.isActive());
        
        // Stop observing for 6 seconds (longer than despawn time)
        monster.updateLookBasedBehavior(playerPos, false, 6.0);
        
        // Should be marked for despawn or dormant
        assertTrue(monster.getCurrentBehavior() == LookBasedMonster.MonsterBehavior.DESPAWNING ||
                  monster.getCurrentBehavior() == LookBasedMonster.MonsterBehavior.DORMANT);
    }
    
    @Test
    @DisplayName("Monster behavior changes based on distance")
    void testMonsterBehaviorTransitions() {
        float[] closePos = {1.0f, 0.0f, 1.0f}; // Close to monster spawn
        float[] farPos = {50.0f, 0.0f, 50.0f}; // Far from monster
        
        // Activate monster with close position
        monster.updateLookBasedBehavior(closePos, true, 0.1);
        
        // Should eventually chase when close
        for (int i = 0; i < 10; i++) {
            monster.updateLookBasedBehavior(closePos, true, 0.1);
        }
        
        // Monster should be in aggressive state when close
        assertTrue(monster.getCurrentBehavior() == LookBasedMonster.MonsterBehavior.CHASING ||
                  monster.getCurrentBehavior() == LookBasedMonster.MonsterBehavior.ATTACKING ||
                  monster.getCurrentBehavior() == LookBasedMonster.MonsterBehavior.STALKING);
    }
    
    @Test
    @DisplayName("Different monster types have different properties")
    void testMonsterTypes() {
        LookBasedMonster shadowFigure = new LookBasedMonster(
            LookBasedMonster.MonsterType.SHADOW_FIGURE, new float[]{0, 0, 0});
        LookBasedMonster tallStalker = new LookBasedMonster(
            LookBasedMonster.MonsterType.TALL_STALKER, new float[]{0, 0, 0});
        
        // Different types have different threat levels
        assertNotEquals(shadowFigure.threatLevel(), tallStalker.threatLevel());
        
        // Different types have different heights
        assertNotEquals(shadowFigure.getMonsterType().getHeight(), 
                       tallStalker.getMonsterType().getHeight());
    }
    
    // === Inventory System Tests ===
    
    @Test
    @DisplayName("Inventory can store and retrieve items")
    void testBasicInventoryOperations() {
        // Add some matches
        assertTrue(inventory.addItem("match", 5));
        assertEquals(5, inventory.countItem("match"));
        
        // Add a candle
        assertTrue(inventory.addItem("candle", 1));
        assertEquals(1, inventory.countItem("candle"));
        
        // Find items
        InventorySystem.InventoryItem matches = inventory.findItem("match");
        assertNotNull(matches);
        assertEquals("Match", matches.getDisplayName());
        assertEquals(5, matches.getQuantity());
    }
    
    @Test
    @DisplayName("Inventory handles item stacking")
    void testInventoryStacking() {
        // Add matches in separate operations
        assertTrue(inventory.addItem("match", 3));
        assertTrue(inventory.addItem("match", 7));
        
        // Should stack together
        assertEquals(10, inventory.countItem("match"));
        
        // Should be in a single slot
        InventorySystem.InventoryItem matches = inventory.findItem("match");
        assertNotNull(matches);
        assertEquals(10, matches.getQuantity());
    }
    
    @Test
    @DisplayName("Inventory tracks weight limits")
    void testInventoryWeight() {
        // Initially empty
        assertEquals(0.0f, inventory.getCurrentWeight(), 0.01f);
        
        // Add some items
        inventory.addItem("torch", 5); // 5.0kg total
        inventory.addItem("flashlight", 2); // 4.0kg total
        
        // Check weight is tracked
        assertTrue(inventory.getCurrentWeight() > 8.0f);
        assertTrue(inventory.getWeightPercentage() > 0.08f); // Over 8% of capacity
    }
    
    @Test
    @DisplayName("Inventory item usage works correctly")
    void testInventoryItemUsage() {
        // Add a bandage
        inventory.addItem("bandage", 3);
        assertEquals(3, inventory.countItem("bandage"));
        
        // Find the slot containing bandages
        InventorySystem.InventoryItem bandage = inventory.findItem("bandage");
        assertNotNull(bandage);
        
        // Find which slot it's in (simple search)
        int slotIndex = -1;
        for (int i = 0; i < InventorySystem.MAX_INVENTORY_SLOTS; i++) {
            InventorySystem.InventoryItem item = inventory.getItem(i);
            if (item != null && item.getId().equals("bandage")) {
                slotIndex = i;
                break;
            }
        }
        
        assertTrue(slotIndex >= 0, "Should find bandage in a slot");
        
        // Use one bandage
        assertTrue(inventory.useItem(slotIndex));
        assertEquals(2, inventory.countItem("bandage"));
    }
    
    @Test
    @DisplayName("Hotbar functionality works")
    void testHotbarFunctionality() {
        // Add item to inventory
        inventory.addItem("flashlight", 1);
        
        // Set hotbar slot to point to inventory slot containing flashlight
        InventorySystem.InventoryItem flashlight = inventory.findItem("flashlight");
        assertNotNull(flashlight);
        
        // Find the slot containing the flashlight
        for (int i = 0; i < InventorySystem.MAX_INVENTORY_SLOTS; i++) {
            if (inventory.getItem(i) != null && 
                inventory.getItem(i).getId().equals("flashlight")) {
                
                assertTrue(inventory.setHotbarSlot(0, i));
                break;
            }
        }
        
        // Get item from hotbar
        InventorySystem.InventoryItem hotbarItem = inventory.getHotbarItem(0);
        assertNotNull(hotbarItem);
        assertEquals("flashlight", hotbarItem.getId());
    }
    
    // === Integration Tests ===
    
    @Test
    @DisplayName("Full horror scenario: player in darkness with approaching grue")
    void testFullHorrorScenario() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Add matches to inventory
        inventory.addItem("match", 3);
        
        // Simulate being in darkness for activation time
        grue.update(playerPos, 4.0f); // 4 seconds, almost activation
        assertFalse(grue.isActive());
        
        // One more second triggers grue
        grue.update(playerPos, 1.0f);
        assertTrue(grue.isActive());
        assertTrue(grue.getDistanceToPlayer() > 0);
        
        // Player lights a match
        InventorySystem.InventoryItem matches = inventory.findItem("match");
        assertNotNull(matches);
        
        LightSource lightedMatch = new LightSource(LightSource.LightType.MATCH, playerPos, 0.0f);
        lightedMatch.light();
        lightManager.addLightSource(lightedMatch);
        lightManager.update(0.1f);
        
        // Position should now be protected
        assertTrue(lightManager.isPositionProtected(playerPos));
        
        // Grue should deactivate
        grue.update(playerPos, 0.1f);
        assertFalse(grue.isActive());
        
        // Match burns out after 10 seconds
        lightedMatch.update(10.1f);
        assertFalse(lightedMatch.isLit());
        assertTrue(lightedMatch.isConsumed());
        
        // Position no longer protected
        lightManager.update(0.1f);
        assertFalse(lightManager.isPositionProtected(playerPos));
    }
    
    @Test
    @DisplayName("Monster and light interaction scenario")
    void testMonsterLightInteraction() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Player observes monster
        monster.updateLookBasedBehavior(playerPos, true, 0.1);
        assertTrue(monster.isActive());
        
        // Add light to area
        LightSource torch = new LightSource(LightSource.LightType.TORCH, playerPos, 0.0f);
        torch.light();
        lightManager.addLightSource(torch);
        lightManager.update(0.1f);
        
        // Update monster behavior (monsters can exist in light, unlike grue)
        monster.updateLookBasedBehavior(playerPos, true, 0.1);
        assertTrue(monster.isActive()); // Monster should still be active
        
        // Stop observing monster
        monster.updateLookBasedBehavior(playerPos, false, 6.0);
        
        // Monster should eventually despawn
        assertTrue(monster.getCurrentBehavior() == LookBasedMonster.MonsterBehavior.DESPAWNING ||
                  monster.getCurrentBehavior() == LookBasedMonster.MonsterBehavior.DORMANT);
    }
    
    @Test
    @DisplayName("Complete game system integration")
    void testCompleteSystemIntegration() {
        float[] playerPos = {0.0f, 0.0f, 0.0f};
        
        // Set up inventory with survival items
        inventory.addItem("match", 10);
        inventory.addItem("candle", 3);
        inventory.addItem("flashlight", 1);
        inventory.addItem("bandage", 5);
        
        // Verify all systems are initialized
        assertNotNull(lightManager);
        assertNotNull(inventory);
        assertNotNull(grue);
        assertNotNull(monster);
        
        // Test light management
        assertTrue(lightManager.getActiveLights().isEmpty());
        assertEquals(0.05f, lightManager.getGlobalLightLevel(), 0.01f); // Ambient light level
        
        // Test monster is dormant
        assertFalse(monster.isActive());
        assertEquals(LookBasedMonster.MonsterBehavior.DORMANT, monster.getCurrentBehavior());
        
        // Test grue is dormant
        assertFalse(grue.isActive());
        assertEquals(0.0f, grue.getDarknessTime(), 0.01f);
        
        // Test inventory has items
        assertEquals(10, inventory.countItem("match"));
        assertEquals(3, inventory.countItem("candle"));
        assertEquals(1, inventory.countItem("flashlight"));
        assertEquals(5, inventory.countItem("bandage"));
        
        // Verify systems can be updated without errors
        lightManager.update(0.1f);
        grue.update(playerPos, 0.1f);
        monster.updateLookBasedBehavior(playerPos, false, 0.1);
        
        System.out.println("✓ All core systems integrated and functional!");
    }
}