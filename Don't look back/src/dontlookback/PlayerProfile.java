package dontlookback;

import java.io.*;
import java.util.Properties;

/**
 * Player Profile and Game Save System
 * 
 * Manages persistent storage of player data, game settings, and progress.
 * Provides save/load functionality for game state continuity across sessions.
 * 
 * Features:
 * - Player profile management
 * - Game progress tracking  
 * - Settings persistence
 * - Multiple save slots
 * - Auto-save functionality
 * - Data integrity checks
 * 
 * @author DLB Team
 * @version 1.0
 */
public class PlayerProfile {
    
    // === Profile Data ===
    
    /** Player name */
    private String playerName = "Unknown";
    
    /** Total playtime in seconds */
    private long totalPlaytime = 0;
    
    /** Number of games completed */
    private int gamesCompleted = 0;
    
    /** Highest room number reached */
    private int highestRoom = 0;
    
    /** Total distance traveled */
    private float totalDistance = 0.0f;
    
    /** Number of times looked back (dangerous!) */
    private int lookBackCount = 0;
    
    /** Player reaction time best score */
    private int bestReactionTime = Integer.MAX_VALUE;
    
    /** Settings */
    private float mouseSensitivity = 1.0f;
    private float soundVolume = 0.8f;
    private boolean controllerEnabled = true;
    private int difficultyLevel = 1; // 0=Easy, 1=Normal, 2=Hard, 3=Nightmare
    
    // === Save System ===
    
    /** Default save directory */
    private static final String SAVE_DIR = System.getProperty("user.home") + File.separator + ".dontlookback";
    
    /** Profile file name */
    private static final String PROFILE_FILE = "player.profile";
    
    /** Game save file prefix */
    private static final String SAVE_FILE_PREFIX = "save_";
    
    /** Maximum number of save slots */
    private static final int MAX_SAVE_SLOTS = 5;
    
    // === Constructors ===
    
    /**
     * Create a new player profile with default values
     */
    public PlayerProfile() {
        this("Player");
    }
    
    /**
     * Create a new player profile with specified name
     * @param playerName the player's name
     */
    public PlayerProfile(String playerName) {
        this.playerName = playerName;
        ensureSaveDirectoryExists();
    }
    
    // === Profile Management ===
    
    /**
     * Save the player profile to disk
     * @return true if successfully saved
     */
    public boolean saveProfile() {
        try {
            Properties props = new Properties();
            
            // Player data
            props.setProperty("playerName", playerName);
            props.setProperty("totalPlaytime", String.valueOf(totalPlaytime));
            props.setProperty("gamesCompleted", String.valueOf(gamesCompleted));
            props.setProperty("highestRoom", String.valueOf(highestRoom));
            props.setProperty("totalDistance", String.valueOf(totalDistance));
            props.setProperty("lookBackCount", String.valueOf(lookBackCount));
            props.setProperty("bestReactionTime", String.valueOf(bestReactionTime));
            
            // Settings
            props.setProperty("mouseSensitivity", String.valueOf(mouseSensitivity));
            props.setProperty("soundVolume", String.valueOf(soundVolume));
            props.setProperty("controllerEnabled", String.valueOf(controllerEnabled));
            props.setProperty("difficultyLevel", String.valueOf(difficultyLevel));
            
            // Save to file
            File profileFile = new File(SAVE_DIR, PROFILE_FILE);
            try (FileOutputStream fos = new FileOutputStream(profileFile)) {
                props.store(fos, "Don't Look Back - Player Profile");
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to save player profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load the player profile from disk
     * @return true if successfully loaded
     */
    public boolean loadProfile() {
        try {
            File profileFile = new File(SAVE_DIR, PROFILE_FILE);
            if (!profileFile.exists()) {
                return false; // No profile to load
            }
            
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(profileFile)) {
                props.load(fis);
            }
            
            // Load player data
            playerName = props.getProperty("playerName", "Player");
            totalPlaytime = Long.parseLong(props.getProperty("totalPlaytime", "0"));
            gamesCompleted = Integer.parseInt(props.getProperty("gamesCompleted", "0"));
            highestRoom = Integer.parseInt(props.getProperty("highestRoom", "0"));
            totalDistance = Float.parseFloat(props.getProperty("totalDistance", "0.0"));
            lookBackCount = Integer.parseInt(props.getProperty("lookBackCount", "0"));
            bestReactionTime = Integer.parseInt(props.getProperty("bestReactionTime", String.valueOf(Integer.MAX_VALUE)));
            
            // Load settings
            mouseSensitivity = Float.parseFloat(props.getProperty("mouseSensitivity", "1.0"));
            soundVolume = Float.parseFloat(props.getProperty("soundVolume", "0.8"));
            controllerEnabled = Boolean.parseBoolean(props.getProperty("controllerEnabled", "true"));
            difficultyLevel = Integer.parseInt(props.getProperty("difficultyLevel", "1"));
            
            return true;
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load player profile: " + e.getMessage());
            return false;
        }
    }
    
    // === Game Save Management ===
    
    /**
     * Save current game state to specified slot
     * @param slot save slot number (0-4)
     * @param gameData serialized game state data
     * @return true if successfully saved
     */
    public boolean saveGame(int slot, String gameData) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            return false;
        }
        
        try {
            File saveFile = new File(SAVE_DIR, SAVE_FILE_PREFIX + slot + ".dat");
            
            Properties props = new Properties();
            props.setProperty("saveTime", String.valueOf(System.currentTimeMillis()));
            props.setProperty("playerName", playerName);
            props.setProperty("gameData", gameData);
            
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                props.store(fos, "Don't Look Back - Game Save Slot " + slot);
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to save game to slot " + slot + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load game state from specified slot
     * @param slot save slot number (0-4)
     * @return game data string or null if failed
     */
    public String loadGame(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            return null;
        }
        
        try {
            File saveFile = new File(SAVE_DIR, SAVE_FILE_PREFIX + slot + ".dat");
            if (!saveFile.exists()) {
                return null;
            }
            
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(saveFile)) {
                props.load(fis);
            }
            
            return props.getProperty("gameData");
            
        } catch (IOException e) {
            System.err.println("Failed to load game from slot " + slot + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if a save slot has data
     * @param slot save slot number (0-4)
     * @return true if slot contains save data
     */
    public boolean hasSaveData(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            return false;
        }
        
        File saveFile = new File(SAVE_DIR, SAVE_FILE_PREFIX + slot + ".dat");
        return saveFile.exists();
    }
    
    // === Statistics Tracking ===
    
    /**
     * Update playtime statistics
     * @param sessionTime time played this session in seconds
     */
    public void addPlaytime(long sessionTime) {
        totalPlaytime += sessionTime;
    }
    
    /**
     * Record a completed game
     */
    public void recordGameCompletion() {
        gamesCompleted++;
    }
    
    /**
     * Update highest room reached
     * @param roomNumber the room number reached
     */
    public void updateHighestRoom(int roomNumber) {
        if (roomNumber > highestRoom) {
            highestRoom = roomNumber;
        }
    }
    
    /**
     * Record distance traveled
     * @param distance distance in game units
     */
    public void addDistance(float distance) {
        totalDistance += distance;
    }
    
    /**
     * Record a dangerous look back action
     */
    public void recordLookBack() {
        lookBackCount++;
        System.out.println("WARNING: You looked back! Total look backs: " + lookBackCount);
    }
    
    /**
     * Update best reaction time if improved
     * @param reactionTime new reaction time in milliseconds
     */
    public void updateBestReactionTime(int reactionTime) {
        if (reactionTime < bestReactionTime) {
            bestReactionTime = reactionTime;
        }
    }
    
    // === Getters and Setters ===
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public long getTotalPlaytime() { return totalPlaytime; }
    public int getGamesCompleted() { return gamesCompleted; }
    public int getHighestRoom() { return highestRoom; }
    public float getTotalDistance() { return totalDistance; }
    public int getLookBackCount() { return lookBackCount; }
    public int getBestReactionTime() { return bestReactionTime; }
    
    public float getMouseSensitivity() { return mouseSensitivity; }
    public void setMouseSensitivity(float sensitivity) { this.mouseSensitivity = Math.max(0.1f, Math.min(3.0f, sensitivity)); }
    
    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float volume) { this.soundVolume = Math.max(0.0f, Math.min(1.0f, volume)); }
    
    public boolean isControllerEnabled() { return controllerEnabled; }
    public void setControllerEnabled(boolean enabled) { this.controllerEnabled = enabled; }
    
    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int level) { this.difficultyLevel = Math.max(0, Math.min(3, level)); }
    
    // === Utility Methods ===
    
    /**
     * Ensure the save directory exists
     */
    private void ensureSaveDirectoryExists() {
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }
    
    /**
     * Get formatted playtime string
     * @return human readable playtime (e.g., "2h 15m")
     */
    public String getFormattedPlaytime() {
        long hours = totalPlaytime / 3600;
        long minutes = (totalPlaytime % 3600) / 60;
        
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
    
    /**
     * Get difficulty level name
     * @return difficulty name string
     */
    public String getDifficultyName() {
        switch (difficultyLevel) {
            case 0: return "Easy";
            case 1: return "Normal";
            case 2: return "Hard";
            case 3: return "Nightmare";
            default: return "Unknown";
        }
    }
    
    /**
     * Clear all profile data (reset to defaults)
     */
    public void resetProfile() {
        totalPlaytime = 0;
        gamesCompleted = 0;
        highestRoom = 0;
        totalDistance = 0.0f;
        lookBackCount = 0;
        bestReactionTime = Integer.MAX_VALUE;
        // Keep settings and player name
    }
}