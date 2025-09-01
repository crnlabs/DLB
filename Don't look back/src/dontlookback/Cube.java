package dontlookback;

import dontlookback.modern.ModernCube;

/**
 * Compatibility wrapper for legacy Cube class
 * Delegates to ModernCube implementation for LWJGL 3.x compatibility
 * 
 * This maintains API compatibility while using modern rendering backend
 */
public class Cube extends Objects {

    private final ModernCube modernCube;

    /**
     * Create a new cube with random position and size
     */
    public Cube() {
        super();
        modernCube = new ModernCube();
        syncFromModern();
    }

    /**
     * Copy constructor for creating cube from another cube
     */
    public Cube(Cube cube) {
        super();
        modernCube = new ModernCube(cube.modernCube);
        syncFromModern();
    }

    /**
     * Create cube with specific position, angle and size
     */
    public Cube(float x, float y, float z, float angle, float width) {
        super(x, y, z, angle);
        modernCube = new ModernCube(x, y, z, angle, width);
        syncFromModern();
    }

    /**
     * Create cube with coordinate array, angle and size
     */
    public Cube(float[] coords, float angle, float width) {
        super(coords, angle);
        modernCube = new ModernCube(coords, angle, width);
        syncFromModern();
    }

    /**
     * Sync properties from modern cube implementation
     */
    private void syncFromModern() {
        this.x = modernCube.getX();
        this.y = modernCube.getY();
        this.z = modernCube.getZ();
        this.orientation = modernCube.getOrientation();
        this.rgb = modernCube.getRGB();
    }

    /**
     * Sync properties to modern cube implementation
     */
    private void syncToModern() {
        modernCube.setX(this.x);
        modernCube.setY(this.y);
        modernCube.setZ(this.z);
        modernCube.setOrientation(this.orientation);
        modernCube.setColor(this.rgb[0], this.rgb[1], this.rgb[2]);
    }

    /**
     * Set the color of this cube
     */
    public void setColor(float red, float green, float blue) {
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
        modernCube.setColor(red, green, blue);
    }

    /**
     * Get the width/size of this cube
     */
    public float getWidth() {
        return modernCube.getWidth();
    }

    /**
     * Set the width/size of this cube
     */
    public void setWidth(float width) {
        modernCube.setWidth(width);
    }

    /**
     * Get the rotation angle
     */
    public float getRotation() {
        return modernCube.getRotation();
    }

    /**
     * Legacy render method - now delegates to modern graphics system
     * Maintained for API compatibility
     */
    public void render() {
        syncToModern();
        modernCube.render();
    }

    /**
     * Set color using legacy interface
     */
    public void setColor() {
        modernCube.setColor();
        syncFromModern();
    }

    /**
     * Set color using color array
     */
    public void setColor(float[] color) {
        modernCube.setColor(color);
        syncFromModern();
    }

    /**
     * Set up VBO for modern rendering (compatibility method)
     */
    public void setUpVBO() {
        modernCube.setUpVBO();
    }

    /**
     * Delete/cleanup resources (compatibility method)
     */
    public void delete() {
        modernCube.delete();
    }

    /**
     * Object behavior/AI (compatibility method)
     */
    public void behavior() {
        modernCube.behavior();
    }

    /**
     * Update cube state
     */
    public void update() {
        syncToModern();
        modernCube.update();
        syncFromModern();
    }

    /**
     * Get the underlying modern cube implementation
     * This allows modern graphics system to access the cube directly
     */
    public ModernCube getModernCube() {
        syncToModern();
        return modernCube;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        if (modernCube != null) {
            modernCube.setX(x);
        }
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        if (modernCube != null) {
            modernCube.setY(y);
        }
    }

    @Override
    public void setZ(float z) {
        super.setZ(z);
        if (modernCube != null) {
            modernCube.setZ(z);
        }
    }

    @Override
    public void setOrientation(float angle) {
        super.setOrientation(angle);
        if (modernCube != null) {
            modernCube.setOrientation(angle);
        }
    }

    @Override
    public String toString() {
        return "Cube[" + modernCube.toString() + "]";
    }
}