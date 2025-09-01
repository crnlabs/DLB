package dontlookback;

/**
 * Game Configuration Settings
 * 
 * Centralized configuration class that manages all game settings including:
 * - Display resolution and graphics options
 * - Gameplay mechanics (monster spawning, difficulty)
 * - Debug and development modes
 * - User interface preferences
 * 
 * This class follows the singleton pattern for global settings access
 * and provides both runtime configuration and build-time options.
 * 
 * @author Carl (original design)
 * @author DLB Team (modernized implementation)
 */
public class Settings {

    // === Development & Debug Settings ===
    
    /** Administrator mode - enables debug features and bypasses restrictions */
    private boolean admin = true; // Default enabled for development/testing
    
    /** Debug level: 0=off, 1=all info, 2=errors and critical only */
    private int debug = 1;
    
    /** Test mode flag for automated testing scenarios */
    private static boolean test = false;
    
    // === Display Settings ===
    
    /** Screen resolution [width, height] - defaults to 1024x768 */
    private static int[] resolution = new int[2];
    
    // === Gameplay Settings ===
    
    /** Enable monster spawning in the game world */
    private final boolean monsterSpawn;
    
    /** Trial mode - potentially for demo/limited gameplay */
    private final boolean trialMode;
    
    /** Grue mode - hardcore difficulty with light-based death mechanic */
    private final boolean grue; // If lights off and no held lights = death
    
    /** Enable flickering lights for normal difficulty */
    private final boolean lightsFlicker; // false = easy mode
    
    /** Enable achievement system */
    private final boolean achievements;
    
    /** Enable feedback/telemetry collection for analytics */
    private final boolean feedback;
    
    // === Runtime State ===
    
    /** Game pause state */
    private static boolean paused = false;

    /**
     * Initialize settings with default values
     * Configures the game for development/testing mode by default
     */
    public Settings() {
        // Set default resolution
        this.resolution[0] = 1024; // Horizontal resolution
        this.resolution[1] = 768;  // Vertical resolution
        
        // Configure gameplay settings
        this.monsterSpawn = true;
        this.admin = true; // Enable for development - disable for production
        this.trialMode = true;
        this.grue = false; // Hardcore mode disabled by default
        this.lightsFlicker = false; // Easy mode by default
        this.achievements = true;
        this.feedback = true; // Enable telemetry for analytics
        this.debug = 1; // Full debug information
        this.paused = false;
        this.test = false;
    }

    // === Pause State Management ===
    
    /**
     * Check if the game is currently paused
     * @return true if game is paused, false otherwise
     */
    public static boolean pausedState() {
        return paused;
    }
    
    /**
     * Set the game pause state
     * @param state true to pause, false to resume
     */
    public static void setPaused(boolean state) {
        paused = state;
    }
    
    // === Test Mode Management ===
    
    /**
     * Check if test mode is enabled
     * @return true if in test mode, false for normal gameplay
     */
    public static boolean testMode() {
        return test;
    }
    
    /**
     * Enable or disable test mode
     * @param state true to enable test mode, false for normal gameplay
     */
    public static void setTest(boolean state) {
        test = state;
    }
    
    // === Settings Accessors ===
    
    /**
     * Get the current screen resolution
     * @return int array [width, height] representing screen resolution
     */
    public static int[] resolution() {
        return resolution;
    }

    /**
     * Check if administrator mode is enabled
     * Administrator mode provides debug features and bypasses game restrictions
     * 
     * @return true if admin mode is active, false otherwise
     * @note Admin unlock key: RaindeerFlotilla
     */
    public boolean admin() {
        return admin; // Admin mode unlock key: RaindeerFlotilla
    }

    /**
     * Get the current debug level
     * @return debug level (0=off, 1=all info, 2=errors and critical only)
     */
    public int debug() {
        return debug;
    }

}
