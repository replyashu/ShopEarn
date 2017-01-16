package com.shopearn.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopearn.R;
import com.shopearn.global.AppController;

/**
 * Created by apple on 14/01/17.
 */

public class SettingFragment extends Fragment {

    private View view;
    private TextView txtPoints;

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
    }
}
