package com.example.user.swap;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText edit_id, edit_pw, edit_pwconfirm;
    EditText edit_name, edit_birth, edit_phone, edit_email;
    TextView tv_title,tv_confirm;

    String str_id;
    int id_check_num = 0;
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("회원가입");

        Intent i = this.getIntent();
        num = i.getIntExtra("signup", 0);

        edit_id = (EditText)findViewById(R.id.edit_id);
        edit_pw = (EditText)findViewById(R.id.edit_pw);
        edit_pwconfirm = (EditText)findViewById(R.id.edit_pwconfirm);
        edit_name = (EditText)findViewById(R.id.edit_name);
        edit_birth = (EditText)findViewById(R.id.edit_birth);
        edit_phone = (EditText)findViewById(R.id.edit_phone);
        edit_email = (EditText)findViewById(R.id.edit_email);
        tv_confirm = (TextView)findViewById(R.id.tv_confirm);
        tv_title = (TextView)findViewById(R.id.tv_title);

        if(num == 2) {
            setTitle("정보수정");
            tv_title.setText("정 보 수 정");
            UserEntity user = LoginActivity.userEntity;
            edit_id.setText(user.getId());
            edit_id.setFocusable(false);
            edit_id.setClickable(false);
            str_id = user.getId();

            edit_pw.setText(user.getPw());
            edit_pwconfirm.setText(user.getPw());
            edit_name.setText(user.getName());
            edit_birth.setText(user.getBirth());
            edit_phone.setText(user.getPhone());
            edit_email.setText(user.getEmail());

            id_check_num = 1;
        }

        //아이디 중복 체크
        findViewById(R.id.id_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AQuery aQuery = new AQuery(SignupActivity.this);
                String url = Config.HOME_URL + "IdCheck";

                Map<String, Object> params = new LinkedHashMap<>();
                str_id = edit_id.getText().toString();
                params.put("id", str_id);

                aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String result, AjaxStatus status) {
                        try {
                            JSONObject jr = new JSONObject(result);
                            if (jr.isNull("result")) {
                                Toast.makeText(SignupActivity.this, "사용 가능한 id입니다.", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alert_idcheck = new AlertDialog.Builder(SignupActivity.this);
                                alert_idcheck.setTitle("아이디 중복확인");
                                alert_idcheck.setMessage("사용 가능한 id입니다. 이 id를 사용하시겠습니까?");
                                alert_idcheck.setIcon(R.drawable.logo);
                                alert_idcheck.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        id_check_num = 1;
                                    }
                                });
                                alert_idcheck.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        id_check_num = 0;
                                        str_id = null;
                                    }
                                });
                                alert_idcheck.show();
                            } else if (!jr.isNull("result")) {
                                AlertDialog.Builder alert_idcheck = new AlertDialog.Builder(SignupActivity.this);
                                alert_idcheck.setTitle("경고");
                                alert_idcheck.setMessage("이미 존재하는 id입니다.");
                                alert_idcheck.setIcon(R.drawable.logo);
                                alert_idcheck.setPositiveButton("예", null);
                                alert_idcheck.show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(SignupActivity.this, e + "//중복확인 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        edit_pwconfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = edit_pw.getText().toString();
                String confirm = edit_pwconfirm.getText().toString();

                if(password.equals(confirm)) {
                    tv_confirm.setText("비밀번호 일치");
                    tv_confirm.setTextColor(Color.GREEN);
                } else {
                    tv_confirm.setText("비밀번호 불일치");
                    tv_confirm.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id_check_num == 0) {
                    AlertDialog.Builder alert_idcheck = new AlertDialog.Builder(SignupActivity.this);
                    alert_idcheck.setTitle("경고");
                    alert_idcheck.setMessage("id 중복확인 바랍니다.");
                    alert_idcheck.setIcon(R.drawable.logo);
                    alert_idcheck.setPositiveButton("예", null);
                    alert_idcheck.show();
                } else {
                    edit_id.setFocusable(true);
                    edit_id.setClickable(true);

                    String str_pw = edit_pw.getText().toString();
                    String str_name = edit_name.getText().toString();
                    String str_birth = edit_birth.getText().toString();
                    String str_phone = edit_phone.getText().toString();
                    String str_email = edit_email.getText().toString();

                    if (TextUtils.isEmpty(str_id) || TextUtils.isEmpty(str_pw)) {
                        Toast.makeText(SignupActivity.this, "모두 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        if(num==1) { //일반 회원가입
                            SignUp(str_id,str_pw,str_name,str_birth,str_phone,str_email);
                        } else if(num == 2) { //기존 정보 수정, id는 못고침!
                            AQuery aQuery = new AQuery(SignupActivity.this);
                            String url = Config.HOME_URL + "UserInfoEdit";

                            Map<String, Object> params = new LinkedHashMap<>();

                            params.put("id", str_id);
                            params.put("pw", str_pw);
                            params.put("name", str_name);
                            params.put("birth", str_birth);
                            params.put("phone", str_phone);
                            params.put("email", str_email);

                            aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
                                @Override
                                public void callback(String url, String result, AjaxStatus status) {
                                    try {
                                        JSONObject jReponse = new JSONObject(result);
                                        if (jReponse != null && !jReponse.isNull("result")) {
                                            AlertDialog.Builder alert_idcheck = new AlertDialog.Builder(SignupActivity.this);
                                            alert_idcheck.setTitle("정보수정 성공");
                                            alert_idcheck.setMessage("정보수정이 완료되었습니다.");
                                            alert_idcheck.setIcon(R.drawable.logo);
                                            alert_idcheck.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            });
                                            alert_idcheck.show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(SignupActivity.this, "수정 하는데 오류가 발생 하였습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void SignUp(String str_id,String str_pw,String str_name,String str_birth,String str_phone,String str_email){
        AQuery aQuery = new AQuery(SignupActivity.this);
        String url = Config.HOME_URL + "SignUp";

        Map<String, Object> params = new LinkedHashMap<>();

        params.put("id", str_id);
        params.put("pw", str_pw);
        params.put("name", str_name);
        params.put("birth", str_birth);
        params.put("phone", str_phone);
        params.put("email", str_email);
        params.put("fcmToken",FirebaseInstanceId.getInstance().getToken());
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                try {
                    JSONObject jReponse = new JSONObject(result);
                    if (jReponse != null && !jReponse.isNull("result")) {
                        AlertDialog.Builder alert_idcheck = new AlertDialog.Builder(SignupActivity.this);
                        alert_idcheck.setTitle("회원가입 성공");
                        alert_idcheck.setMessage("회원가입이 완료되었습니다.");
                        alert_idcheck.setIcon(R.drawable.logo);
                        alert_idcheck.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        alert_idcheck.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SignupActivity.this, "등록 하는데 오류가 발생 하였습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
