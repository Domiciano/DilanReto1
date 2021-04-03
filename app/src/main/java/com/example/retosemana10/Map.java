package com.example.retosemana10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class Map extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private  LocationManager manager;
    private Marker me;

    private String address = "";

    private newPlace newP;

    private ArrayList<Marker> points;

    private OnNewAddressListener adrdressListener;

    private MapView mapV;
    private Button myPosButton;
    private Button continuarButton;


    public Map() {
        // Required empty public constructor
    }


    public static Map newInstance() {
        Map fragment = new Map();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mapV = (MapView) root.findViewById(R.id.mapV);
        mapV.onCreate(savedInstanceState);
        mapV.getMapAsync(this);


        //Log.e(">>>>>>>", getActivity().toString());

        points = new ArrayList<>();

        myPosButton = root.findViewById(R.id.myPosButton);
        myPosButton.setOnClickListener(this);

        continuarButton = root.findViewById(R.id.contiuarButton);
        continuarButton.setOnClickListener(this);
        continuarButton.setEnabled(false);

        newP = newPlace.newInstance();

        return root;

    }





    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

         FragmentActivity fragmentAc = getActivity();
         manager = (LocationManager) fragmentAc.getSystemService(LOCATION_SERVICE);

         this.setInitialPos();

         manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,2,this);

         mMap.setOnMapClickListener(this);
         mMap.setOnMapLongClickListener(this);
         mMap.setOnMarkerClickListener(this);

    }



    @SuppressLint("MissingPermission")
    public void setInitialPos(){
       Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            this.updateMyLocation(location);
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
      this.updateMyLocation(location);
    }

    public void updateMyLocation(Location location){
        LatLng myPos = new LatLng(location.getLatitude(),location.getLongitude());
        if(me == null){
           me = mMap.addMarker(new MarkerOptions().position(myPos).title("Ubicación actual").icon(BitmapDescriptorFactory.fromResource(R.drawable.me)));
        }else{
            me.setPosition(myPos);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos,17));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}


    @Override
    public void onMapClick(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
      Marker p =  mMap.addMarker(new MarkerOptions().position(latLng).title("Nuevo lugar"));
      points.add(p);
      continuarButton.setEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(this.getActivity(),marker.getPosition().latitude +","+ marker.getPosition().longitude, Toast.LENGTH_LONG).show();
        String address = this.getAddressFromLatLng(marker.getPosition()).toString();
        marker.setSnippet(address);
        marker.showInfoWindow();
        return true;
    }


    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.myPosButton:
                   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me.getPosition(),17));
                   break;
           case R.id.contiuarButton:

               adrdressListener.onNewAddress("HOLA MUNDO");


               Log.e(">>>","BOTON CONTINUAR SIN PROBLEMA");
               FragmentManager fragmentManager = getFragmentManager();
               FragmentTransaction transaction = getFragmentManager().beginTransaction();
               transaction.replace(R.id.fragmentContainer,newP);
               transaction.commit();
           break;
       }
    }


    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( getActivity() );
        String address = "";
        try {
            address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 );
         } catch (IOException e ) {

        }
       return address;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mapV != null) {
            mapV.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mapV != null) {
            mapV.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapV != null) {
            try {
                mapV.onDestroy();
            } catch (NullPointerException e) {
                Log.e(">>>", "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapV != null) {
            mapV.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapV != null) {
            mapV.onSaveInstanceState(outState);
        }
    }

    //Metodo de suscripción
    public void setObserver(OnNewAddressListener observer){
        this.adrdressListener = observer;
        Log.e(">>>","METODO SET SIN PROBLEMA");
    }


    public interface OnNewAddressListener{
        void onNewAddress (String address);
    }


}

