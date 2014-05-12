
package dontlookback;


public interface Player extends Human{
    public int speed();
    public int weight();
    public boolean jump();
    public int jumpHeight();
    public int height();
    public int width();
    public int depth();
    public Tangible[] inverntory(); 
    public Tangible heldItem();
}

