package com.hireboss.android.roachlabs;

/**
 * Created by Arjun on 13-01-2016.
 */
import java.util.ArrayList;

public interface RoutingListener {
    void onRoutingFailure();

    void onRoutingStart();

    void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex);

    void onRoutingCancelled();
}

