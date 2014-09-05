package dontlookback;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Cube extends Objects {

    private float width;

    public Cube() {
        super();
        setWidth(0);
    }

    public Cube(float x, float y, float z, float angle, float width) {
        super(x, y, z, angle);
        setWidth(width);
    }

    public Cube(float[] coords, float angle, float width) {
        super(coords, angle);
        setWidth(width);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void render() {
        float cX = x, cY = y, cZ = z;
        glPushMatrix();

        glTranslatef(x, y, z);
        glRotatef(orientation, 0, 1, 0);
        glTranslatef(-1 * x, -1 * y, -1 * z);

        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER,handle);
        glVertexPointer(3, GL_FLOAT, 24, 0); //stride is weird.
        glEnableClientState(GL_COLOR_ARRAY);
        glColorPointer(3, GL_FLOAT, 24, 12); //should the 12 become 18?
        glDrawArrays(GL_TRIANGLES, 0, 36); //type, first, count
        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);

        glPopMatrix();

    }
    
    public void setUpVBO(){
        
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(216);
        
        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z + (width / 2)), //+Z THIS IS A SIDE????  (flipped to -z? what) 
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        
        vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z + (width / 2)), //BECAUSE Z IS A SIDE AND Y IS vertical I GET IT NOW
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z + (width / 2)), 
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        
        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z + (width / 2)), //+X (flipped to -X)
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});

        vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
	vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z + (width / 2)), //-X
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z - (width / 2)),
	    (rgb[0]),(rgb[1]),(rgb[2])});

	vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z - (width / 2)),
	    (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});

        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z + (width / 2)), //+Y  (TOP)
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
	
	vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z - (width / 2)), //-Y (error)
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});

	vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z + (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
	vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});

        vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z - (width / 2)), //-Z (flipped to be positive Z?) (error)
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x + (width / 2)),(y - (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});

	vertexData.put(new float[]{(x + (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
	vertexData.put(new float[]{(x - (width / 2)),(y - (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        vertexData.put(new float[]{(x - (width / 2)),(y + (width / 2)),(z - (width / 2)),
            (rgb[0]),(rgb[1]),(rgb[2])});
        
       vertexData.flip();
        
        handle=glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,handle);
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        
    }
    
    public void delete(){
        
        glDeleteBuffers(handle);
        
    }
    
}
