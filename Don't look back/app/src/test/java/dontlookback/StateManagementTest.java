package dontlookback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the state management system
 */
public class StateManagementTest {

    private StateManager stateManager;

    @BeforeEach
    void setUp() {
        stateManager = new StateManager();
    }

    @Test
    @DisplayName("State manager initializes with LOADING state")
    void testInitialState() {
        assertEquals(GameState.LOADING, stateManager.getCurrentState());
        assertNull(stateManager.getPreviousState());
    }

    @Test
    @DisplayName("Valid state transitions work correctly")
    void testValidTransitions() {
        // LOADING -> MAIN_MENU should work
        assertTrue(stateManager.changeState(GameState.MAIN_MENU));
        assertEquals(GameState.MAIN_MENU, stateManager.getCurrentState());
        assertEquals(GameState.LOADING, stateManager.getPreviousState());
        
        // MAIN_MENU -> PLAYING should work
        assertTrue(stateManager.changeState(GameState.PLAYING));
        assertEquals(GameState.PLAYING, stateManager.getCurrentState());
        assertEquals(GameState.MAIN_MENU, stateManager.getPreviousState());
    }

    @Test
    @DisplayName("Pause and resume functionality works")
    void testPauseResume() {
        // Go to playing state first
        stateManager.changeState(GameState.MAIN_MENU);
        stateManager.changeState(GameState.PLAYING);
        
        // Test pause
        assertTrue(stateManager.pauseGame());
        assertEquals(GameState.PAUSED, stateManager.getCurrentState());
        
        // Test resume
        assertTrue(stateManager.resumeGame());
        assertEquals(GameState.PLAYING, stateManager.getCurrentState());
    }

    @Test
    @DisplayName("Secondary states work correctly")
    void testSecondaryStates() {
        stateManager.changeState(GameState.PLAYING);
        
        // Add secondary state
        stateManager.addSecondaryState(GameState.HIDING);
        assertTrue(stateManager.isStateActive(GameState.HIDING));
        assertTrue(stateManager.isStateActive(GameState.PLAYING));
        
        // Remove secondary state
        stateManager.removeSecondaryState(GameState.HIDING);
        assertFalse(stateManager.isStateActive(GameState.HIDING));
        assertTrue(stateManager.isStateActive(GameState.PLAYING));
    }

    @Test
    @DisplayName("State properties work correctly")
    void testStateProperties() {
        // Test LOADING state properties
        assertEquals(GameState.LOADING, stateManager.getCurrentState());
        assertFalse(stateManager.isInputAllowed()); // LOADING doesn't allow input
        assertFalse(stateManager.areMonstersActive());
        assertTrue(stateManager.isTimeActive());
        
        // Test PLAYING state properties
        stateManager.changeState(GameState.MAIN_MENU);
        stateManager.changeState(GameState.PLAYING);
        assertTrue(stateManager.isInputAllowed());
        assertTrue(stateManager.areMonstersActive());
        assertTrue(stateManager.isTimeActive());
        
        // Test PAUSED state properties
        stateManager.pauseGame();
        assertTrue(stateManager.isInputAllowed()); // Can still navigate menus
        assertFalse(stateManager.areMonstersActive());
        assertFalse(stateManager.isTimeActive());
    }

    @Test
    @DisplayName("State timing works correctly")
    void testStateTiming() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        
        // Wait a small amount
        Thread.sleep(10);
        
        long timeInState = stateManager.getTimeInCurrentState();
        assertTrue(timeInState >= 10, "Time in state should be at least 10ms");
        
        double timeInStateSeconds = stateManager.getTimeInCurrentStateSeconds();
        assertTrue(timeInStateSeconds >= 0.01, "Time in state should be at least 0.01 seconds");
    }

    @Test
    @DisplayName("Force state change works")
    void testForceState() {
        // Force change to an invalid transition (should work anyway)
        stateManager.forceState(GameState.WIN);
        assertEquals(GameState.WIN, stateManager.getCurrentState());
        assertEquals(GameState.LOADING, stateManager.getPreviousState());
    }

    @Test
    @DisplayName("Game state enum properties work")
    void testGameStateProperties() {
        // Test menu state
        assertTrue(GameState.MAIN_MENU.isMenuState());
        assertFalse(GameState.MAIN_MENU.isGameplayState());
        assertFalse(GameState.MAIN_MENU.isTerminalState());
        
        // Test gameplay state
        assertFalse(GameState.PLAYING.isMenuState());
        assertTrue(GameState.PLAYING.isGameplayState());
        assertFalse(GameState.PLAYING.isTerminalState());
        
        // Test terminal state
        assertFalse(GameState.WIN.isMenuState());
        assertFalse(GameState.WIN.isGameplayState());
        assertTrue(GameState.WIN.isTerminalState());
        
        // Test threat states
        assertTrue(GameState.CHASED.isThreatActive());
        assertFalse(GameState.SAFE.isThreatActive());
    }

    @Test
    @DisplayName("State change listener notifications work")
    void testStateChangeListener() {
        final boolean[] listenerCalled = {false};
        final GameState[] receivedStates = {null, null}; // old, new
        
        StateManager.StateChangeListener listener = new StateManager.StateChangeListener() {
            @Override
            public void onStateChanged(GameState oldState, GameState newState) {
                listenerCalled[0] = true;
                receivedStates[0] = oldState;
                receivedStates[1] = newState;
            }
            
            @Override
            public void onSecondaryStateAdded(GameState state) {}
            
            @Override
            public void onSecondaryStateRemoved(GameState state) {}
        };
        
        stateManager.addStateChangeListener(listener);
        stateManager.changeState(GameState.MAIN_MENU);
        
        assertTrue(listenerCalled[0], "State change listener should be called");
        assertEquals(GameState.LOADING, receivedStates[0]);
        assertEquals(GameState.MAIN_MENU, receivedStates[1]);
        
        stateManager.removeStateChangeListener(listener);
    }

    @Test
    @DisplayName("Settings integration works")
    void testSettingsIntegration() {
        // Test that Settings can work with state manager
        Settings.setStateManager(stateManager);
        
        stateManager.changeState(GameState.MAIN_MENU);
        stateManager.changeState(GameState.PLAYING);
        
        // Test pause through Settings
        Settings.setPaused(true);
        assertEquals(GameState.PAUSED, stateManager.getCurrentState());
        assertTrue(Settings.pausedState());
        
        // Test resume through Settings
        Settings.setPaused(false);
        assertEquals(GameState.PLAYING, stateManager.getCurrentState());
        assertFalse(Settings.pausedState());
    }
}