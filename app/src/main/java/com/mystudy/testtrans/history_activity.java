package com.mystudy.testtrans;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class history_activity extends Activity {
    private String user;
    private ListView listView ;
    private int num;
    private ArrayList<String> selectwordlist = new ArrayList<>();
    private ArrayList<String> selecttolist = new ArrayList<>();

    private void setword(String word){
        selectwordlist.add(word);
    }

    private void setto(String to){
        selecttolist.add(to);
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            ArrayList<String> word = new ArrayList<>();
            ArrayList<String> trans = new ArrayList<>();
            ArrayList<String> to = new ArrayList<>();

            ArrayList<String> wordto = new ArrayList<>();
            int wordsize = msg.arg1;

            super.handleMessage(msg);
            if (msg.what == 0){
                Bundle bundle = msg.getData();
                word = bundle.getStringArrayList("word");

                trans = bundle.getStringArrayList("trans");

                to = bundle.getStringArrayList("to");

                for (int i = 0;i<wordsize;i++){
                    wordto.add(word.get(i).toString()+"("+to.get(i).toString()+")");
                    setword(word.get(i).toString());
                    setto(to.get(i).toString());
                }


                //showMsg(Integer.toString(wordsize));

                //如果不在主函数中，this需要前加上activity的名字，不能直接用this
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(history_activity.this, android.R.layout.simple_expandable_list_item_1,wordto);

                listView.setAdapter(adapter);
                ArrayList<String> finalTrans = trans;
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(history_activity.this);
                        builder.setTitle("查看释义");
                        builder.setMessage(finalTrans.get(position));
                        builder.show();
                    }
                });
            }
        }


    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        Bmob.initialize(this, "a18083945319361c525d355cf565e3c4");
        showMsg("点击查看释义， 长按删除");
        initview();
    }

    private void initview() {
        //初始化组件
        listView = findViewById(R.id.history_view);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.width = 1400;
        listView.setLayoutParams(params);

        Intent i = getIntent();
        user = i.getStringExtra("usr");
        //查询当前用户的收藏夹
        initdata();
        inititem();
    }

    private void inititem() {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(history_activity.this);
                builder.setTitle("提示");
                builder.setMessage("您是否要删除该条历史记录？");
                builder.setPositiveButton("我意已决，删除！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String delete_name = selectwordlist.get(position).toString();
                        String delete_to = selecttolist.get(position).toString();
                        //showMsg(delete_name);
                        history history1 = new history();
                        BmobQuery<history> historyBmobQuery = new BmobQuery<>();
                        historyBmobQuery.addWhereEqualTo("word",delete_name);
                        historyBmobQuery.addWhereEqualTo("to",delete_to);
                        historyBmobQuery.addWhereEqualTo("user",user);
                        historyBmobQuery.findObjects(new FindListener<history>() {
                            @Override
                            public void done(List<history> list, BmobException e) {
                                showMsg(Integer.toString(list.size()));
                                history1.setObjectId(list.get(0).getObjectId());
                                history1.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                        if (e == null){
                                            showMsg("您已删除记录:"+parent.getItemAtPosition(position).toString());
                                            onCreate(null);
                                        }else {
                                            showMsg("删除失败，请重试");
                                        }
                                    }
                                });

                            }
                        });
                    }

                });

                builder.setNegativeButton("舍不得，还是算了吧", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showMsg("已取消删除操作");
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    private void initdata() {
        BmobQuery<history> historyBmobQuery = new BmobQuery<>();
        historyBmobQuery.addWhereEqualTo("user",user);
        historyBmobQuery.findObjects(new FindListener<history>() {
            @Override
            public void done(List<history> list, BmobException e) {
                if (e == null){
                    //showMsg(Integer.toString(list.size()));
                    for (int i = 0;i<list.size();i++){
                        Message message = Message.obtain();
                        message.what = 0;
                        message.arg1 = list.size();
                        Bundle bundle = new Bundle();
                        ArrayList<String> wordlist = new ArrayList<>();
                        ArrayList<String> translist = new ArrayList<>();
                        ArrayList<String> tolist = new ArrayList<>();
                        for (int ii = 0;ii <list.size(); ii++){
                            wordlist.add(list.get(ii).getWord());
                            translist.add(list.get(ii).getTrans());
                            tolist.add(list.get(ii).getTo());
                        }
                        bundle.putStringArrayList("word",wordlist);
                        bundle.putStringArrayList("trans",translist);
                        bundle.putStringArrayList("to",tolist);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }else{
                    showMsg(e.toString());
                }
            }

        });
    }

    private void showMsg(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

}
