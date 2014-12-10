package dontlookback;

/**
 *
 * @author Carl
 */
public class cubeArray {

    int position;
    int end;

    public cubeArray() {
        //using cube arrays to insert cubes into as we generate and plop them a sort of master list of cubes to delete and create.
        Cube[] cubeArr;
    }

    //maybe use the poker deck code, which shrinks the list over and over again as it occurs 
    //or better yet just use two way nodes. or some fancy new list? just be great people haha
    public void createArray(cubeArray[] cubeArr) {
        cubeArr[0] = null;
    }

    public void addCube(Cube[] cubeArr, int pointer, Cube cube) { //adds specific cube to array
        Cube[] temp = new Cube[cubeArr.length + 1];
        for (Cube a : cubeArr) {
            temp[position] = a;
        }
        temp[cubeArr.length] = cube;
        cubeArr = temp;
        this.end = cubeArr.length;
    }

    public void addCube(Cube[] cubeArr, int pointer) { //adds a new random or default cube to the array
        Cube cube = new Cube();
        Cube[] temp = new Cube[cubeArr.length + 1];
        for (Cube a : cubeArr) {
            temp[position] = a;
        }
        temp[cubeArr.length] = cube;
        cubeArr = temp;
        this.end = cubeArr.length;
    }
}
