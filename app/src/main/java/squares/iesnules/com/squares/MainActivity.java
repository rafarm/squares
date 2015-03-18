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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void showOfflineOption(View view) {
        LinearLayout optionsLayout = (LinearLayout)findViewById(R.id.optionsLayout);
        optionsLayout.setVisibility(View.VISIBLE);
        Button offline = (Button) findViewById(R.id.offlineButton);
        offline.setVisibility(View.INVISIBLE);
    }

    public void launchMatchActivity(View view) {
        Intent intent = new Intent(this, MatchActivity.class);

        int players = 0;

        if (view.getId() == R.id.twoplayersButton) {
            players = 2;
        }
        else if (view.getId()==R.id.threeplayersButton){
            players = 3;
        }
        else if (view.getId()==R.id.fourplayersButton{
            players = 4;
        }


        intent.putExtra(NUMBER_OF_PLAYERS, 2);
        intent.putExtra(NUMBER_OF_PLAYERS, 3);
        intent.putExtra(NUMBER_OF_PLAYERS, 4);
        startActivity(intent);
    }
}
