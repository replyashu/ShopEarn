package com.shopearn.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shopearn.R;
import com.shopearn.global.AppController;
import com.shopearn.model.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by apple on 14/01/17.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private View view;

    private LinearLayout linearText;
    private LinearLayout linearEdit;

    private Button btnShare;
    private Button btnSave;
    private Button btnEdit;

    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editAddress;
    private EditText editAccountNumber;
    private EditText editBankName;
    private EditText editBankBranch;
    private EditText editBankIFSC;

    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPhone;
    private TextView txtAddress;
    private TextView txtAccountNumber;
    private TextView txtBankName;
    private TextView txtBankBranch;
    private TextView txtBankIFSC;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String account;
    private String bank;
    private String branch;
    private String ifsc;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeLayoutVariables();
        return view;
    }

    private void initializeLayoutVariables(){
        btnShare = (Button) view.findViewById(R.id.btnShare);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnEdit = (Button) view.findViewById(R.id.btnEdit);

        editName = (EditText) view.findViewById(R.id.editName);
        editEmail = (EditText) view.findViewById(R.id.editEmail);
        editPhone = (EditText) view.findViewById(R.id.editPhone);
        editAddress = (EditText) view.findViewById(R.id.editAddress);
        editAccountNumber = (EditText) view.findViewById(R.id.editAccountNum);
        editBankName = (EditText) view.findViewById(R.id.editBankName);
        editBankBranch = (EditText) view.findViewById(R.id.editBankBranch);
        editBankIFSC = (EditText) view.findViewById(R.id.editBankIFSC);

        txtName = (TextView) view.findViewById(R.id.txtName);
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtPhone = (TextView) view.findViewById(R.id.txtPhone);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtAccountNumber = (TextView) view.findViewById(R.id.txtAccountNum);
        txtBankName = (TextView) view.findViewById(R.id.txtBankName);
        txtBankBranch = (TextView) view.findViewById(R.id.txtBankBranch);
        txtBankIFSC = (TextView) view.findViewById(R.id.txtBankIFSC);

        linearEdit = (LinearLayout) view.findViewById(R.id.linearEdit);
        linearText = (LinearLayout) view.findViewById(R.id.linearText);

        sp = getActivity().getSharedPreferences("user", 0);
        editor = sp.edit();

        btnSave.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
            case R.id.btnSave:
                linearText.setVisibility(View.GONE);
                checkForNullAndSave();
                break;

            case R.id.btnEdit:
                linearText.setVisibility(View.GONE);
                linearEdit.setVisibility(View.VISIBLE);
                break;

            case R.id.btnShare:
                break;
        }

    }

    private void checkForNullAndSave(){
        if(editName.getText().toString().isEmpty() || editName.getText().toString() == null ||
                editEmail.getText().toString().isEmpty() || editEmail.getText().toString() == null ||
                editPhone.getText().toString().isEmpty() || editPhone.getText().toString() == null ||
                editAddress.getText().toString().isEmpty() || editAddress.getText().toString() == null ||
                editAccountNumber.getText().toString().isEmpty() || editAccountNumber.getText().toString() == null ||
                editBankName.getText().toString().isEmpty() || editBankName.getText().toString() == null ||
                editBankBranch.getText().toString().isEmpty() || editBankBranch.getText().toString() == null ||
                editBankIFSC.getText().toString().isEmpty() || editBankIFSC.getText().toString() == null){
            Toast.makeText(getActivity(), "Fill All the Values", Toast.LENGTH_LONG).show();
        }
        else
            saveToFirebaseAndSharedPf();
    }

    private void saveToFirebaseAndSharedPf(){
        name = editName.getText().toString();
        email = editEmail.getText().toString();
        phone = editPhone.getText().toString();
        address = editAddress.getText().toString();
        account = editAccountNumber.getText().toString();
        bank = editBankBranch.getText().toString();
        branch = editBankBranch.getText().toString();
        ifsc = editBankIFSC.getText().toString();

        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.putString("account", account);
        editor.putString("bank", bank);
        editor.putString("branch", branch);
        editor.putString("ifsc", ifsc);
        editor.commit();

        writeToFirebase();
        setValuesToTextViews();

        linearEdit.setVisibility(View.GONE);
        linearText.setVisibility(View.VISIBLE);
    }
    private void setValuesToTextViews(){
        txtName.setText(name);
        txtEmail.setText(email);
        txtPhone.setText(phone);
        txtAddress.setText(address);
        txtAccountNumber.setText(account);
        txtBankName.setText(bank);
        txtBankBranch.setText(branch);
        txtBankIFSC.setText(ifsc);
    }

    private void writeToFirebase(){
        database = FirebaseDatabase.getInstance();

        // Get a reference to the todoItems child items it the database
        myRef = database.getReference("profile/");

        String id = AppController.getInstance().getAndroidId();

        Profile profile = new Profile();
        String refer = sp.getString("ref", "none");

        profile.setAndroidId(AppController.getInstance().getAndroidId());
        profile.setEmail(email);
        profile.setName(name);
        profile.setPhone(phone);
        profile.setAccount(account);
        profile.setBank(bank);
        profile.setBranch(branch);
        profile.setIfsc(ifsc);
        profile.setReferCode(refer);
        profile.setReferrefCode("none");

        myRef.child(id).setValue(profile);
    }
}
