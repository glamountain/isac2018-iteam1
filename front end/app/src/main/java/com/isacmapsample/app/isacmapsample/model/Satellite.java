package com.isacmapsample.app.isacmapsample.model;

import com.google.android.gms.maps.model.LatLng;

public class Satellite {
    LatLng mPosition;
    String mName;

    private Satellite(){}//unable default contractor to make this class immutable

    public Satellite(final LatLng aPosition, final String aName) {
        this.mPosition = aPosition;
        this.mName = aName;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public String getName() {
        return mName;
    }
}
