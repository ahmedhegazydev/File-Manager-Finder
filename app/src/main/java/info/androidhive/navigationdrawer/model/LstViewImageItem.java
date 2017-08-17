package info.androidhive.navigationdrawer.model;

/**
 * Created by ahmed on 16/08/17.
 */

public class LstViewImageItem {

    String imgUri = "";
    String imgName = "";
    String imgCreationDate = "";
    String imgSizeKb = "";


    public String getImgUri() {
        return imgUri;
    }

    public LstViewImageItem(String imgUri, String imgName, String imgCreationDate, String imgSizeKb) {
        this.imgUri = imgUri;
        this.imgName = imgName;
        this.imgCreationDate = imgCreationDate;
        this.imgSizeKb = imgSizeKb;
    }

    public String getImgName() {
        return imgName;
    }

    public String getImgCreationDate() {
        return imgCreationDate;
    }

    public String getImgSizeKb() {
        return imgSizeKb;
    }
}
