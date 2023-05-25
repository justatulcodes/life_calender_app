package app.expeknow.lifecalender;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.expeknow.lifecalender.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    int width, height;
    ImageView imageView;
    Button button;
    Bitmap bitmap;
    TextView textView;
    TextView infoText;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        button = findViewById(R.id.datePicker);
        textView = findViewById(R.id.textView);
        infoText = findViewById(R.id.info);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        imageView = findViewById(R.id.imageView);

        createImage();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new app.expeknow.lifecalender.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                saveWallpaper();
            } else {
                Toast.makeText(getApplicationContext(), "Please provide the storage access to save the calender in storage.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void createImage(){
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, width, height, paint);
        imageView.setImageBitmap(bitmap);
    }

    public void drawRectangles(int birth_year, int current_year, int birth_month, int current_month){

        int boxesToColor = (current_year - birth_year - 1) * 12 + (12 - birth_month) + (current_month+1);
        if(boxesToColor < 1){
            Toast.makeText(getApplicationContext(), "Please select a valid DOB!", Toast.LENGTH_LONG).show();
            return;
        }
        createImage();

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        int top_margin = width/3;
        int left_margin = width/12;
        int right_margin = width - width/10;
        int bottom_margin = height - width/6;
        int box_area = height/90;

        int count = 0;
        paint.setColor(Color.WHITE);
        infoText.setText("");
        for(int i=top_margin; i+box_area<bottom_margin; i+=box_area+10){
            for(int j=left_margin; j+box_area<right_margin; j+=box_area+10){
                canvas.drawRect(j, i, j+box_area, i+box_area, paint);
                count++;
                if(count == boxesToColor){
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                }
                if(count == 960){
                    String msg = "Each filled box is a month passed in your life.";
                    textView.setText(msg);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setWallpaper();
                    }
                    saveWallpaper();
                    return;
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        int current_month = Calendar.getInstance().get(Calendar.MONTH);
        drawRectangles(year, current_year, month, current_month);
    }

    public void saveWallpaper() {

        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
            return;
        }

        String filename = "myBitmap.png";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            // PNG is lossless, the second parameter is ignored
            outputStream.flush();
            outputStream.close();
            //You can use MediaScannerConnection.scanFile() to notify the media scanner about the new file
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.toString()}, null, null);
        } catch (Exception e) {
            // handle exception
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setWallpaper(){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
        } catch (IOException e) {
            try {
                wallpaperManager.setBitmap(bitmap);
            } catch (IOException f){
                f.printStackTrace();
            }
            e.printStackTrace();
        }

    }
}