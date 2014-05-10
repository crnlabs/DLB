
package dontlookback;


public interface Player {
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

