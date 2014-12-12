package dontlookback;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author Carl
 */
public class testData extends StaticList {

    public testData() {
        super();
        float x,y,z;
        
        float[][] position = {{30, 15, 30}, {2f, 5f, 3f}, {-2f, 7f, -2f}, {-1f, 10f, 3f}, {-3f, 0f, -2f}, {-5f, 5f, 1f}};
        //x,y,z //remember flat plane is x and Z and vertical plane is y
        for (int i = 0; i < 6; i++) {
            this.add(new Cube());
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
