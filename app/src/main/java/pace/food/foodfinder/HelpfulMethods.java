package pace.food.foodfinder;

import java.util.ArrayList;

/**
 * Created by Jacob on 3/29/16.
 */
public class HelpfulMethods {

    /**
     * sort
     * @param list - an arraylist to sort
     * sorts an arraylist by name
     */
    public static void sort(ArrayList<FoodItem> list) {

        int sub;
        for(int i=0;i<list.size()-1;i++) {
            sub = i;
            for(int j=i;j<list.size();j++) {
                if(list.get(j).getName().compareTo(list.get(sub).getName())<0) {
                    sub = j;
                }
            }
            swap(i, sub, list);
        }
    }

    /**
     * swap
     * @param first
     * @param second
     * @param list
     * standard swap procedure
     */
    private static void swap(int first, int second, ArrayList<FoodItem> list) {
        FoodItem temp = list.get(first);
        list.set(first, list.get(second));
        list.set(second, temp);
    }


}
