package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ratan on 7/29/2015.
 */
public class FragmentDelivery extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.deliveryfragment,null);
    }


}
