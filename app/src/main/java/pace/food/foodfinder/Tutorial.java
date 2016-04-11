package pace.food.foodfinder;

import android.content.Context;
import android.content.Intent;
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

        iv.setImageResource(R.drawable.screenonetutorialdone);

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

    }

    public void changePic(int num, ImageView iv) {

        switch (num) {
            case 0:
                //Picasso.with(getBaseContext()).load(R.drawable.screenonetutorialdone).into(iv);
                iv.setImageResource(R.drawable.screenonetutorialdone);
                break;

            case 1:
                //Picasso.with(getBaseContext()).load(R.drawable.mainscreentutorialcategories).into(iv);
                iv.setImageResource(R.drawable.mainscreentutorialcategories);
                break;

            case 2:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialusefunction).into(iv);
                iv.setImageResource(R.drawable.mainscreentutorialusefunction);
                break;

            case 3:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialmanuallyadd).into(iv);
                iv.setImageResource(R.drawable.mainscreentutorialmanuallyadd);
                break;

            case 4:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.manualaddtutorialname).into(iv);
                iv.setImageResource(R.drawable.manualaddtutorialname);
                break;

            case 5:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.manualaddtutorialamount).into(iv);
                iv.setImageResource(R.drawable.manualaddtutorialamount);
                break;

            case 6:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.manualaddtutorialdate).into(iv);
                iv.setImageResource(R.drawable.manualaddtutorialdate);
                break;

            case 7:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.manualaddtutorialadd).into(iv);
                iv.setImageResource(R.drawable.manualaddtutorialadd);
                break;

            case 8:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialbarcodeadd).into(iv);
                iv.setImageResource(R.drawable.mainscreentutorialbarcodeadd);
                break;

            case 9:
                //Picasso.with(Tutorial.this.getBaseContext()).load(R.drawable.mainscreentutorialshare).into(iv);
                iv.setImageResource(R.drawable.mainscreentutorialshare);
                next.setText("Go to app");
                break;

            case 10:

                Intent intent = new Intent(Tutorial.this, FoodFinderButtons.class);
                startActivity(intent);

                Tutorial.this.finish();

                overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
                break;

        }
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
