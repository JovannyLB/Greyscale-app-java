package com.example.greyscaleappjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button chooseImageButton, greyScaleButton;
    private ImageView imageView;
    Uri imageUri;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        chooseImageButton = (Button) findViewById(R.id.chooseImageButton);
        greyScaleButton = (Button) findViewById(R.id.greyScaleButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryButton();
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

    private void ConvertImageButton(){
        imageView.setImageBitmap(ConvertGreyScale(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}