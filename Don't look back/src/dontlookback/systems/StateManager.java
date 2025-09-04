package dontlookback.systems;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import dontlookback.GameState;

/**
 * Comprehensive State Management System
 * 
 * Manages game state transitions, state stacking for complex scenarios,
 * and provides event-driven state change notifications. Supports both
 * single states and composite state scenarios (e.g., PLAYING + HIDING + UNSAFE).
 * 
 * Features:
 * - Thread-safe state management
 * - State transition validation
 * - State change event system
 * - State stack for complex scenarios
 * - Automatic state conflict resolution
 * - Debug and logging capabilities
 * 
 * @author DLB Team
 * @version 1.0
 */
public class StateManager {
    
    // === Core State Management ===
    
    /** Primary game state - the main state driving game behavior */
    private volatile GameState primaryState;
    
    /** Secondary active states - for complex state combinations */
    private final Set<GameState> activeStates;
    
    /** State history for debugging and rollback */
    private final Deque<GameState> stateHistory;
    
    /** Maximum history size to prevent memory leaks */
    private static final int MAX_HISTORY_SIZE = 50;
    
    
    // === Event System ===
    
    /** State change listeners for event notifications */
    private final List<StateChangeListener> listeners;
    
    /** Thread-safe listener management */
    private final Object listenerLock = new Object();
    
    
    // === Timing and Transitions ===
    
    /** Time when current state was entered (for state duration tracking) */
    private long stateEnterTime;
    
    /** Previous state (for transition validation) */
    private GameState previousState;
    
    /** Whether state transitions are currently locked (during critical operations) */
    private volatile boolean transitionsLocked = false;
    
    
    // === State Transition Rules ===
    
    /** Valid state transitions - maps from state to allowed next states */
    private static final Map<GameState, Set<GameState>> TRANSITION_RULES;
    
    static {
        TRANSITION_RULES = new ConcurrentHashMap<>();
        initializeTransitionRules();
    }
    
    
    /**
     * Initialize state manager with LOADING state
     */
    public StateManager() {
        this.activeStates = ConcurrentHashMap.newKeySet();
        this.stateHistory = new ArrayDeque<>();
        this.listeners = new ArrayList<>();
        
        // Start in loading state
        this.primaryState = GameState.LOADING;
        this.previousState = null;
        this.stateEnterTime = System.currentTimeMillis();
        
        addToHistory(GameState.LOADING);
        notifyStateChange(null, GameState.LOADING);
    }
    
    
    // === Primary State Management ===
    
    /**
     * Get the current primary game state
     * @return Current primary state
     */
    public GameState getCurrentState() {
        return primaryState;
    }
    
    /**
     * Get the previous state (for transition logic)
     * @return Previous state, null if first state
     */
    public GameState getPreviousState() {
        return previousState;
    }
    
    /**
     * Change to a new primary state with validation
     * @param newState The state to transition to
     * @return true if transition succeeded, false if blocked
     */
    public boolean changeState(GameState newState) {
        if (newState == null) {
            throw new IllegalArgumentException("New state cannot be null");
        }
        
        if (transitionsLocked) {
            System.err.println("State transition blocked - transitions are locked");
            return false;
        }
        
        if (newState == primaryState) {
            // Already in requested state
            return true;
        }
        
        // Validate transition
        if (!isTransitionValid(primaryState, newState)) {
            System.err.println("Invalid state transition: " + primaryState + " -> " + newState);
            return false;
        }
        
        // Perform transition
        GameState oldState = primaryState;
        previousState = primaryState;
        primaryState = newState;
        stateEnterTime = System.currentTimeMillis();
        
        // Update state collections
        addToHistory(newState);
        cleanupIncompatibleStates(newState);
        
        // Notify listeners
        notifyStateChange(oldState, newState);
        
        System.out.println("State transition: " + oldState + " -> " + newState);
        return true;
    }
    
    /**
     * Force a state change without validation (for emergency scenarios)
     * @param newState The state to force
     */
    public void forceState(GameState newState) {
        if (newState == null) {
            throw new IllegalArgumentException("New state cannot be null");
        }
        
        GameState oldState = primaryState;
        previousState = primaryState;
        primaryState = newState;
        stateEnterTime = System.currentTimeMillis();
        
        addToHistory(newState);
        cleanupIncompatibleStates(newState);
        notifyStateChange(oldState, newState);
        
        System.out.println("Forced state transition: " + oldState + " -> " + newState);
    }
    
    
    // === Composite State Management ===
    
    /**
     * Add a secondary state (for complex state combinations)
     * @param state State to add as secondary
     */
    public void addSecondaryState(GameState state) {
        if (state != null && !state.equals(primaryState)) {
            if (activeStates.add(state)) {
                System.out.println("Added secondary state: " + state);
                notifySecondaryStateAdded(state);
            }
        }
    }
    
    /**
     * Remove a secondary state
     * @param state State to remove
     */
    public void removeSecondaryState(GameState state) {
        if (activeStates.remove(state)) {
            System.out.println("Removed secondary state: " + state);
            notifySecondaryStateRemoved(state);
        }
    }
    
    /**
     * Check if a specific state is currently active (primary or secondary)
     * @param state State to check
     * @return true if state is active
     */
    public boolean isStateActive(GameState state) {
        return state.equals(primaryState) || activeStates.contains(state);
    }
    
    /**
     * Get all currently active states
     * @return Set of all active states (primary + secondary)
     */
    public Set<GameState> getAllActiveStates() {
        Set<GameState> allStates = new HashSet<>(activeStates);
        allStates.add(primaryState);
        return allStates;
    }
    
    
    // === State Queries ===
    
    /**
     * Check if player input should be processed in current state
     * @return true if input is allowed
     */
    public boolean isInputAllowed() {
        // Check primary state first
        if (!primaryState.allowsInput()) {
            return false;
        }
        
        // Check if any active secondary state blocks input
        return activeStates.stream().allMatch(GameState::allowsInput);
    }
    
    /**
     * Check if monsters should be active and processing AI
     * @return true if monsters should be active
     */
    public boolean areMonstersActive() {
        // Monsters active if primary state allows OR any secondary state requires them
        return primaryState.areMonstersActive() || 
               activeStates.stream().anyMatch(GameState::areMonstersActive);
    }
    
    /**
     * Check if game time should progress
     * @return true if time should progress
     */
    public boolean isTimeActive() {
        // Time active if primary state allows AND no secondary state blocks it
        return primaryState.isTimeActive() && 
               activeStates.stream().allMatch(GameState::isTimeActive);
    }
    
    /**
     * Check if there's an active threat to the player
     * @return true if any threat is active
     */
    public boolean isThreatActive() {
        return primaryState.isThreatActive() || 
               activeStates.stream().anyMatch(GameState::isThreatActive);
    }
    
    /**
     * Check if currently in any menu state
     * @return true if in a menu
     */
    public boolean isInMenu() {
        return primaryState.isMenuState() || 
               activeStates.stream().anyMatch(GameState::isMenuState);
    }
    
    /**
     * Check if game is in active gameplay
     * @return true if gameplay is active
     */
    public boolean isInGameplay() {
        return primaryState.isGameplayState();
    }
    
    
    // === Convenience State Changes ===
    
    /**
     * Pause the game (if not already paused)
     * @return true if successfully paused
     */
    public boolean pauseGame() {
        if (primaryState == GameState.PAUSED) {
            return true; // Already paused
        }
        
        if (primaryState.isGameplayState()) {
            return changeState(GameState.PAUSED);
        }
        
        return false; // Cannot pause from current state
    }
    
    /**
     * Resume from pause (return to previous gameplay state)
     * @return true if successfully resumed
     */
    public boolean resumeGame() {
        if (primaryState != GameState.PAUSED) {
            return true; // Not paused
        }
        
        // Try to return to previous state if it was gameplay
        if (previousState != null && previousState.isGameplayState()) {
            return changeState(previousState);
        }
        
        // Default to normal playing state
        return changeState(GameState.PLAYING);
    }
    
    /**
     * Enter main menu
     * @return true if successfully entered menu
     */
    public boolean enterMainMenu() {
        return changeState(GameState.MAIN_MENU);
    }
    
    /**
     * Start gameplay
     * @return true if successfully started
     */
    public boolean startGameplay() {
        return changeState(GameState.PLAYING);
    }
    
    /**
     * Trigger game over with specific outcome
     * @param won true for victory, false for defeat
     * @return true if state changed successfully
     */
    public boolean endGame(boolean won) {
        return changeState(won ? GameState.WIN : GameState.LOSE);
    }
    
    
    // === State Timing ===
    
    /**
     * Get time spent in current state (in milliseconds)
     * @return Time in current state
     */
    public long getTimeInCurrentState() {
        return System.currentTimeMillis() - stateEnterTime;
    }
    
    /**
     * Get time spent in current state (in seconds)
     * @return Time in current state in seconds
     */
    public double getTimeInCurrentStateSeconds() {
        return getTimeInCurrentState() / 1000.0;
    }
    
    
    // === Transition Validation ===
    
    private boolean isTransitionValid(GameState from, GameState to) {
        // Allow emergency transitions to error states
        if (to == GameState.ERROR) {
            return true;
        }
        
        // Check transition rules
        Set<GameState> allowedTransitions = TRANSITION_RULES.get(from);
        if (allowedTransitions == null) {
            return true; // No restrictions defined
        }
        
        return allowedTransitions.contains(to);
    }
    
    private static void initializeTransitionRules() {
        // Loading state can go to main menu or error
        TRANSITION_RULES.put(GameState.LOADING, Set.of(
            GameState.MAIN_MENU, GameState.ERROR
        ));
        
        // Main menu can go to gameplay, settings, credits, or exit
        TRANSITION_RULES.put(GameState.MAIN_MENU, Set.of(
            GameState.PLAYING, GameState.SETTINGS_MENU, GameState.CREDITS, GameState.LOADING
        ));
        
        // Settings menu can return to main menu or previous state
        TRANSITION_RULES.put(GameState.SETTINGS_MENU, Set.of(
            GameState.MAIN_MENU, GameState.PLAYING, GameState.PAUSED
        ));
        
        // Credits can return to main menu
        TRANSITION_RULES.put(GameState.CREDITS, Set.of(
            GameState.MAIN_MENU
        ));
        
        // Playing state is flexible - can go to many states
        TRANSITION_RULES.put(GameState.PLAYING, Set.of(
            GameState.PAUSED, GameState.WIN, GameState.LOSE, GameState.GAME_OVER,
            GameState.MAIN_MENU, GameState.SETTINGS_MENU, GameState.SAFE, GameState.UNSAFE,
            GameState.HIDING, GameState.UNSEEN, GameState.SEEN, GameState.CHASED, GameState.COMBAT,
            GameState.DISCOVERY, GameState.PUZZLE, GameState.READING, GameState.CUTSCENE
        ));
        
        // Paused can resume to playing or go to menus
        TRANSITION_RULES.put(GameState.PAUSED, Set.of(
            GameState.PLAYING, GameState.MAIN_MENU, GameState.SETTINGS_MENU
        ));
        
        // Terminal states can go to main menu or restart
        for (GameState terminalState : Arrays.asList(GameState.WIN, GameState.LOSE, GameState.GAME_OVER)) {
            TRANSITION_RULES.put(terminalState, Set.of(
                GameState.MAIN_MENU, GameState.PLAYING, GameState.CREDITS
            ));
        }
    }
    
    
    // === State History and Cleanup ===
    
    private void addToHistory(GameState state) {
        stateHistory.addLast(state);
        
        // Limit history size
        while (stateHistory.size() > MAX_HISTORY_SIZE) {
            stateHistory.removeFirst();
        }
    }
    
    private void cleanupIncompatibleStates(GameState newPrimaryState) {
        // Remove secondary states that conflict with new primary state
        activeStates.removeIf(state -> isStateIncompatible(newPrimaryState, state));
    }
    
    private boolean isStateIncompatible(GameState primary, GameState secondary) {
        // Menu states are incompatible with gameplay states
        if (primary.isMenuState() && secondary.isGameplayState()) {
            return true;
        }
        if (primary.isGameplayState() && secondary.isMenuState()) {
            return true;
        }
        
        // Terminal states are incompatible with most other states
        if (primary.isTerminalState() && secondary.isGameplayState()) {
            return true;
        }
        
        return false;
    }
    
    
    // === Event System ===
    
    /**
     * Interface for state change notifications
     */
    public interface StateChangeListener {
        void onStateChanged(GameState oldState, GameState newState);
        void onSecondaryStateAdded(GameState state);
        void onSecondaryStateRemoved(GameState state);
    }
    
    /**
     * Add a state change listener
     * @param listener Listener to add
     */
    public void addStateChangeListener(StateChangeListener listener) {
        synchronized (listenerLock) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a state change listener
     * @param listener Listener to remove
     */
    public void removeStateChangeListener(StateChangeListener listener) {
        synchronized (listenerLock) {
            listeners.remove(listener);
        }
    }
    
    private void notifyStateChange(GameState oldState, GameState newState) {
        synchronized (listenerLock) {
            for (StateChangeListener listener : listeners) {
                try {
                    listener.onStateChanged(oldState, newState);
                } catch (Exception e) {
                    System.err.println("Error in state change listener: " + e.getMessage());
                }
            }
        }
    }
    
    private void notifySecondaryStateAdded(GameState state) {
        synchronized (listenerLock) {
            for (StateChangeListener listener : listeners) {
                try {
                    listener.onSecondaryStateAdded(state);
                } catch (Exception e) {
                    System.err.println("Error in secondary state listener: " + e.getMessage());
                }
            }
        }
    }
    
    private void notifySecondaryStateRemoved(GameState state) {
        synchronized (listenerLock) {
            for (StateChangeListener listener : listeners) {
                try {
                    listener.onSecondaryStateRemoved(state);
                } catch (Exception e) {
                    System.err.println("Error in secondary state listener: " + e.getMessage());
                }
            }
        }
    }
    
    
    // === Debug and Utility ===
    
    /**
     * Get debug information about current state
     * @return Debug string with state information
     */
    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== State Manager Debug Info ===\n");
        sb.append("Primary State: ").append(primaryState).append("\n");
        sb.append("Previous State: ").append(previousState).append("\n");
        sb.append("Time in State: ").append(getTimeInCurrentStateSeconds()).append("s\n");
        sb.append("Secondary States: ").append(activeStates).append("\n");
        sb.append("Input Allowed: ").append(isInputAllowed()).append("\n");
        sb.append("Monsters Active: ").append(areMonstersActive()).append("\n");
        sb.append("Time Active: ").append(isTimeActive()).append("\n");
        sb.append("Threat Active: ").append(isThreatActive()).append("\n");
        sb.append("Transitions Locked: ").append(transitionsLocked).append("\n");
        sb.append("State History (last 5): ");
        
        GameState[] historyArray = stateHistory.toArray(new GameState[0]);
        int start = Math.max(0, historyArray.length - 5);
        for (int i = start; i < historyArray.length; i++) {
            if (i > start) sb.append(" -> ");
            sb.append(historyArray[i]);
        }
        sb.append("\n");
        
        return sb.toString();
    }
    
    /**
     * Lock state transitions (for critical operations)
     */
    public void lockTransitions() {
        transitionsLocked = true;
    }
    
    /**
     * Unlock state transitions
     */
    public void unlockTransitions() {
        transitionsLocked = false;
    }
    
    /**
     * Check if transitions are currently locked
     * @return true if transitions are locked
     */
    public boolean areTransitionsLocked() {
        return transitionsLocked;
    }
}