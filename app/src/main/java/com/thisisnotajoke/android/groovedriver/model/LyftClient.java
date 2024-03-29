package com.thisisnotajoke.android.groovedriver.model;

import com.thisisnotajoke.android.groovedriver.model.lyft.LocationBody;
import com.thisisnotajoke.android.groovedriver.model.lyft.RideTypesResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface LyftClient {
    String LYFT_URL = "https://api.lyft.com";

    @POST("/users")
    void getUserPayload(Callback<RideTypesResponse.UserPayloadResponse> response);

    @POST("/users")
    void getNearbyDrivers(@Body LocationBody body, Callback<RideTypesResponse> response);
}
