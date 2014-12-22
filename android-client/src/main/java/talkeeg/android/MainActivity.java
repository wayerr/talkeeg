package talkeeg.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import talkeeg.android.barcode.CreateBarcodeActivity;
import talkeeg.android.barcode.ReadBarcodeActivity;

/**
 * main app activity, potentially contains list of most used activities
 */
public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * show view with options fo decoding alien barcode
     * @param view
     */
    public void showActivity(View view) {
        Class<? extends Activity> activityClass;
        switch(view.getId()) {
            case R.id.showCreateBarcodeActivity:
                activityClass = CreateBarcodeActivity.class;
            break;
            case R.id.showReadBarcodeActivity:
                activityClass = ReadBarcodeActivity.class;
            break;
            case R.id.showMessagesActivity:
                activityClass = MessagesActivity.class;
            break;
            default:
                activityClass = null;
        }
        if(activityClass == null) {
            return;
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionAcquairedUsers:
                startActivity(new Intent(this, AcquaintedUsersActivity.class));
                return true;
        }
        return super.onContextItemSelected(item);
    }
}

