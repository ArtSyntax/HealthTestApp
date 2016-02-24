package com.artsyntax.fitnesstest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.artsyntax.fitnesstest.R;
import com.artsyntax.fitnesstest.adapter.ResultListAdapter;
import com.artsyntax.fitnesstest.dao.PhotoItemCollectionDao;
import com.artsyntax.fitnesstest.manager.ResultListManager;
import com.artsyntax.fitnesstest.manager.http.HttpManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ArtSyntax on 27/1/2559.
 */
public class RecordingFragment extends Fragment implements View.OnClickListener {

    int someVar;

    EditText etID;
    EditText etScore;
    Button btSubmit;
    Button btStation;
    ListView listView;
    ResultListAdapter listAdapter;

    public static RecordingFragment newInstance(int someVar){
        RecordingFragment fragment = new RecordingFragment();
        Bundle args = new Bundle();
        args.putInt("tag1",someVar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        someVar = getArguments().getInt("someVar"); // read args
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recording, container, false);
        initInstances(rootView);

        btSubmit.setOnClickListener(this);
        btStation.setOnClickListener(this);
        return rootView;
    }

    private void initInstances(View rootView) {
        etID = (EditText) rootView.findViewById(R.id.etID);
        etScore = (EditText) rootView.findViewById(R.id.etScore);
        btSubmit = (Button) rootView.findViewById(R.id.btSubmit);
        btStation = (Button) rootView.findViewById(R.id.btStation);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new ResultListAdapter();
        listView.setAdapter(listAdapter);


        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new Callback<PhotoItemCollectionDao>() {
            @Override
            public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
                if (response.isSuccess()) {
                    PhotoItemCollectionDao dao = response.body();
                    ResultListManager.getInstance().setDao(dao);
                    listAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(),
                            dao.getData().get(0).getCaption(),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {              // 404 not found
                    try {
                        Toast.makeText(getActivity(),
                                response.errorBody().string(),
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {     // cannot connect server

                Toast.makeText(getActivity(),
                        t.toString(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void setEditText(String text){
        etID.setText(text);
        etScore.setText(text);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.contentContainer);
        if (v == btSubmit) { // send score to server
            Log.d("submit", "submit ok");
        }
        if (v == btStation) { // send score to server
            if (fragment instanceof StationFragment == false) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.from_right, R.anim.to_left,
                                R.anim.from_left, R.anim.to_right
                        )
                        .replace(R.id.contentContainer, StationFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
            Log.d("submit", "submit ok");
        }
    }
}
