package com.mystudy.testtrans;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mystudy.testtrans.R;
import com.mystudy.testtrans.Use;

import java.util.List;

public class login_activity extends AppCompatActivity {

    private Button denglu,zhuce;
    private EditText username,psw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bmob.initialize(this, "a18083945319361c525d355cf565e3c4");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        denglu=(Button)findViewById(R.id.button);
        username=findViewById(R.id.editTextNumber);
        psw=findViewById(R.id.editTextPassword);
        zhuce=(Button)findViewById(R.id.zhuce);
        denglu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yhm=username.getText().toString();
                String mima=psw.getText().toString();
                if (yhm.length()!=0){
                    BmobQuery<Use> bmobQuery = new BmobQuery<Use>();
                    bmobQuery.addWhereEqualTo("name", yhm);
                    bmobQuery.findObjects(new FindListener<Use>() {
                        @Override
                        public void done(List<Use> list, BmobException e) {
                            if(e==null){
                                if (list.size() == 0){
                                    showMsg("用户名不存在，请注册！");
                                }else{
                                    String datapsw=list.get(0).getPsw();
                                    if(mima.equals(datapsw)) {
                                        Intent MaintoTrans=new Intent(login_activity.this,MainActivity.class);
                                        MaintoTrans.putExtra("user",yhm);
                                        startActivity(MaintoTrans);
                                        Toast.makeText(login_activity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                    }else{
                                        showMsg("密码错误");
                                    }
                                }
                            }
                            else{
                                showMsg("用户名不存在！");
                            }
                        }
                    });

                }else {
                    showMsg("请输入用户名！！");
                }
            }
        });

        zhuce.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //跳转到button演示界面
                Intent MaintoButton=new Intent(login_activity.this,registerActivity.class);
                startActivity(MaintoButton);
            }
        });
    }
    private void showMsg(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }
}


