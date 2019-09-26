package com.example.testfirebase.GetterSetterClass;

import com.example.testfirebase.GetterSetterClass.Route;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
