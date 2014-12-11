package dontlookback;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author Carl
 */
public class testData extends StaticList {

    protected Cube[] data;
    //this is really shitty code so that we can test this faster.
    //i should rewrite this as if it's created INTO an array list.
    //which should make it shorter, for each accecible and best of all
    //easier to edit and closer to what we'll be using later

    public testData() {
        float[][] position = {{30, 15, 30}, {2f, 5f, 3f}, {-2f, 7f, -2f}, {-1f, 10f, 3f}, {-3f, 0f, -2f}, {-5f, 5f, 1f}};
        //x,y,z //remember flat plane is x and Z and vertical plane is y
        for (int i = 0; i < 6; i++) {
            data[i]=new Cube(position[i],0,i+1);
            this.add(new Cube(position[i], 0, i+1));
            System.out.println(data[i]);
        }
        for (Cube c : data) {
            c.setColor(randomColor());
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

    public float[] randomColor() {
        
        float R = (int) (Math.random() * 256);
        float G = (int) (Math.random() * 256);
        float B = (int) (Math.random() * 256);
             
        float[] color = {R, G, B}; //random color, but can be bright or dull
        
        //to get rainbow, pastel colors
        /*
        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
        final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
        color = Color.getHSBColor(hue, saturation, luminance); */
        return color;
    }

    public Cube[] allData() {
        return data;
    }
}
