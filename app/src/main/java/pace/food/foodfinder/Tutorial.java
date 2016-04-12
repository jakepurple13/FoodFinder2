package pace.food.foodfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Tutorial extends AppCompatActivity {

    Button next;
    Button previous;
    ImageView iv;
    int counter = 0;
    Vibrator myVib;
    boolean mSettingEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        next = (Button) findViewById(R.id.button);
        previous = (Button) findViewById(R.id.button2);
        iv = (ImageView) findViewById(R.id.imageView);

        iv.setImageResource(R.drawable.homescreen);

        myVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Haptic feedback so user knows they pressed the button
        int val = Settings.System.getInt(getContentResolver(),
                Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
        mSettingEnabled = val != 0;


        previous.setEnabled(false);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous.setEnabled(true);
                counter++;
                changePic(counter, iv);
                previous.setEnabled(true);
                if(mSettingEnabled) {
                    myVib.vibrate(25);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter--;
                changePic(counter, iv);
                if(counter==0) {
                    previous.setEnabled(false);
                }

                if(mSettingEnabled) {
                    myVib.vibrate(25);
                }
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous.setEnabled(true);
                counter++;
                changePic(counter, iv);
                previous.setEnabled(true);
                if(mSettingEnabled) {
                    myVib.vibrate(25);
                }
            }
        });

    }

    public void changePic(int num, ImageView iv) {

        int pictureID = 0;

        switch (num) {
            case 0:
                pictureID = R.drawable.homescreen;
                break;

            case 1:
                //iv.setImageResource(R.drawable.mainscreentutorialcategories);
                pictureID = R.drawable.mainscreentutorial2;
                break;

            case 2:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialusefunction).into(iv);
                //iv.setImageResource(R.drawable.mainscreentutorialusefunction);
                pictureID = R.drawable.mainscreentutorial3;
                break;

            case 3:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialmanuallyadd).into(iv);
                //iv.setImageResource(R.drawable.mainscreentutorialmanuallyadd);
                pictureID = R.drawable.mainscreentutorial4;
                break;

            case 4:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.manualaddtutorialname).into(iv);
                //iv.setImageResource(R.drawable.manualaddtutorialname);
                pictureID = R.drawable.mainscreentutorial5;
                break;

            case 5:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.manualaddtutorialamount).into(iv);
                //iv.setImageResource(R.drawable.manualaddtutorialamount);
                pictureID = R.drawable.mainscreentutorial7;
                break;

            case 6:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialbarcodeadd).into(iv);
                //iv.setImageResource(R.drawable.mainscreentutorialbarcodeadd);
                pictureID = R.drawable.manualaddtutorial2;
                break;

            case 7:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialshare).into(iv);
                //iv.setImageResource(R.drawable.mainscreentutorialshare);
                pictureID = R.drawable.manualaddtutorial3;
                break;

            case 8:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialshare).into(iv);
                //iv.setImageResource(R.drawable.mainscreentutorialshare);
                pictureID = R.drawable.manualaddtutorial4;
                break;

            case 9:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialshare).into(iv);
                //iv.setImageResource(R.drawable.mainscreentutorialshare);
                pictureID = R.drawable.manualaddtutorial5;
                break;

            case 10:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialshare).into(iv);
                //iv.setImageResource(R.drawable.mainscreentutorialshare);
                pictureID = R.drawable.color2;
                break;

            case 11:

                Intent intent = new Intent(Tutorial.this, FoodFinderButtons.class);
                startActivity(intent);

                Tutorial.this.finish();

                overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
                break;

        }

        iv.setImageResource(pictureID);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
