package dontlookback;

public interface NPC {

    public boolean Interactive();

    public boolean Quest();

    public int movementPattern();

    public int state(); //0 - inactive //1 - active //2 - hostile //3 - ???

}
