package pace.food.foodfinder;

/**
 * Created by Jacob on 3/28/16.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("gtin")
    @Expose
    public String gtin;
    @SerializedName("outpan_url")
    @Expose
    public String outpanUrl;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("attributes")
    @Expose
    public Attributes attributes;
    @SerializedName("images")
    @Expose
    public List<Object> images = new ArrayList<Object>();
    @SerializedName("videos")
    @Expose
    public List<Object> videos = new ArrayList<Object>();
    @SerializedName("categories")
    @Expose
    public List<Object> categories = new ArrayList<Object>();

    @Override
    public String toString() {
        return this + "";
    }

}




