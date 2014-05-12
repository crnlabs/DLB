package dontlookback;

/**
 *
 * @author Carl
 * For this Class I want to just bundle together all of the values that we might
 * need to pull or use anywhere else, gravity, wind speed, or even something
 * as simple as Admin mode = on/off; All of those options should be in here.
 * 
 */
public class World {
    private final boolean admin;
    private final boolean monsterSpawn;
    private final boolean trialMode;
    private final boolean grue;
    private final boolean lightsFlicker;
    private final boolean achievements;
    private final boolean feedback;
    
    public boolean lightsOn;

    public World() {
        this.monsterSpawn = true;
        this.admin = true;
        this.trialMode = true;
        this.grue = false; //grue: animated...maybe, if lights = off and no held lights then death = true. hahahaha //hardmode
        this.lightsFlicker = false; //nomal mode, do the lightos go off, if not its easy mode
        this.achievements = true; 
        this.feedback = true; //before we even test beta we need to have these games send back stats to a server that records times, deaths, by what, as much data as we can.
    }
    
}
