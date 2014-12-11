package dontlookback;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author Carl
 */
public class testData extends StaticList {

    //protected Cube[] data;
    //this is really shitty code so that we can test this faster.
    //i should rewrite this as if it's created INTO an array list.
    //which should make it shorter, for each accecible and best of all
    //easier to edit and closer to what we'll be using later

    public testData() {
        super();
        float[][] position = {{30, 15, 30}, {2f, 5f, 3f}, {-2f, 7f, -2f}, {-1f, 10f, 3f}, {-3f, 0f, -2f}, {-5f, 5f, 1f}};
        //x,y,z //remember flat plane is x and Z and vertical plane is y
        for (int i = 0; i < 6; i++) {
            this.add(new Cube(position[i], 0, i+1));
        }
        for (Objects c : renderList) {
            c.setColor();
            c.setUpVBO();
        }
//
//        cube1 = new Cube(); //style 1: initilize and then set up
//        cube1.setX(30);
//        cube1.setY(15);
//        cube1.setZ(30);
//        cube1.setOrientation(45);
//        cube1.setWidth(30);
//        cube1.setColor(1, 0, 0);
//        cube2 = new Cube(testCenter, 0, 1); //center position, orientation, width.
//        cube2.setColor(0, 1, 0);
//        cube3 = new Cube(testCenter2, 0, 2); //center position, orientation, width.
//        cube3.setColor(0, 0, 1);
//        cube4 = new Cube(testCenter3, 4, 3); //center position, orientation, width.
//        cube4.setColor(1, 1, 0);
//        cube5 = new Cube(testCenter4, 140, 4); //center position, orientation, width.
//        cube5.setColor(0, 1, 1);
//        jess = new Cube(testCenter5, 140, 4); //center position, orientation, width.
//        jess.setColor(12, 6, 4);
//
//        //Dyanmically generate these cubes in an array and pull method the whole sequence.        
//        cube1.setUpVBO();
//        cube2.setUpVBO();
//        cube3.setUpVBO();
//        cube4.setUpVBO();
//        cube5.setUpVBO();
//        jess.setUpVBO();
        }


    public  Objects[] allData() {
        return this.renderList;
    }
}
