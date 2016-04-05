package pace.food.foodfinder;

/**
 * Created by Jacob on 3/28/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("Brand")
    @Expose
    public String Brand;
    @SerializedName("Volume")
    @Expose
    public String Volume;
    @SerializedName("Ingredients")
    @Expose
    public String Ingredients;

    public String getBrand() {
        return Brand;
    }

    @Override
    public String toString() {
        return this + "";
    }

}