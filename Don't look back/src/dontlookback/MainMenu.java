package dontlookback;

/**
 * Simple Main Menu Implementation
 * 
 * Basic main menu state handler for the game.
 * Provides options to start game, view settings, credits, or exit.
 * 
 * @author DLB Team
 * @version 1.0
 */
public class MainMenu {
    
    // === Menu Configuration ===
    
    /** Menu options */
    public enum MenuOption {
        START_GAME("Start Game"),
        SETTINGS("Settings"), 
        CREDITS("Credits"),
        EXIT("Exit");
        
        private final String displayText;
        
        MenuOption(String displayText) {
            this.displayText = displayText;
        }
        
        public String getDisplayText() {
            return displayText;
        }
    }
    
    // === Menu State ===
    
    /** Currently selected menu option */
    private MenuOption selectedOption = MenuOption.START_GAME;
    
    /** Reference to state manager */
    private StateManager stateManager;
    
    /** Time spent in menu for auto-demo purposes */
    private double timeInMenu = 0.0;
    
    
    /**
     * Initialize main menu with state manager
     * @param stateManager State manager instance
     */
    public MainMenu(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    
    /**
     * Update main menu logic
     * @param deltaTime Time since last update in seconds
     */
    public void update(double deltaTime) {
        timeInMenu += deltaTime;
        
        // Auto-start game after 3 seconds for demo purposes
        if (timeInMenu > 3.0 && selectedOption == MenuOption.START_GAME) {
            System.out.println("Main Menu: Auto-starting game (demo mode)");
            executeCurrentOption();
        }
    }
    
    
    /**
     * Handle input for menu navigation
     * @param key Input key identifier
     */
    public void handleInput(String key) {
        switch (key.toLowerCase()) {
            case "up":
            case "w":
                navigateUp();
                break;
            case "down": 
            case "s":
                navigateDown();
                break;
            case "enter":
            case "space":
                executeCurrentOption();
                break;
            case "escape":
                selectedOption = MenuOption.EXIT;
                executeCurrentOption();
                break;
        }
    }
    
    
    /**
     * Navigate to previous menu option
     */
    private void navigateUp() {
        MenuOption[] options = MenuOption.values();
        int currentIndex = selectedOption.ordinal();
        int newIndex = (currentIndex - 1 + options.length) % options.length;
        selectedOption = options[newIndex];
        System.out.println("Main Menu: Selected " + selectedOption.getDisplayText());
    }
    
    
    /**
     * Navigate to next menu option
     */
    private void navigateDown() {
        MenuOption[] options = MenuOption.values();
        int currentIndex = selectedOption.ordinal();
        int newIndex = (currentIndex + 1) % options.length;
        selectedOption = options[newIndex];
        System.out.println("Main Menu: Selected " + selectedOption.getDisplayText());
    }
    
    
    /**
     * Execute the currently selected menu option
     */
    private void executeCurrentOption() {
        System.out.println("Main Menu: Executing " + selectedOption.getDisplayText());
        
        switch (selectedOption) {
            case START_GAME:
                stateManager.startGameplay();
                break;
            case SETTINGS:
                stateManager.changeState(GameState.SETTINGS_MENU);
                break;
            case CREDITS:
                stateManager.changeState(GameState.CREDITS);
                break;
            case EXIT:
                System.out.println("Main Menu: Exiting game");
                System.exit(0);
                break;
        }
    }
    
    
    /**
     * Render main menu (console version for headless demo)
     */
    public void render() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           DON'T LOOK BACK");
        System.out.println("         A Horror Survival Game");
        System.out.println("=".repeat(50));
        System.out.println();
        
        MenuOption[] options = MenuOption.values();
        for (MenuOption option : options) {
            String prefix = (option == selectedOption) ? " ► " : "   ";
            System.out.println(prefix + option.getDisplayText());
        }
        
        System.out.println();
        System.out.println("Use W/S or Arrow Keys to navigate");
        System.out.println("Press Enter or Space to select");
        System.out.println("Press Escape to exit");
        System.out.println("=".repeat(50));
    }
    
    
    /**
     * Get currently selected option
     * @return Selected menu option
     */
    public MenuOption getSelectedOption() {
        return selectedOption;
    }
    
    
    /**
     * Set selected option programmatically
     * @param option Option to select
     */
    public void setSelectedOption(MenuOption option) {
        this.selectedOption = option;
        System.out.println("Main Menu: Selected " + option.getDisplayText());
    }
    
    
    /**
     * Reset menu state (when returning to menu)
     */
    public void reset() {
        selectedOption = MenuOption.START_GAME;
        timeInMenu = 0.0;
        System.out.println("Main Menu: Reset to default state");
    }
}