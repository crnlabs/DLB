package dontlookback;

public abstract class Objects implements Entities {

    protected float x, y, z;
    protected float orientation;
    protected int handle;

    protected float[] rgb = {0.5f, 0.5f, 0.5f};

    public Objects() {

        setX(0);
        setY(0);
        setZ(0);
        setOrientation(0);

    }

    public Objects(float x, float y, float z, float angle) {

        setX(x);
        setY(y);
        setZ(z);
        setOrientation(angle);

    }

    public Objects(float[] coords, float angle) {
        setCenter(coords);
        setOrientation(angle);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setOrientation(float angle) {
        orientation = angle;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setCenter(float[] coords) {
        setX(coords[0]);
        setY(coords[1]);
        setZ(coords[2]);
    }

    public float[] getCenter() {
        return new float[]{x, y, z};
    }

    public void setRGB(float[] rgb) {
        this.rgb = rgb;
    }

    public float[] getRGB() {
        return rgb;
    }

    public abstract void render();
    
    public abstract void setUpVBO();
    
    public abstract void delete();

}
