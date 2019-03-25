package com.example.user.swap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class WriteBoard extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CODE = 0x0100;

    ImageView write_img;
    Button write_btn;
    EditText write_pName,write_pWant,write_pContent;
    Spinner category;
    String[] str_category={"생활용품","전자기기","패션","기타"};

    File seltedPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board);

        write_img=(ImageView)findViewById(R.id.write_img);
        write_btn=(Button)findViewById(R.id.write_btn);
        write_pName=(EditText)findViewById(R.id.write_pName);
        write_pWant=(EditText)findViewById(R.id.write_pWant);
        write_pContent=(EditText)findViewById(R.id.write_pContent);
        category=(Spinner)findViewById(R.id.write_spinner);

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,str_category);
        category.setAdapter(adapter);

        write_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGallery();
            }
        });
        write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_category,str_pName,str_pWant,str_pContent;
                str_category=category.getSelectedItem().toString();
                str_pName=write_pName.getText().toString();
                str_pWant=write_pWant.getText().toString();
                str_pContent=write_pContent.getText().toString();

                if(TextUtils.isEmpty(str_pName) || TextUtils.isEmpty(str_pWant) || TextUtils.isEmpty(str_pContent)){
                    Toast.makeText(WriteBoard.this, "모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    AQuery aQuery = new AQuery(WriteBoard.this);
                    String url = Config.HOME_URL + "Board";

                    Map<String, Object> params = new LinkedHashMap<>();

                    params.put("userId",LoginActivity.Current_user);
                    params.put("category", str_category);
                    params.put("pName", str_pName);
                    params.put("pWant", str_pWant);
                    params.put("pContent", str_pContent);
                    if(seltedPhoto != null){
                        params.put("image", seltedPhoto);
                    }

                    aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
                        @Override
                        public void callback(String url, String result, AjaxStatus status) {
                            try {
                                JSONObject jReponse = new JSONObject(result);

                                if (jReponse != null && !jReponse.isNull("result")) {
                                    Toast.makeText(WriteBoard.this, "등록이 완료 되었습니다", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } catch (Exception e) {
                                Toast.makeText(WriteBoard.this, "등록 하는데 오류가 발생 하였습니다"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

    protected boolean fileCopy(Uri in, File out) {
        try {
            File inFile = new File(in.getPath());
            InputStream is = new FileInputStream(inFile);
            // InputStream is =
            // context.getContentResolver().openInputStream(in);
            FileOutputStream outputStream = new FileOutputStream(out);

            BufferedInputStream bin = new BufferedInputStream(is);
            BufferedOutputStream bout = new BufferedOutputStream(outputStream);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = bin.read(buffer, 0, 1024)) != -1) {
                bout.write(buffer, 0, bytesRead);
            }

            bout.close();
            bin.close();

            outputStream.close();
            is.close();
        } catch (IOException e) {
            InputStream is;
            try {
                is = getContentResolver().openInputStream(in);

                FileOutputStream outputStream = new FileOutputStream(out);

                BufferedInputStream bin = new BufferedInputStream(is);
                BufferedOutputStream bout = new BufferedOutputStream(outputStream);

                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = bin.read(buffer, 0, 1024)) != -1) {
                    bout.write(buffer, 0, bytesRead);
                }

                bout.close();
                bin.close();

                outputStream.close();
                is.close();


            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            }

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_IMAGE_CODE) {
                Uri selectedImage = data.getData();

                File tmpCacheFile = new File(getCacheDir(), UUID.randomUUID() + ".jpg");
                if(fileCopy(selectedImage, tmpCacheFile)){
                    seltedPhoto = tmpCacheFile;

                    Bitmap bitmap = BitmapFactory.decodeFile(seltedPhoto.getAbsolutePath());
                    write_img.setImageBitmap(bitmap);
                }
            }
        }

    }

    public void startGallery() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_IMAGE_CODE);
            }
            else {
                Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
                cameraIntent.setType("image/*");
                if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CODE);
                }
            }
        }
        else {
            Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
            cameraIntent.setType("image/*");
            if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CODE);
            }
        }
    }

}
