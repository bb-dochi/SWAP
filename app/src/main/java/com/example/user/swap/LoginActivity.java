package com.example.user.swap;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeResponse;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class LoginActivity extends AppCompatActivity {
    public static String Current_user = "";
    public static UserEntity userEntity;
    SessionCallback callback;

    //현재 로그인 유저 아이디 정보

    EditText login_id,login_pw;
    Button loginBtn,loginBtnKakao;
    LoginButton kakaobtn;
    TextView signup,find;

    String str_id,str_pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        login_id = (EditText)findViewById(R.id.login_id);
        login_pw = (EditText)findViewById(R.id.login_pw);
        signup = (TextView)findViewById(R.id.SignupBtn);
        find = (TextView)findViewById(R.id.FindBtn);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtnKakao = (Button)findViewById(R.id.loginBtnKakao);
        kakaobtn =(LoginButton)findViewById(R.id.btn_kakao_login);

        //회원가입
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                i.putExtra("signup", 1);
                startActivity(i);
            }
        });

        //Id,Pw 찾기
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(LoginActivity.this, FindActivity.class);
                startActivity(i2);
            }
        });

        //로그인
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCheck();
            }
        });
        loginBtnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaobtn.performClick();
            }
        });
    }

    public void loginCheck(){
        str_id = login_id.getText().toString();
        str_pw = login_pw.getText().toString();

        AQuery aQuery = new AQuery(LoginActivity.this);
        String url = Config.HOME_URL + "Login";

        Map<String, Object> params = new LinkedHashMap<>();

        params.put("id", str_id);
        params.put("pw", str_pw);

        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                try {
                    Gson gson = new Gson();
                    if (jReponse != null && !jReponse.isNull("result")) {
                        JSONArray jarLogin = jReponse.optJSONArray("result");
                        userEntity = gson.fromJson(jarLogin.optJSONObject(0).toString(), UserEntity.class);

                        Toast.makeText(LoginActivity.this, userEntity.getName() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                        Current_user=userEntity.getId();
                        Intent it = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(it);
                    } else {
                        AlertDialog.Builder alert_idcheck = new AlertDialog.Builder(LoginActivity.this);
                        alert_idcheck.setTitle("경고");
                        alert_idcheck.setMessage("id와 password를 다시 한 번 확인해주세요.");
                        alert_idcheck.setIcon(R.drawable.logo);
                        alert_idcheck.setPositiveButton("예", null);
                        alert_idcheck.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e + "!!@", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            List<String> keys = new ArrayList<>();
            keys.add("properties.nickname");
            keys.add("kakao_account.birthday");
            keys.add("kakao_account.email");
            UserManagement.getInstance().me(keys,new MeV2ResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
//                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSuccess(MeV2Response response) {
                    Log.d("user id : ",""+response.getId());
                    Log.d("user name : " ,""+ response.getNickname());
                    Log.d("email: " ,""+ response.getKakaoAccount().getEmail());
                    Log.d("birth: " ,""+ response.getKakaoAccount().getBirthday());

                    StringTokenizer st = new StringTokenizer(response.getKakaoAccount().getEmail(),"@");
                    String userId = st.nextToken();  //이메일 앞부분만 따와서 아이디로 인식

                    LoginActivity.Current_user= userId;
                    Intent it = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(it);
                }


                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    System.out.println("ccc"+errorResult.getErrorMessage());
                }

            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ

        }
    }
}
