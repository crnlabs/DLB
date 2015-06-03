package dontlookback;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Cube extends Objects {

    private float width;

    public Cube() {
        super();
        randomXYZ();
        randomSize();
        orientation = 0;
        setCurrent();
    }

    public Cube(Cube cube) {
        super();
        rgb = cube.rgb;
        width = cube.width;
        x = cube.x;
        y = cube.y;
        z = cube.z;
        setCurrent();
    }

    public Cube(float x, float y, float z, float angle, float width) {
        super(x, y, z, angle);
        setWidth(width);
        setCurrent();
    }

    public Cube(float[] coords, float angle, float width) {
        super(coords, angle);
        setWidth(width);
        setCurrent();
    }

    private void setCurrent() {
        cX = x;
        cY = y;
        cZ = z;
    }

    public void setColor(float red, float green, float blue) {
        float[] color = {red, green, blue};
        this.setRGB(color);
    }

    public void setColor(float[] rgb) {

        this.setRGB(rgb);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public void render() {
        float cX = x, cY = y, cZ = z;
        glPushMatrix();

        glTranslatef(x, y, z);              //then put it back, or move it to it's new spot if x,y,z has changed
        glRotatef(orientation++, 1, 1, 0);     //then do the rotation
        glTranslatef(-cX, -cY, -cZ);   //put it at 0,0,0
        setCurrent();
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, handle);
        glVertexPointer(3, GL_FLOAT, 24, 0); //stride is weird.
        glEnableClientState(GL_COLOR_ARRAY);
        glColorPointer(3, GL_FLOAT, 24, 12); //should the 12 become 18?
        glDrawArrays(GL_TRIANGLES, 0, 36); //type, first, count
        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);

        glPopMatrix();

    }

    public void setUpVBO() {

        FloatBuffer vertexData = BufferUtils.createFloatBuffer(216);

        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z + (width / 2)), //+Z THIS IS A SIDE????  (flipped to -z? what) 
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z + (width / 2)), //BECAUSE Z IS A SIDE AND Y IS vertical I GET IT NOW
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z + (width / 2)), //+X (flipped to -X)
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z + (width / 2)), //-X
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z + (width / 2)), //+Y  (TOP)
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z - (width / 2)), //-Y (error)
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z + (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z - (width / 2)), //-Z (flipped to be positive Z?) (error)
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.put(new float[]{(x + (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y - (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)), (y + (width / 2)), (z - (width / 2)),
            (rgb[0]), (rgb[1]), (rgb[2])});

        vertexData.flip();

        handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, handle);
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    public void delete() {

        glDeleteBuffers(handle);

    }

    @Override
    public void setColor() {
        float[] color = Objects.randomColor();
        this.setRGB(color);
    }

    public void randomBehavior() {  //no y movement to be clean
        x = x + (((float) (Math.random() * 0.2)) - ((float) (Math.random() * 0.2))); // random amount
        //y = (float) (Math.random() * -15); //
        z = z + (((float) (Math.random() * 0.1)) - ((float) (Math.random() * 0.1))); // 
        rotate();
        rgb[0] = rgb[0]++;
        rgb[1] = rgb[1]++;
        rgb[2] = rgb[2]++;

    }

    public void rotate() {
        orientation = orientation + .1f;
        if (orientation >= 360f) {
            orientation = orientation - 360f;
        }
    }

    @Override
    public void randomXYZ() {
        x = (float) (Math.random() * -50); // * 256 removed for testing
        y = (float) (Math.random() * 25); // * 256 removed for testing
        z = (float) (Math.random() * -50); // * 256 removed for testing
        //return new float[] {x,y,z};
    }

    private void randomSize() {
        width = (float) (Math.random() * 5); // * 256 removed for testing

    }

    @Override
    public void behavior() {
        if (Settings.testMode() == true) {
            randomBehavior();
        }
    }

    @Override
    public void update() {

        if (Settings.pausedState() == false) {
            behavior();
        }
        render();
    }

    private void move(float[] input) {

    }

}
