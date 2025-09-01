package dontlookback.modern;

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
 * Modern graphics system using LWJGL 3.x
 * Replaces the legacy DLB_Graphics class with modern OpenGL 3.3+ support
 */
public class ModernGraphics {
    
    private long window;
    private int width = 1024;
    private int height = 768;
    
    // Camera variables
    private float cameraX = 0.0f;
    private float cameraY = -1.75f;
    private float cameraZ = 0.0f;
    private float rotX = 0.0f;
    private float rotY = 0.0f;
    private float rotZ = 0.0f;
    
    // Timing
    private double lastTime = 0.0;
    private float deltaTime = 0.0f;
    
    public ModernGraphics() {
        initializeGraphics();
        gameLoop();
        cleanup();
    }
    
    private void initializeGraphics() {
        System.out.println("Don't Look Back - Modern Graphics System");
        System.out.println("Initializing LWJGL " + Version.getVersion());
        
        // Setup error callback
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
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        
        // Create window
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
            
            // Get the window size
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
        
        // Initialize OpenGL capabilities
        GL.createCapabilities();
        
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("OpenGL renderer: " + glGetString(GL_RENDERER));
        
        // Enable depth testing
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        
        // Enable face culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);
        
        // Set clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        // Initialize timing
        lastTime = glfwGetTime();
    }
    
    private void gameLoop() {
        while (!glfwWindowShouldClose(window)) {
            // Calculate delta time
            double currentTime = glfwGetTime();
            deltaTime = (float)(currentTime - lastTime);
            lastTime = currentTime;
            
            // Poll events
            glfwPollEvents();
            
            // Update game state
            update();
            
            // Render frame
            render();
            
            // Swap buffers
            glfwSwapBuffers(window);
        }
    }
    
    private void update() {
        // Handle input
        handleInput();
        
        // Update camera and game objects
        updateCamera();
        
        // Handle window resize
        handleResize();
    }
    
    private void handleInput() {
        final float speed = 5.0f * deltaTime;
        
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            cameraZ += speed;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            cameraZ -= speed;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            cameraX += speed;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            cameraX -= speed;
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            cameraY -= speed;
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            cameraY += speed;
        }
        
        // Mouse look (simplified for now)
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
            rotX += 45.0f * deltaTime;
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
            rotX -= 45.0f * deltaTime;
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
            rotY += 45.0f * deltaTime;
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            rotY -= 45.0f * deltaTime;
        }
    }
    
    private void updateCamera() {
        // Setup projection matrix (simplified fixed-function style for compatibility)
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        // Perspective projection
        float fov = 68.0f;
        float aspect = (float) width / (float) height;
        float near = 0.3f;
        float far = 4000.0f;
        
        float fH = (float) Math.tan(Math.toRadians(fov) / 2.0f) * near;
        float fW = fH * aspect;
        glFrustum(-fW, fW, -fH, fH, near, far);
        
        // Setup modelview matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        // Apply camera transformations
        glRotatef(rotX, 1, 0, 0);
        glRotatef(rotY, 0, 1, 0);
        glRotatef(rotZ, 0, 0, 1);
        glTranslatef(cameraX, cameraY, cameraZ);
    }
    
    private void handleResize() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            
            glfwGetFramebufferSize(window, pWidth, pHeight);
            
            int newWidth = pWidth.get(0);
            int newHeight = pHeight.get(0);
            
            if (newWidth != width || newHeight != height) {
                width = newWidth;
                height = newHeight;
                glViewport(0, 0, width, height);
            }
        }
    }
    
    private void render() {
        // Clear buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Render simple test geometry
        renderTestCube();
        
        // Reset matrix for next frame
        glLoadIdentity();
    }
    
    private void renderTestCube() {
        // Simple cube rendering using immediate mode (temporary for testing)
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
    
    private void cleanup() {
        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    // Getters for compatibility
    public float getCameraX() { return cameraX; }
    public float getCameraY() { return cameraY; }
    public float getCameraZ() { return cameraZ; }
    public float getRotX() { return rotX; }
    public float getRotY() { return rotY; }
    public float getRotZ() { return rotZ; }
    public float getDeltaTime() { return deltaTime; }
    
    // Setters for compatibility
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