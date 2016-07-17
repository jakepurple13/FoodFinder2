package pace.food.foodfinder;

import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Jacob on 3/18/16.
 */
public class FoodItem implements Comparator<FoodItem> {

    String name;
    int quantity;
    Date dateAdded;


    public FoodItem(String name, int amount, Date dateAdded) {

        this.name = name;
        quantity = amount;
        this.dateAdded = dateAdded;

    }

    public FoodItem() {

        this.name = "Pizza";
        quantity = 5;
        this.dateAdded = new Date();

    }

    /**Expiration
      Returns 0 --> if time is within a week
      Returns 1 --> if time is within 2 weeks
      Returns -1 --> if time is 2 weeks and/or more
      Returns 2 --> if something goes wrong
    */
    private int expiration() {
        //TODO: change dateAdded to long stuff
        long time = System.currentTimeMillis()-dateAdded.getTime();
        long seconds = 604800;
        long ms = seconds * 1000;
        long oneWeek = 1000*60*60*24*7;

        if(time < oneWeek) {
            return 0;
        } else if(time >= oneWeek*2) {
            return -1;
        } else if(time >= oneWeek) {
            return 1;
        }
        return 2;
    }

    public int getExpiredColor() {
        int count = expiration();
        if(count==0) {
            return Color.BLACK;
        } else if(count==1) {
            return Color.rgb(229, 122, 48);
        } else if(count==-1) {
            return Color.RED;
        } else if(count==2) {
            return Color.BLUE;
        }

        return Color.BLACK;
    }

    public void removeOne() {
        quantity--;
    }

    public void addOne() {
        quantity++;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public long getDateInMs() {
        return dateAdded.getTime();
    }


    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "\t" + quantity + "\t" + new SimpleDateFormat("MM-dd-yyyy").format(dateAdded);
    }

    public String toStrings() {
        return name + "\n Date Added: " + new SimpleDateFormat("MM-dd-yyyy").format(dateAdded);
    }
    
    public static ArrayList<FoodItem> createFoodList(int numFood) {
        ArrayList<FoodItem> foodList = new ArrayList<FoodItem>();

        for (int i=0;i<numFood;i++) {
            foodList.add(new FoodItem());
        }
        return foodList;
    }

    @Override
    public int compare(FoodItem lhs, FoodItem rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
}
