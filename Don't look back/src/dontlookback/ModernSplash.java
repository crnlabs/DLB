package dontlookback;

import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

/**
 * Modern Splash Screen System
 * 
 * Integrated splash screen for LWJGL 3.x graphics system.
 * Displays game title, team credits, and loading progress during game initialization.
 * 
 * Features:
 * - OpenGL-based rendering (compatible with modern graphics system)
 * - Animated text and fade effects
 * - Team credits and GitHub information
 * - Loading progress indication
 * - Smooth transitions to main menu
 * 
 * @author DLB Team
 * @version 1.0
 */
public class ModernSplash {
    
    // === Splash Configuration ===
    
    /** Total splash screen duration in seconds */
    private static final double SPLASH_DURATION = 4.0;
    
    /** Time for fade-in effect */
    private static final double FADE_IN_TIME = 0.5;
    
    /** Time for fade-out effect */
    private static final double FADE_OUT_TIME = 0.5;
    
    /** Time to display main title */
    private static final double TITLE_DISPLAY_TIME = 2.0;
    
    /** Time to display credits */
    private static final double CREDITS_DISPLAY_TIME = 1.0;
    
    
    // === Animation State ===
    
    /** Current time in splash sequence */
    private double currentTime = 0.0;
    
    /** Whether splash is complete */
    private boolean isComplete = false;
    
    /** Current alpha for fade effects */
    private float currentAlpha = 0.0f;
    
    /** Text position for animated effects */
    private float textY = 0.0f;
    
    /** Whether running in headless mode (no graphics) */
    private boolean headlessMode = false;
    
    /** Last output time for headless rendering to avoid spam */
    private double lastHeadlessOutputTime = -1.0;
    
    
    // === Content Configuration ===
    
    /** Main game title */
    private static final String GAME_TITLE = "Don't Look Back";
    
    /** Subtitle */
    private static final String SUBTITLE = "A Horror Survival Experience";
    
    /** Team name */
    private static final String TEAM_NAME = "DLB Team";
    
    /** GitHub organization */
    private static final String GITHUB_ORG = "crnlabs/DLB";
    
    /** Additional credits */
    private static final String[] CREDITS = {
        "Game A Day Studios",
        "Built with LWJGL 3.x",
        "OpenGL 3.3+ Graphics",
        "Modern Java Game Engine"
    };
    
    
    /**
     * Initialize splash screen
     */
    public ModernSplash() {
        this(false); // Default to non-headless mode for backward compatibility
    }
    
    /**
     * Initialize splash screen with headless mode option
     * @param headlessMode If true, skip all graphics operations
     */
    public ModernSplash(boolean headlessMode) {
        this.headlessMode = headlessMode;
        
        if (headlessMode) {
            System.out.println("Initializing modern splash screen (headless mode)...");
        } else {
            System.out.println("Initializing modern splash screen...");
        }
        
        currentTime = 0.0;
        isComplete = false;
        currentAlpha = 0.0f;
        textY = 0.0f;
    }
    
    
    /**
     * Update splash screen animation
     * @param deltaTime Time since last update in seconds
     */
    public void update(double deltaTime) {
        if (isComplete) {
            return;
        }
        
        currentTime += deltaTime;
        
        // Calculate current animation phase
        if (currentTime < FADE_IN_TIME) {
            // Fade in phase
            currentAlpha = (float)(currentTime / FADE_IN_TIME);
            textY = -50.0f + (50.0f * currentAlpha); // Slide up
        } else if (currentTime < SPLASH_DURATION - FADE_OUT_TIME) {
            // Display phase
            currentAlpha = 1.0f;
            textY = 0.0f;
        } else if (currentTime < SPLASH_DURATION) {
            // Fade out phase
            double fadeProgress = (currentTime - (SPLASH_DURATION - FADE_OUT_TIME)) / FADE_OUT_TIME;
            currentAlpha = 1.0f - (float)fadeProgress;
            textY = 0.0f - (20.0f * (float)fadeProgress); // Slide down slightly
        } else {
            // Splash complete
            isComplete = true;
            currentAlpha = 0.0f;
        }
        
        // Clamp alpha
        currentAlpha = Math.max(0.0f, Math.min(1.0f, currentAlpha));
    }
    
    
    /**
     * Render splash screen content
     * @param windowWidth Current window width
     * @param windowHeight Current window height
     */
    public void render(int windowWidth, int windowHeight) {
        render(windowWidth, windowHeight, true);
    }
    
    /**
     * Render splash screen content with OpenGL context validation
     * @param windowWidth Current window width
     * @param windowHeight Current window height
     * @param openGLContextValid Whether OpenGL context is safe to use
     */
    public void render(int windowWidth, int windowHeight, boolean openGLContextValid) {
        if (isComplete || currentAlpha <= 0.0f) {
            return;
        }
        
        // In headless mode or when OpenGL context is invalid, just output text to console
        if (headlessMode || !openGLContextValid) {
            renderHeadlessOutput();
            return;
        }
        
        // Additional safety check: verify OpenGL context is actually current
        try {
            // Test if OpenGL context is actually available by making a safe call
            glGetError(); // This will throw if no context is current
        } catch (Exception e) {
            System.err.println("OpenGL context validation failed, falling back to headless mode: " + e.getMessage());
            renderHeadlessOutput();
            return;
        }
        
        // Clear to black background
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
        // Set up 2D rendering
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, windowWidth, windowHeight, 0, -1, 1);
        
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();
        
        // Enable blending for alpha effects
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Render content based on current time
        double phaseTime = currentTime;
        
        if (phaseTime < TITLE_DISPLAY_TIME + FADE_IN_TIME) {
            renderTitlePhase(windowWidth, windowHeight);
        } else if (phaseTime < TITLE_DISPLAY_TIME + CREDITS_DISPLAY_TIME + FADE_IN_TIME) {
            renderCreditsPhase(windowWidth, windowHeight);
        } else {
            renderLoadingPhase(windowWidth, windowHeight);
        }
        
        // Restore OpenGL state
        glDisable(GL_BLEND);
        
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
    }
    
    /**
     * Render splash content to console for headless mode
     */
    private void renderHeadlessOutput() {
        // Only output once per phase to avoid spam
        double phaseTime = currentTime;
        
        // Check if we're in a new phase that needs output
        boolean shouldOutput = false;
        String message = "";
        
        if (phaseTime < TITLE_DISPLAY_TIME + FADE_IN_TIME && lastHeadlessOutputTime < 0) {
            shouldOutput = true;
            message = "🎮 " + GAME_TITLE + " - Horror Survival Game";
            lastHeadlessOutputTime = 0.0;
        } else if (phaseTime >= TITLE_DISPLAY_TIME + FADE_IN_TIME && 
                   phaseTime < TITLE_DISPLAY_TIME + CREDITS_DISPLAY_TIME + FADE_IN_TIME &&
                   lastHeadlessOutputTime < TITLE_DISPLAY_TIME) {
            shouldOutput = true;
            message = "👥 A Game By: Game A Day Studios";
            lastHeadlessOutputTime = TITLE_DISPLAY_TIME;
        } else if (phaseTime >= TITLE_DISPLAY_TIME + CREDITS_DISPLAY_TIME + FADE_IN_TIME &&
                   lastHeadlessOutputTime < TITLE_DISPLAY_TIME + CREDITS_DISPLAY_TIME) {
            shouldOutput = true;
            message = "⚡ Initializing Game Systems...";
            lastHeadlessOutputTime = TITLE_DISPLAY_TIME + CREDITS_DISPLAY_TIME;
        }
        
        if (shouldOutput) {
            System.out.println(message);
        }
    }
    
    
    /**
     * Render the title display phase
     */
    private void renderTitlePhase(int windowWidth, int windowHeight) {
        float centerX = windowWidth / 2.0f;
        float centerY = windowHeight / 2.0f + textY;
        
        // Main title
        glColor4f(1.0f, 1.0f, 1.0f, currentAlpha);
        renderText(GAME_TITLE, centerX, centerY - 40, 3.0f, true);
        
        // Subtitle
        glColor4f(0.8f, 0.8f, 0.8f, currentAlpha * 0.8f);
        renderText(SUBTITLE, centerX, centerY + 20, 1.5f, true);
        
        // Add some visual flair
        renderTitleDecoration(centerX, centerY, windowWidth, windowHeight);
    }
    
    
    /**
     * Render the credits display phase
     */
    private void renderCreditsPhase(int windowWidth, int windowHeight) {
        float centerX = windowWidth / 2.0f;
        float centerY = windowHeight / 2.0f + textY;
        
        // Team name
        glColor4f(1.0f, 1.0f, 1.0f, currentAlpha);
        renderText(TEAM_NAME, centerX, centerY - 60, 2.5f, true);
        
        // GitHub info
        glColor4f(0.7f, 0.9f, 1.0f, currentAlpha);
        renderText("GitHub: " + GITHUB_ORG, centerX, centerY - 20, 1.2f, true);
        
        // Additional credits
        float creditY = centerY + 20;
        for (String credit : CREDITS) {
            glColor4f(0.8f, 0.8f, 0.8f, currentAlpha * 0.7f);
            renderText(credit, centerX, creditY, 1.0f, true);
            creditY += 25;
        }
    }
    
    
    /**
     * Render the loading phase
     */
    private void renderLoadingPhase(int windowWidth, int windowHeight) {
        float centerX = windowWidth / 2.0f;
        float centerY = windowHeight / 2.0f + textY;
        
        // Loading message
        glColor4f(1.0f, 1.0f, 1.0f, currentAlpha);
        renderText("Initializing Game Systems...", centerX, centerY, 1.5f, true);
        
        // Loading progress bar
        renderLoadingBar(centerX, centerY + 40, 300, 10);
    }
    
    
    /**
     * Render decorative elements for title phase
     */
    private void renderTitleDecoration(float centerX, float centerY, int windowWidth, int windowHeight) {
        // Simple decorative lines
        glColor4f(0.5f, 0.5f, 0.5f, currentAlpha * 0.5f);
        
        float lineWidth = 200.0f * currentAlpha;
        
        glBegin(GL_LINES);
        // Top line
        glVertex2f(centerX - lineWidth/2, centerY - 80);
        glVertex2f(centerX + lineWidth/2, centerY - 80);
        
        // Bottom line
        glVertex2f(centerX - lineWidth/2, centerY + 60);
        glVertex2f(centerX + lineWidth/2, centerY + 60);
        glEnd();
    }
    
    
    /**
     * Render a loading progress bar
     */
    private void renderLoadingBar(float x, float y, float width, float height) {
        // Calculate progress based on current time
        double progress = Math.min(1.0, currentTime / SPLASH_DURATION);
        
        // Background
        glColor4f(0.3f, 0.3f, 0.3f, currentAlpha);
        glBegin(GL_QUADS);
        glVertex2f(x - width/2, y);
        glVertex2f(x + width/2, y);
        glVertex2f(x + width/2, y + height);
        glVertex2f(x - width/2, y + height);
        glEnd();
        
        // Progress fill
        glColor4f(0.2f, 0.8f, 0.2f, currentAlpha);
        float progressWidth = width * (float)progress;
        glBegin(GL_QUADS);
        glVertex2f(x - width/2, y);
        glVertex2f(x - width/2 + progressWidth, y);
        glVertex2f(x - width/2 + progressWidth, y + height);
        glVertex2f(x - width/2, y + height);
        glEnd();
        
        // Progress percentage
        glColor4f(1.0f, 1.0f, 1.0f, currentAlpha);
        String progressText = String.format("%.0f%%", progress * 100);
        renderText(progressText, x, y - 20, 1.0f, true);
    }
    
    
    /**
     * Simple text rendering using OpenGL (basic implementation)
     * In a full game, this would use a proper font rendering system
     */
    private void renderText(String text, float x, float y, float scale, boolean centered) {
        // This is a placeholder implementation
        // In a real game, you would use a proper font rendering library
        // For now, we'll just print to console to indicate what would be displayed
        
        if (currentAlpha > 0.1f) { // Only log when visible
            String prefix = centered ? "[CENTERED] " : "[LEFT] ";
            System.out.println("SPLASH: " + prefix + text + " (alpha: " + 
                             String.format("%.2f", currentAlpha) + ")");
        }
        
        // For visual feedback in the actual game, implement bitmap font rendering here
        // This would involve loading a font texture and rendering character quads
    }
    
    
    /**
     * Check if splash screen is complete
     * @return true if splash should transition to next state
     */
    public boolean isComplete() {
        return isComplete;
    }
    
    
    /**
     * Force splash to complete immediately
     */
    public void skip() {
        isComplete = true;
        currentAlpha = 0.0f;
        System.out.println("Splash screen skipped");
    }
    
    
    /**
     * Get current splash progress (0.0 to 1.0)
     * @return Progress value
     */
    public double getProgress() {
        return Math.min(1.0, currentTime / SPLASH_DURATION);
    }
    
    
    /**
     * Get time remaining in splash (in seconds)
     * @return Remaining time
     */
    public double getTimeRemaining() {
        return Math.max(0.0, SPLASH_DURATION - currentTime);
    }
}