package com.thisisnotajoke.android.groovedriver;

import java.io.IOException;

import retrofit.client.Client;

import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class ConnectivityAwareUrlClient implements Client {

    public ConnectivityAwareUrlClient(NetworkConnectivityManager ncm) {
        this(new OkClient(), ncm);
    }

    public ConnectivityAwareUrlClient(Client wrappedClient, NetworkConnectivityManager ncm) {
        this.wrappedClient = wrappedClient;
        this.ncm = ncm;
    }

    Client wrappedClient;
    private NetworkConnectivityManager ncm;

    @Override
    public Response execute(Request request) throws IOException {
        if (!ncm.isConnected()) {
            throw new NoConnectivityException();
        }
        return wrappedClient.execute(request);
    }
}