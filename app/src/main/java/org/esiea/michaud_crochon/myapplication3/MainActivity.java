package org.esiea.michaud_crochon.myapplication3;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private TextView tv_hw;
    private DatePickerDialog dpd;
    private AlertDialog.Builder alertDialog;
    private DialogInterface.OnClickListener closeNotification;
    public RecyclerView rv_br;


    public static final String BIERS_UPDATE = "org.esiea.michaud_crochon.myapplication3.BIERS_UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tv_hw = (TextView) findViewById(R.id.tv_hello_world);
        rv_br = (RecyclerView) findViewById(R.id.rv_bieres);

        String now = DateUtils.formatDateTime(getApplicationContext(), (new Date()).getTime(), DateFormat.FULL);
        tv_hw.setText(now);

        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = DateUtils.formatDateTime(getApplicationContext(), new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis(), DateFormat.FULL);
                tv_hw.setText(date);
            }
        }, 2015, 11, 13);

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.blague);
        alertDialog.setNeutralButton(R.string.button_3, closeNotification);

        GetBiersService.startActionBiers(this);

        IntentFilter intentFilter = new IntentFilter(BIERS_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BierUpdate(), intentFilter);

        rv_br.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_br.setAdapter(new BiersAdapter(getBiersFromFIle()));

    }

    public JSONArray getBiersFromFIle() {
        try {
            InputStream in = new FileInputStream(getCacheDir() + "/" + "bieres.json");
            byte buffer[] = new byte[in.available()];
            in.read(buffer);
            in.close();
            return new JSONArray(new String(buffer, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();

            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }


    public void goPage2(View v) {
        Intent lien = new Intent(this, SecondeActivity.class);
        startActivity(lien);
    }

    public void notification(View v) {

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("Notification");
        notification.setContentText("Vous avez une notification");
        notification.setSmallIcon(R.drawable.android_bleu);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
    }

    public void alerte(View v) {
        alertDialog.show();
    }

    public void SelectDate(View v) {
        dpd.show();
    }

    public void ToastMe(View v) {
        Toast.makeText(getApplicationContext(),R.string.button_2, Toast.LENGTH_SHORT).show();

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






    class BierUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BierDownload", getIntent().getAction());
            Toast.makeText(getApplicationContext(),R.string.bier_button, Toast.LENGTH_LONG).show();
            BiersAdapter ba = (BiersAdapter) rv_br.getAdapter();
            ba.setNewBier(getBiersFromFIle());
        }
    }

    class BiersAdapter extends RecyclerView.Adapter<BiersAdapter.BierHolder> {

        private JSONArray biers;

        public BiersAdapter(JSONArray array) {
            this.biers = array;
        }

        public void setNewBier(JSONArray update) {
            this.notifyDataSetChanged();
        }

        @Override
        public BierHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bier_element, parent, false);
            return new BierHolder(view);
        }


    @Override
    public void onBindViewHolder(BierHolder holder, int position) {
        try {
            JSONObject jsonObject = biers.getJSONObject(position);
            String name = jsonObject.getString("name");
            holder.name.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return biers.length();
    }

    class BierHolder extends RecyclerView.ViewHolder{

        public TextView name;

        public BierHolder(View itemView) {

            super(itemView);
            this.name = (TextView)itemView;
        }
    }
}

}