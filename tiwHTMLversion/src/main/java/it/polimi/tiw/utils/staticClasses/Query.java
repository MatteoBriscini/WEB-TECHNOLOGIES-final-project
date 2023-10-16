package it.polimi.tiw.utils.staticClasses;

public class Query {
    public static String categoryOrder = " ORDER BY CAST(ID AS nchar) ";
    public static String treeQuery = "WHERE t.ID IN (\n" +
            "\tWITH RECURSIVE subTree (fatherID, ID) AS (\n" +
            "\t\tSELECT fatherID, ID FROM categories\n" +
            "\t\tWHERE fatherID = ?\n" +
            "\t\tUNION ALL\n" +
            "\t\tSELECT c.fatherID, c.ID FROM (subTree AS f) JOIN (categories AS c) ON f.ID = c.fatherID\n" +
            "\t)\n" +
            "\tSELECT c.ID FROM subTree AS f JOIN categories AS c ON f.ID = c.ID\n" +
            "\tUNION ALL\n" +
            "\tSELECT ID from categories WHERE ID=?\n" +
            ")";
}
