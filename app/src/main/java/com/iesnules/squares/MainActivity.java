package com.iesnules.squares;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;

import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;


public class MainActivity extends Activity {
    public static final String NUMBER_OF_PLAYERS = "NumberOfPlayers";

    private LinearLayout mOptionsLayout;
    private Button mOfflineButton;

    private int mPlayServicesAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOptionsLayout = (LinearLayout)findViewById(R.id.optionsLayout);
        mOfflineButton = (Button)findViewById(R.id.offlineButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        mOptionsLayout.setVisibility(View.GONE);
        mOfflineButton.setEnabled(true);

        mPlayServicesAvailability = isGooglePlayServicesAvailable(this);
        if (mPlayServicesAvailability == ConnectionResult.SUCCESS) {

        }
        else {
            Dialog errorDialog = getErrorDialog(mPlayServicesAvailability,this,1001);
            errorDialog.show();
        }
    }

    /*
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    public void showOfflineOptions(View view) {
        mOfflineButton.setEnabled(false);
        mOptionsLayout.setVisibility(View.VISIBLE);
    }

    public void launchMatchActivity(View view) {
        Intent intent = new Intent(this, MatchActivity.class);

        int viewId = view.getId();

        if (viewId == R.id.twoPlayersButton) {
            intent.putExtra(NUMBER_OF_PLAYERS, 2);
        }
        else if (viewId == R.id.threePlayersButton) {
            intent.putExtra(NUMBER_OF_PLAYERS, 3);
        }
        else if (viewId == R.id.fourPlayersButton) {
            intent.putExtra(NUMBER_OF_PLAYERS, 4);
        }

        startActivity(intent);
    }
}
