package com.thisisnotajoke.android.groovedriver.model.lyft;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RideTypesResponse {
    public ArrayList<RideType> rideTypes;

    public class RideType {
        public String id;
        public ArrayList<Driver> drivers;
    }

    public class Driver {
        public String id;
        public Location location;
    }

    public class Location {
        public double lat;
        public double lng;
    }

    public static class UserPayloadResponse {
        @SerializedName("user")
        User user;

        class User {
            String id;
            String joinDate;
            String firstName;
            String lastName;
            String email;
            String facebookUid;
            boolean wheelchair;
            String userPhoto;
            String region;
            boolean approvedDriver;
            boolean approvedMentee;
            int ridesTaken;
            double driverRating;
            int rideCount;
            Phone phone;
            Vehicle vehicle;

            class Vehicle {
                String photo;
                String color;
                int year;
                String make;
                String model;
                String state;
                String licensePlate;
            }

            class Phone {
                String number;
            }
        }
        /**
         * {
         "verifyFields": [
         "phone",
         "ccZip"
         ],
         "creditcards": [
         {
         "lastFour": "1008",
         "type": "American Express",
         "id": "card_5xFJ71N7UrRSed",
         "default": true,
         "label": "Personal",
         "labelType": "personal",
         "source": ""
         }
         ],
         "chargeaccounts": [
         {
         "id": "chargeAccount_stripe_card_user_2066436666_f0VnVdvvakBirQQn",
         "cardId": "card_5xFJ71N7UrRSed",
         "default": true,
         "label": "Personal",
         "labelType": "personal",
         "lastFour": "1008",
         "brand": "American Express",
         "last_four": "1008",
         "source": "",
         "type": "American Express",
         "method": "card",
         "provider": "stripe",
         "clientPaymentMethod": "card",
         "customerId": "cus_3JyjprI7BLqtFB",
         "fingerprint": "f0VnVdvvakBirQQn",
         "billingType": "credit"
         }
         ],
         "shortcuts": [
         {
         "label": "home",
         "place": {
         "lat": 33.7715,
         "lng": -84.3859,
         "address": "620 Peachtree St NE",
         "formattedAddress": "620 Peachtree St NE",
         "routableAddress": "620 Peachtree St NE, Atlanta, GA 30308, USA",
         "placeName": "620 Peachtree St NE"
         }
         }
         ],
         "facebookUid": "1107360430",
         "googlePushToken": "APA91bG7jEHvqrTs259AN76HHvGFFSuoQm-0rcM9qkDUIGKYC6Kdgz7Jg2zI54El74uzgW6vQFwW93Kz9D2zh7LHYWT0GJlBfn-vPQYQelwpTj8XtWvLeSZc4m-qQTOhnjaTGPt9JS1Ri53q_1IvJ-Udq0i-pAMGEw"
         },
         "rideTypes": [
         {
         "id": "standard",
         "pricing": {
         "minimum": "$6.00",
         "pickup": "$1.35",
         "perMile": "$1.29",
         "perMinute": "$0.17"
         },
         "drivers": [
         {
         "id": "ccea3ff8a264f59b276ceb1cd17aeba2351c4874bdae579a2ba5569c3018cdab",
         "location": {
         "lat": 33.78071,
         "lng": -84.39111
         }
         },
         {
         "id": "4d303f623174dea262892631e7bcfae8aab8b02df3c4a37edc161d8bbfc219f1",
         "location": {
         "lat": 33.78329,
         "lng": -84.38522
         }
         },
         {
         "id": "dd726a4a7e969a8f60358b476635d60886aac06c75ff53b300118ce65819990b",
         "location": {
         "lat": 33.78597,
         "lng": -84.38305
         }
         },
         {
         "id": "dbd3358c7b4348266803ad52e429af0c80e0bcd47f63c26dfe747f7639f1fd16",
         "location": {
         "lat": 33.7714,
         "lng": -84.37926
         }
         },
         {
         "id": "7ecfa6350212a5ad1c683a31532c5aea71b342a1c3c483aa47b7b504caebef08",
         "location": {
         "lat": 33.76069,
         "lng": -84.38889
         }
         },
         {
         "id": "94dc3897c5d6f6e1adc6df7d03c826a699031b097d43694e0f11d36975c7ec38",
         "location": {
         "lat": 33.80114,
         "lng": -84.41577
         }
         },
         {
         "id": "74dbdebd7ca3354aaf4b029fea85e5bebc0e74d2ee2911ceaff0f8dceed62750",
         "location": {
         "lat": 33.75992,
         "lng": -84.37984
         }
         },
         {
         "id": "e3bc4718c8f3b8fc7674fdf16c6cea3416b350a43b61a69ec791ac8973ce547f",
         "location": {
         "lat": 33.79671,
         "lng": -84.37028
         }
         }
         ],
         "isDefault": true,
         "mapViewBanner": ""
         },
         {
         "id": "plus",
         "pricing": {
         "minimum": "$9.00",
         "pickup": "$2.02",
         "perMile": "$1.93",
         "perMinute": "$0.25"
         },
         "drivers": [
         {
         "id": "dd726a4a7e969a8f60358b476635d60886aac06c75ff53b300118ce65819990b",
         "location": {
         "lat": 33.78597,
         "lng": -84.38305
         }
         },
         {
         "id": "8d116aa52d08034c363d23449196a1b01a57dad873d76d026931729fcb131cb9",
         "location": {
         "lat": 33.76458,
         "lng": -84.38813
         }
         }
         ],
         "isDefault": false,
         "mapViewBanner": ""
         }
         ],
         "generatedAt": "2015-05-04T20:11:45Z",
         "timestamp": 1430770305807
         }
         */
    }
}
