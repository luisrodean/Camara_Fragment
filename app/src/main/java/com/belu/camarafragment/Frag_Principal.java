package com.belu.camarafragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Luis on 15/11/2014.
 */
public class Frag_Principal extends Fragment {

    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private static String name = "";
    private Button btnAction;
    private RadioButton rbtnGallery;
    private RadioButton rbtnFull;
    private ImageView iv;

    public Frag_Principal(){

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = Environment.getExternalStorageDirectory() + "/test.jpg";


        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                int code = TAKE_PICTURE;

                if (rbtnFull.isChecked()) {
                    Uri output = Uri.fromFile(new File(name));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, output);

                } else if (rbtnGallery.isChecked()){
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    code = SELECT_PICTURE;
                }
                startActivityForResult(intent, code);
            }});
    }

    public static Drawable Escala(Bitmap original, int w, int h) {

        // cargamos la imagen de origen
        Bitmap BitmapOrg = original;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        // calculamos el escalado de la imagen destino
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // para poder manipular la imagen
        // debemos crear una matriz

        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,
                width, height, matrix, true);

        // si queremos poder mostrar nuestra imagen tenemos que crear un
        // objeto drawable y así asignarlo a un botón, imageview...
        return new BitmapDrawable(resizedBitmap);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
                }
            } else {
                Bitmap bmImg = BitmapFactory.decodeFile(name);
                iv.setImageDrawable(Escala(bmImg,200,200));

                new MediaScannerConnection.MediaScannerConnectionClient() {
                    private MediaScannerConnection msc = null; {
                        msc = new MediaScannerConnection(getActivity().getApplicationContext(), this);
                        msc.connect();
                    }
                    public void onMediaScannerConnected() {
                        msc.scanFile(name, null);
                    }
                    public void onScanCompleted(String path, Uri uri) {
                        msc.disconnect();
                    }
                };
            }
        } else if (requestCode == SELECT_PICTURE){
            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getActivity().getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                iv.setImageDrawable(Escala(bitmap,200,200));
            } catch (FileNotFoundException e) {}
        }}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View vista = inflater.inflate(R.layout.fragment_principal,container,false);
        btnAction = (Button) vista.findViewById(R.id.btnPic);
        iv = (ImageView)vista.findViewById(R.id.imgView);
        rbtnFull = (RadioButton)vista.findViewById(R.id.radbtnFull);
        rbtnGallery = (RadioButton)vista.findViewById(R.id.radbtnGall);
        return vista;
    }


}
