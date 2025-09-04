package dontlookback.entities;

/**
 * Cube object for 3D rendering in Don't Look Back
 * 
 * Represents a cubic game object with position, size, color, and rotation.
 * Used for building game environments, obstacles, and interactive elements.
 * 
 * This implementation uses modern OpenGL rendering through the graphics system.
 */
public class Cube extends Objects {

    private float width;
    private final float rotation = (float) (Math.random() * 3);

    /**
     * Create a new cube with random position and size
     */
    public Cube() {
        super();
        randomXYZ();
        randomSize();
        orientation = 0;
        setCurrent();
    }

    /**
     * Copy constructor for creating cube from another cube
     */
    public Cube(Cube cube) {
        super();
        rgb = cube.rgb.clone();
        width = cube.width;
        x = cube.x;
        y = cube.y;
        z = cube.z;
        setCurrent();
    }

    /**
     * Create cube with specific position, angle and size
     */
    public Cube(float x, float y, float z, float angle, float width) {
        super(x, y, z, angle);
        setWidth(width);
        setCurrent();
    }

    /**
     * Create cube with coordinate array, angle and size
     */
    public Cube(float[] coords, float angle, float width) {
        super(coords, angle);
        setWidth(width);
        setCurrent();
    }

    /**
     * Set current position as reference point
     */
    private void setCurrent() {
        cX = x;
        cY = y;
        cZ = z;
    }

    /**
     * Set the color of this cube
     */
    public void setColor(float red, float green, float blue) {
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
    }

    /**
     * Set random position within reasonable bounds
     */
    public void randomXYZ() {
        x = (float) (Math.random() * 20 - 10); // -10 to 10
        y = (float) (Math.random() * 5);       // 0 to 5
        z = (float) (Math.random() * 20 - 10); // -10 to 10
    }

    /**
     * Set random size for the cube
     */
    private void randomSize() {
        width = (float) (Math.random() * 2 + 0.5); // 0.5 to 2.5
    }

    /**
     * Get the width/size of this cube
     */
    public float getWidth() {
        return width;
    }

    /**
     * Set the width/size of this cube
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Get the rotation angle
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Get the color components
     */
    public float[] getRGB() {
        return rgb.clone();
    }

    /**
     * Render this cube using the modern graphics pipeline
     * The actual rendering is handled by the graphics system
     */
    public void render() {
        // Modern rendering is handled by the graphics system
        // This method exists for compatibility but actual rendering
        // is done through the modern graphics pipeline
    }

    /**
     * Set color using legacy interface
     */
    public void setColor() {
        // Set default color if none specified
        setColor(0.5f, 0.5f, 0.5f);
    }

    /**
     * Set color using color array
     */
    public void setColor(float[] color) {
        if (color.length >= 3) {
            setColor(color[0], color[1], color[2]);
        }
    }

    /**
     * Set up VBO for modern rendering (compatibility method)
     */
    public void setUpVBO() {
        // VBO setup is now handled by the modern graphics system
        // This method exists for compatibility
    }

    /**
     * Delete/cleanup resources (compatibility method)
     */
    public void delete() {
        // Resource cleanup is now handled by the modern graphics system
        // This method exists for compatibility
    }

    /**
     * Object behavior/AI (compatibility method)
     */
    public void behavior() {
        // Basic cube behavior - can be extended for moving/animated cubes
    }

    /**
     * Update cube state (position, animation, etc.)
     */
    public void update() {
        // Update logic can be implemented here
        // For now, cubes are static but this allows for future animation
    }

    @Override
    public String toString() {
        return String.format("Cube[pos=(%.2f,%.2f,%.2f), width=%.2f, rotation=%.2f]", 
                           x, y, z, width, rotation);
    }
}