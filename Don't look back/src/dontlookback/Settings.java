package dontlookback;

/**
 *
 * @author Carl
 * For this Class I want to just bundle together all of the values that we might
 * need to pull or use anywhere else, gravity, wind speed, or even something
 * as simple as administrator mode = on/off; All of those options should be in here.
 * 
 */
public class Settings {
    private final boolean admin;
    private final boolean monsterSpawn;
    private final boolean trialMode;
    private final boolean grue;
    private final boolean lightsFlicker;
    private final boolean achievements;
    private final boolean feedback;
    private final int[] resolution = new int[2]; //to be honest this needs to be changed to ...profiles? or something? it's opengl something or another. james should know
    
    public boolean lightsOn;

    public Settings() {
        this.resolution[0] = 1024; //set horizonal resolutoin
        this.resolution[1] = 768; //set verticle resolution
        this.monsterSpawn = true;
        this.admin = true;
        this.trialMode = true;
        this.grue = false; //grue: animated...maybe, if lights = off and no held lights then death = true. hahahaha //hardmode
        this.lightsFlicker = false; //nomal mode, do the lightos go off, if not its easy mode
        this.achievements = true; 
        this.feedback = true; //before we even test beta we need to have these games send back stats to a server that records times, deaths, by what, as much data as we can.
    }
    
    public int[] resolution(){
        return resolution;
    }
}
