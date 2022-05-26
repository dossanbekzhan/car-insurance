package kz.saqtandyru.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import kz.saqtandyru.R;


public class NewInsuranceFragment extends Fragment {


    public static NewInsuranceFragment newInstance(String param1, String param2) {
        NewInsuranceFragment fragment = new NewInsuranceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_insurance, container, false);

        return view;
    }
}