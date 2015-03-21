package squares.iesnules.com.squares;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends ActionBarActivity {
    public static String NUMBER_OF_PLAYERS = "NumberOfPlayers";

    private LinearLayout mOptionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOptionsLayout = (LinearLayout)findViewById(R.id.optionsLayout);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showOfflineOptions(View view) {
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
