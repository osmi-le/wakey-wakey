package com.example.androidmain;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    //????????? ?????? ??????

    ///// ????????? ?????? /////
    com.google.api.services.calendar.Calendar mService;
    ///// ???????????? & ?????? /////
    private TextView tvresult; //?????? ???????????? ??????
    private Button chg_cal; //????????? ?????? ??????
    private TextView ext_event; //?????? ????????? ?????? ??????
    private Button btn_sign; //???????????? ??????

    ///// ????????? ????????? ?????? ???????????? ?????? /////
    List<String> eventStrings = new ArrayList<>();
    List<EventData> eventData;
    List<CalendarData> calendarData;
    static int calValue = 0;

    ///// LOG ????????? ?????? ?????? /////
    private static final String TAG = "MainActivity";

    ///// ?????? ????????? ?????? ?????? /////
    GoogleAccountCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    HttpRequestInitializer initializer;

    ///// ?????? ????????? ???????????? ?????? ????????? ?????? /////
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    //// ??? ?????? ?????? ?????? - ???????????? ?????? ?????? ?????? ?????? /////
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    /**
     * main activity??? ??????.
     *
     * @param savedInstanceState ????????? ????????? instance data.
     */
    //////////////////????????? ?????? ?????? ?????? ?????? ///////////////////

    static TextView main_Alram;
    TextView main_calendar;
    Button btn_getloc;
    private TextView tv_result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        main_Alram = (TextView) findViewById(R.id.main_Alram);
        main_calendar = (TextView) findViewById(R.id.main_calendar);

        ////////////////////// ????????? ?????? ?????? ///////////////////////////////////////

        // ????????? tvresult ???????????? ?????? ??????????????? ???????????????.
        tvresult = findViewById(R.id.main_calendar);
        chg_cal = findViewById(R.id.chg_cal);

        // Initialize credentials and service object.
        // ?????? ?????? ????????? ???????????? ??? ????????? > ????????? ????????? ????????? ?????? ?????? ?????? ??????
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = UserData.getCredential();

        // ????????? ????????? ???????????????
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        ////////////////////////////////////////////////////////////////////////////

        main_Alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_Intent = new Intent(MainActivity.this, Alram_add.class);

                add_Intent.putExtras(add_Intent);
                startActivity(add_Intent);
                finish();
            }
        });

        main_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent calendar_Intent = new Intent(MainActivity.this, calendar_check.class);
                if(eventStrings.isEmpty()) {}
                else{
                    calendar_Intent.putExtra("eventlist", (Serializable) eventData);
                    Log.d(MainActivity.class.getName(),String.valueOf(eventData));
                    calendar_Intent.putExtras(calendar_Intent);
                    startActivity(calendar_Intent);
                    finish();
                }

            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent state_Intent = new Intent(MainActivity.this, Main_Alarm.class);

                state_Intent.putExtras(state_Intent);
                startActivity(state_Intent);
                finish();
            }
        });
        btn_getloc = (Button) findViewById(R.id.btn_getloc);
        btn_getloc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tv_result = findViewById(R.id.txv_result);
                ImageView iv_icon = findViewById(R.id.iv_icon);
                Bitmap icon = null;
                int lon, lat;

                final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    android.location.Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String provider = location.getProvider();
                    lon = (int) location.getLongitude();//lon = ??????
                    lat = (int) location.getLatitude();//lat = ??????

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

                    OpenWeatherApiTask t = new OpenWeatherApiTask();
                    GetWeatherIconTask wt = new GetWeatherIconTask(iv_icon);

                    try {
                        Weather w = t.execute(lat, lon).get();

                        int kel_to_cel = w.getTemperature() - 273;//?????? ??????->?????? ??????. ???????????? ?????? ??????????????????.
                        String temperature = String.valueOf(kel_to_cel);
                        String mcity = w.getCity();
                        String micon = w.getIcon();

                        wt.execute(micon);

                        tv_result.setText(" ?????? : " + lat + " ?????? : " + lon + "\n" + mcity + " ?????? : " + temperature + "??? " + "????????? ??????" + micon);
                        //"?????? : "+lon + " ?????? : " + lat + "\n" +
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            int lon = (int) location.getLongitude();
            int lat = (int) location.getLatitude();

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public void getWeather(View view) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent state_Intent = new Intent(MainActivity.this, Setting.class);

            state_Intent.putExtras(state_Intent);
            startActivity(state_Intent);
        } else if (id == R.id.action_state) {
            Intent state_Intent = new Intent(MainActivity.this, Analyze.class);

            state_Intent.putExtras(state_Intent);
            startActivity(state_Intent);
            finish();
        } else if (id == R.id.nfctag_add) {
            Intent state_Intent = new Intent(MainActivity.this, Nfctag_add.class);

            state_Intent.putExtras(state_Intent);
            startActivity(state_Intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
////////////////////////////////????????? ?????? ?????? ///////////////////////////////////////////
    /*****************************************************************************
     *  ??? ???????????? ?????? ????????? ?????? ??????????????????
     *  refreshResults()??? ?????? ????????? ???????????? ????????? ???????????????
     *  ???????????? ????????? ??? ????????? ??????????????? ??? ??? ????????????
     *****************************************************************************/

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults(); //?????? ???????????? ????????? ??????
            reflashAlarmList(); //?????? ???????????? ????????? ??????
        } else {
            tvresult.setText("Google Play Service??? ???????????????. ?????? ?????? ?????? ?????????????????????.");
        }
    }

    public void reflashAlarmList(){
        AlarmListAsyncTask alarmListAsyncTask = new AlarmListAsyncTask();
        alarmListAsyncTask.execute(1);
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        refreshResults();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    tvresult.setText("????????? ???????????????.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (isDeviceOnline()) {
            new ApiAsyncTask(this).execute();
        } else {
            tvresult.setText("??????????????? ????????? ??? ????????????.");
        }
    }

    /**
     * Clear any existing Google Calendar API data from the TextView and update
     * the header message; called from background threads and async tasks
     * that need to update the UI (in the UI thread).
     */
    public void clearResultsText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvresult.setText("????????? ??????????????????");
            }
        });
    }

    /**
     * Fill the data TextView with the given List of Strings; called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     *
     * @param event a List of EventData to populate the main TextView with.
     */
    public void updateResultsText(final List<EventData> event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == null) {
                    tvresult.setText("?????? ?????? ??????! ??????????????? ???????????????.");
                } else if (event.size() == 0) {
                    tvresult.setText("?????? ????????? ????????? ????????????.");
                } else {
                    eventData = event;
                    eventStrings.clear();

                    tvresult.setText("?????? ?????? ???");

                    if (event.size() > 5) {
                        int i = 0;

                        for (i = 0; i < 5; i++) {
                            String s;
                            EventData e = event.get(i);
                            if (e.getStarttime().equals(" ")) {
                                s = String.format(Locale.KOREA, "%s, %s???",
                                        e.getSummary(), e.getStartdate());
                            } else {
                                s = String.format(Locale.KOREA, "%s, %s??? %s ~ %s",
                                        e.getSummary(), e.getStartdate(), e.getStarttime(), e.getEndtime());
                            }
                            eventStrings.add(s);
                            Log.d(TAG, e.getSummary() + "  " + e.getStarttime() + " ~ " + e.getEndtime());
                        }
                        eventStrings.add("...");
                    } else {
                        for (EventData e : event) {
                            String s;
                            if (e.getStarttime().equals(" ")) {
                                s = String.format(Locale.KOREA, "%s, %s???",
                                        e.getSummary(), e.getStartdate());
                            } else {
                                s = String.format(Locale.KOREA, "%s, %s??? %s ~ %s",
                                        e.getSummary(), e.getStartdate(), e.getStarttime(), e.getEndtime());
                            }
                            eventStrings.add(s);
                            Log.d(TAG, e.getSummary() + "  " + e.getStarttime() + " ~ " + e.getEndtime());
                        }
                    }
                    tvresult.setText(TextUtils.join("\n\n", eventStrings));
                }
            }
        });
    }

    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     *
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvresult.setText(message);
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                googleAPI.isGooglePlayServicesAvailable(this);
        if (connectionStatusCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(connectionStatusCode)) {
                googleAPI.getErrorDialog(this, connectionStatusCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }

        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(() -> {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            apiAvailability.getErrorDialog(this,
                    connectionStatusCode,
                    REQUEST_GOOGLE_PLAY_SERVICES).show();

        });
    }

    /**
     * Asynctask?????? ????????? ???????????? ????????? ????????? ?????????
     * MainActivity??? ?????? ????????? ????????? ???????????????.
     *
     * @param list ????????? ?????? ???????????? ?????????
     */
    void getCalendarData(List<CalendarData> list) {
        calendarData = list;
    }

    /**
     * chg_cal ????????? ????????? ??? ???????????? ?????????
     * ???????????? ????????? ????????? ?????????
     *
     * @param view ?????? view??? ?????????.
     */
    public void changeCalendar(View view) throws IOException {
        PopupMenu popup = new PopupMenu(this, view);

        //xml????????? ?????? ??????????????? ????????????????????? ????????? ??????
        MenuInflater inflater = popup.getMenuInflater();

        Menu menu = popup.getMenu();

        int i = 0;
        for (CalendarData c : calendarData) {
            String e = c.getSummary();
            MenuItem newItem = menu.add(i, i, i, e);
            i++;
        }
        //?????? ?????? ??????????????? ???????????? ?????? menu ????????? ?????????
        inflater.inflate(R.menu.popupmenu, menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override

            public boolean onMenuItemClick(MenuItem item) {
                //??? ????????? ???????????? ???????????? ????????? ?????????
                for (int i = 0; i < calendarData.size(); i++) {
                    if (item.getItemId() == i) {
                        //calValue = item.getTitle().toString();
                        calValue = item.getItemId();
                    }
                }
                refreshResults();
                return false;
            }


        });
        //????????? ??????????????? ???????????? ??????

        popup.show();

    }


    /**
     * ext_event ????????? ????????? ??? ???????????? ?????????
     * ???????????? ?????? ?????? ?????? ????????? ???????????????.
     * Intent ?????? ??? ????????? ???????????????.
     *
     * @param view ?????? view??? ?????????.
     */
    public void seeMoreEvent(View view) {
        Intent intent = new Intent(MainActivity.this, calendar_check.class);

        //????????? ??? eventData??? ????????? ???????????? ?????? ?????? ????????? ????????? ???????????????.
        if ((eventStrings.isEmpty())) {
            Toast.makeText(this, "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("eventlist", (Serializable) eventData);
            startActivity(intent);//???????????? ?????????

        }
    }

    public void signoutAcc(View view) {
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this);

        oDialog.setMessage("???????????? ???????????????????")
                .setTitle(" ")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Dialog", "??????");
                    }
                })
                .setNeutralButton("???", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UserData.setCredential(null);
                        credential = null;
                        //????????????

                        Intent it = new Intent(getApplicationContext(), SubLogin.class);
                        startActivity(it);
                        finish();
                    }
                })
                .setCancelable(false) // ??????????????? ???????????? ????????? ????????? ??????.
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        calValue = 0;
        // ??? ????????? calValue ?????? ?????? 0?????? ??????????????????
        //????????? ????????? ?????????...
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        calValue = 0;
        // ??? ????????? calValue ?????? ?????? 0?????? ??????????????????
        //????????? ????????? ?????????...
    }

}
