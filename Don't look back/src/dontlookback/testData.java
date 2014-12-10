package dontlookback;

/**
 *
 * @author Carl
 */
public class testData {

    //this is really shitty code so that we can test this faster.

    Cube cube1, cube2, cube3, cube4, cube5;

    public testData() {
        float[] testCenter = {2f, 5f, 3f}; //x,y,z //remember flat plane is x and Z and vertical plane is y
        float[] testCenter2 = {-2f, 7f, -2f};
        float[] testCenter3 = {-1f, 10f, 3f};
        float[] testCenter4 = {-3f, 0f, -2f};

        cube1 = new Cube(); //style 1: initilize and then set up
        cube1.setX(30);
        cube1.setY(15);
        cube1.setZ(30);
        cube1.setOrientation(45);
        cube1.setWidth(30);
        cube1.setColor(1, 0, 0);
        cube2 = new Cube(testCenter, 0, 1); //center position, orientation, width.
        cube2.setColor(0, 1, 0);
        cube3 = new Cube(testCenter2, 0, 2); //center position, orientation, width.
        cube3.setColor(0, 0, 1);
        cube4 = new Cube(testCenter3, 4, 3); //center position, orientation, width.
        cube4.setColor(1, 1, 0);
        cube5 = new Cube(testCenter4, 140, 4); //center position, orientation, width.
        cube5.setColor(0, 1, 1);

        //Dyanmically generate these cubes in an array and pull method the whole sequence.        
        cube1.setUpVBO();
        cube2.setUpVBO();
        cube3.setUpVBO();
        cube4.setUpVBO();
        cube5.setUpVBO();

    }

    public Cube cubeData(int cubeNum) {
        Cube data = new Cube();

        {

            switch (cubeNum) {

                case 0:
                    data = cube1;
                    break;
                case 1:
                    data = cube2;
                    break;
                case 2:
                    data = cube3;
                    break;
                case 3:
                    data = cube4;
                    break;
                case 4:
                    data = cube5;
                    break;
                default:
                    data = cube1; //this is not a real thing. but in case i do 1-5 instead of 0-4 it still renders it al
            }

            return data;
        }
    }
    
    public Cube[] allData(){
        return new Cube[] {cube1,cube2,cube3,cube4,cube5};
    }
}
