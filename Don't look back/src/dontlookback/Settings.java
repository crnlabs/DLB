package dontlookback;

/**
 *
 * @author Carl For this Class I want to just bundle together all of the values
 * that we might need to pull or use anywhere else, gravity, wind speed, or even
 * something as simple as administrator mode = on/off; All of those options
 * should be in here.
 *
 */
public class Settings {

    private boolean admin = true; //default to on for bug testing purposes, so it's obvious if settings aren't initilized correctly.
    private int debug; 
    private static int[] resolution = new int[2]; //to be honest this needs to be changed to ...profiles? or something? it's opengl something or another. james should know
    private final boolean monsterSpawn;
    private final boolean trialMode;
    private final boolean grue;
    private final boolean lightsFlicker;
    private final boolean achievements;
    private final boolean feedback;
    private static boolean paused;
    private static boolean test;

    public Settings() {
        this.resolution[0] = 1024; //set horizonal resolutoin
        this.resolution[1] = 768; //set verticle resolution
        this.monsterSpawn = true;
        this.admin = true; //for playtesting or other production turn this off before compiling.
        this.trialMode = true;
        this.grue = false; //grue: animated...maybe, if lights = off and no held lights then death = true. hahahaha //hardmode
        this.lightsFlicker = false; //nomal mode, do the lightos go off, if not its easy mode
        this.achievements = true;
        this.feedback = true; //before we even test beta we need to have these games send back stats to a server that records times, deaths, by what, as much data as we can.
        this.debug = 1; //0 off, 1 all info, 2 errors and critical info only.
        this.paused = false;
        this.test = false;
    }

    public static boolean pausedState(){
        return paused;
    }
    
    public static void setPaused(boolean state){
        paused = state;
    }
    
    public static boolean testMode(){
        return test;
    }
    
    public static void setTest(boolean state){
        test = state;
    }
    
    public static int[] resolution() {
        return resolution;
    }

    public boolean admin() {
        return admin;//admin mode unlock key: RaindeerFlotilla
    }

    public int debug() {
        return debug;
    }

}
