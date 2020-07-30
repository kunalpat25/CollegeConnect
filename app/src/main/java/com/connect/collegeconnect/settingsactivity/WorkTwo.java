package com.connect.collegeconnect.settingsactivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.connect.collegeconnect.R;
import com.connect.collegeconnect.datamodels.Resume;
import com.connect.collegeconnect.datamodels.SaveSharedPreference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;


public class WorkTwo extends Fragment {

    private ImageButton back;
    private EditText email, linkedIn, github, behance, medium;
    private ImageButton upload;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private LinearLayout blurrScreen;
    private ProgressBar progressBar;
    private String aboutMe, website, resumeLink;
    private ListenerRegistration listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        assert args != null;
        aboutMe = args.getString("aboutMe");
        website = args.getString("website");
        resumeLink = args.getString("resume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_work_two, container, false);
        back = view.findViewById(R.id.button5);
        email = view.findViewById(R.id.enterWorkEmail);
        linkedIn = view.findViewById(R.id.enterWorkLinkedIn);
        github = view.findViewById(R.id.enterWorkGitHub);
        behance = view.findViewById(R.id.enterWorkBehance);
        medium = view.findViewById(R.id.enterWorkMedium);
        upload = view.findViewById(R.id.button4);
        blurrScreen = view.findViewById(R.id.work_blurr);
        progressBar = view.findViewById(R.id.workProgressBar);
        Log.i("TAG", "onCreateView: " + aboutMe + " " + website + " " + resumeLink);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Get user id
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        documentReference = firebaseFirestore.collection("resume").document(userId);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        setValuesFirestore();

        email.setText(SaveSharedPreference.getUserName(getActivity()));

        final String email = SaveSharedPreference.getUserName(getActivity());

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                blurrScreen.setVisibility(View.VISIBLE);
                final String strlinkedIn = linkedIn.getText().toString();
                final String strGitHub = github.getText().toString();
                final String strBehance = behance.getText().toString();
                String strMedium = medium.getText().toString();
                final Resume resume = new Resume(SaveSharedPreference.getUser(getActivity()), aboutMe, website, resumeLink, email, strlinkedIn, strGitHub, strBehance, strMedium);
                Log.i("TAG2", "onCreateView: " + strBehance + " " + strGitHub + " " + strlinkedIn);
                firebaseFirestore = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = firebaseFirestore.collection("resume");
                collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(resume).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Resume", "onSuccess: Resume Uploaded");
                        Toast.makeText(getContext(), "Resume Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        blurrScreen.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        getActivity().startActivity(new Intent(getContext(), SettingsActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Resume", "onFailure: Resume failed :" + e.getMessage());
                        Toast.makeText(getContext(), "An error occurred. Please try later!", Toast.LENGTH_SHORT).show();
                        blurrScreen.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void setValuesFirestore() {

        listener = documentReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                try {
                    assert documentSnapshot != null;
                    String strLinkedIn = documentSnapshot.getString("linkedIn");
                    String strGitHub = documentSnapshot.getString("github");
                    String strBehance = documentSnapshot.getString("behance");
                    String strMedium = documentSnapshot.getString("medium");
                    linkedIn.setText(strLinkedIn);
                    github.setText(strGitHub);
                    behance.setText(strBehance);
                    medium.setText(strMedium);
                } catch (Exception e) {
                    Log.d("WorkTwo", "onEvent: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (listener != null)
            listener.remove();
        super.onDestroyView();
    }
}
