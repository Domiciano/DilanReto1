package com.example.retosemana10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    private newPlace newP;
    private Map map;
    private BottomNavigationView navigator;

    public static final int  PERMISSIONS_CALLBACK = 11;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        newP = newPlace.newInstance();
        map = Map.newInstance();

        navigator = findViewById(R.id.navigator);
        showFragment(newP);


        //Suscripcion del observer al observable
        map.setObserver(newP);
        Log.e(">>>","SUSCRIPCION SIN PROBLEMA");


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        },PERMISSIONS_CALLBACK);


        navigator.setOnNavigationItemSelectedListener(
                (MenuItem) ->{
                    switch (MenuItem.getItemId()){

                        case R.id.newPlace:
                            showFragment(newP);
                            break;

                        case R.id.newMap:
                            showFragment(map);
                            break;

                        case R.id.newSearch:
                            break;
                    }
                return true;
            }
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSIONS_CALLBACK){
            boolean allGrant = true ;
            for (int i = 0 ; i<grantResults.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allGrant = false ;
                    break;
                }
            }
            if(allGrant){
                Toast.makeText(this,"Todos los permisos concedidos",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Ojo, no todos los permisos fueron concedidos",Toast.LENGTH_LONG).show();
            }
        }
    }


    public void showFragment(Fragment fragment){
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.commit();
    }


    public newPlace getNewP() {
        return newP;
    }

    public Map getMap() {
        return map;
    }
}