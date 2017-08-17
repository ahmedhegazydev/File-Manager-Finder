package info.androidhive.navigationdrawer.model;

/**
 * Created by ahmed on 05/08/17.
 */

public class AnyItem {


    String strName;
    String strDate;
    String strFolderPath = "";
    int drawableId = 0;


    public AnyItem(String strName, String strDate, String strFolderPath, int drawableId) {
        this.strName = strName;
        this.strDate = strDate;
        this.strFolderPath = strFolderPath;
        this.drawableId = drawableId;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrDate() {
        return strDate;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public String getStrFolderPath() {
        return strFolderPath;
    }
}
