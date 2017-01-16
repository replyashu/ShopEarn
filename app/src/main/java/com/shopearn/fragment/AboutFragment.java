package com.shopearn.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopearn.R;

/**
 * Created by apple on 14/01/17.
 */

public class AboutFragment extends Fragment {

    private View view;

    private TextView txtVersion;

    public AboutFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        txtVersion = (TextView) view.findViewById(R.id.version);

        PackageInfo pInfo = null;
        String version;
        try {
            pInfo = getActivity().getPackageManager().
                    getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            // Intent intent = new Intent(getActivity().getApplicationContext(),
            // 		ErrorPage.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);
        }
        version = pInfo.versionName;

        txtVersion.setText("© EarnShop 2017" + "\n\nVersion: " + version);

        return view;
    }
}
