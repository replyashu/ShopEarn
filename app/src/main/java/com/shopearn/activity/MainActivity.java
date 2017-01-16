package com.shopearn.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shopearn.R;
import com.shopearn.adapter.BannerAdapter;
import com.shopearn.adapter.ShopAdapter;
import com.shopearn.fragment.AboutFragment;
import com.shopearn.fragment.EarnFragment;
import com.shopearn.fragment.ProfileFragment;
import com.shopearn.fragment.ShopFragment;
import com.shopearn.global.AppController;
import com.shopearn.model.User;
import com.shopearn.service.TimerService;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ShopAdapter.ViewHolder.ClickListener, BottomNavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener{

    private ArrayList<String> shopCategory;

    private RecyclerView recyclerView;

    private ShopAdapter adapter;

    private Fragment fragment;

    private FragmentManager fm;
    private FragmentTransaction ft;

    private Fragment mFragment;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private BannerAdapter bannerAdapter;

    private String name;
    private String phone;
    private String address;
    private String account;
    private String bank;
    private String branch;
    private String ifsc;

    private String email;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private int ACCOUNT_PERMISSION_CODE = 23;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        getInvitationCode();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mFragment = getSupportFragmentManager().findFragmentById(R.id.content_main);

        callHome();

        sp = getSharedPreferences("user", 0);
        editor = sp.edit();

        email = sp.getString("emailread", "guest");
        /*private String name;
        private String email;
        private String phone;
        private String address;
        private String account;
        private String bank;
        private String branch;
        private String ifsc;
        */

        name = sp.getString("name", "none");
        phone = sp.getString("phone", "none");
        address = sp.getString("address", "none");
        account = sp.getString("account", "none");
        bank = sp.getString("bank", "none");
        branch = sp.getString("branch", "none");
        ifsc = sp.getString("ifsc", "none");


        if (!isReadAccountAllowed()) {
            getAccountPermission();
        } else {
            if(!email.equalsIgnoreCase("guest")){
                editor.putString("emailread", getUserName()).commit();
            }
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        database = FirebaseDatabase.getInstance();


        // Get a reference to the todoItems child items it the database
        myRef = database.getReference("users/");

        String id = AppController.getInstance().getAndroidId();

        User user = new User();

        user.setAndroidId(AppController.getInstance().getAndroidId());
        user.setEmail(email);
//        user.setName("none");
//        user.setPhone("none");
//        user.setAccount("none");
//        user.setBank("none");
//        user.setBranch("none");
//        user.setIfsc("none");
//        user.setReferCode("none");
//        user.setReferrefCode("none");
//        user.setSelfPoints("none");
//        user.setPointsRefer("none");

        myRef.child(id).setValue(user);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Intent serviceIntent = new Intent(this, TimerService.class);
        startService(serviceIntent);

    }

    private void getInvitationCode(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d("invite", "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        });
    }

    private boolean isReadAccountAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    private void getAccountPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, ACCOUNT_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == ACCOUNT_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String user = getUserName();

                editor.putString("emailread", user);
                }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Collections.shuffle(shopCategory);
//
//        adapter = new ShopAdapter(getApplicationContext(), shopCategory, this);
//        recyclerView.setAdapter(adapter);

//        Log.d("newCat", shopCategory.get(0));
    }

    private void initializeLayoutVars(){
        shopCategory = new ArrayList<>();
        shopCategory.add("apparels");
        shopCategory.add("books");
        shopCategory.add("footwear");
        shopCategory.add("home");
        shopCategory.add("kitchen");
        shopCategory.add("laptop");
        shopCategory.add("miscellaneous");
        shopCategory.add("smartphones");

//        recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);
        GridLayoutManager grid= new GridLayoutManager(getApplicationContext(), 2);

        recyclerView.setLayoutManager(grid);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Leaving Without Shopping?? ")
                    .setMessage("Are you sure?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);                        }
                    }).setNegativeButton("no", null).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.shop:
                callHome();
                break;

            case R.id.earn:
                Log.d("earnclick", "here");
                if (!(mFragment instanceof EarnFragment)) {
                    fragment = EarnFragment.newInstance();
                    fm = this.getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    if(fm.getBackStackEntryCount() >= 0) {
                        fm.popBackStack();
                    }
                    ft.replace(R.id.content_main, fragment).addToBackStack("earn");
                    ft.commit();
                }

                break;

            case R.id.profile:
                if (!(mFragment instanceof ProfileFragment)) {
                    fragment = new ProfileFragment();
                    fm = this.getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    if(fm.getBackStackEntryCount() >= 0) {
                        fm.popBackStack();
                    }
                    ft.replace(R.id.content_main, fragment).addToBackStack("profile");
                    ft.commit();
                }
                break;

//            case R.id.settings:
//                break;

            case R.id.about:
                if (!(mFragment instanceof AboutFragment)) {
                    fragment = new AboutFragment();
                    fm = this.getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    if(fm.getBackStackEntryCount() >= 0) {
                        fm.popBackStack();
                    }
                    ft.replace(R.id.content_main, fragment).addToBackStack("about");
                    ft.commit();
                }
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void callHome(){
        if (!(mFragment instanceof ShopFragment)) {
            fragment = new ShopFragment();
            fm = this.getSupportFragmentManager();
            ft = fm.beginTransaction();
            if(fm.getBackStackEntryCount() >= 0) {
                fm.popBackStack();
            }
            ft.replace(R.id.content_main, fragment).addToBackStack("shop");
            ft.commit();
        }
    }



    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onMenuButtonClick(View view, int position) {

    }

    public String getUserName(){
        String user = "";
        AccountManager manager = AccountManager.get(getApplicationContext());
        Account[] accounts = manager.getAccountsByType("com.google");

        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            user = possibleEmails.get(0);
        }

        return  user;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
