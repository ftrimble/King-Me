package ftrimble.kingme.device;

import ftrimble.kingme.device.record.RideRecorder;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import java.io.File;

public class KingMe extends FragmentActivity
    implements GooglePlayServicesClient.ConnectionCallbacks,
               GooglePlayServicesClient.OnConnectionFailedListener {

    private LocationClient mLocationClient;
    private Location mCurrentLocation;

    private RideRecorder mRecorder;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private OnClickListener mStartListener = new OnClickListener() {
            public void onClick(View v) {
                if ( !mRecorder.getIsRecording() ) {
                    mRecorder.execute();
                } else {
                    // send the pause signal
                    // mRecorder.pause();
                }
            }
        };

    private OnClickListener mLapListener = new OnClickListener() {
            public void onClick(View v) {
                if ( mRecorder.getIsRecording() ) {
                    // send the lap signal
                    // mRecorder.lap();
                } else {
                    // send the reset signal
                    // mRecorder.reset();
                }
            }
        };

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult
        (int requestCode, int resultCode, Intent data) {

        // Decide what to do based on the original request code
        switch (requestCode) {
        case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            switch (resultCode) {
            case Activity.RESULT_OK :
                // Try the request again
                break;
            }
        }
     }


    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if ( connectionResult.hasResolution() ) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult
                    (this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Thrown if Google Play services canceled
                // the original PendingIntent
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Could not acquire a connection. " +
                           "Please try again later.",
                           Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mLocationClient = new LocationClient(this, this, this);

        // views to update
        TextView[] views = new TextView[]
            { (TextView) findViewById(R.id.speed_value),
              (TextView) findViewById(R.id.distance_value),
              (TextView) findViewById(R.id.time_value) };
        mRecorder = new RideRecorder(mLocationClient, views);
        mRecorder.beginRecording(getFilesDir());


        Button start_pause = (Button) findViewById(R.id.start_pause);
        Button lap_reset = (Button) findViewById(R.id.lap_reset);

        start_pause.setOnClickListener(mStartListener);
        lap_reset.setOnClickListener(mLapListener);
    }

    /**
     * Called when the activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    /**
     * Called when the activity is no longer visible.
     */
    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStart();
    }

}