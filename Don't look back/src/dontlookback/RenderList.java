package dontlookback;

public class RenderList {

    protected Objects[] renderList;

    public RenderList() {
        renderList = new Objects[0];
    }

    public RenderList(RenderList list) {
        renderList = list.renderList;
    }

    public void add(Objects entry) {
        Objects[] temp;
        int i = 0;
        temp = new Objects[renderList.length + 1];
        for (Objects o : renderList) {
            temp[i] = o;
            i++;
        }
        temp[renderList.length] = entry;
        renderList = temp;
    }

    public void add(Objects[] entry) {
        Objects[] temp;
        int i = 0;
        temp = new Objects[renderList.length + entry.length];
        for (Objects o : renderList) {
            temp[i] = o;
            i++;
        }
        for (Objects o : entry) {
            temp[i] = o;
            i++;
        }
        renderList = temp;
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
        renderList = temp;
    }

    public void render() {
        for (Objects o : renderList) {
            o.render();
        }
    }

}
