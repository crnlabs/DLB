package dontlookback;

/**
 * Headless demonstration of state management system
 * Runs without graphics to demonstrate state transitions
 */
public class HeadlessStateDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Don't Look Back - State Management Demo ===");
        System.out.println("Testing comprehensive state management system...\n");
        
        try {
            // Create state manager
            StateManager stateManager = new StateManager();
            System.out.println("✓ StateManager created successfully");
            System.out.println("Initial state: " + stateManager.getCurrentState());
            
            // Add a listener to track state changes
            stateManager.addStateChangeListener(new StateManager.StateChangeListener() {
                @Override
                public void onStateChanged(GameState oldState, GameState newState) {
                    System.out.println("  → State changed: " + oldState + " → " + newState);
                }
                
                @Override
                public void onSecondaryStateAdded(GameState state) {
                    System.out.println("  + Added secondary state: " + state);
                }
                
                @Override
                public void onSecondaryStateRemoved(GameState state) {
                    System.out.println("  - Removed secondary state: " + state);
                }
            });
            
            // Test state transitions
            System.out.println("\n--- Testing State Transitions ---");
            
            // LOADING → MAIN_MENU
            System.out.println("Transitioning to main menu...");
            boolean success = stateManager.changeState(GameState.MAIN_MENU);
            System.out.println(success ? "✓ Transition successful" : "✗ Transition failed");
            
            // Wait briefly
            Thread.sleep(500);
            
            // MAIN_MENU → PLAYING
            System.out.println("\nStarting gameplay...");
            success = stateManager.startGameplay();
            System.out.println(success ? "✓ Gameplay started" : "✗ Failed to start gameplay");
            
            // Test secondary states
            System.out.println("\n--- Testing Secondary States ---");
            stateManager.addSecondaryState(GameState.UNSAFE);
            stateManager.addSecondaryState(GameState.HIDING);
            
            // Test pause/resume
            System.out.println("\n--- Testing Pause/Resume ---");
            System.out.println("Pausing game...");
            success = stateManager.pauseGame();
            System.out.println(success ? "✓ Game paused" : "✗ Failed to pause");
            
            Thread.sleep(300);
            
            System.out.println("Resuming game...");
            success = stateManager.resumeGame();
            System.out.println(success ? "✓ Game resumed" : "✗ Failed to resume");
            
            // Test state properties
            System.out.println("\n--- Testing State Properties ---");
            System.out.println("Input allowed: " + stateManager.isInputAllowed());
            System.out.println("Monsters active: " + stateManager.areMonstersActive());
            System.out.println("Time active: " + stateManager.isTimeActive());
            System.out.println("Threat active: " + stateManager.isThreatActive());
            System.out.println("In gameplay: " + stateManager.isInGameplay());
            
            // Test Settings integration
            System.out.println("\n--- Testing Settings Integration ---");
            Settings.setStateManager(stateManager);
            System.out.println("Settings pause state: " + Settings.pausedState());
            Settings.setPaused(true);
            System.out.println("After Settings.setPaused(true): " + stateManager.getCurrentState());
            Settings.setPaused(false);
            System.out.println("After Settings.setPaused(false): " + stateManager.getCurrentState());
            
            // Display final debug info
            System.out.println("\n--- Final State Information ---");
            System.out.println(stateManager.getDebugInfo());
            
            System.out.println("\n✓ State management demo completed successfully!");
            
        } catch (Exception e) {
            System.err.println("✗ Error during state management demo:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}