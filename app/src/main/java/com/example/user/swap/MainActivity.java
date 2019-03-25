package com.example.user.swap;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager manager = getFragmentManager();

    MenuItem mSearch;
    LinearLayout spinnerLayout;
    Spinner sp_category, sp_dealstate;
    ArrayAdapter adapter, adapter2;
    int position_category = 0, position_dealstate=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //액션바에서 타이틀문자 지우기

        spinnerLayout = (LinearLayout)findViewById(R.id.spinnerLayout);
        sp_category = (Spinner)findViewById(R.id.sp_category);
        sp_dealstate = (Spinner)findViewById(R.id.sp_dealstate);
        spinner();
        spinner2();


        //플로팅버튼 눌렀을 때 이벤트
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(),WriteBoard.class);
                startActivity(it);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //메인플래그먼트 띄워주기
        Fragment ft = new Flag_SubMenu();
        Bundle bundle = new Bundle();
        bundle.putInt("id",0);
        bundle.putInt("dealstate_id", 99);
        ft.setArguments(bundle);
        String fragmentTag = "전체보기";
        getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(ft!=null) {
            manager.beginTransaction().replace(R.id.content_main, ft).addToBackStack(fragmentTag).commit();
        }

        //----------------------------------------------------------------------
        //네이게이션 헤더레이아웃 위젯관련(마이페이지, 알람, 로그아웃)
        View headerContainer = navigationView.getHeaderView(0); // This returns the container layout in nav_drawer_header.xml (e.g., your RelativeLayout or LinearLayout)
        TextView textView1 = (TextView)headerContainer.findViewById(R.id.textView1);
        Button btnMypage = (Button)headerContainer.findViewById(R.id.btnMypage);
        Button btnAlarm = (Button)headerContainer.findViewById(R.id.btnAlarm);
        Button btnLogout = (Button)headerContainer.findViewById(R.id.btnLogout);
        if(LoginActivity.Current_user != null) {
            textView1.setText(LoginActivity.Current_user + "님, 환영합니다.");
        } else {
            textView1.setText("로그인이 필요합니다.");
        }

        btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment ft = new Flag_Mypage();
                Bundle bundle = new Bundle();
                if(LoginActivity.Current_user != null) {
                    bundle.putInt("mypage", 0);
                }
                ft.setArguments(bundle);
                String fragmentTag = "mypage";
                getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if(ft!=null)
                    manager.beginTransaction().replace(R.id.content_main,ft,fragmentTag).commit();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment ft = new Flag_Alarm();
                Bundle bundle = new Bundle();
                String fragmentTag = ft.getClass().getSimpleName();
                getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                if(ft!=null)
                    manager.beginTransaction().replace(R.id.content_main,ft).commit();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout();
                LoginActivity.Current_user="";
            }
        });

    }

    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    public void homeBtn(View v){ //메인으로 돌아오기
        sp_category.setSelection(0);
        adapter.notifyDataSetChanged();
        sp_dealstate.setSelection(0);
        adapter2.notifyDataSetChanged();

        Fragment ft = new Flag_SubMenu();
        Bundle bundle = new Bundle();
        bundle.putInt("id",0);
        bundle.putInt("dealstate_id", 99);
        ft.setArguments(bundle);
        String fragmentTag = ft.getClass().getSimpleName();
        getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(ft!=null)
            manager.beginTransaction().replace(R.id.content_main,ft).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mSearch = menu.findItem(R.id.action_search);

        mSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);

                ad.setTitle("물품명 검색");       // 제목 설정

                final EditText et = new EditText(MainActivity.this);
                ad.setView(et);

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment ft = new Flag_SubMenu();
                        Bundle bundle = new Bundle();
                        bundle.putString("searchText", et.getText().toString());
                        ft.setArguments(bundle);
                        String fragmentTag = ft.getClass().getSimpleName();
                        getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        if(ft!=null)
                            manager.beginTransaction().replace(R.id.content_main,ft).commit();
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                ad.show();

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment ft = new Flag_SubMenu();
        Bundle bundle = new Bundle();

        if (id == R.id.totalMenu) {
            bundle.putInt("id",0);
        } else if (id == R.id.dailyMenu) {
            bundle.putInt("id",1);
        } else if (id == R.id.electMenu) {
            bundle.putInt("id",2);
        } else if (id == R.id.fashionMenu) {
            bundle.putInt("id",3);
        } else if (id == R.id.otherMenu) {
            bundle.putInt("id",4);
        } else if (id == R.id.callMenu) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/7890-1234"));
            startActivity(intent);
        }

        bundle.putInt("dealstate_id",99);
        ft.setArguments(bundle);
        String fragmentTag = item.getTitle().toString();
        getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if(ft!=null) {
            manager.beginTransaction().replace(R.id.content_main, ft).addToBackStack(fragmentTag).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void spinner() {
        adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.spinnerCategory, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(adapter);

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment ft = new Flag_SubMenu();
                Bundle bundle = new Bundle();

                position_category = i;
                if (position_category == 0) {
                    bundle.putInt("id",0);
                } else if (position_category == 1) {
                    bundle.putInt("id",1);
                } else if (position_category == 2) {
                    bundle.putInt("id",2);
                } else if (position_category == 3) {
                    bundle.putInt("id", 3);
                }else if (position_category == 4) {
                    bundle.putInt("id", 4);
                }

                sp_dealstate.setSelection(0);
                bundle.putInt("dealstate_id",99);
                ft.setArguments(bundle);
                String fragmentTag = sp_category.getSelectedItem().toString();
                getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if(ft!=null) {
                    manager.beginTransaction().replace(R.id.content_main, ft).addToBackStack(fragmentTag).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void spinner2() {
        adapter2 = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.spinnerDealstate, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_dealstate.setAdapter(adapter2);

        sp_dealstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment ft = new Flag_SubMenu();
                Bundle bundle = new Bundle();

                position_dealstate = i;
                if (position_dealstate == 0) {
                    bundle.putInt("dealstate_id",99);
                } else if (position_dealstate == 1) { //거래전
                    bundle.putInt("dealstate_id",0);
                } else if (position_dealstate == 2) { //거래중
                    bundle.putInt("dealstate_id",1);
                } else if (position_dealstate == 3) { //거래완료
                    bundle.putInt("dealstate_id",2);
                }

                bundle.putInt("id", position_category);
                ft.setArguments(bundle);
                String fragmentTag = sp_dealstate.getSelectedItem().toString();
                getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if(ft!=null) {
                    manager.beginTransaction().replace(R.id.content_main, ft).addToBackStack(fragmentTag).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void spinnerView(int i){
        if(i==1)
            spinnerLayout.setVisibility(View.VISIBLE);
        else
            spinnerLayout.setVisibility(View.GONE);
    }

}
