package dontlookback.interfaces;

public interface Entities {

    public float getX();

    public float getY();

    public float getZ();

    public float getOrientation();
            
    public void setX(float x);

    public void setY(float y);

    public void setZ(float z);

    public void setOrientation(float angle);

    public float[] getCenter();

    public void setCenter(float[] coords);

    public float[] getRGB();

    public void setRGB(float[] rgb);

    public void render();
    
    public void setUpVBO();
    
    public void delete();

}