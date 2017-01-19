package com.shopearn.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopearn.R;
import com.shopearn.adapter.BannerAdapter;
import com.shopearn.adapter.ShopAdapter;
import com.shopearn.global.AppController;
import com.shopearn.model.Banner;
import com.shopearn.model.Category;
import com.shopearn.tracker.GATracking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 14/01/17.
 */

public class ShopFragment extends Fragment implements ShopAdapter.ViewHolder.ClickListener{

    private View view;

    private List<Category> categories;

    private List<Banner> banners;

    private RecyclerView recyclerView;

    private ShopAdapter adapter;


    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private BannerAdapter bannerAdapter;

    private ViewPager pager;

    private String email;

    private String urlOfItemOnAmazonSite;

    private SharedPreferences sp;
    public ShopFragment(){

    }

    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController application = (AppController) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        Fresco.initialize(getActivity());

        sp = getActivity().getSharedPreferences("user", 0);
        email = sp.getString("email", "guest");

        deleteNode();
        new GetCategories().execute();
        new GetBanners().execute();

    }

    private void deleteNode(){
        database = FirebaseDatabase.getInstance();
        // Get a reference to the todoItems child items it the database
        myRef = database.getReference("appliances/");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d("firebasekabachha", childDataSnapshot.getRef().getKey() + "");
//                        childDataSnapshot.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                dataSnapshot.getChildren().iterator().next().getValue(Banner.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AppController.getInstance().isInternetOn()) {
            mTracker.setScreenName("Image~" + "Shop Home");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        container.removeAllViews();
        view = inflater.inflate(R.layout.fragment_shop, container, false);

        GATracking.trackScreenVisit(getActivity(), "Shop Home");
        initializeLayoutVars();



        return view;
    }

    private void initializeLayoutVars(){
        pager = (ViewPager) view.findViewById(R.id.pager);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerHome);
        GridLayoutManager grid= new GridLayoutManager(getActivity(), 2);

        recyclerView.setLayoutManager(grid);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        Collections.shuffle(shopCategory);
//
//        adapter = new ShopAdapter(getActivity(), shopCategory, this);
//        recyclerView.setAdapter(adapter);
        super.onActivityCreated(savedInstanceState);
    }

    private class GetBanners extends AsyncTask<Void, Integer, String> {

        private List<Banner> banners;
        @Override
        protected String doInBackground(Void... voids) {
            banners = fetchBannerDetails();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

    private List<Banner> fetchBannerDetails(){

        database = FirebaseDatabase.getInstance();

        banners = new ArrayList<>();
        // Get a reference to the todoItems child items it the database
        myRef = database.getReference("banners/");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Banner banner = childDataSnapshot.getValue(Banner.class);
                    banners.add(banner);
                }
                bannerAdapter = new BannerAdapter(getActivity(),banners );

                bannerAdapter.notifyDataSetChanged();
                pager.setAdapter(bannerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                dataSnapshot.getChildren().iterator().next().getValue(Banner.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return banners;
    }

    private class GetCategories extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            fetchCategories();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

    private void fetchCategories(){

        categories = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

         // Get a reference to the todoItems child items it the database
        myRef = database.getReference("categories/");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Category category = childDataSnapshot.getValue(Category.class);
                    categories.add(category);

                    adapter = new ShopAdapter(getActivity(),categories, ShopFragment.this);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                dataSnapshot.getChildren().iterator().next().getValue(Banner.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onItemClick(int position) {
        GATracking.trackClickEvents(getActivity(), categories.get(position).getName());
        GATracking.trackClickEvents(getActivity(), categories.get(position).getLink());

        handleClickWebOrApp(categories.get(position).getLink());
    }

    private void handleClickWebOrApp(String url){
        if(url.contains("flipkart")){

            String extraParams = "&affExtParam1=" + AppController.getInstance().getAndroidId()
                    + "&affExtParam2=" + email;
            PackageManager manager = getActivity().getPackageManager();
            try {
                Intent i = manager.getLaunchIntentForPackage("com.flipkart.android");
                if (i == null) {
                    throw new PackageManager.NameNotFoundException();
                }
                i.addCategory(Intent.ACTION_VIEW);

                i.setData(Uri.parse(url + extraParams));
                startActivity(i);

            } catch (PackageManager.NameNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://affiliate.flipkart.com/install-app?affid=ashuinbit"+ extraParams)));

                mTracker.setScreenName("Fallback Install");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Install")
                        .setAction(url)
                        .build());
            }
        }
        else {
            if(url.contains("amazon")) {
                forAmazon(url);
            }

            else if(url.contains("snapdeal")){
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url+ "&aff_sub=" +
                        email+ "&aff_sub2=abc" + AppController.getInstance().getAndroidId())));
            }
        }

    }

    private void forAmazon(String url){
//        urlOfItemOnAmazonSite = url + AppController.getInstance().getAndroidId() + "~~email~~" + email;
        try {

            urlOfItemOnAmazonSite = "com.amazon.mobile.shopping://amazon.in/products/";


            if(url.contains("/aw/d")) {
                int start = url.indexOf("/aw/d/");
                urlOfItemOnAmazonSite = urlOfItemOnAmazonSite + url.substring(start + 6, start + 16) +
                        AppController.getInstance().getAndroidId() + "~~email~~"+email + url.substring(40);
            }
            else if(url.contains("/gp/product")) {
                int start = url.indexOf("/gp/product/");
                urlOfItemOnAmazonSite = urlOfItemOnAmazonSite + url.substring(start + 12, start + 22) +
                        AppController.getInstance().getAndroidId() + "~~email~~" +email;
            }

            else if(url.contains("/aw/ol")) {
                int start = url.indexOf("/aw/ol/");
                urlOfItemOnAmazonSite = urlOfItemOnAmazonSite + url.substring(start + 7, start + 17) +
                        AppController.getInstance().getAndroidId() + "~~email~~" +email + url.substring(40);
            }

            else if(url.contains("/aw/gb") && !url.contains("gp/aw/gb")) {
                int start = url.indexOf("/aw/gb/");
                urlOfItemOnAmazonSite = urlOfItemOnAmazonSite + url.substring(start + 6, start + 16) +
                        AppController.getInstance().getAndroidId() + "~~email~~" +email;
            }

            else if(url.contains("in/dp")) {
                int start = url.indexOf("in/dp/");
                urlOfItemOnAmazonSite = urlOfItemOnAmazonSite + url.substring(start + 6, start + 16) +
                        AppController.getInstance().getAndroidId() + "~~email~~" +email + url.substring((start + 16));
            }

            else if(url.contains("/dp")) {
                int start = url.indexOf("/dp/");

                urlOfItemOnAmazonSite = urlOfItemOnAmazonSite + url.substring(start + 4, start + 14) +
                        AppController.getInstance().getAndroidId() + "~~email~~" + email + url.substring((start + 14));
            }
            else
                urlOfItemOnAmazonSite = url;

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlOfItemOnAmazonSite)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=in.amazon.mShop.android.shopping")));
        }
    }

    @Override
    public void onMenuButtonClick(View view, int position) {

    }
}
