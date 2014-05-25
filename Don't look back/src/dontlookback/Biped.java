package dontlookback;

public interface Biped {

    public float speed();

    public double weight();

    public boolean jump();

    public int jumpHeight();

    public int height(); //default to 1.75

    public int width();

    public int depth();

    public float positionX();

    public float positionY();

    public float positionZ();

    public float rotX();

    public float rotY();

    public float rotZ();

    public Object[] inverntory();

    public Object heldItem();

    public boolean isHolding();

    public int reactionTime();

    public boolean gender();
}
