package com.shopearn.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shopearn.R;
import com.shopearn.global.AppController;
import com.google.android.gms.appinvite.AppInviteInvitation;

import static android.app.Activity.RESULT_OK;

/**
 * Created by apple on 14/01/17.
 */

public class EarnFragment extends Fragment implements View.OnClickListener{

    private View view;

    private Button btnInvite;
    private Button btnDashboard;

    private int REQUEST_INVITE = 1;

    private String inviteString;

    private SharedPreferences sp;

    public EarnFragment(){

    }

    public static EarnFragment newInstance(){
        EarnFragment fragment = new EarnFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        container.removeAllViews();
        view = inflater.inflate(R.layout.fragment_earn, container,false);
        initializeLayoutVariables();
        return view;
    }

    private void initializeLayoutVariables(){
        btnInvite = (Button) view.findViewById(R.id.btnInvite);
        btnDashboard = (Button) view.findViewById(R.id.btnDashboard);

        sp = getActivity().getSharedPreferences("email",0);

        String email = sp.getString("email", "guest");

        inviteString = "https://play.google.com/store/apps/details?id=com.shopearn" + "&referrer="+ email +
                "&utm_source=" + "shopearn&utm_medium=shareinvite&utm_content="+
                AppController.getInstance().getAndroidId() +"&utm_campaign=socialcircle";
        btnInvite.setOnClickListener(this);
        btnDashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnInvite){

            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                    .setMessage(getString(R.string.invitation_message))
                    .setDeepLink(Uri.parse(inviteString))
//                    .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                    .setCallToActionText(getString(R.string.invitation_cta))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
        } else if(view.getId() == R.id.btnDashboard){
            Snackbar.make(view, "Its in progress, would be shown soon. All data is saved, See you soon here :)", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("invite", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("invite", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }
}
