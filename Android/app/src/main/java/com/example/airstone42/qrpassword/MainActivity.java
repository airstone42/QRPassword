package com.example.airstone42.qrpassword;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.airstone42.qrpassword.classes.Crypto;
import com.example.airstone42.qrpassword.classes.DataAdapter;
import com.example.airstone42.qrpassword.classes.PasswordData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String serverURL = "http://192.168.0.2/extension/info.php";
    final DatabaseHelper dbHelper = new DatabaseHelper(this, "data.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).setCaptureActivity(ToolbarCaptureActivity.class).initiateScan();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view);
        final List<PasswordData> dataList = new ArrayList<>();
        Cursor cursor = dbHelper.getListData();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()){
                PasswordData passwordData = new PasswordData(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                dataList.add(passwordData);
                DataAdapter dataAdapter = new DataAdapter(this, R.layout.list_item, dataList);
                listView.setAdapter(dataAdapter);
            }
        } else {
        DataAdapter dataAdapter = new DataAdapter(this, R.layout.list_item, dataList);
        listView.setAdapter(dataAdapter);
        }

        showListView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            final AlertDialog.Builder addData = new AlertDialog.Builder(MainActivity.this);
            View addDataDialog = View.inflate(MainActivity.this, R.layout.dialog_data, null);
            TextView title = new TextView(MainActivity.this);
            title.setPadding(0, 50, 0, 0);
            title.setText("Add new data");
            title.setTextSize(24);
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER);
            final EditText editWebsite = (EditText) addDataDialog.findViewById(R.id.add_data_website);
            final EditText editUrl = (EditText) addDataDialog.findViewById(R.id.add_data_url);
            final EditText editUsername = (EditText) addDataDialog.findViewById(R.id.add_data_username);
            final EditText editPassword = (EditText) addDataDialog.findViewById(R.id.add_data_password);
            addData.setCustomTitle(title);
            addData.setView(addDataDialog);
            addData.setCancelable(true);
            addData.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("website", editWebsite.getText().toString());
                    values.put("url", editUrl.getText().toString());
                    values.put("username", editUsername.getText().toString());
                    values.put("password", editPassword.getText().toString());
                    db.insert("passwd", null, values);
                    values.clear();
                    showListView();
                }
            });
            addData.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            addData.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            return true;
        } else if (id == R.id.nav_gallery) {
            return true;
        } else if (id == R.id.nav_slideshow) {
            return true;
        } else if (id == R.id.nav_manage) {
            return true;
        } else if (id == R.id.nav_share) {
            return true;
        } else if (id == R.id.nav_send) {
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showListView() {
        final ListView listView = (ListView) findViewById(R.id.list_view);
        final List<PasswordData> dataList = new ArrayList<>();
        Cursor data = dbHelper.getListData();
        if (data.getCount() != 0) {
            while (data.moveToNext()){
                PasswordData passwordData = new PasswordData(Integer.parseInt(data.getString(0)), data.getString(1), data.getString(2), data.getString(3), data.getString(4));
                dataList.add(passwordData);
                DataAdapter dataAdapter = new DataAdapter(this, R.layout.list_item, dataList);
                listView.setAdapter(dataAdapter);
            }
        } else {
            DataAdapter dataAdapter = new DataAdapter(this, R.layout.list_item, dataList);
            listView.setAdapter(dataAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PasswordData passwordData = dataList.get(position);
                AlertDialog.Builder viewData = new AlertDialog.Builder(MainActivity.this);
                View addDataDialog = View.inflate(MainActivity.this, R.layout.dialog_data, null);
                TextView title = new TextView(MainActivity.this);
                title.setPadding(0, 50, 0, 0);
                title.setText("View data");
                title.setTextSize(24);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                final EditText editWebsite = (EditText) addDataDialog.findViewById(R.id.add_data_website);
                editWebsite.setText(passwordData.getWebsite());
                final EditText editUrl = (EditText) addDataDialog.findViewById(R.id.add_data_url);
                editUrl.setText(passwordData.getUrl());
                final EditText editUsername = (EditText) addDataDialog.findViewById(R.id.add_data_username);
                editUsername.setText(passwordData.getUsername());
                final EditText editPassword = (EditText) addDataDialog.findViewById(R.id.add_data_password);
                editPassword.setText(passwordData.getPassword());
                viewData.setCustomTitle(title);
                viewData.setView(addDataDialog);
                viewData.setCancelable(true);
                viewData.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("website", editWebsite.getText().toString());
                        values.put("url", editUrl.getText().toString());
                        values.put("username", editUsername.getText().toString());
                        values.put("password", editPassword.getText().toString());
                        db.update("passwd", values, "id = ?", new String[]  { Integer.toString(passwordData.getId()) });
                        values.clear();
                        showListView();
                    }
                });
                viewData.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("passwd", "id = ?", new String[] { Integer.toString(passwordData.getId()) });
                        showListView();
                    }
                });
                viewData.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                viewData.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Canceled scan");
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                String codeContent = new String(Base64.decode(result.getContents(), Base64.DEFAULT));
                try {
                    JSONObject codeJSON = new JSONObject(codeContent);
                    String sessionID = codeJSON.getString("id");
                    String secretKey = codeJSON.getString("skey");
                    String initVector = codeJSON.getString("iv");
                    String hostname = Crypto.decrypt(secretKey, initVector, codeJSON.getString("hostname"));
                    String username, password;
                    Cursor cursor = dbHelper.getListData();
                    boolean found = false;
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()){
                            if (cursor.getString(2).equals("http://" + hostname) || cursor.getString(2).equals("https://" + hostname)) {
                                found = true;
                                username = cursor.getString(3);
                                password = cursor.getString(4);
                                JSONObject infoJSON = new JSONObject();
                                infoJSON.put("id", sessionID.replaceAll("[\\r\\n]", ""));
                                infoJSON.put("hostname", Objects.requireNonNull(Crypto.encrypt(secretKey, initVector, hostname)).replaceAll("[\\r\\n]", ""));
                                infoJSON.put("username", Objects.requireNonNull(Crypto.encrypt(secretKey, initVector, username)).replaceAll("[\\r\\n]", ""));
                                infoJSON.put("password", Objects.requireNonNull(Crypto.encrypt(secretKey, initVector, password)).replaceAll("[\\r\\n]", ""));
                                sendRequest(infoJSON.toString());
                                Toast.makeText(this, "Succeeded", Toast.LENGTH_LONG).show();
                            }
                        }
                        if (!found) {
                            Toast.makeText(this, "No matching result", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Empty database", Toast.LENGTH_LONG).show();
                    }
                    // Toast.makeText(this, hostname + " " + id, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendRequest(final String string) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), string);
                    Request request = new Request.Builder()
                            .url(serverURL)
                            .post(requestBody)
                            .build();
                    client.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
