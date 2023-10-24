package it.polimi.tiw.beams;

public class Category implements Comparable<Category>{
    private long code;
    private String name;
    private int level;
    private int padding;
    private CategoryState state;
    public Category(long code, String name){
        this.code = code;
        this.name = name;
        state = CategoryState.BASE;
    }

    /*
    *       set methods
    */

    public void setState(CategoryState state) {
        this.state = state;
    }

    public void setLevel(int level) {
        this.level = level;
        this.padding = level*30;
    }

    /*
     *       get methods
     */

    public CategoryState getState() {
        return state;
    }

    public long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getPadding() {
        return padding;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return this.getCode() == ((Category) o).getCode();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(Category o) {
        return (Long.toString(this.getCode()).compareTo(Long.toString(o.getCode())));
    }
}
