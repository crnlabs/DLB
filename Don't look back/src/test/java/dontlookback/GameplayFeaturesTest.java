package dontlookback;

/**
 * Test suite for new gameplay features
 * 
 * Tests the enhanced Player class functionality including:
 * - Inventory system
 * - Controller support
 * - Player profile persistence
 * - Movement tracking
 * 
 * @author DLB Team
 */
public class GameplayFeaturesTest {
    
    public static void main(String[] args) {
        System.out.println("=== Gameplay Features Test Suite ===");
        
        testInventorySystem();
        testControllerInput();
        testPlayerProfile();
        testMovementTracking();
        
        System.out.println("=== All Gameplay Features Tests Completed ===");
    }
    
    /**
     * Test the new inventory system
     */
    private static void testInventorySystem() {
        System.out.println("\n--- Testing Inventory System ---");
        
        try {
            Player player = new Player("TestPlayer");
            
            // Test initial state
            assert !player.isHolding() : "Player should not be holding anything initially";
            assert player.heldItem() == null : "Held item should be null initially";
            
            // Test basic functionality (since Objects class might not be fully implemented)
            Objects[] inventory = player.inventory();
            assert inventory != null : "Inventory should not be null";
            assert inventory.length == 10 : "Inventory should have 10 slots";
            
            System.out.println("✓ Inventory system basic functionality works");
            
            // Test reaction time
            assert player.reactionTime() == 250 : "Default reaction time should be 250ms";
            player.setReactionTime(200);
            assert player.reactionTime() == 200 : "Reaction time should update";
            
            System.out.println("✓ Player stats system works");
            
            // Test debuff system
            int[] debuffs = player.deBuffs();
            assert debuffs.length == 0 : "Should have no debuffs initially";
            
            player.addDebuff(1);
            player.addDebuff(2);
            debuffs = player.deBuffs();
            assert debuffs.length == 2 : "Should have 2 debuffs";
            
            player.removeDebuff(1);
            debuffs = player.deBuffs();
            assert debuffs.length == 1 : "Should have 1 debuff remaining";
            
            System.out.println("✓ Debuff system works");
            
        } catch (Exception e) {
            System.out.println("✗ Inventory system test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test controller input system
     */
    private static void testControllerInput() {
        System.out.println("\n--- Testing Controller Input ---");
        
        try {
            InputManager input = new InputManager();
            
            // Test basic state
            assert !input.isButtonPressed(InputManager.BUTTON_MOVE_FORWARD) : "Button should not be pressed initially";
            assert !input.isButtonJustPressed(InputManager.BUTTON_INTERACT) : "Button should not be just pressed initially";
            
            // Test movement vector
            float[] movement = input.getMovementVector();
            assert movement != null : "Movement vector should not be null";
            assert movement.length == 2 : "Movement vector should have X and Y components";
            assert movement[0] == 0.0f && movement[1] == 0.0f : "Movement should be zero initially";
            
            // Test configuration
            input.setSensitivity(0.5f);
            input.setControllerEnabled(false);
            input.setKeyboardEnabled(true);
            
            System.out.println("✓ Controller input system basic functionality works");
            
            // Test safety features
            assert !input.isLookBackTriggered() : "Look back should not be triggered initially";
            String warning = input.getDangerWarning();
            assert warning == null : "Should have no danger warning initially";
            
            System.out.println("✓ Safety features work");
            
        } catch (Exception e) {
            System.out.println("✗ Controller input test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test player profile persistence
     */
    private static void testPlayerProfile() {
        System.out.println("\n--- Testing Player Profile ---");
        
        try {
            PlayerProfile profile = new PlayerProfile("TestUser");
            
            // Test basic properties
            assert "TestUser".equals(profile.getPlayerName()) : "Player name should be set correctly";
            assert profile.getTotalPlaytime() == 0 : "Initial playtime should be 0";
            assert profile.getGamesCompleted() == 0 : "Initial games completed should be 0";
            assert profile.getLookBackCount() == 0 : "Initial look back count should be 0";
            
            // Test statistics tracking
            profile.addPlaytime(3600); // 1 hour
            assert profile.getTotalPlaytime() == 3600 : "Playtime should be tracked";
            
            profile.recordGameCompletion();
            assert profile.getGamesCompleted() == 1 : "Game completion should be tracked";
            
            profile.updateHighestRoom(5);
            assert profile.getHighestRoom() == 5 : "Highest room should be tracked";
            
            profile.addDistance(100.5f);
            assert Math.abs(profile.getTotalDistance() - 100.5f) < 0.001f : "Distance should be tracked";
            
            profile.recordLookBack();
            assert profile.getLookBackCount() == 1 : "Look back count should be tracked";
            
            System.out.println("✓ Statistics tracking works");
            
            // Test settings
            profile.setMouseSensitivity(1.5f);
            assert Math.abs(profile.getMouseSensitivity() - 1.5f) < 0.001f : "Mouse sensitivity should be set";
            
            profile.setSoundVolume(0.7f);
            assert Math.abs(profile.getSoundVolume() - 0.7f) < 0.001f : "Sound volume should be set";
            
            profile.setDifficultyLevel(2);
            assert profile.getDifficultyLevel() == 2 : "Difficulty level should be set";
            assert "Hard".equals(profile.getDifficultyName()) : "Difficulty name should be correct";
            
            System.out.println("✓ Settings management works");
            
            // Test formatted output
            String playtime = profile.getFormattedPlaytime();
            assert playtime != null : "Formatted playtime should not be null";
            assert playtime.contains("h") || playtime.contains("m") : "Formatted playtime should contain time units";
            
            System.out.println("✓ Formatted output works");
            
        } catch (Exception e) {
            System.out.println("✗ Player profile test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test movement tracking
     */
    private static void testMovementTracking() {
        System.out.println("\n--- Testing Movement Tracking ---");
        
        try {
            Player player = new Player("MovementTest");
            PlayerProfile profile = player.getProfile();
            
            // Test initial position
            assert player.positionX() == 0.0f : "Initial X position should be 0";
            assert player.positionY() == 0.0f : "Initial Y position should be 0";
            assert player.positionZ() == 0.0f : "Initial Z position should be 0";
            
            // Test position setting
            player.setPosition(10.0f, 20.0f, 30.0f);
            assert player.positionX() == 10.0f : "X position should be updated";
            assert player.positionY() == 20.0f : "Y position should be updated";
            assert player.positionZ() == 30.0f : "Z position should be updated";
            
            System.out.println("✓ Position management works");
            
            // Test movement with distance tracking
            float initialDistance = profile.getTotalDistance();
            player.moveToRight();
            float newDistance = profile.getTotalDistance();
            assert newDistance > initialDistance : "Distance should increase after movement";
            
            System.out.println("✓ Movement tracking works");
            
            // Test speed control
            float originalSpeed = player.speed();
            player.setSpeed(0.005f);
            assert Math.abs(player.speed() - 0.005f) < 0.0001f : "Speed should be updated";
            
            System.out.println("✓ Speed control works");
            
            // Test system access
            assert player.getProfile() != null : "Profile should be accessible";
            assert player.getInputManager() != null : "Input manager should be accessible";
            
            System.out.println("✓ System integration works");
            
        } catch (Exception e) {
            System.out.println("✗ Movement tracking test failed: " + e.getMessage());
        }
    }
}