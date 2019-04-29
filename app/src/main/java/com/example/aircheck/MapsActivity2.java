package com.example.aircheck;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.aircheck.MapsActivity.myTAG;

public class MapsActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final static LatLng default_location = new LatLng(13.75398, 100.50144); //Your LatLong
    ArrayList<LatLng> provinceLocation;

    int x = 9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default_location, 5));  //move camera to location
        markAllProvince();
        makeFakeData();
    }

    public void markAllProvince(){

    }


    public void makeFakeData(){
        provinceLocation = new ArrayList<>();
        String data = "Amnat Charoen,15.823,104.562,186\n" +
                "Ang Thong,14.588,100.459,378\n" +
                "Bangkok,13.753,100.500,313\n" +
                "Bueng Kan,18.031,103.711,172\n" +
                "Buri Ram,14.999,103.104,185\n" +
                "Chachoengsao,13.707,101.100,53\n" +
                "Chai Nat,15.263,100.041,446\n" +
                "Chaiyaphum,16.610,101.921,332\n" +
                "Chanthaburi,12.465,102.066,19\n" +
                "Chiang Mai,18.785,98.985,2\n" +
                "Chiang Rai,19.907,99.832,249\n" +
                "Chon Buri,12.611,100.889,172\n" +
                "Chumphon,10.840,99.497,310\n" +
                "Kalasin,16.437,103.522,210\n" +
                "Kamphaeng Phet,16.261,99.752,442\n" +
                "Kanchanaburi,13.960,99.663,278\n" +
                "Khon Kaen,16.505,102.231,254\n" +
                "Krabi,7.561,99.144,180\n" +
                "Lampang,18.254,99.479,437\n" +
                "Lamphun,18.558,99.009,374\n" +
                "Loei,17.523,101.705,205\n" +
                "Lop Buri,14.792,100.671,489\n" +
                "Mae Hong Son,18.003,97.981,235\n" +
                "Maha Sarakham,15.484,103.206,61\n" +
                "Mukdahan,16.716,104.728,490\n" +
                "Nakhon Nayok,14.208,100.949,486\n" +
                "Nakhon Pathom,13.734,100.174,285\n" +
                "Nakhon Phanom,17.019,104.660,122\n" +
                "Nakhon Ratchasima,14.985,102.687,185\n" +
                "Nakhon Sawan,15.855,100.504,247\n" +
                "Nakhon Si Thammarat,8.081,100.257,175\n" +
                "Nan,19.246,100.855,156\n" +
                "Narathiwat,6.486,101.667,375\n" +
                "Nong Bua Lam Phu,17.294,102.073,444\n" +
                "Nong Khai,17.846,102.422,333\n" +
                "Nonthaburi,13.822,100.417,142\n" +
                "Pathum Thani,13.983,100.537,208\n" +
                "Pattani,6.802,101.448,8\n" +
                "Phang-nga,8.565,97.626,297\n" +
                "Phatthalung,7.473,99.989,234\n" +
                "Phayao,19.537,100.337,222\n" +
                "Phetchabun,16.759,101.293,9\n" +
                "Phetchaburi,13.158,99.891,292\n" +
                "Phichit,15.963,100.276,442\n" +
                "Phitsanulok,16.620,100.253,413\n" +
                "Phra Nakhon Si Ayutthaya,14.340,100.570,298\n" +
                "Phrae,18.271,100.266,329\n" +
                "Phuket,7.829,98.415,306\n" +
                "Prachin Buri,13.893,101.274,228\n" +
                "Prachuap Khiri Khan,12.086,99.908,296\n" +
                "Ranong,9.782,98.447,47\n" +
                "Ratchaburi,13.841,99.831,449\n" +
                "Rayong,12.568,101.455,27\n" +
                "Roi Et,16.140,103.637,21\n" +
                "Sa kaeo,13.590,102.514,276\n" +
                "Sakon Nakhon,17.034,104.157,87\n" +
                "Samut Prakarn,13.697,100.561,285\n" +
                "Samut Sakhon,13.637,100.292,431\n" +
                "Samut Songkhram,13.493,99.945,3\n" +
                "Saraburi,14.544,100.807,156\n" +
                "Satun,6.704,99.871,347\n" +
                "Si Sa Ket,15.093,104.128,23\n" +
                "Sing Buri,14.916,100.400,391\n" +
                "Songkhla,6.794,100.700,365\n" +
                "Sukhothai,17.183,99.872,46\n" +
                "Suphan Buri,14.490,100.208,19\n" +
                "Surat Thani,9.706,99.675,140\n" +
                "Surin,14.686,103.339,44\n" +
                "Tak,16.368,98.768,51\n" +
                "Trang,7.075,99.394,337\n" +
                "Trat,12.161,102.247,393\n" +
                "Ubon Ratchathani,15.134,105.364,425\n" +
                "Udon Thani,17.469,102.771,43\n" +
                "Uthai Thani,15.427,99.874,188\n" +
                "Uttaradit,17.702,100.174,396\n" +
                "Yala,6.480,101.258,204\n" +
                "Yasothon,15.877,104.335,354";

        IconGenerator iconFactory = new IconGenerator(MapsActivity2.this);
        MarkerOptions mMarker;

        String[] provinceRecord = data.split("\n");
        for(int i=0; i< provinceRecord.length; i++){
            String[] str = provinceRecord[i].split(",");
            String province = str[0];
            double lat = Double.parseDouble(str[1]);
            double lng = Double.parseDouble(str[2]);
            String pmValue = str[3];

            mMarker = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(pmValue))).
                    position(new LatLng(lat, lng)).
                    anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()).title(province);

            mMap.addMarker(mMarker);


        }
//        Log.i(myTAG, "sData size = "+sData.length);

    }
}
