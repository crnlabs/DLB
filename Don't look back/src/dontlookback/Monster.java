package dontlookback;

public interface Monster extends Enemy{

    public short legLength();
    
    public boolean hostile();
    
    public double lastSeen();
    
    public double scale();
    
    public float orientation();
    
}
