package com.example.retosemana10;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class newPlace extends Fragment implements View.OnClickListener , Map.OnNewAddressListener {


    private EditText txtnewName;
    private Button btnOpenMap;
    public TextView lblDireccion;
    private Button btnImage;
    private ImageView imageLugar ;
    private Button btnadd;
    private Button btnCamera;

    private View rootMapa;
    private String path = null;

    private  Map map;

    private String direccionDellegada;

    public static final int CAMERA_CALLBACK = 12;
    public static final int GALLERY_CALLBACK = 13;

    private File file;


    public newPlace() {
        // Required empty public constructor
        direccionDellegada = "";
    }


 static newPlace newInstance() {
        newPlace fragment = new newPlace();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_new_place, container, false);

        txtnewName = root.findViewById(R.id.newName);
        btnOpenMap = root.findViewById(R.id.btnOpenMap);
        lblDireccion = root.findViewById(R.id.lblDireccion);

        btnImage = root.findViewById(R.id.btnImage2);
        btnCamera = root.findViewById(R.id.btnCamera);
        imageLugar = root.findViewById(R.id.imageLugar);

        btnadd = root.findViewById(R.id.btnAdd);



        btnOpenMap.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnadd.setOnClickListener(this);
        txtnewName.setOnClickListener(this);


        btnOpenMap.setEnabled(false);

        txtnewName.setText(direccionDellegada);

         map = Map.newInstance();


        if(path != null){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageLugar.setImageBitmap(bitmap);
        }



        return root;

    }



    @Override
    public void onClick(View v)  {
        switch (v.getId()){

            case R.id.newName:
                if(!txtnewName.getText().equals("")){
                    btnOpenMap.setEnabled(true);
                }else{
                    btnOpenMap.setEnabled(false);
                }

                break;

            case R.id.btnOpenMap:

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,map);
                transaction.commit();

                break;

            case R.id.btnImage2:

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,GALLERY_CALLBACK);

                break;

            case R.id.btnCamera:

                FragmentActivity appcom = getActivity();
                Intent j = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File( appcom.getExternalFilesDir(null)+"/photo.png");
                Uri uri = FileProvider.getUriForFile(appcom, "com.example.retosemana10" , file);
                j.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(j,CAMERA_CALLBACK);

                break;

            case R.id.btnAdd:

                String place = txtnewName.getText().toString();

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK && requestCode == GALLERY_CALLBACK){

            Uri uri = data.getData();
            path = UtilDomi.getPath(getActivity(), uri);
            Bitmap image1 = BitmapFactory.decodeFile(path);
            Bitmap thumbnail1 = Bitmap.createScaledBitmap(image1,image1.getWidth()/4, image1.getHeight()/4,true);
            imageLugar.setImageBitmap(thumbnail1);

        } else if (resultCode == RESULT_OK && requestCode == CAMERA_CALLBACK){

            Bitmap imagen2 = BitmapFactory.decodeFile(file.getPath());
            Bitmap thumbnail2 = Bitmap.createScaledBitmap(imagen2,imagen2.getWidth()/4, imagen2.getHeight()/4,true);
            imageLugar.setImageBitmap(thumbnail2);
        }
    }


    @Override
    public void onNewAddress(String address) {
        direccionDellegada = address;
    }

}