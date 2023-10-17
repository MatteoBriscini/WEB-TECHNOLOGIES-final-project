package it.polimi.tiw.beams;

public class UpdateCategory {
    private int oldId;
    public int newId;

    public UpdateCategory(int oldId, int newId) {
        this.oldId = oldId;
        this.newId = newId;
    }

    public int getNewId() {
        return newId;
    }

    public int getOldId() {
        return oldId;
    }
}
