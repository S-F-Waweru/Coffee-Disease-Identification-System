package com.example.coffeediasfp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    GridView cardsGV;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        cardsGV = view.findViewById(R.id.dashboardGrid);

        // create a list of  Cards
        ArrayList <CardModal> cardModalArrayList =  new ArrayList<>();
        cardModalArrayList.add(new CardModal("Diseases", R.drawable.leaf, "Diseases", DiseaseList.class));
        cardModalArrayList.add(new CardModal("Identify Disease", R.drawable.search, "Predict", IdentifyDiseaseActivity.class));
        cardModalArrayList.add(new CardModal("Farm Fields", R.drawable.farm, "All Fields", FarmFieldsList.class));
        cardModalArrayList.add(new CardModal("Recommendations", R.drawable.baseline_done_all_24, " View", Recommendation.class));
        cardModalArrayList.add(new CardModal(" All Diagnosis", R.drawable.baseline_done_all_24, " View", DiagnosisList.class));
        cardModalArrayList.add(new CardModal(" Disease Mapping", R.drawable.ic_disease_map, " View", DiseaseHeatmapActivity.class));
        cardModalArrayList.add(new CardModal(" Farm Reports", R.drawable.ic_disease_map, " View", Reports.class));




        CardGVAdapter adapter = new CardGVAdapter(requireContext(), cardModalArrayList);
        cardsGV.setAdapter(adapter);

        return  view;
    }
}