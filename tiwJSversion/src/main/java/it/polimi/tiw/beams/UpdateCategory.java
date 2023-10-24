package it.polimi.tiw.beams;

public class UpdateCategory {
    private long oldId;
    public long newId;

    public UpdateCategory(long oldId,long newId) {
        this.oldId = oldId;
        this.newId = newId;
    }

    public long getNewId() {
        return newId;
    }

    public long getOldId() {
        return oldId;
    }
}
