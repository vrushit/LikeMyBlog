package com.example.android.likemyblog;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private Button deactivateBtn;
    private Button changePasswordBtn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private CircleImageView accountImage;
    private Uri mainImageUri = null;
    private Boolean isChanged = false;
    private StorageReference storageReference;
    private String userId;

    private TextView AccountUserName;
    private EditText AccountDesc;
    private EditText CollegeName;
    private Button saveAccountInfoBtn;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();


        deactivateBtn = view.findViewById(R.id.deactivateBtn);
        changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        accountImage = view.findViewById(R.id.user_image_account);

        AccountUserName = view.findViewById(R.id.user_name_account);
        AccountDesc = view.findViewById(R.id.user_desc_account);
        CollegeName = view.findViewById(R.id.user_college);


         final String common = "userDescription";



        saveAccountInfoBtn = view.findViewById(R.id.account_setup_btn);
        //
        //New Idea//////////////////////////////////////

            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        mainImageUri = Uri.parse(image);

                        AccountUserName.setText(name);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.mipmap.ic_launcher_round);
                        Glide.with(getContext()).setDefaultRequestOptions(placeholderRequest).load(image).into(accountImage);

                    } else {

                        Intent setupIntent = new Intent(getContext(), SetupActivity.class);
                        startActivity(setupIntent);
                        //String error = task.getException().getMessage();
                        //Toast.makeText(getContext(), "FIRESTORE Retrieve Error : " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });

        saveAccountInfoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                final String account_desc = AccountDesc.getText().toString();
                final String college_text = CollegeName.getText().toString();

                //accountFragmentProgressBar.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(account_desc) && !TextUtils.isEmpty(college_text))
                {

                    userId = firebaseAuth.getCurrentUser().getUid();

                    firebaseFirestore.collection("Users").document(userId).collection("User Description").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                Map<String, String> accountMap = new HashMap<>();

                                accountMap.put("college", college_text);
                                accountMap.put("Account Description", account_desc );

                                firebaseFirestore.collection("Users").document(userId).collection("User Description").document(userId).set(accountMap);
                                Toast.makeText(getContext(), "Updated", Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                                Toast.makeText(getContext(), "Data doesn't Exists", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            firebaseFirestore.collection("Users").document(userId).collection("User Description").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.getResult().exists())
                    {
                        String description = task.getResult().getString("Account Description");
                        String college = task.getResult().getString("college");

                        AccountDesc.setText(description);
                        CollegeName.setText(college);
                    }
                    else
                    {
                        // Toast.makeText(getContext(), "FireStore Retrieve Error", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }

            ///////////////////////
        deactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DeactivateActivity.class);
                startActivity(intent);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent changeIntent = new Intent(getContext(), ChangePasswordActivity.class);
                    startActivity(changeIntent);
            }
        });


        return view;

    }

}
