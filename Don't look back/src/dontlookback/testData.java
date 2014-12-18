package dontlookback;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author Carl
 */
public class testData extends StaticList {

    public testData(int Amount) {
        super();
        //x,y,z //remember flat plane is x and Z and vertical plane is y
        for (int i = 0; i < Amount; i++) {
            this.add(new Cube()); //default cube is a random, because of testing. if we pass in a int = 0 it should be on the ground, and int = 1 should be in the air.
        }

        for (Objects c : renderList) {
            c.setColor();
            c.setUpVBO();
        }
    }

    public Objects[] allData() {
        return this.renderList;
    }
}
