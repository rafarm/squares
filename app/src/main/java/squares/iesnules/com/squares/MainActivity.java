package squares.iesnules.com.squares;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

//http://stackoverflow.com/questions/23751905/error-implementing-googleapiclient-builder-for-android-development

public class MainActivity extends Activity {
    public static final String NUMBER_OF_PLAYERS = "NumberOfPlayers";

    private LinearLayout mOptionsLayout;
    private Button mOfflineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOptionsLayout = (LinearLayout)findViewById(R.id.optionsLayout);
        mOfflineButton = (Button)findViewById(R.id.offlineButton);

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .setAccountName("users.account.name@gmail.com")
                .build();
        client.connect();

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
