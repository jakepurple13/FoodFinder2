package pace.food.foodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("Name", "Fridge");
                startActivity(intent);


                SplashScreen.this.finish();

                overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
            }
        }, 2500);

    }
}
