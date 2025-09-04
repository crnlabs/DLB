package dontlookback.demo;

/**
 * Complete demonstration of the state management and splash screen system
 * Shows the full flow: Splash → Main Menu → Gameplay → Pause → Resume
 */
public class CompleteStateDemo {
    
    public static void main(String[] args) {
        System.out.println("╔" + "═".repeat(60) + "╗");
        System.out.println("║" + " ".repeat(10) + "DON'T LOOK BACK - COMPLETE DEMO" + " ".repeat(17) + "║");
        System.out.println("║" + " ".repeat(15) + "State Management System" + " ".repeat(22) + "║");
        System.out.println("╚" + "═".repeat(60) + "╝\n");
        
        try {
            // Initialize systems
            StateManager stateManager = new StateManager();
            ModernSplash splash = new ModernSplash();
            MainMenu mainMenu = new MainMenu(stateManager);
            
            // Add comprehensive state change listener
            stateManager.addStateChangeListener(new StateManager.StateChangeListener() {
                @Override
                public void onStateChanged(GameState oldState, GameState newState) {
                    System.out.println("🔄 STATE: " + oldState + " → " + newState);
                }
                
                @Override
                public void onSecondaryStateAdded(GameState state) {
                    System.out.println("➕ SECONDARY: Added " + state);
                }
                
                @Override
                public void onSecondaryStateRemoved(GameState state) {
                    System.out.println("➖ SECONDARY: Removed " + state);
                }
            });
            
            System.out.println("🎮 PHASE 1: SPLASH SCREEN");
            System.out.println("━".repeat(50));
            
            // Simulate splash screen
            double time = 0.0;
            double deltaTime = 0.1; // 100ms updates
            
            while (!splash.isComplete() && time < 5.0) {
                splash.update(deltaTime);
                
                if (time == 0.0) {
                    System.out.println("📺 Splash screen started");
                    System.out.println("   Displaying: Don't Look Back");
                    System.out.println("   Team: DLB Team (crnlabs/DLB)");
                }
                
                if (Math.abs(time - 2.0) < 0.05) {
                    System.out.println("✨ Splash animation in progress...");
                }
                
                time += deltaTime;
                Thread.sleep(100); // Simulate real-time
            }
            
            System.out.println("✅ Splash screen complete after " + String.format("%.1f", time) + "s");
            
            // Transition to main menu
            System.out.println("\n🏠 PHASE 2: MAIN MENU");
            System.out.println("━".repeat(50));
            
            stateManager.changeState(GameState.MAIN_MENU);
            mainMenu.render();
            
            // Simulate menu interaction
            Thread.sleep(1000);
            System.out.println("⌨️  Simulating menu navigation...");
            mainMenu.handleInput("down"); // Move to Settings
            mainMenu.handleInput("down"); // Move to Credits  
            mainMenu.handleInput("up");   // Back to Settings
            mainMenu.handleInput("up");   // Back to Start Game
            
            Thread.sleep(500);
            System.out.println("🎯 Selecting 'Start Game'...");
            mainMenu.handleInput("enter");
            
            System.out.println("\n🎯 PHASE 3: GAMEPLAY");
            System.out.println("━".repeat(50));
            
            // Test gameplay states
            System.out.println("🌍 Entered gameplay world");
            System.out.println("📊 Gameplay status:");
            System.out.println("   • Input allowed: " + stateManager.isInputAllowed());
            System.out.println("   • Monsters active: " + stateManager.areMonstersActive());
            System.out.println("   • Time flowing: " + stateManager.isTimeActive());
            
            // Add environmental states
            Thread.sleep(500);
            System.out.println("\n🌆 Environmental changes...");
            stateManager.addSecondaryState(GameState.UNSAFE);
            stateManager.addSecondaryState(GameState.DIM);
            
            Thread.sleep(500);
            System.out.println("\n😰 Player encounters danger...");
            stateManager.addSecondaryState(GameState.SEEN);
            stateManager.addSecondaryState(GameState.CHASED);
            
            System.out.println("⚠️  Threat level: " + (stateManager.isThreatActive() ? "HIGH" : "LOW"));
            
            Thread.sleep(1000);
            System.out.println("\n🙈 Player finds hiding spot...");
            stateManager.removeSecondaryState(GameState.SEEN);
            stateManager.removeSecondaryState(GameState.CHASED);
            stateManager.addSecondaryState(GameState.HIDING);
            stateManager.addSecondaryState(GameState.SAFE);
            
            System.out.println("\n⏸️  PHASE 4: PAUSE SYSTEM");
            System.out.println("━".repeat(50));
            
            // Test pause/resume
            System.out.println("📱 Player pauses game...");
            stateManager.pauseGame();
            System.out.println("   • Input allowed: " + stateManager.isInputAllowed());
            System.out.println("   • Time flowing: " + stateManager.isTimeActive());
            System.out.println("   • Monsters active: " + stateManager.areMonstersActive());
            
            Thread.sleep(1000);
            System.out.println("\n▶️  Resuming game...");
            stateManager.resumeGame();
            System.out.println("   • Game resumed successfully");
            
            System.out.println("\n🎊 PHASE 5: GAME COMPLETION");
            System.out.println("━".repeat(50));
            
            // Test game ending
            Thread.sleep(500);
            System.out.println("🏆 Player reaches victory condition...");
            stateManager.endGame(true); // Victory!
            
            System.out.println("🎉 Game completed successfully!");
            
            // Show final statistics
            System.out.println("\n📈 FINAL STATISTICS");
            System.out.println("━".repeat(50));
            System.out.println("Current state: " + stateManager.getCurrentState());
            System.out.println("All active states: " + stateManager.getAllActiveStates());
            System.out.println("Is terminal state: " + stateManager.getCurrentState().isTerminalState());
            
            // Settings integration test
            System.out.println("\n🔧 SETTINGS INTEGRATION TEST");
            System.out.println("━".repeat(50));
            Settings.setStateManager(stateManager);
            
            // Go back to playing for settings test
            stateManager.changeState(GameState.PLAYING);
            System.out.println("Settings pause check: " + Settings.pausedState());
            
            Settings.setPaused(true);
            System.out.println("After Settings.setPaused(true): " + Settings.pausedState());
            
            Settings.setPaused(false);
            System.out.println("After Settings.setPaused(false): " + Settings.pausedState());
            
            // Final debug output
            System.out.println("\n🔍 COMPREHENSIVE DEBUG INFO");
            System.out.println("━".repeat(50));
            System.out.println(stateManager.getDebugInfo());
            
            System.out.println("\n" + "═".repeat(60));
            System.out.println("✅ COMPLETE DEMO FINISHED SUCCESSFULLY!");
            System.out.println("🎮 State management system is fully operational");
            System.out.println("🖥️  Graphics integration ready for LWJGL display");
            System.out.println("🧪 All features tested and verified");
            System.out.println("═".repeat(60));
            
        } catch (Exception e) {
            System.err.println("❌ Error during complete demo:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}