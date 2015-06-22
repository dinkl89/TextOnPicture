package com.example.mint.textonpicture;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView ivPicture;
    private Bitmap bitmapInView, bitmapToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelect = (Button) findViewById(R.id.btnSelectPicture);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        ivPicture = (ImageView) findViewById(R.id.ivPicture);

        final EditText edText1 = (EditText) findViewById(R.id.edText1);
        final EditText edText2 = (EditText) findViewById(R.id.edText2);

        Button btnDraw = (Button) findViewById(R.id.btnCreate);
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPicture.setImageBitmap(drawTextToBitmap(bitmapInView, edText1.getText().toString(), edText2.getText().toString()));
            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDisc(bitmapToSave);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            bitmapInView = BitmapFactory.decodeFile(picturePath);

            ivPicture.setImageBitmap(bitmapInView);
        }
    }

    private Bitmap drawTextToBitmap(Bitmap bitmap, String text, String text2){
        if (bitmap == null) {
            return null;
        }

        Bitmap.Config config = bitmap.getConfig();

        bitmap = bitmap.copy(config, true);

        Canvas canvas = new Canvas(bitmap);

        Typeface face = Typeface.createFromAsset(getAssets(), "impact.ttf");

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setTextSize(bitmap.getWidth() / 3);
        paint.setShadowLayer(1f, 0, 1f, Color.BLACK);
        paint.setTypeface(face);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        float x = (bitmap.getWidth() - bounds.width())/2;
        float y = bounds.height()*13/10;

        float fontSize = paint.getTextSize();

        while (paint.measureText(text) > bitmap.getWidth()) {
            fontSize /= 2;
            paint.setTextSize(fontSize);

            bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            y = bounds.height()*13/10;
        }

        canvas.drawText(text, x, y, paint);

        //draw text2

        bounds = new Rect();
        paint.getTextBounds(text2, 0, text2.length(), bounds);

        x = (bitmap.getWidth() - bounds.width())/2;
        y = bitmap.getHeight()*9/10;

        fontSize = paint.getTextSize();

        while (paint.measureText(text2) > bitmap.getWidth()) {
            fontSize /= 2;
            paint.setTextSize(fontSize);

            bounds = new Rect();
            paint.getTextBounds(text2, 0, text2.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
        }

        canvas.drawText(text2, x, y, paint);

        bitmapToSave = bitmap;

        return bitmap;
    }

    private void saveToDisc(Bitmap bitmap){
        final String path_name = "/PictureWithLabels";          //change it to any folder name you want

        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + path_name;

        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();

        String filename = SystemClock.currentThreadTimeMillis() + ".png";

        File file = new File(dir, filename);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);

        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Файл сохранен: " + filename, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
