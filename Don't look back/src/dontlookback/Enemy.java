package dontlookback;

public interface Enemy extends NPC {

    public int reactionTime();
    
    public double scale();
    
    public int threatLevel();
    
    public int Cost();
    
    public float movementSpeed();
    
    public int intellegence();
    
    public double weight();

    public boolean jump();

    public int jumpHeight();

    public int[] dimensions();
}
