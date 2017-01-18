package com.shopearn.fragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopearn.R;
import com.shopearn.adapter.ShopAdapter;
import com.shopearn.global.AppController;
import com.shopearn.model.Category;
import com.shopearn.model.GlobalPoints;

import java.util.List;

/**
 * Created by apple on 14/01/17.
 */

public class SettingFragment extends Fragment {

    private View view;
    private TextView txtPoints;
    private TextView txtGlobalSavedPoints;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private long t;

    private List<GlobalPoints> pointses;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        intitializeLayoutVars();
        fetchAndPopulate();
        return view;
    }

    private void intitializeLayoutVars(){
        txtPoints = (TextView) view.findViewById(R.id.txtPoints);
        txtGlobalSavedPoints = (TextView) view.findViewById(R.id.txtCashback);
    }

    private void fetchAndPopulate(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("leaderboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(AppController.getInstance().getAndroidId())) {
                    txtPoints.setText("Points:  " +  snapshot.child(
                            AppController.getInstance().getAndroidId()).getValue());
                    // run some code
                }
                else
                    txtPoints.setText("Points:  0" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database = FirebaseDatabase.getInstance();

        // Get a reference to the todoItems child items it the database
        myRef = database.getReference("global/");

        t = 0;


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                    t += (Long)childDataSnapshot.getValue();

                    Log.d("pointaaa", childDataSnapshot.getValue() + " " + t);

////                    GlobalPoints points = childDataSnapshot.getValue();
////                    pointses.add(points);
//
////                    Log.d("pointaa", childDataSnapshot.getValue(String.class));
////
////                    String points = childDataSnapshot.getValue(String.class);
//                     int total = pointses.size();
//
//                    for(int i = 0; i < total; i++)
//                        t += pointses.get(i).getPoints();
//
////                    txtGlobalSavedPoints.setText(t);

//                    adapter = new ShopAdapter(getActivity(),categories, ShopFragment.this);
//                    adapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(adapter);
                }

                startCountAnimation((int)t);
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

    private void startCountAnimation(int t) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(0, t);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                txtGlobalSavedPoints.setText("Rs. " + (int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}
