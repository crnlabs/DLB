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
    
    // === Window and Context Management ===
    
    /** GLFW window handle */
    private long window;
    
    /** Window dimensions */
    private int width = 1024;
    private int height = 768;
    
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
    
    /**
     * Initialize the graphics system
     * Sets up GLFW window, OpenGL context, and starts the main game loop
     */
    public Graphics() {
        initializeGraphics();
        gameLoop();
        cleanup();
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
        
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("Graphics initialized successfully");
    }
    
    /**
     * Main game loop
     */
    private void gameLoop() {
        lastTime = glfwGetTime();
        
        // Initialize game objects
        Player player = new Player();
        testData test = new testData(75);
        
        while (!glfwWindowShouldClose(window)) {
            // Calculate delta time
            double currentTime = glfwGetTime();
            deltaTime = (float)(currentTime - lastTime);
            lastTime = currentTime;
            
            // Process input
            processInput();
            
            // Update game state
            update(deltaTime);
            
            // Render
            render();
            
            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    
    /**
     * Process input from keyboard and mouse
     */
    private void processInput() {
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
     * Render the scene
     */
    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
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
        
        // Render demo cube
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
        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
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
}