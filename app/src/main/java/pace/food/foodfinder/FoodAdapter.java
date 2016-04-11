package pace.food.foodfinder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jacob on 3/24/16.
 */
public class FoodAdapter extends
        RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<FoodItem> mFoods;
    static boolean delete = false;
    boolean vib = true;
    private Vibrator myVib;;
    FoodItem getter;
    Button button;

    // Pass in the contact array into the constructor
    public FoodAdapter(List<FoodItem> food) {
        mFoods = food;
    }

    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        myVib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.food_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(FoodAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final FoodItem food = mFoods.get(position);
        getter = food;

        // Set item views based on the data model
        final TextView textView = viewHolder.nameTextView;
        textView.setText(food.toString());
        if(food.getExpiredColor()!=Color.BLACK) {
            textView.setTextColor(food.getExpiredColor());
            textView.setTypeface(null, Typeface.BOLD);
        }



        button = viewHolder.messageButton;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set it so that if contact is null, do that add stuff
                if (delete) {
                    removeAt(position);
                } else {
                    if (food.getQuantity() != 0) {
                        food.removeOne();
                    } else {
                        count = position;
                    }

                    Log.d("FOOD ITEM", food.toString());
                    textView.setText(food.toString());
                    getter = food;
                }

                button.setText("Quantity: " + food.getQuantity());

                //Haptic feedback so user knows they pressed the button
                if(vib) {
                    myVib.vibrate(25);
                }

            }
        });

        button.setText("Quantity: " + food.getQuantity());

    }

    public void setVib(boolean viber) {
        vib = viber;
    }

    public void removeAt(int position) {
        mFoods.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mFoods.size());
    }

    public void setDelete(boolean deleted) {
        delete = deleted;
    }

    public boolean getDelete() {
        return delete;
    }

    public boolean empty() {
        return false;
    }

    int count;

    public FoodItem getEmptyFood() {
        try {
            return mFoods.remove(count);
        } catch(NullPointerException e) {
            return null;
        }
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mFoods.size();
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
        }

    }

}