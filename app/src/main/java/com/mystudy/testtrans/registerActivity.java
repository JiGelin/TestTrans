package com.mystudy.testtrans;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.SharedPreferences;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import android.content.pm.ActivityInfo;

public class registerActivity extends AppCompatActivity {

    private RadioGroup mRg1;
    private EditText username,psw,pswagain;
    private Button zhuce;
    private String Sex="男";

    public class Use extends BmobObject {
        private String name;
        private String psw;
        private String sex;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPsw() {
            return psw;
        }
        public void setPsw(String psw) {
            this.psw = psw;
        }


        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bmob.initialize(this, "a18083945319361c525d355cf565e3c4");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.editname);
        psw=findViewById(R.id.editPassword);
        pswagain=findViewById(R.id.editrePassword);
        mRg1 = (RadioGroup) findViewById(R.id.rg_1);
        zhuce = (Button) findViewById(R.id.register);
        mRg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton sex = (RadioButton) radioGroup.findViewById(i);
                Sex= sex.getText().toString();
                Toast.makeText(registerActivity.this,sex.getText().toString(),Toast.LENGTH_SHORT).show();
                //显示点击
            }
        });
        zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strname = username.getText().toString();//获取用户名
                String strpassword = psw.getText().toString();//获取密码
                String strrepassword = pswagain.getText().toString();
                if(strpassword.length()!= 0&& strname.length()!=0){
                    if (strpassword.equals(strrepassword)) {
                        //Toast.makeText(registerActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Use p2 = new Use();
                        p2.setName(strname);
                        p2.setPsw(strpassword);
                        p2.setSex(Sex);
                        p2.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(registerActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(registerActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                        Intent registertoMain = new Intent(registerActivity.this, login_activity.class);
                        startActivity(registertoMain);
                    } else {
                        Toast.makeText(registerActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(registerActivity.this, "请完善注册信息！", Toast.LENGTH_SHORT).show();
                }

            }
            });
    }
}
