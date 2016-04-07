package pace.food.foodfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FoodFinderButtons extends AppCompatActivity {

    Button fridge;
    Button freezer;
    Button pantry;
    Button everything;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_finder_buttons);

        fridge = (Button) findViewById(R.id.fridgebutton);
        freezer = (Button) findViewById(R.id.freezerbutton);
        pantry = (Button) findViewById(R.id.pantrybutton);
        everything = (Button) findViewById(R.id.everythingbutton);

        fridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                toNewIntent("Fridge");

            }
        });

        freezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toNewIntent("Freezer");

            }
        });

        pantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                toNewIntent("Pantry");

            }
        });

        everything.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toNewIntent("Everything");


            }
        });
    }

    public void toNewIntent(String nameToGoto) {
        Intent i = new Intent(FoodFinderButtons.this, MainActivity.class);
        i.putExtra("Name", nameToGoto);
        startActivity(i);
        overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
        finish();
    }
}