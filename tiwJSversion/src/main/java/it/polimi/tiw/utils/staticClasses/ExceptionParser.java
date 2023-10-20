package it.polimi.tiw.utils.staticClasses;

import com.google.gson.JsonObject;

public class ExceptionParser {
    public static JsonObject parse(Exception ex){
        String exMSG = ex.getMessage();
        String exTitle = ex.getClass().getSimpleName();

        JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMSG",exMSG);
        jsonObject.addProperty("errorTitle", exTitle);

        return jsonObject;
    }
}
