package dontlookback;

/**
 * Comprehensive Game State Enumeration
 * 
 * Defines all possible game states for comprehensive state-based context management.
 * States are categorized as:
 * - Universal states (apply everywhere): PAUSED, LOADING, MENU, etc.
 * - Area-based states: SAFE, UNSAFE, etc.
 * - Player-based states: HIDING, etc.
 * - Monster/AI-based states: SEEN, UNSEEN, etc.
 * - Gameplay outcome states: WIN, LOSE, etc.
 * 
 * @author DLB Team
 * @version 1.0
 */
public enum GameState {
    
    // === Universal States (apply everywhere) ===
    
    /** Game is loading resources, splash screen, or initializing */
    LOADING("Loading game resources and initializing systems"),
    
    /** Main menu - player can start game, change settings, etc. */
    MAIN_MENU("Main menu - game selection and options"),
    
    /** Game is paused - all gameplay suspended */
    PAUSED("Game paused - all action suspended"),
    
    /** Settings/options menu */
    SETTINGS_MENU("Settings and configuration menu"),
    
    /** Credits screen */
    CREDITS("Credits and acknowledgments screen"),
    
    
    // === Gameplay Core States ===
    
    /** Normal gameplay in progress */
    PLAYING("Active gameplay in progress"),
    
    /** Player has won the game */
    WIN("Victory - player has completed the game"),
    
    /** Player has lost the game */
    LOSE("Defeat - player has failed and game is over"),
    
    /** Game over with specific death reason */
    GAME_OVER("Game over - displaying death/failure reason"),
    
    
    // === Area-Based States ===
    
    /** Safe area - monsters cannot spawn or enter */
    SAFE("Safe zone - no monster threats"),
    
    /** Unsafe area - monsters can spawn and hunt */
    UNSAFE("Dangerous area - monster spawning active"),
    
    /** Starting/tutorial area with guidance */
    TUTORIAL("Tutorial area with player guidance"),
    
    
    // === Player State Conditions ===
    
    /** Player is hiding - reduced detection chance */
    HIDING("Player concealed - reduced monster detection"),
    
    /** Player is exposed but undetected */
    UNSEEN("Player active but undetected by monsters"),
    
    /** Player has been detected by one or more monsters */
    SEEN("Player detected - monsters aware and reacting"),
    
    /** Player is being actively chased */
    CHASED("Player being actively pursued by monsters"),
    
    /** Player is in combat or immediate danger */
    COMBAT("Player in immediate combat or danger"),
    
    
    // === Environmental/Lighting States ===
    
    /** Area is well-lit and safe from grue */
    LIT("Well-lit area - safe from darkness dangers"),
    
    /** Area has dim lighting - some visibility */
    DIM("Dimly lit area - limited visibility"),
    
    /** Area is dark - grue danger if enabled */
    DARK("Dark area - danger from grue if lights fail"),
    
    /** Lights are flickering - tension and uncertainty */
    FLICKERING("Unstable lighting - flickering and tension"),
    
    
    // === Monster/AI States ===
    
    /** Monsters are patrolling in normal patterns */
    MONSTER_PATROL("Monsters in normal patrol patterns"),
    
    /** Monsters are actively searching for player */
    MONSTER_SEARCH("Monsters actively searching area"),
    
    /** Monsters are in alert/agitated state */
    MONSTER_ALERT("Monsters in heightened alert state"),
    
    /** Monsters are dormant/inactive */
    MONSTER_DORMANT("Monsters inactive or dormant"),
    
    
    // === Special Gameplay States ===
    
    /** Player has found important item or clue */
    DISCOVERY("Important discovery made - key item or clue"),
    
    /** Player is solving puzzle or interactive element */
    PUZZLE("Player engaged with puzzle or interactive element"),
    
    /** Player is reading text, lore, or story content */
    READING("Player viewing text or story content"),
    
    /** Cutscene or scripted sequence playing */
    CUTSCENE("Cutscene or scripted sequence active"),
    
    
    // === System/Debug States ===
    
    /** Debug mode active - dev tools available */
    DEBUG("Debug mode - development tools active"),
    
    /** Error state - graceful error handling */
    ERROR("Error condition - attempting graceful recovery");
    
    
    // === State Properties ===
    
    /** Human-readable description of this state */
    private final String description;
    
    
    /**
     * Create a game state with description
     * @param description Human-readable description
     */
    GameState(String description) {
        this.description = description;
    }
    
    
    // === Property Calculation Methods ===
    
    private boolean calculateInputAllowed() {
        switch (this) {
            case LOADING:
            case CUTSCENE:
            case GAME_OVER:
            case ERROR:
                return false;
            default:
                return true;
        }
    }
    
    private boolean calculateMonstersActive() {
        switch (this) {
            case PLAYING:
            case UNSAFE:
            case UNSEEN:
            case SEEN:
            case CHASED:
            case COMBAT:
            case MONSTER_PATROL:
            case MONSTER_SEARCH:
            case MONSTER_ALERT:
                return true;
            case SAFE:
            case PAUSED:
            case MAIN_MENU:
            case SETTINGS_MENU:
            case CREDITS:
            case LOADING:
            case WIN:
            case LOSE:
            case GAME_OVER:
            case TUTORIAL:
            case DEBUG:
            case ERROR:
            default:
                return false;
        }
    }
    
    private boolean calculateTimeActive() {
        switch (this) {
            case PAUSED:
            case MAIN_MENU:
            case SETTINGS_MENU:
            case CREDITS:
            case GAME_OVER:
            case DEBUG:
            case ERROR:
                return false;
            default:
                return true;
        }
    }
    
    
    // === Public Accessors ===
    
    /**
     * Get human-readable description of this state
     * @return Description string
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if player input should be processed in this state
     * @return true if input is allowed
     */
    public boolean allowsInput() {
        return calculateInputAllowed();
    }
    
    /**
     * Check if monsters should be active and processing AI
     * @return true if monsters should be active
     */
    public boolean areMonstersActive() {
        return calculateMonstersActive();
    }
    
    /**
     * Check if game time should progress (affects timers, animations, etc.)
     * @return true if time should progress
     */
    public boolean isTimeActive() {
        return calculateTimeActive();
    }
    
    
    // === State Category Checks ===
    
    /**
     * Check if this is a menu-related state
     * @return true if this is a menu state
     */
    public boolean isMenuState() {
        switch (this) {
            case MAIN_MENU:
            case SETTINGS_MENU:
            case CREDITS:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Check if this is an active gameplay state
     * @return true if gameplay is active
     */
    public boolean isGameplayState() {
        switch (this) {
            case PLAYING:
            case SAFE:
            case UNSAFE:
            case HIDING:
            case UNSEEN:
            case SEEN:
            case CHASED:
            case COMBAT:
            case LIT:
            case DIM:
            case DARK:
            case FLICKERING:
            case DISCOVERY:
            case PUZZLE:
            case READING:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Check if this is a terminal state (game ending)
     * @return true if game has ended
     */
    public boolean isTerminalState() {
        switch (this) {
            case WIN:
            case LOSE:
            case GAME_OVER:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Check if monsters pose a threat in this state
     * @return true if monsters are dangerous
     */
    public boolean isThreatActive() {
        switch (this) {
            case UNSAFE:
            case SEEN:
            case CHASED:
            case COMBAT:
            case MONSTER_SEARCH:
            case MONSTER_ALERT:
                return true;
            default:
                return false;
        }
    }
}