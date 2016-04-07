package pace.food.foodfinder;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomEventListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    final String FRIDGE = "Fridge";
    final String FREEZER = "Freezer";
    final String PANTRY = "Pantry";

    final String CURRENT = "Current";
    final String OUTOF = "Out Of";
    final String ALL = "All";
    final String REMIND = "Remind to Pick Up";

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    MainActivity m;

    public String FILE_NAME = "FoodFinder";

    //make 4 placeholderfragments
    PlaceholderFragment currentFrag;
    PlaceholderFragment outFrag;
    PlaceholderFragment allFrag;
    PlaceholderFragment remindFrag;

    private List<Post> posts;

    public ArrayList<FoodItem> currents;
    public ArrayList<FoodItem> out;
    public ArrayList<FoodItem> reminder;
    public ArrayList<FoodItem> everything;

    FloatingActionButton barcodeScanner;
    FloatingActionButton manualAdd;

    String category;

    PostFetcher fetcher;

    /**
     * onCreate
     * creates the view that the user sees
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        System.out.println(getIntent().getStringExtra("Name"));
        category = getIntent().getStringExtra("Name");
        FILE_NAME+=category+".txt";

        currents = new ArrayList<FoodItem>();
        out = new ArrayList<FoodItem>();
        reminder = new ArrayList<FoodItem>();
        everything = new ArrayList<FoodItem>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Food Finder - " + category);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        //if the user wants to see everything, then get all data from the other categories
        if(category.equals("Everything")) {
            readIn();
            category = FRIDGE;
            FILE_NAME = "FoodFinder"+category+".txt";
            readIn();
            category = FREEZER;
            FILE_NAME = "FoodFinder"+category+".txt";
            readIn();
            category = PANTRY;
            FILE_NAME = "FoodFinder"+category+".txt";
            readIn();
            category="Everything";
            deleteDuplicates();
        } else {
            //otherwise, read in and get all data from text file for the wanted category
            readIn();
        }

        currentFrag = new PlaceholderFragment();
        outFrag = new PlaceholderFragment();
        allFrag = new PlaceholderFragment();
        remindFrag = new PlaceholderFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(currentFrag, CURRENT).commit();
        fm.beginTransaction().add(outFrag, OUTOF).commit();
        fm.beginTransaction().add(allFrag, ALL).commit();
        fm.beginTransaction().add(remindFrag, REMIND).commit();

        mSectionsPagerAdapter = new SectionsPagerAdapter(fm);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                String currentTab = mSectionsPagerAdapter.getPageTitle(mViewPager.getCurrentItem()).toString();
                PlaceholderFragment fragment_obj = getActiveFragment();

                if (currentTab.equals(CURRENT)) {
                    //change data to what's current
                    barcodeScanner.show();
                    manualAdd.show();
                    System.out.println(CURRENT);
                    searchForZero(currents);
                    getActiveFragment().changeData(currents);

                } else if (currentTab.equals(OUTOF)) {
                    //change data to what the user is out of
                    barcodeScanner.hide();
                    manualAdd.hide();
                    System.out.println(OUTOF);
                    getActiveFragment().changeData(out);

                } else if (currentTab.equals(ALL)) {
                    //change to everything
                    barcodeScanner.hide();
                    manualAdd.hide();
                    System.out.println(ALL);
                    getActiveFragment().changeData(everything);

                } else if (currentTab.equals(REMIND)) {
                    //change to reminder
                    barcodeScanner.hide();
                    manualAdd.hide();
                    System.out.println(REMIND);
                    getActiveFragment().changeData(reminder);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //used for other subclasses
        m = this;
        //to make sure all data stays on screen until destroyed
        mViewPager.setOffscreenPageLimit(4);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //the button to activate the barcode scanner
        barcodeScanner = (FloatingActionButton) findViewById(R.id.fab);
        barcodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(m, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                //intent.putExtra(BarcodeCaptureActivity.UseFlash, true);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        //the button to let the user add their own food
        manualAdd = (FloatingActionButton) findViewById(R.id.fab1);
        manualAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.getWindow().getAttributes().windowAnimations = R.style.Animation;
                dialog.setContentView(R.layout.additemmanually);
                dialog.setTitle("Add your own item");

                // set the custom dialog components - text, image and button
                final EditText nameOfFood = (EditText) dialog.findViewById(R.id.foodName);

                final EditText quantityOfFood = (EditText) dialog.findViewById(R.id.quantityofFood);

                final CalendarView cv = (CalendarView) dialog.findViewById(R.id.datePicker);

                final TextView dateView = (TextView) dialog.findViewById(R.id.dateViewer);

                cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        Date initialDate = new Date(year - 1900, month, dayOfMonth);
                        dateView.setText("Being added " + new SimpleDateFormat("MM-dd-yyyy").format(initialDate));
                    }
                });

                Date initialDate = new Date(cv.getDate());
                dateView.setText("Being added " + new SimpleDateFormat("MM-dd-yyyy").format(initialDate));

                /*Date initialDate = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());

                dateView.setText("Being added " + new SimpleDateFormat("MM-dd-yyyy").format(initialDate));

                dp.init(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Date initialDate = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());
                        dateView.setText("Being added " + new SimpleDateFormat("MM-dd-yyyy").format(initialDate));
                    }
                });*/


                Button dialogButton = (Button) dialog.findViewById(R.id.submitItem);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PlaceholderFragment fragment_obj = getActiveFragment();

                        String name = String.valueOf(nameOfFood.getText());
                        int amount = Integer.parseInt(String.valueOf(quantityOfFood.getText()));
                        //Date d = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());
                        Date d = new Date(cv.getDate());
                        currents.add(new FoodItem(name, amount, d));
                        everything.add(new FoodItem(name, amount, d));
                        fragment_obj.changeData(currents);

                        dialog.dismiss();
                    }
                });


                dialog.show();

            }
        });




    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 13: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    try {
                        WriteToFile(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //WriteToFile();

                    try {
                     String fileName = URLEncoder.encode(FILE_NAME, "UTF-8");

                    String PATH =  getFilesDir()+"/"+fileName.trim().toString();
                    File f = new File(getFilesDir(), FILE_NAME);
                    //Uri uri = Uri.fromFile(f);
                    Uri uri = Uri.parse(FILE_NAME);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_EMAIL, "");
                    i.putExtra(Intent.EXTRA_SUBJECT,"android - email with attachment");
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    i.putExtra(Intent.EXTRA_STREAM, uri);

                    m.startActivity(Intent.createChooser(i, "Select application"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e("NOPE!", "It didnt go through");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    /**
        readFromFile
        Reads in a text file and returns the text in String format
    */
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream;

            //TODO: HERE IS PART 2 OF THE READ FROM EXTERNAL DATA
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_NAME);
            if(file.exists()) {
                Log.e("Its here", "1");
                inputStream = new FileInputStream(file);//openFileInput(file.getPath().trim());
            } else {
                inputStream = openFileInput(FILE_NAME);
                Log.e("Its here", "2");
            }
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    /**
        WriteToFile
        Writes the data from the everything arraylist to a text file using json
    */
    public void WriteToFile() throws IOException, JSONException {
        JSONObject obj = new JSONObject();
        obj.put("Location", getIntent().getStringExtra("Name"));
        obj.put("Category", "All");
        obj.put("ItemCount", everything.size());
        for(int i=0;i<everything.size();i++) {
            JSONArray food = new JSONArray();
            food.put(everything.get(i).getName());
            food.put(everything.get(i).getQuantity());
            food.put(everything.get(i).getDateInMs());
            obj.put("Food " + i, food);
        }

        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(obj.toString());
            outputStreamWriter.close();
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + obj);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     WriteToFile(
     Writes the data from the everything arraylist to a text file using json
     */
    public void WriteToFile(boolean sharingWriter) throws IOException, JSONException {
        JSONObject obj = new JSONObject();
        obj.put("Location", getIntent().getStringExtra("Name"));
        obj.put("Category", "All");
        obj.put("ItemCount", everything.size());
        for(int i=0;i<everything.size();i++) {
            JSONArray food = new JSONArray();
            food.put(everything.get(i).getName());
            food.put(everything.get(i).getQuantity());
            food.put(everything.get(i).getDateInMs());
            obj.put("Food " + i, food);
        }

        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_NAME);
        myFile.createNewFile();
        FileOutputStream fOut = new FileOutputStream(myFile);
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(fOut);//openFileOutput(f.toString() + "/" + FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(obj.toString());
            outputStreamWriter.close();
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + obj);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
        readIn
        reads manipulates the data from the readFromFile method and parses the json data,
        adding the data to where it needs to go
    */
    public void readIn() {
        String s = readFromFile();

        JSONObject jo = null;
        int numOfItems = 0;
        try {
            jo = new JSONObject(s);
            numOfItems = jo.getInt("ItemCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for(int i=0;i<numOfItems;i++) {
                JSONArray ja = jo.getJSONArray("Food " + i);
                String name = ja.getString(0);
                int amount = ja.getInt(1);
                long date = ja.getLong(2);
                FoodItem fi = new FoodItem(name,amount,new Date(date));
                if(fi.getQuantity()==0) {
                    out.add(fi);
                    reminder.add(fi);
                } else if(amount>0) {
                    currents.add(fi);
                }
                everything.add(fi);
                Log.d("JSON GET", fi.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
        onDestroy
        This is where everything is saved
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            WriteToFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * onPause
     * just close the app when the home button is pressed
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (!this.isFinishing()){
            //Insert code for HOME  key Event
            ///Toast.makeText(this,"Good bye! Have a nice day!", Toast.LENGTH_LONG).show();
            //MainActivity.this.finish();
        }
    }

    /**
     * onKeyDown
     * @param keyCode
     * @param event
     * @return true
     * when the back button is pressed, close the app
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Toast.makeText(this,"Good bye! Have a nice day!", Toast.LENGTH_LONG).show();
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_NAME).delete();
            MainActivity.this.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }



    /**
        searchForZero
        searches for any food items that the quantity is 0
    */
    public void searchForZero(ArrayList<FoodItem> fi) {
        for(int i=0;i<currents.size();i++) {
            if(currents.get(i).getQuantity()==0) {
                out.add(currents.get(i));
                reminder.add(currents.remove(i));
                i--;
            }
        }
    }


    /**
     * deleteDuplicates
     * deletes all duplicates from all categories
     */
    public void deleteDuplicates() {
        everything = new ArrayList<FoodItem>(new LinkedHashSet<FoodItem>(everything));
        out = new ArrayList<FoodItem>(new LinkedHashSet<FoodItem>(out));
        currents = new ArrayList<FoodItem>(new LinkedHashSet<FoodItem>(currents));
        reminder = new ArrayList<FoodItem>(new LinkedHashSet<FoodItem>(reminder));
    }

    /**
        getEverything
        returns the everything arraylist
    */
    public ArrayList<FoodItem> getEverything() {
        return everything;
    }

    /**
     getCurrent
     returns the currents arraylist
     */
    public ArrayList<FoodItem> getCurrent() {
        return currents;
    }

    /**
        handlePostsList
        Shows the brand of the item
    */
    private void handlePostsList(List<Post> posts) {
        this.posts = posts;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(Post post : MainActivity.this.posts) {
                    Toast.makeText(MainActivity.this, post.attributes.Brand, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
        failedLoadingPosts
        If something goes wrong with the connection
    */
    private void failedLoadingPosts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Failed to load Posts. Have a look at LogCat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
        getActiveFragment
        returns the current fragment that is on the screen
    */
    public PlaceholderFragment getActiveFragment() {
        return (PlaceholderFragment) mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    /**
        onActiviyResult
        deals with the result from the barcode scanner
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String goodText = "Barcode read: " + barcode.displayValue;


                    fetcher = new PostFetcher(barcode.displayValue, this);
                    fetcher.execute();

                    System.out.println("DONE DONE DONE!!!");

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    Log.d("INFO", goodText);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    /**
        onCreateOptionsMenu
        returns true to let the menu show up
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
        onOptionsItemSelected
        deals with the item selection from the menu
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deleted) {
            if(getActiveFragment().adapter.getDelete()) {
                getActiveFragment().adapter.setDelete(false);
                item.setTitle("Add");
            } else if(!getActiveFragment().adapter.getDelete()) {
                getActiveFragment().adapter.setDelete(true);
                item.setTitle("Delete");
            }
            return true;
        } else if(id == R.id.fridge) {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            i.putExtra("Name", "Fridge");
            startActivity(i);
            MainActivity.this.finish();
            overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
            return true;
        } else if(id == R.id.freezer) {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            i.putExtra("Name", "Freezer");
            startActivity(i);
            MainActivity.this.finish();
            overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
            return true;
        } else if(id == R.id.pantry) {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            i.putExtra("Name", "Pantry");
            startActivity(i);
            MainActivity.this.finish();
            overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
            return true;
        } else if(id == R.id.everything) {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            i.putExtra("Name", "Everything");
            startActivity(i);
            MainActivity.this.finish();
            overridePendingTransition(R.anim.to_middle, R.anim.from_middle);
            return true;
        } else if(id == R.id.share) {

                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);


                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {

                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                13);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.



                    }
                } else {
                    try {
                        WriteToFile(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_NAME);
                    Uri uri = Uri.fromFile(f);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    //i.setType("text/plain");
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, "");
                    i.putExtra(Intent.EXTRA_SUBJECT,"android - email with attachment");
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    i.putExtra(Intent.EXTRA_STREAM, uri);

                    m.startActivity(Intent.createChooser(i, "Select application"));

                    /*if(f.delete()) {
                        Log.e("RIGHT!", "CORRECT!");
                    }*/

                }



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
        onEventCompleted
        This is where the data is added from the barcode scanner
    */
    @Override
    public void onEventCompleted() {

        PlaceholderFragment fragment_obj = getActiveFragment();
        try {
            currents.add(new FoodItem(fetcher.getInfo().name, 1, new Date()));
            everything.add(new FoodItem(fetcher.getInfo().name, 1, new Date()));
            fragment_obj.changeData(currents);
            Log.d("Fetcher Info", fetcher.getInfo().name);
        } catch(NullPointerException e) {
            Toast.makeText(this, "Sorry, could not find this item on the shelves.", Toast.LENGTH_LONG).show();
        }

    }

    /**
        onEventFailed
        if PostFetcher fails
    */
    @Override
    public void onEventFailed() {

    }

    /**
        PostFetcher
        Class that gets data from Outpan
    */
    private class PostFetcher extends AsyncTask<Void, Void, String> {
        private static final String TAG = "PostFetcher";
        public String SERVER_URL = "https://api.outpan.com/v2/products/";
        //outpan api key       085e62cbbac69c8ea0d37e2633c8bed8

        public Post info;
        CustomEventListener cel;
        /**
            Constructor
            takes the UPC, Universal Product Code, Code and a custom listener
        */
        public PostFetcher(String upcCode, CustomEventListener cel) {
            SERVER_URL+=upcCode+"?apikey=085e62cbbac69c8ea0d37e2633c8bed8";
            this.cel = cel;
        }

        /**
            getInfo
            returns the info variable which contains the data from Outpan
        */
        public Post getInfo() {
            return info;
        }

        /**
            onPostExecute
            runs after doInBackground is executed
        */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(cel!=null)
                cel.onEventCompleted();
        }

        /**
            doInBackground
            gets data from Outpan and makes sure it's all correct
        */
        @Override
        protected String doInBackground(Void... params) {
            try {
                //Create an HTTP client
                HttpClient client = new DefaultHttpClient();
                HttpGet post = new HttpGet(SERVER_URL);

                //Perform the request and check the status code
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    try {
                        //Read the server response and attempt to parse it as JSON
                        Reader reader = new InputStreamReader(content);

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                        Gson gson = gsonBuilder.create();
                        List<Post> posts = new ArrayList<Post>();
                        posts.add(gson.fromJson(reader, Post.class));
                        content.close();

                        for(Post p : posts) {
                            System.out.println(p.attributes.getBrand());
                        }

                        info = posts.get(0);

                        handlePostsList(posts);
                    } catch (Exception ex) {
                        Log.e(TAG, "Failed to parse JSON due to: " + ex);
                        failedLoadingPosts();
                    }
                } else {
                    Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                    failedLoadingPosts();
                }
            } catch(Exception ex) {
                Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
                failedLoadingPosts();
            }
            return null;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        ArrayList<FoodItem> foodItems;
        FoodAdapter adapter;
        RecyclerView rvFood;
        String title = "";

        public PlaceholderFragment(String titles) {
            title = titles;
        }

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        /**
         * getFood
         * @return the foodItems arraylist
         */
        public ArrayList<FoodItem> getFood() {
            return foodItems;
        }

        /**
         * onCreateView
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return the fragment that the user sees
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.layoutFrag);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            rvFood = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            // Initialize contacts
            foodItems = new ArrayList<FoodItem>();//FoodItem.createContactsList(5);
            // Create adapter passing in the sample user data

            adapter = new FoodAdapter(foodItems);
            // Attach the adapter to the recyclerview to populate items
            rvFood.setAdapter(adapter);
            // Set layout manager to position the items
            rvFood.setLayoutManager(new LinearLayoutManager(getActivity()));

            int val = Settings.System.getInt(getContentResolver(),
                    Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
            boolean mSettingEnabled = val != 0;

            adapter.setVib(mSettingEnabled);

            changeData(m.getCurrent());

            return rootView;
        }

        /**
         * changeData
         * @param food - the new FoodItem that will be put into the list
         * deals with the data that is shown
         */
        public void changeData(FoodItem food) {
            int sub = search(food.getName());
            if(sub==-1) {
                foodItems.add(0, food);
                getEverything().add(foodItems.get(foodItems.size() - 1));
            } else if(food.getQuantity()!= 1) {
                int sum = foodItems.get(sub).getQuantity()+food.getQuantity();
                foodItems.get(sub).setQuantity(sum);
                getEverything().get(sub).setQuantity(sum);
            } else {
                foodItems.get(sub).addOne();
                getEverything().get(sub).addOne();
            }

            if(adapter.empty()) {
                outFrag.changeData(adapter.getEmptyFood());
            }

            HelpfulMethods.sort(foodItems);

            adapter.notifyItemInserted(foodItems.size() - 1);
            rvFood.setAdapter(adapter);

        }

        /**
         * changeData
         * @param food - the new FoodItem that will be put into the list
         * deals with the data that is shown
         */
        public void changeData(ArrayList<FoodItem> food) {

            foodItems.clear();

            for(int i=0;i<food.size();i++) {

                foodItems.add(0, food.get(i));

            }

            HelpfulMethods.sort(foodItems);

            adapter.notifyItemInserted(foodItems.size()-1);
            rvFood.setAdapter(adapter);

        }

        /**
         * search
         * @param key - the string wanted
         * @return the subscript of where key is
         * searches the foodItems arraylist for the key
         */
        public int search(String key) {
            int sub = -1;
            for(int i=0;i<foodItems.size();i++) {
                if(key.equals(foodItems.get(i).getName())) {
                    sub = i;
                }
            }

            return sub;
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        PlaceholderFragment currentFrag;
        PlaceholderFragment outFrag;
        PlaceholderFragment allFrag;
        PlaceholderFragment remindFrag;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);

            currentFrag = new PlaceholderFragment(CURRENT);
            outFrag = new PlaceholderFragment(OUTOF);
            allFrag = new PlaceholderFragment(ALL);
            remindFrag = new PlaceholderFragment(REMIND);

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return currentFrag;
                case 1:
                    return outFrag;
                case 2:
                    return allFrag;
                case 3:
                    return remindFrag;
            }
            return new PlaceholderFragment().newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return CURRENT;
                case 1:
                    return OUTOF;
                case 2:
                    return ALL;
                case 3:
                    return REMIND;
            }
            return null;
        }
    }
}

