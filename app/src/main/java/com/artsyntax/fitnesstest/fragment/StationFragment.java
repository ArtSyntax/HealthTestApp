package com.artsyntax.fitnesstest.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.artsyntax.fitnesstest.R;
import com.artsyntax.fitnesstest.adapter.StationListAdapter;
import com.artsyntax.fitnesstest.dao.StationListDao;
import com.artsyntax.fitnesstest.manager.StationListManager;
import com.artsyntax.fitnesstest.utils.TestInfo;
import com.artsyntax.fitnesstest.manager.http.SQLManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StationFragment extends Fragment {
    ListView listView;
    StationListAdapter listAdapter;
    TestInfo testInfo;
    StationListDao dao;

    public StationFragment() {
        super();
    }

    public static StationFragment newInstance() {
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_station, container, false);
        initInstances(rootView);
        return rootView;
    }

    private void initInstances(View rootView) {
        // init instance with rootView.findViewById here
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new StationListAdapter();
        listView.setAdapter(listAdapter);
        Log.d("station", "testcode : " + testInfo.getTestCode());
        Log.d("station", "testname : " + testInfo.getTestName());

        Call<StationListDao> call = SQLManager.getInstance().getStations().loadStationsList(testInfo.getTestCode());
        call.enqueue(new Callback<StationListDao>() {
            @Override
            public void onResponse(Call<StationListDao> call, Response<StationListDao> response) {
                if (response.isSuccess()) {
                    dao = response.body();
                    StationListManager.getInstance().setDao(dao);
                    listAdapter.notifyDataSetChanged();
                    Log.d("station", "create");
                    if (testInfo.getTestName()==null){
                        Toast.makeText(getActivity(),
                                "กรุณาใส่รหัสแบบทดสอบใหม่",
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {              // 404 not found
                    try {
                        Toast.makeText(getActivity(),
                                "กรุณาใส่รหัสแบบทดสอบใหม่",
                                Toast.LENGTH_SHORT)
                                .show();
                        Log.d("Error! 404 Not found: ", response.errorBody().string());      // error message
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<StationListDao> call, Throwable t) {     // cannot connect server
                hiddenKeyboard(getView());
                Toast.makeText(getActivity(),
                        "เครือข่ายมีปัญหา!",
                        Toast.LENGTH_SHORT)
                        .show();
                Log.d("Error! no server: ", t.toString());         // error message
            }
        });

    }

    private void hiddenKeyboard(View v) {
        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore Instance State here
        }
    }
}
