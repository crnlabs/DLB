package dontlookback;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Graphics System for Don't Look Back
 * 
 * Modern OpenGL 3.3+ rendering pipeline using LWJGL 3.x,
 * replacing the legacy OpenGL 1.1 fixed-function pipeline.
 * 
 * Features:
 * - LWJGL 3.x with GLFW window management
 * - OpenGL 3.3+ core profile support
 * - Modern shader-based rendering pipeline
 * - Cross-platform native library support
 * - Hardware-accelerated graphics
 */
public class Graphics {
    
    // === State Management ===
    
    /** Central state manager for game flow control */
    private StateManager stateManager;
    
    /** Splash screen system */
    private ModernSplash splashScreen;
    
    /** Main menu system */
    private MainMenu mainMenu;
    
    // === Window and Context Management ===
    
    /** GLFW window handle */
    private long window;
    
    /** Window dimensions */
    private int width = 1024;
    private int height = 768;
    
    /** Flag indicating if graphics system is running in headless mode */
    private boolean headlessMode = false;
    
    /** Flag indicating if OpenGL context is properly initialized */
    private boolean openGLContextValid = false;
    
    // === Camera System ===
    
    /** Camera position in world space */
    private float cameraX = 0.0f;
    private float cameraY = -1.75f; // Slightly below ground level for FPS view
    private float cameraZ = 0.0f;
    
    /** Camera rotation angles (Euler angles) */
    private float rotX = 0.0f; // Pitch
    private float rotY = 0.0f; // Yaw  
    private float rotZ = 0.0f; // Roll
    
    // === Timing System ===
    
    /** Last frame timestamp for delta time calculation */
    private double lastTime = 0.0;
    
    /** Time elapsed since last frame (for frame-rate independent movement) */
    private float deltaTime = 0.0f;
    
    // === Game Objects ===
    
    /** Player object */
    private Player player;
    
    /** Test data object */
    private testData test;
    
    /**
     * Initialize Graphics System and State Management
     * 
     * Sets up GLFW window, OpenGL context, state management, and starts the main game loop
     */
    public Graphics() {
        System.out.println("Initializing modern graphics system with state management...");
        
        // Check if we're in headless mode before attempting graphics initialization
        headlessMode = isHeadlessMode();
        
        if (headlessMode) {
            System.out.println("Running in headless mode - graphics operations will be skipped");
            // Initialize minimal state for headless mode
            stateManager = new StateManager();
            stateManager.addStateChangeListener(new GameStateChangeListener());
            Settings.setStateManager(stateManager);
            splashScreen = new ModernSplash(headlessMode);
            mainMenu = new MainMenu(stateManager);
            runHeadlessLoop();
            cleanup();
            return;
        }
        
        // Initialize state management
        stateManager = new StateManager();
        stateManager.addStateChangeListener(new GameStateChangeListener());
        
        // Register state manager with Settings for compatibility
        Settings.setStateManager(stateManager);
        
        // Initialize splash screen (with headless awareness)
        splashScreen = new ModernSplash(headlessMode);
        
        // Initialize main menu
        mainMenu = new MainMenu(stateManager);
        
        // Set up graphics and start main loop
        try {
            initializeGraphics();
            gameLoop();
        } catch (Exception e) {
            System.err.println("Graphics initialization failed: " + e.getMessage());
            throw new RuntimeException("Failed to initialize graphics system", e);
        } finally {
            cleanup();
        }
    }
    
    /**
     * Initialize GLFW, create window, and set up OpenGL context
     */
    private void initializeGraphics() {
        System.out.println("Don't Look Back - Graphics System");
        System.out.println("Initializing LWJGL " + Version.getVersion());
        
        // Setup error callback for GLFW debugging
        GLFWErrorCallback.createPrint(System.err).set();
        
        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        
        // Create the window
        window = glfwCreateWindow(width, height, "Don't Look Back", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        // Setup key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });
        
        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            
            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);
            
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            
            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }
        
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        
        // Enable v-sync
        glfwSwapInterval(1);
        
        // Make the window visible
        glfwShowWindow(window);
        
        // Initialize OpenGL bindings
        GL.createCapabilities();
        
        // Set up OpenGL
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        
        // Set clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        // Mark OpenGL context as valid
        openGLContextValid = true;
        
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("Graphics initialized successfully");
    }
    
    /**
     * Run a simplified loop for headless mode
     */
    private void runHeadlessLoop() {
        System.out.println("Running headless simulation for 3 seconds...");
        
        // Set initial time
        lastTime = System.nanoTime() / 1_000_000_000.0;
        
        double runTime = 0.0;
        final double MAX_RUN_TIME = 3.0; // Run for 3 seconds in headless mode
        
        while (runTime < MAX_RUN_TIME) {
            // Calculate delta time
            double currentTime = System.nanoTime() / 1_000_000_000.0;
            deltaTime = (float)(currentTime - lastTime);
            lastTime = currentTime;
            runTime += deltaTime;
            
            // Update current state logic
            updateCurrentState(deltaTime);
            
            // Update game objects based on state
            if (stateManager.isTimeActive()) {
                // Initialize game objects if entering gameplay for first time
                if (player == null && stateManager.isInGameplay()) {
                    player = new Player();
                    test = new testData(75);
                    System.out.println("Game objects initialized for gameplay");
                }
                
                // Update game objects (non-graphics)
                update(deltaTime);
            }
            
            // Simulate frame timing (roughly 60 FPS)
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("Headless simulation completed successfully");
    }
    
    /**
     * Checks if running in headless mode (without graphics support)
     */
    private boolean isHeadlessMode() {
        // Check if headless system property is set
        boolean headlessProperty = Boolean.getBoolean("java.awt.headless");
        
        // Check if DISPLAY environment variable is available (Linux/Unix)
        String display = System.getenv("DISPLAY");
        boolean noDisplay = (display == null || display.trim().isEmpty());
        
        // Check if we're in a typical CI environment
        boolean ciMode = System.getenv("CI") != null || 
                        System.getenv("GITHUB_ACTIONS") != null ||
                        System.getenv("JENKINS_URL") != null;
        
        return headlessProperty || (noDisplay && ciMode);
    }
    
    /**
     * Main game loop with state management
     */
    private void gameLoop() {
        lastTime = glfwGetTime();
        
        // Initialize game objects (delayed until after splash)
        Player player = null;
        testData test = null;
        
        while (!glfwWindowShouldClose(window)) {
            // Calculate delta time
            double currentTime = glfwGetTime();
            deltaTime = (float)(currentTime - lastTime);
            lastTime = currentTime;
            
            // Update current state
            updateCurrentState(deltaTime);
            
            // Process input based on current state
            if (stateManager.isInputAllowed()) {
                processInput();
            }
            
            // Handle special key inputs (always processed)
            processSpecialInput();
            
            // Update game objects based on state
            if (stateManager.isTimeActive()) {
                // Initialize game objects if entering gameplay for first time
                if (player == null && stateManager.isInGameplay()) {
                    player = new Player();
                    test = new testData(75);
                    System.out.println("Game objects initialized for gameplay");
                }
                
                // Update game objects
                update(deltaTime);
            }
            
            // Render current state
            render();
            
            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    
    /**
     * Update current state logic
     * @param deltaTime Time since last update
     */
    private void updateCurrentState(double deltaTime) {
        switch (stateManager.getCurrentState()) {
            case LOADING:
                // Update splash screen
                splashScreen.update(deltaTime);
                
                // Check if splash is complete
                if (splashScreen.isComplete()) {
                    stateManager.changeState(GameState.MAIN_MENU);
                }
                break;
                
            case MAIN_MENU:
                // Handle main menu logic
                updateMainMenu(deltaTime);
                break;
                
            case PLAYING:
                // Normal gameplay updates handled in main update method
                break;
                
            case PAUSED:
                // Paused state - minimal updates
                break;
                
            default:
                // Other states handled as needed
                break;
        }
    }
    
    /**
     * Update main menu state
     * @param deltaTime Time since last update
     */
    private void updateMainMenu(double deltaTime) {
        mainMenu.update(deltaTime);
    }
    
    /**
     * Process input from keyboard and mouse (gameplay)
     */
    private void processInput() {
        // Skip input processing in headless mode
        if (headlessMode || !openGLContextValid) {
            return;
        }
        
        // Only process movement input during gameplay
        if (!stateManager.isInGameplay()) {
            return;
        }
        
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            // Move forward
            cameraZ -= 0.1f * deltaTime * 60; // 60 FPS normalized movement
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            // Move backward
            cameraZ += 0.1f * deltaTime * 60;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            // Move left
            cameraX -= 0.1f * deltaTime * 60;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            // Move right
            cameraX += 0.1f * deltaTime * 60;
        }
    }
    
    /**
     * Process special input (always active)
     */
    private void processSpecialInput() {
        // Skip input processing in headless mode
        if (headlessMode || !openGLContextValid) {
            return;
        }
        
        // ESC key handling
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            handleEscapeKey();
        }
        
        // Skip splash screen with Space or Enter
        if (stateManager.getCurrentState() == GameState.LOADING) {
            if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS || 
                glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS) {
                splashScreen.skip();
            }
        }
        
        // Pause/unpause with P key
        if (glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS) {
            handlePauseKey();
        }
    }
    
    /**
     * Handle escape key press based on current state
     */
    private void handleEscapeKey() {
        switch (stateManager.getCurrentState()) {
            case PLAYING:
                stateManager.pauseGame();
                break;
            case PAUSED:
                stateManager.enterMainMenu();
                break;
            case MAIN_MENU:
                // Close game - in headless mode, just complete the loop
                if (headlessMode) {
                    System.out.println("Escape pressed - closing game");
                } else {
                    glfwSetWindowShouldClose(window, true);
                }
                break;
            default:
                stateManager.enterMainMenu();
                break;
        }
    }
    
    /**
     * Handle pause key press
     */
    private void handlePauseKey() {
        if (stateManager.getCurrentState() == GameState.PLAYING) {
            stateManager.pauseGame();
        } else if (stateManager.getCurrentState() == GameState.PAUSED) {
            stateManager.resumeGame();
        }
    }
    
    /**
     * Process input from keyboard and mouse (legacy method for compatibility)
     */
    private void processInputLegacy() {
        // Skip input processing in headless mode
        if (headlessMode || !openGLContextValid) {
            return;
        }
        
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            // Move forward
            cameraZ -= 0.1f * deltaTime * 60; // 60 FPS normalized movement
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            // Move backward
            cameraZ += 0.1f * deltaTime * 60;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            // Move left
            cameraX -= 0.1f * deltaTime * 60;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            // Move right
            cameraX += 0.1f * deltaTime * 60;
        }
    }
    
    /**
     * Update game state
     */
    private void update(float deltaTime) {
        // Update camera and game objects here
    }
    
    /**
     * Render the scene based on current state
     */
    private void render() {
        // Skip rendering if in headless mode or no valid OpenGL context
        if (headlessMode || !openGLContextValid) {
            return;
        }
        
        // Get current window size (for responsive rendering)
        int[] windowWidth = new int[1];
        int[] windowHeight = new int[1];
        glfwGetWindowSize(window, windowWidth, windowHeight);
        width = windowWidth[0];
        height = windowHeight[0];
        
        // Update viewport
        glViewport(0, 0, width, height);
        
        // Render based on current state
        switch (stateManager.getCurrentState()) {
            case LOADING:
                renderSplashScreen();
                break;
            case MAIN_MENU:
                renderMainMenu();
                break;
            case PLAYING:
                renderGameplay();
                break;
            case PAUSED:
                renderGameplay();  // Render game in background
                renderPauseOverlay();
                break;
            default:
                renderDefault();
                break;
        }
    }
    
    /**
     * Render splash screen
     */
    private void renderSplashScreen() {
        // Skip if no valid OpenGL context
        if (!openGLContextValid) {
            return;
        }
        
        splashScreen.render(width, height);
    }
    
    /**
     * Render main menu
     */
    private void renderMainMenu() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Dark blue background for menu
        glClearColor(0.1f, 0.1f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
        // Render menu (console output for headless demo)
        mainMenu.render();
        
        // Set up 2D rendering for menu (placeholder for actual UI)
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();
        
        // In a full implementation, render menu UI elements here
        // For now, we just output to console
        
        // Restore matrices
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
    }
    
    /**
     * Render gameplay
     */
    private void renderGameplay() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Dark background for horror atmosphere
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        // Set up projection matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = (float) width / height;
        // Simple perspective projection
        glFrustum(-aspectRatio, aspectRatio, -1.0f, 1.0f, 1.0f, 100.0f);
        
        // Set up modelview matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        // Apply camera transformations
        glRotatef(rotX, 1.0f, 0.0f, 0.0f);
        glRotatef(rotY, 0.0f, 1.0f, 0.0f);
        glRotatef(rotZ, 0.0f, 0.0f, 1.0f);
        glTranslatef(-cameraX, -cameraY, -cameraZ);
        
        // Render game world
        renderDemoCube();
        
        // Render UI elements
        renderGameplayUI();
    }
    
    /**
     * Render pause overlay
     */
    private void renderPauseOverlay() {
        // Set up 2D rendering for overlay
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();
        
        // Semi-transparent overlay
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(width, 0);
        glVertex2f(width, height);
        glVertex2f(0, height);
        glEnd();
        
        glDisable(GL_BLEND);
        
        // Pause text (placeholder)
        System.out.println("PAUSED - Press P to resume, ESC for menu");
        
        // Restore matrices
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
    }
    
    /**
     * Render gameplay UI elements
     */
    private void renderGameplayUI() {
        // Placeholder for HUD, crosshair, etc.
        // Would be implemented with proper 2D rendering
    }
    
    /**
     * Render default fallback
     */
    private void renderDefault() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.2f, 0.0f, 0.2f, 1.0f); // Purple background for unknown states
        renderDemoCube();
    }
    
    /**
     * Render a simple demo cube
     */
    private void renderDemoCube() {
        glBegin(GL_QUADS);
        
        // Front face
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f( 1.0f, -1.0f, 1.0f);
        glVertex3f( 1.0f,  1.0f, 1.0f);
        glVertex3f(-1.0f,  1.0f, 1.0f);
        
        // Back face
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);
        
        // Top face
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);
        glVertex3f(-1.0f,  1.0f,  1.0f);
        glVertex3f( 1.0f,  1.0f,  1.0f);
        glVertex3f( 1.0f,  1.0f, -1.0f);
        
        // Bottom face
        glColor3f(1.0f, 1.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f, -1.0f,  1.0f);
        glVertex3f(-1.0f, -1.0f,  1.0f);
        
        // Right face
        glColor3f(1.0f, 0.0f, 1.0f);
        glVertex3f( 1.0f, -1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f, -1.0f);
        glVertex3f( 1.0f,  1.0f,  1.0f);
        glVertex3f( 1.0f, -1.0f,  1.0f);
        
        // Left face
        glColor3f(0.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f,  1.0f);
        glVertex3f(-1.0f,  1.0f,  1.0f);
        glVertex3f(-1.0f,  1.0f, -1.0f);
        
        glEnd();
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        // Skip cleanup in headless mode since we never initialized GLFW
        if (headlessMode) {
            System.out.println("Cleanup completed (headless mode)");
            return;
        }
        
        // Free the window callbacks and destroy the window
        if (window != 0) {
            Callbacks.glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        }
        
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        
        System.out.println("Graphics cleanup completed");
    }
    
    // === Compatibility methods for existing code ===
    
    public float getCameraX() { return cameraX; }
    public float getCameraY() { return cameraY; }
    public float getCameraZ() { return cameraZ; }
    public float getRotX() { return rotX; }
    public float getRotY() { return rotY; }
    public float getRotZ() { return rotZ; }
    public float getDeltaTime() { return deltaTime; }
    
    public void setCameraPosition(float x, float y, float z) {
        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
    }
    
    public void setRotation(float x, float y, float z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
    }
    
    /**
     * Get current state manager
     * @return State manager instance
     */
    public StateManager getStateManager() {
        return stateManager;
    }
    
    /**
     * State change listener for graphics system
     */
    private class GameStateChangeListener implements StateManager.StateChangeListener {
        
        @Override
        public void onStateChanged(GameState oldState, GameState newState) {
            System.out.println("Graphics: State changed from " + oldState + " to " + newState);
            
            // Handle graphics-specific state changes
            switch (newState) {
                case LOADING:
                    // Reset graphics for loading
                    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    break;
                    
                case MAIN_MENU:
                    // Set menu graphics
                    glClearColor(0.1f, 0.1f, 0.3f, 1.0f);
                    break;
                    
                case PLAYING:
                    // Set gameplay graphics
                    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    glEnable(GL_DEPTH_TEST);
                    break;
                    
                case PAUSED:
                    // Graphics remain same as gameplay
                    break;
                    
                default:
                    break;
            }
        }
        
        @Override
        public void onSecondaryStateAdded(GameState state) {
            System.out.println("Graphics: Secondary state added: " + state);
        }
        
        @Override
        public void onSecondaryStateRemoved(GameState state) {
            System.out.println("Graphics: Secondary state removed: " + state);
        }
    }
}