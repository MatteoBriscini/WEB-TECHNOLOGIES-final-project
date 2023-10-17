package it.polimi.tiw.dao;

import it.polimi.tiw.beams.Category;
import it.polimi.tiw.beams.CategoryState;
import it.polimi.tiw.beams.UpdateCategory;
import it.polimi.tiw.exceptions.CategoryDBException;
import it.polimi.tiw.utils.ConnectionsHandler;
import it.polimi.tiw.utils.staticClasses.Query;
import jakarta.servlet.ServletContext;
import jakarta.servlet.UnavailableException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoriesDAO {

    /**
     * this method retrieve the taxonomy from the DB
     * @param from an optional parameters needed when some of the categories are selected from the user
     * @return a list of category correctly ordered to represent the retrieved taxonomy
     */
    public static List<Category> getCategories(int  from, ServletContext context) throws SQLException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        cnt.setAutoCommit(false); // disable autocommit
        List<Category> categories = new ArrayList<>();
        String query = "SELECT id, name, length(CAST(ID AS nchar)) FROM categories " + Query.categoryOrder;
        Statement st = null;

        try {
            st = cnt.createStatement();
            ResultSet result = st.executeQuery(query);
            Category category;

            ArrayList<Integer> tree = getSubTree(from, cnt);

            while(result.next()) {
                category = new Category(result.getInt("ID"), result.getString("name"));
                if(!tree.isEmpty() && category.getCode()==tree.get(0)){
                    category.setState(CategoryState.SELECTED);
                    tree.remove(0);
                }
                category.setLevel(result.getInt(3)-1);
                categories.add(category);
            }
        }  catch (Exception e) {
            cnt.rollback();
            throw new RuntimeException(e);
        }finally {
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
            ConnectionsHandler.releaseConnection(cnt);
        }
        return categories;
    }
    /**
     * this method retrieve the taxonomy from the DB
     * @return a list of category correctly ordered to represent the retrieved taxonomy
     */
    public static List<Category> getCategories(ServletContext context) throws SQLException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        List<Category> categories = CategoriesDAO.getCategoriesSupport(cnt);

        ConnectionsHandler.releaseConnection(cnt);
        return categories;
    }
    public static void updateCategories(int code,String newName, ServletContext context) throws UnavailableException, SQLException {
        Connection cnt = ConnectionsHandler.takeConnection(context);
        cnt.setAutoCommit(false); // disable autocommit
        String query = "UPDATE categories SET name=? WHERE ID=?";
        PreparedStatement st= null;

        try {

            CategoriesDAO.triggerEnable(cnt); //enable trigger on categories update

            st = cnt.prepareStatement(query);
            st.setString(1, newName);
            st.setInt(2, code);
            st.executeUpdate();
        } catch (SQLException e) {
            cnt.rollback();
            throw new RuntimeException(e);
        }finally {
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
            ConnectionsHandler.releaseConnection(cnt);
        }
    }
    private static List<Category> getCategoriesSupport(Connection cnt) throws SQLException, UnavailableException {
        String query = "SELECT id, name, length(CAST(ID AS nchar)) FROM categories " + Query.categoryOrder;
        java.sql.Statement st = cnt.createStatement();
        ResultSet result = st.executeQuery(query);
        Category category;

        List<Category> categories = new ArrayList<>();
        while(result.next()) {
            category = new Category(result.getInt("ID"), result.getString("name"));
            category.setLevel(result.getInt(3)-1);
            categories.add(category);
        }
        return categories;
    }

    /**
     * add a new category to the DB
     * @param fatherCode the category ID under which the new elements have to be added
     * @param name the new category name
     * @param creator the ID of the user that create the new category
     * @throws CategoryDBException if fatherCode is a non-existing category, or if the creator hasn't the authorization to make modify on the DB, or if the father category hasn't free places
     */
    public static List<Category> addCategory(int fatherCode, String name, String creator, ServletContext context) throws SQLException, CategoryDBException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        cnt.setAutoCommit(false); // disable autocommit
        int newID;
        try {
            newID = getNewId(fatherCode, cnt);
            addNewCategories(newID,name,creator,cnt);
            cnt.commit();
            return CategoriesDAO.getCategoriesSupport(cnt);
        }  catch (Exception e) {
            cnt.rollback();
            throw new CategoryDBException(e.getMessage());
        }finally {
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
            ConnectionsHandler.releaseConnection(cnt);
        }
    }

    /**
     * remove a category and all the ones that depend on from the just removed one
     * @param fatherCode the ID of the category the user want to remove
     */
    public static List<Category> removeSubTree(int fatherCode, ServletContext context) throws SQLException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        CategoriesDAO.triggerEnable(cnt); //enable trigger on categories delete

        cnt.setAutoCommit(false); // disable autocommit
        try {
            ArrayList<Integer> tree = getSubTree(fatherCode, cnt);
            Collections.sort(tree);

            for (int i = tree.size()-1;i>=0;i--) removeCategory(tree.get(i), cnt);
            CategoriesDAO.triggerDisable(cnt);//disable trigger on categories delete

            updateSubTreeAfterRemoval(fatherCode, cnt);

            cnt.commit();
            return CategoriesDAO.getCategoriesSupport(cnt);
        }  catch (Exception e) {
            cnt.rollback();
            throw new RuntimeException(e);
        }finally {
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
            ConnectionsHandler.releaseConnection(cnt);
        }
    }

    /**
     * paste a selected tree under a specific category
     * the selected tree can be removed (to implement cut and paste) or not (to implement copy and paste)
     * @param from the father category ID on top of the selected tree
     * @param where the category ID under which the selected tree have to be pasted
     * @param creator the ID of the user that create the new category
     * @param remove if this boolean is true the selected tree will be removed
     * @throws CategoryDBException if where is a non-existing category, or if the creator hasn't the authorization to make modify on the DB, or if where hasn't free places
     */
    public static List<Category> pasteSubTree(int from, int where, String creator, boolean remove, ServletContext context) throws SQLException, UnavailableException, CategoryDBException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        cnt.setAutoCommit(false); // disable autocommit
        try {
            ArrayList<Category> newSubTree = getNewIdForSubTree(from, getNewId(where, cnt), cnt);

            for (Category s:newSubTree){
                addNewCategories(s.getCode(),s.getName(),creator, cnt);
            }

            if(remove){
                CategoriesDAO.triggerEnable(cnt); //enable trigger on categories delete

                ArrayList<Integer> selected = getSubTree(from, cnt);
                Collections.sort(selected);

                for (int i = selected.size()-1;i>=0;i--) removeCategory(selected.get(i), cnt);
                CategoriesDAO.triggerDisable(cnt); //disable trigger on categories delete

                updateSubTreeAfterRemoval(from, cnt);
            }

            cnt.commit();

            return CategoriesDAO.getCategoriesSupport(cnt);
        }  catch (Exception e) {
            cnt.rollback();
            throw new CategoryDBException(e.getMessage());
        }finally {
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
            ConnectionsHandler.releaseConnection(cnt);
        }
    }

    /**
     * add a new category, on the top of the taxonomy, to the DB
     * @param name the new category name
     * @param creator the ID of the user that create the new category
     * @throws CategoryDBException if there is no more free place on the top of the taxonomy, or if the creator hasn't the authorization to make modify on the DB
     */
    public static List<Category> addNewHead(String name, String creator, ServletContext context) throws SQLException, CategoryDBException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);
        int code;

        cnt.setAutoCommit(false);
        try {
            code = getNewHeadID(cnt);
            addNewCategories(code, name, creator, cnt);
            cnt.commit();
            return CategoriesDAO.getCategoriesSupport(cnt);
        }  catch (Exception e) {
            cnt.rollback();
            throw new CategoryDBException(e.getMessage());
        }finally {
            ConnectionsHandler.releaseConnection(cnt);
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
        }
    }

    /**
     * used to implements the search bar with multiple input
     * this method doesn't return only the categories that respect the input but also all the categories dominated from those
     * the categories that respect the input are marked as selected and displayed differently in the UI
     * @param search all the multiple input inserted by the user
     * @return  a list of category correctly ordered to represent the retrieved taxonomy with only the interesting categories
     */
    public static ArrayList<Category> searchCategory(ArrayList<String> search, ServletContext context) throws SQLException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        ArrayList<Category> searchResults = new ArrayList<>();

        cnt.setAutoCommit(false); // disable autocommit
        try{
            for(String s: search){
                ResultSet result = getSearchedID(s, cnt);
                while(result.next()) getSubTreeSearchedList(result.getInt(1),searchResults,cnt);
            }
        } catch (Exception e) {  //TODO gestione eccezioni
            cnt.rollback();
            throw new RuntimeException(e);
        }finally {
            ConnectionsHandler.releaseConnection(cnt);
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
        }

        Collections.sort(searchResults);
        return searchResults;
    }

    public static List<Category> getModifiedTaxonomy(int from, int where, boolean remove, ServletContext context) throws SQLException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);
        ArrayList<Integer> selectedSubTree = new ArrayList<>();
        ArrayList<Integer> brotherSubTree = new ArrayList<>();
        List<Category> categories;

        cnt.setAutoCommit(false); // disable autocommit

        if(remove) {
            selectedSubTree = getSubTree(from,cnt);
            ResultSet resultSet = getChildrenBiggerThanGiven(from / 10, from, cnt);//get the selected son and all his brother bigger than him
            while (resultSet.next()) {
                brotherSubTree.addAll(getSubTree(resultSet.getInt(1),cnt));
            }
        }

        try {
            String query = "SELECT id, name, length(CAST(ID AS nchar)) FROM categories " + Query.categoryOrder;
            java.sql.Statement st = cnt.createStatement();
            ResultSet result = st.executeQuery(query);
            Category category;

            categories = new ArrayList<>();
            int i = 0;
            while(result.next()) {
                category = new Category(result.getInt("ID"), result.getString("name"));
                category.setLevel(result.getInt(3) - 1);

                if(!selectedSubTree.isEmpty() && selectedSubTree.get(0)==result.getInt("ID")) {
                    category.setState(CategoryState.REMOVED);
                    selectedSubTree.remove(0);
                }else if (!brotherSubTree.isEmpty() && brotherSubTree.get(0)==result.getInt("ID")){
                    category.setState(CategoryState.UPDATED);
                    brotherSubTree.remove(0);
                }
                categories.add(category);
            }

            categories.addAll(getNewIdForSubTree(from, getNewId(where, cnt), cnt));
            Collections.sort(categories);
        }  catch (Exception e) {
            cnt.rollback();
            throw new RuntimeException(e);
        }finally {
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
            ConnectionsHandler.releaseConnection(cnt);
        }

        return categories;
    }
    /**
     * remove a single category from the DB
     * @param ID the ID of the category to remove
     */
    private static void removeCategory(int ID, Connection cnt) throws SQLException{
        String query = "DELETE FROM categories WHERE ID=?";
        PreparedStatement statement = cnt.prepareStatement(query);
        statement.setInt(1,ID);
        statement.executeUpdate();
    }

    /**
     * get all the categories dominate by a specific ID
     * @param fatherCode the id on the top of the searched subtree
     * @return an ordered list of categories representing the required subtree
     */
    private static ArrayList<Integer> getSubTree(int fatherCode, Connection cnt) throws SQLException {
        String query = "SELECT ID FROM categories AS t\n" + Query.treeQuery + Query.categoryOrder;

        ArrayList<Integer> tree = new ArrayList<>();

        PreparedStatement statement = cnt.prepareStatement(query);
        for(int i=1;i<=2;i++)statement.setInt(i, fatherCode);
        ResultSet result = statement.executeQuery();

        while (result.next()) tree.add(result.getInt(1));

        return tree;
    }

    /**
     * evaluate the first free ID for a new added category
     * @param fatherCode the category under witch the is necessary to add the new category
     * @return the required ID
     * @throws CategoryDBException if the given fatherCode is referred to a non-existing category, or if the father code hasn't free space
     */
    private static int getNewId(int fatherCode, Connection cnt) throws SQLException, CategoryDBException {
        String query = "SELECT max(ID) FROM categories\n" +
                "\twhere fatherID = ? AND fatherID IS NOT NULL";

        PreparedStatement st= cnt.prepareStatement(query);
        st.setInt(1, fatherCode);
        ResultSet result = st.executeQuery();
        cnt.commit();

        result.next();

        if(result.getInt(1)>=(fatherCode*10+10)) throw new CategoryDBException("no free place");

        if (result.getInt(1)==0)return (fatherCode*10)+1;

        return result.getInt(1)+1;
    }

    /**
     * evaluate the first free ID for a new category added on the top of the taxonomy
     * @return the required ID
     * @throws CategoryDBException if there is no more free space on the top of the taxonomy
     */
    private static int getNewHeadID(Connection cnt) throws SQLException, CategoryDBException {
        String query = "SELECT MAX(ID)+1 FROM categories\n" +
                "WHERE ID NOT IN (SELECT ID FROM categories\n" +
                "\tWHERE fatherID IS NOT NULL AND fatherID > 0\n" +
                ")";
        java.sql.Statement st = cnt.createStatement();
        ResultSet result = st.executeQuery(query);
        result.next();

        if(result.getInt(1)>9) throw new CategoryDBException("no free place");

        if (result.getInt(1)==0)return 1;


        return result.getInt(1);
    }

    /**
     * search for all the existing children bigger than another child if there are ones
     * this method is particular useful in case of deletion, if exist category on same level but bigger than the remove ones
     * @param fatherCode the category ID of the father
     * @param givenCode the category ID of the given child
     * @return all the existing children bigger than another child
     */
    private static ResultSet getChildrenBiggerThanGiven(int fatherCode, int givenCode, Connection cnt) throws SQLException {
        String query = "SELECT ID FROM categories WHERE fatherID = ? AND ID > ?";
        PreparedStatement st= cnt.prepareStatement(query);
        st.setInt(1, fatherCode);
        st.setInt(2, givenCode);
        return st.executeQuery();
    }


    /**
     * evaluate new ID for an entire pasted subtree
     * @param from from where the subtree is copied or cut
     * @param newID the new ID for the category on the top of the subtree
     */
    private static ArrayList<Category> getNewIdForSubTree(int from, int newID, Connection cnt) throws SQLException {
        String query = "SELECT CONCAT(?,substring(t.ID ,?)), name FROM categories AS t\n" + Query.treeQuery;

        ArrayList<Category> results = new ArrayList<>();

        PreparedStatement statement = cnt.prepareStatement(query);
        statement.setInt(1, newID);
        statement.setInt(2, String.valueOf(from).length()+1);
        for(int i=3;i<=4;i++)statement.setInt(i, from);

        ResultSet result = statement.executeQuery();

        while (result.next()) {
            Category tmp = new Category(result.getInt(1), result.getString("name"));
            tmp.setLevel(String.valueOf(tmp.getCode()).length()-1);
            tmp.setState(CategoryState.SELECTED);
            results.add(tmp);
        }

        return results;
    }

    /**
     * when a child is remove all the children on the same level bigger than the removed one has an inconsistent ID
     * is necessary to update that ID
     * @param fatherCode the ID of the father category under witch a child has been removed
     */
    private static void updateSubTreeAfterRemoval(int fatherCode, Connection cnt) throws SQLException {
        ResultSet resultSet = getChildrenBiggerThanGiven(fatherCode/10, fatherCode, cnt);
        while (resultSet.next()){
            ResultSet resultSet1 = getNewIDForSubTreeAfterRemoval(resultSet.getInt(1), cnt);

            ArrayList<UpdateCategory>results = new ArrayList<>();

            while (resultSet1.next()) {
                results.add(new UpdateCategory(resultSet1.getInt(1), resultSet1.getInt(2)));

            }
            for(int i = 0; i<results.size();i++){
                if(i==results.size()-1)CategoriesDAO.triggerEnable(cnt); //enable trigger on categories update
                updateID(results.get(i).getOldId(), results.get(i).getNewId(), cnt);
            }
        }
    }
    /**
     * evaluate new ID for an entire subtree knowing the last ID of the father category
     * this method is particular useful in case of deletion, if exist category on same level but bigger than the remove ones
     * @param oldFatherCode the last ID of the father category
     * @return a list with the tree already updated
     */

    private static ResultSet getNewIDForSubTreeAfterRemoval(int oldFatherCode, Connection cnt) throws SQLException {
        String query = "SELECT  t.ID, t.ID-POWER(10, (LENGTH(CAST(t.ID AS nchar))-?)) FROM categories AS t, lastSon AS l\n" + Query.treeQuery;
        PreparedStatement st= cnt.prepareStatement(query);
        st.setInt(1, (String.valueOf(oldFatherCode)).length());
        st.setInt(2, oldFatherCode);
        st.setInt(3, oldFatherCode);
        return st.executeQuery();
    }

    /**
     * given the last and the new ID for a specific category this method update the ID in the DB
     * @param oldID the last ID for the specific category
     * @param newID the updated ID
     */
    private static void updateID(int oldID, int newID, Connection cnt) throws SQLException {
        String query = "UPDATE categories SET ID=?, fatherID=? WHERE ID=?";
        PreparedStatement st= cnt.prepareStatement(query);
        st.setInt(1, newID);
        st.setInt(2, newID/10);
        st.setInt(3, oldID);
        st.executeUpdate();
    }

    /**
     * insert a new category in the DB
     * @param ID the category ID
     * @param name the category name
     * @param creator the ID of the user who create the new category
     */
    private static void addNewCategories(int ID, String name, String creator, Connection cnt) throws SQLException {
        String query = "INSERT INTO categories (ID,fatherID, name, lastModifier) VALUES (?,?,?,?)";
        PreparedStatement statement = cnt.prepareStatement(query);
        statement.setInt(1, ID);
        statement.setInt(2, ID/10);
        statement.setString(3, name);
        statement.setString(4, creator);
        statement.executeUpdate();
    }

    /**
     * get the ID of the specific category knowing the category name
     * @param s the category name
     */
    private static ResultSet getSearchedID(String s, Connection cnt) throws SQLException {
        String query = "SELECT ID FROM categories WHERE ID=? OR name LIKE(?);";
        PreparedStatement st = cnt.prepareStatement(query);
        st.setString(1,s);
        st.setString(2,"%"+s+"%");

        return st.executeQuery();
    }

    /**
     * used to mark as selected all the categories dominated by a copied or cut categories
     * this method is necessary for UI implementation
     * @param fatherCode the copied or cut category
     * @param searchResults the ordered list with the taxonomy passed by reference
     */
    private static void getSubTreeSearchedList(int fatherCode, ArrayList<Category> searchResults, Connection cnt) throws SQLException{
        String query = "SELECT id, name, length(CAST(ID AS nchar)) FROM categories AS t\n" + Query.treeQuery;

        PreparedStatement statement = cnt.prepareStatement(query);
        for(int i=1;i<=2;i++)statement.setInt(i, fatherCode);
        ResultSet result = statement.executeQuery();

        while (result.next()){
            Category category = new Category(result.getInt("id"),result.getString("name"));
            category.setLevel(result.getInt(3)-1);
            if(searchResults.contains(category) && category.getCode()==fatherCode){

                for(Category c : searchResults)if(c.equals(category))c.setState(CategoryState.SELECTED);
            }else if(!searchResults.contains(category)){
                if(category.getCode()==fatherCode) category.setState(CategoryState.SELECTED);
                searchResults.add(category);
            }
        }
    }

    /**
     * used to enable triggers on update and delete in the DB
     * this type of trigger are disable as default to make multiple modification (or delete) possible
     * in case of multiple modifications is necessary to activate the trigger before the last update query
     * @param cnt connection with the DB
     */
    private static void triggerEnable(Connection cnt) throws SQLException {
        String query = "SET @trigger_disable = 0";
        java.sql.Statement st = cnt.createStatement();
        st.executeUpdate(query);
    }
    private static void triggerDisable(Connection cnt) throws SQLException {
        String query = "SET @trigger_disable = 1";
        java.sql.Statement st = cnt.createStatement();
        st.executeUpdate(query);
    }
}