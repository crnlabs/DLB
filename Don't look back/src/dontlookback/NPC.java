/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dontlookback;

/**
 *
 * @author Carl
 */
public interface NPC {
    
    public boolean Interactive();
    public boolean Quest();
    public int movementPattern();
    public int state(); //0 - inactive //1 - active //2 - hostile //3 - ???
    
}
