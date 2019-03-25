package com.example.user.swap;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class FindActivity extends AppCompatActivity {
    EditText id_editName, id_editPhone;
    EditText pw_editId, pw_editPhone;

    String id_strName, id_strPhone;
    String pw_strId, pw_strPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        id_editName = (EditText)findViewById(R.id.id_editName);
        id_editPhone = (EditText)findViewById(R.id.id_editPhone);
        pw_editId = (EditText)findViewById(R.id.pw_editId);
        pw_editPhone = (EditText)findViewById(R.id.pw_editPhone);

        findViewById(R.id.id_btnFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_strName = id_editName.getText().toString();
                id_strPhone = id_editPhone.getText().toString();

                AQuery aQuery = new AQuery(FindActivity.this);
                String url = Config.HOME_URL + "IdFind";

                Map<String, Object> params = new LinkedHashMap<>();

                params.put("name", id_strName);
                params.put("phone", id_strPhone);

                aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                        try {
                            Gson gson = new Gson();
                            if (jReponse != null && !jReponse.isNull("result")) {
                                JSONArray jarFindId = jReponse.optJSONArray("result");
                                UserEntity userEntity = gson.fromJson(jarFindId.optJSONObject(0).toString(), UserEntity.class);

                                StringBuffer bf = new StringBuffer(userEntity.getId());

                                AlertDialog.Builder alert_FindId = new AlertDialog.Builder(FindActivity.this);
                                alert_FindId.setTitle("ID 찾기 결과");
                                alert_FindId.setMessage(bf.replace(1, bf.length() - 2, "***").toString() + " 입니다.");
                                alert_FindId.setIcon(R.drawable.logo);
                                alert_FindId.setPositiveButton("확인", null);
                                alert_FindId.show();
                            } else {
                                AlertDialog.Builder alert_FindId = new AlertDialog.Builder(FindActivity.this);
                                alert_FindId.setTitle("ID 찾기 경고");
                                alert_FindId.setMessage("이름과 핸드폰 번호가 일치하는지 확인 바랍니다.");
                                alert_FindId.setIcon(R.drawable.logo);
                                alert_FindId.setPositiveButton("확인", null);
                                alert_FindId.show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(FindActivity.this, e + "!!@", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        findViewById(R.id.pw_btnFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw_strId = pw_editId.getText().toString();
                pw_strPhone = pw_editPhone.getText().toString();

                AQuery aQuery = new AQuery(FindActivity.this);
                String url = Config.HOME_URL + "PwFind";

                Map<String, Object> params = new LinkedHashMap<>();

                params.put("id", pw_strId);
                params.put("phone", pw_strPhone);

                aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                        try {
                            Gson gson = new Gson();
                            if (jReponse != null && !jReponse.isNull("result")) {
                                JSONArray jarFindId = jReponse.optJSONArray("result");
                                UserEntity userEntity = gson.fromJson(jarFindId.optJSONObject(0).toString(), UserEntity.class);

                                StringBuffer bf = new StringBuffer(userEntity.getPw());

                                AlertDialog.Builder alert_FindId = new AlertDialog.Builder(FindActivity.this);
                                alert_FindId.setTitle("PASSWORD 찾기 결과");
                                alert_FindId.setMessage(bf.replace(1, bf.length() - 2, "*******").toString() + " 입니다.");
                                alert_FindId.setIcon(R.drawable.logo);
                                alert_FindId.setPositiveButton("확인", null);
                                alert_FindId.show();
                            } else {
                                AlertDialog.Builder alert_FindId = new AlertDialog.Builder(FindActivity.this);
                                alert_FindId.setTitle("PASSWORD 찾기 경고");
                                alert_FindId.setMessage("id와 핸드폰 번호가 일치하는지 확인 바랍니다.");
                                alert_FindId.setIcon(R.drawable.logo);
                                alert_FindId.setPositiveButton("확인", null);
                                alert_FindId.show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(FindActivity.this, e + "!!@", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
