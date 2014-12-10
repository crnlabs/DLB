package dontlookback;

/**
 *
 * @author Carl
 */
public class RenderList {

    protected Objects[] renderList;

    public void add(Objects entry) {
        Objects[] temp;
        int i = 0;
        temp = new Objects[renderList.length + 1];
        for (Objects o : renderList) {
            temp[i] = o;
            i++;
        }
        temp[renderList.length] = entry;
    }

    public void remove(Objects entry) {
        Objects[] temp;
        int i = 0;
        temp = new Objects[renderList.length - 1];
        for (Objects o : renderList) {
            if (!(o.equals(entry))) {
                temp[i] = o;
                i++;
            }
        }
    }

    public void render() {
        for (Objects o : renderList) {
            o.render();
        }
    }

}
