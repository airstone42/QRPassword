package com.example.airstone42.qrpassword;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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


import com.example.airstone42.qrpassword.classes.DataAdapter;
import com.example.airstone42.qrpassword.classes.PasswordData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final DatabaseHelper dbHelper = new DatabaseHelper(this, "data.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
