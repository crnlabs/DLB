package dontlookback.interfaces;

public interface Monster extends Enemy{

    public short legLength();
    
    public boolean hostile();
    
    public double lastSeen(); //if a negative number then wait, if 0 then destroy.
    
    public double scale();
    
    public float orientation();
    
    
}