package com.example.greyscaleappjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int ACCESS_CAMERA = 1000, SAVE_PHOTO = 1100;

    private Button chooseImageGalleryButton, chooseImageCameraButton, greyScaleButton;
    private ImageView imageView;

    Uri galleryImageUri, cameraImageUri;
    OutputStream outputStream;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        chooseImageGalleryButton = (Button) findViewById(R.id.chooseImageGalleryButton);
        chooseImageCameraButton = (Button) findViewById(R.id.chooseImageCameraButton);
        greyScaleButton = (Button) findViewById(R.id.greyScaleButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, ACCESS_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, SAVE_PHOTO);
        }

        chooseImageGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryButton();
            }
        });

        chooseImageCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraButton();
            }
        });

        greyScaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() != null) {
                    ConvertImageButton();
                } else {
                    Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void GalleryButton(){
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(gallery, "Select a picture"), PICK_IMAGE);
    }

    private void CameraButton(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the greyscale APP");
        cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(camera, ACCESS_CAMERA);
    }

    private void ConvertImageButton(){
        imageView.setImageBitmap(ConvertGreyScale(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
        SaveImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            galleryImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryImageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == ACCESS_CAMERA && resultCode == RESULT_OK) {
            imageView.setImageURI(cameraImageUri);
        }
    }

    private Bitmap ConvertGreyScale(Bitmap originalImage) {
        Bitmap greyImage = Bitmap.createBitmap(originalImage.getWidth(), originalImage.getHeight(), originalImage.getConfig());

        int A, R, G, B;
        int colorPixel;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                colorPixel = originalImage.getPixel(x, y);
                A = Color.alpha(colorPixel);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);
                int greyScale = (R + G + B) / 3;

                greyImage.setPixel(x, y, Color.argb(A, greyScale, greyScale, greyScale));
            }
        }

        return greyImage;
    }

    private void SaveImage(){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        File filepath = Environment.getExternalStorageDirectory();
        File directory = new File(filepath.getAbsolutePath()+"/GreyScale/");
        directory.mkdir();
        File greyScaleFile = new File(directory, System.currentTimeMillis()+".jpg");

        try {
            outputStream = new FileOutputStream(greyScaleFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        Toast.makeText(MainActivity.this, "Grey scaled image saved", Toast.LENGTH_SHORT).show();

        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}