package dontlookback;

/**
 *
 * @author Carl
 */
public class cubeArray {

    public cubeArray() {
        //using cube arrays to insert cubes into as we generate and plop them a sort of master list of cubes to delete and create.
        int size;
        int position;
        int lastEntry;
        int[] empty;
        cubeArray[] cubeArr;
    }

    //maybe use the poker deck code, which shrinks the list over and over again as it occurs 
    //or better yet just use two way nodes. or some fancy new list? just be great people haha
    public static void createArray(cubeArray[] cubeArr) {
        cubeArr[0] = null;
    }

    public static void addCube(Cube[] cubeArr, int pointer, Cube cube) {
        cube = new Cube();
        cubeArr[pointer] = cube;
    }

    public static void addCube(Cube[] cubeArr, int pointer) {
        Cube cube;
        cube = new Cube();
        cubeArr[pointer] = cube;
    }

}
