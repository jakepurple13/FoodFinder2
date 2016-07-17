package pace.food.foodfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        //TODO: These 2 lines of code control whether or not the tutorial screen comes up
        //firstTime = sharedPref.getBoolean("First", false);
        firstTime = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(firstTime) {

                    intent = new Intent(SplashScreen.this, FoodFinderButtons.class);

                } else {

                    intent = new Intent(SplashScreen.this, Tutorial.class);

                }

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("First", true);
                editor.commit();

                startActivity(intent);

                SplashScreen.this.finish();

                overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
            }
        }, 2500);

    }
}
