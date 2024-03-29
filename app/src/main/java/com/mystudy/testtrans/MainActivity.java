package com.mystudy.testtrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {

    private LinearLayout beforeLay;//翻译之前的布局
    private Spinner spLanguage;//语言选择下拉框
    private LinearLayout afterLay;//翻译之后的布局
    private TextView tvFrom;//翻译源语言
    private TextView tvTo;//翻译目标语言

    private EditText edContent;//输入框（要翻译的内容）
    private ImageView ivClearTx;//清空输入框按钮
    private TextView tvTranslation;//翻译

    private ImageView tvCollect; //点击进行收藏


    private LinearLayout resultLay;//翻译结果布局
    private TextView tvResult;//翻译的结果
    private ImageView ivCopyTx;//复制翻译的结果

    private String fromLanguage = "auto";//目标语言
    private String toLanguage = "auto";//翻译语言

    private ClipboardManager myClipboard;//复制文本
    private ClipData myClip; //剪辑数据

    private String appId = "20210523000839113";
    private String key = "5NDRgYbNMO0IHu61yu7Z";

    private int collect_flag ; // 0 代表是新词，未被收藏过； 1 代表已经被收藏过;
    private String inputTx;
    private Button incollect;
    private Button inhistory;
    private String user;

    //配置初始数据
    private final String data[] = {"自动检测语言", "中文 → 英文", "英文 → 中文",
            "中文 → 繁体中文", "中文 → 粤语", "中文 → 日语",
            "中文 → 韩语", "中文 → 法语", "中文 → 俄语",
            "中文 → 阿拉伯语", "中文 → 西班牙语 ", "中文 → 意大利语"};

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int flag;
            flag = msg.arg1;
            //showMsg(Integer.toString(flag));
            if (flag == 1) {
                tvCollect.setImageResource(R.drawable.collect_after);
            } else if (flag == 0) {
                tvCollect.setImageResource(R.drawable.collect_before);
            }
            if (msg.what == 100) {

            }
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == 11){
                Bundle bundle = msg.getData();
                String from = bundle.getString("from");
                String to = bundle.getString("to");
                String input = edContent.getText().toString().trim();
                String result = tvResult.getText().toString().trim();
                from = setFrom(from);
                to = setTo(to);

                //加入历史记录
                history history1 = new history();
                history1.setWord(input);
                history1.setTrans(result);
                history1.setUser(user);
                history1.setFrom(from);
                history1.setTo(to);
                history1.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                    }
                });
                //设置收藏按钮
                BmobQuery<word> wordBmobQuery = new BmobQuery<>();
                wordBmobQuery.addWhereEqualTo("word",input);
                wordBmobQuery.addWhereEqualTo("from",from);
                wordBmobQuery.addWhereEqualTo("to",to);
                wordBmobQuery.addWhereEqualTo("user",user);
                wordBmobQuery.findObjects(new FindListener<word>() {
                    @Override
                    public void done(List<word> list, BmobException e) {
                        if (e == null){
                            int flag = 0 ;
                            Message message = Message.obtain();
                            message.what = 100;
                            if (list.size() == 0){
                                flag = 0 ;
                            }else {
                                flag = 1 ;
                            }
                            //showMsg(Integer.toString(flag));
                            message.arg1 = flag;
                            handler1.sendMessage(message);

                        }else{
                            showMsg(e.toString());
                        }
                    }

                });

            }
        }




    };

    private  String setFrom(String from) {
        switch (from) {
            case "zh":
                from = "中文";
                break;
            case "en":
                from = "英文";
                break;
            case "yue":
                from = "粤语";
                break;
            case "cht":
                from = "繁体中文";
                break;
            case "jp":
                from = "日语";
                break;
            case "kor":
                from = "韩语";
                break;
            case "fra":
                from = "法语";
                break;
            case "ru":
                from = "俄语";
                break;
            case "ara":
                from = "阿拉伯语";
                break;
            case "spa":
                from = "西班牙语";
                break;
            case "it":
                from = "意大利语";
                break;
            default:
                break;
        }
        return from;
    }

    private String setTo(String to){
        switch (to){
            case "zh":
                to = "中文";
                break;
            case "en":
                to = "英文";
                break;
            case "yue":
                to = "粤语";
                break;
            case "cht":
                to = "繁体中文";
                break;
            case "jp":
                to = "日语";
                break;
            case "kor":
                to = "韩语";
                break;
            case "fra":
                to = "法语";
                break;
            case "ru":
                to = "俄语";
                break;
            case "ara":
                to = "阿拉伯语";
                break;
            case "spa":
                to = "西班牙语";
                break;
            case "it":
                to = "意大利语";
                break;
            default:
                break;
        }
        return to;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "a18083945319361c525d355cf565e3c4");
        initView();

    }
    private void initView() {
        //控件初始化
        beforeLay = findViewById(R.id.before_lay);
        spLanguage = findViewById(R.id.sp_language);
        afterLay = findViewById(R.id.after_lay);
        tvFrom = findViewById(R.id.tv_from);
        tvTo = findViewById(R.id.tv_to);
        edContent = findViewById(R.id.ed_content);
        ivClearTx = findViewById(R.id.iv_clear_tx);
        tvTranslation = findViewById(R.id.tv_translation);
        resultLay = findViewById(R.id.result_lay);
        tvResult = findViewById(R.id.tv_result);
        ivCopyTx = findViewById(R.id.iv_copy_tx);
        tvCollect = findViewById(R.id.collect);
        incollect = findViewById(R.id.collect_btn);
        inhistory = findViewById(R.id.history_btn);

        Intent i = getIntent();
        user=i.getStringExtra("user");

        //设置收藏按钮初始化
        tvCollect.setImageResource(R.drawable.collect_before);

        //点击时间
        ivClearTx.setOnClickListener((View.OnClickListener) this);
        ivCopyTx.setOnClickListener((View.OnClickListener) this);
        tvTranslation.setOnClickListener((View.OnClickListener) this);
        tvCollect.setOnClickListener(this);
        incollect.setOnClickListener(this);
        inhistory.setOnClickListener(this);

        //设置下拉数据
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLanguage.findViewById(R.id.sp_language);
        spLanguage.setAdapter(adapter);

        editTextListener();//输入框监听
        spinnerListener();//下拉框选择监听
        //获取系统粘贴板服务
        myClipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
    }

    private void editTextListener() {
        edContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ivClearTx.setVisibility(View.VISIBLE);//显示清除按钮
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClearTx.setVisibility(View.VISIBLE);//显示清除按钮
            }

            @Override
            public void afterTextChanged(Editable s) {
                ivClearTx.setVisibility(View.VISIBLE);//显示清除按钮

                String content = edContent.getText().toString().trim();
                if (content.isEmpty()) {//为空
                    tvCollect.setImageResource(R.drawable.collect_before);
                    resultLay.setVisibility(View.GONE);
                    tvTranslation.setVisibility(View.VISIBLE);
                    beforeLay.setVisibility(View.VISIBLE);
                    afterLay.setVisibility(View.GONE);
                    ivClearTx.setVisibility(View.GONE);
                }
            }
        });

    }

    private void  spinnerListener(){
        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://自动检测
                        fromLanguage = "auto";
                        toLanguage = fromLanguage;
                        break;
                    case 1://中文 → 英文
                        fromLanguage = "zh";
                        toLanguage = "en";
                        break;
                    case 2://英文 → 中文
                        fromLanguage = "en";
                        toLanguage = "zh";
                        break;
                    case 3://中文 → 繁体中文
                        fromLanguage = "zh";
                        toLanguage = "cht";
                        break;
                    case 4://中文 → 粤语
                        fromLanguage = "zh";
                        toLanguage = "yue";
                        break;
                    case 5://中文 → 日语
                        fromLanguage = "zh";
                        toLanguage = "jp";
                        break;
                    case 6://中文 → 韩语
                        fromLanguage = "zh";
                        toLanguage = "kor";
                        break;
                    case 7://中文 → 法语
                        fromLanguage = "zh";
                        toLanguage = "fra";
                        break;
                    case 8://中文 → 俄语
                        fromLanguage = "zh";
                        toLanguage = "ru";
                        break;
                    case 9://中文 → 阿拉伯语
                        fromLanguage = "zh";
                        toLanguage = "ara";
                        break;
                    case 10://中文 → 西班牙语
                        fromLanguage = "zh";
                        toLanguage = "spa";
                        break;
                    case 11://中文 → 意大利语
                        fromLanguage = "zh";
                        toLanguage = "it";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear_tx://清空输入框
                edContent.setText("");//清除文本
                ivClearTx.setVisibility(View.GONE);//清除数据之后隐藏按钮
                break;
            case R.id.iv_copy_tx://复制翻译后的结果
                String inviteCode = tvResult.getText().toString();
                myClip = ClipData.newPlainText("text", inviteCode);
                myClipboard.setPrimaryClip(myClip);
                showMsg("已复制");
                break;
            case R.id.tv_translation://翻译
                translation();//翻译
                inputTx = edContent.getText().toString().trim(); //获取输入内容
                String resultTx1 = tvResult.getText().toString().trim();
                String from = tvFrom.getText().toString();
                String to = tvTo.getText().toString();
                init_collect(inputTx,from,to);

                break;
            case R.id.collect://收藏按键
                inputTx = edContent.getText().toString().trim();//获取输入内容
                String resultTx = tvResult.getText().toString().trim();//获取输出内容
                String from1 = tvFrom.getText().toString();
                String to1 = tvTo.getText().toString();
                collect_click(inputTx,resultTx,user,from1,to1);//点击动作
                break;
            case R.id.collect_btn://收藏夹
                Intent main2collect=new Intent(MainActivity.this,collectbox.class);
                main2collect.putExtra("usr",user);
                startActivity(main2collect);
                break;
            case R.id.history_btn://历史记录
                Intent main2history = new Intent(MainActivity.this,history_activity.class);
                main2history.putExtra("usr",user);
                startActivity(main2history);

            default:
                break;
        }
    }

    private void collect_click(String inputTx,String resultTx,
                               String user,String from,String to) {
        word word1 =new word();
        word1.setWord(inputTx);
        word1.setTrans(resultTx);
        word1.setUser(user);
        word1.setFrom(from);
        word1.setTo(to);

        word1.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                tvCollect.setImageResource(R.drawable.collect_after);
                if(e==null){
                    showMsg("收藏成功");
                }else{
                    showMsg("您已经收藏过该单词");
                }
            }
        });
    }

    private void init_collect(String inputTx,String from,String to) {

        //from = tvFrom.getText().toString();
        //to = tvTo.getText().toString();
        ifrepitition(inputTx,from,to);
        //收藏按钮设置

    }



    private void ifrepitition(String inputTx,String from,String to) {


    }


    private void translation() {
        //获取输入的内容
        String inputTx = edContent.getText().toString().trim();
        //判断输入内容是否为空
        if (!inputTx.isEmpty() || !"".equals(inputTx)) {//不为空
            tvTranslation.setText("翻译中...");
            tvTranslation.setEnabled(false);//不可更改，同样就无法点击
            String salt = num(1);//随机数
            //拼接一个字符串然后加密
            String spliceStr = appId + inputTx + salt + key;//根据百度要求 拼接
            String sign = stringToMD5(spliceStr);//将拼接好的字符串进行MD5加密   作为一个标识
            //异步Get请求访问网络
            asyncGet(inputTx, fromLanguage, toLanguage, salt, sign);
        } else {//为空
            showMsg("请输入要翻译的内容！");
        }
    }

    public static String num(int a) {
        Random r = new Random(a);
        int ran1 = 0;
        for (int i = 0; i < 5; i++) {
            ran1 = r.nextInt(100);
            System.out.println(ran1);
        }
        return String.valueOf(ran1);
    }

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5")
                    .digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    private void asyncGet(String content, String fromType, String toType, String salt, String sign) {
        //通用翻译API HTTP地址：
        //http://api.fanyi.baidu.com/api/trans/vip/translate
        //通用翻译API HTTPS地址：
        //https://fanyi-api.baidu.com/api/trans/vip/translate

        //String httpStr = "http://api.fanyi.baidu.com/api/trans/vip/translate";
        String httpsStr = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        //拼接请求的地址
        String url = httpsStr +
                "?appid=" + appId + "&q=" + content + "&from=" + fromType + "&to=" +
                toType + "&salt=" + salt + "&sign=" + sign;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                //异常返回
                goToUIThread(e.toString(), 0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //正常返回
                goToUIThread(response.body().string(), 1);
            }
        });
    }

    private void goToUIThread(final Object object, final int key) {
        //切换到主线程处理数据
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTranslation.setText("翻译");
                tvTranslation.setEnabled(true);

                if (key == 0) {//异常返回
                    showMsg("异常信息：" + object.toString());
                    Log.e("MainActivity",object.toString());
                } else {//正常返回
                    //通过Gson 将 JSON字符串转为实体Bean
                    final TranslateResult result = new Gson().fromJson(object.toString(), TranslateResult.class);
                    tvTranslation.setVisibility(View.GONE);
                    //显示翻译的结果
                    tvResult.setText(result.getTrans_result().get(0).getDst());
                    tvResult.setText(object.toString());
                    resultLay.setVisibility(View.VISIBLE);
                    beforeLay.setVisibility(View.GONE);
                    afterLay.setVisibility(View.VISIBLE);
                    //翻译成功后的语言判断显示
                    initAfter(result.getFrom(), result.getTo());
                    Message message = Message.obtain();
                    message.what = 11 ;
                    Bundle bundle = new Bundle();
                    bundle.putString("from",result.getFrom());
                    bundle.putString("to",result.getTo());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void initAfter(String from, String to) {
        if (("zh").equals(from)) {
            tvFrom.setText("中文");
        } else if (("en").equals(from)) {
            tvFrom.setText("英文");
        } else if (("yue").equals(from)) {
            tvFrom.setText("粤语");
        } else if (("cht").equals(from)) {
            tvFrom.setText("繁体中文");
        } else if (("jp").equals(from)) {
            tvFrom.setText("日语");
        } else if (("kor").equals(from)) {
            tvFrom.setText("韩语");
        } else if (("fra").equals(from)) {
            tvFrom.setText("法语");
        } else if (("ru").equals(from)) {
            tvFrom.setText("俄语");
        } else if (("ara").equals(from)) {
            tvFrom.setText("阿拉伯语");
        } else if (("spa").equals(from)) {
            tvFrom.setText("西班牙语");
        } else if (("it").equals(from)) {
            tvFrom.setText("意大利语");
        }
        if (("zh").equals(to)) {
            tvTo.setText("中文");
        } else if (("en").equals(to)) {
            tvTo.setText("英文");
        } else if (("yue").equals(to)) {
            tvTo.setText("粤语");
        } else if (("cht").equals(to)) {
            tvTo.setText("繁体中文");
        } else if (("jp").equals(to)) {
            tvTo.setText("日语");
        } else if (("kor").equals(to)) {
            tvTo.setText("韩语");
        } else if (("fra").equals(to)) {
            tvTo.setText("法语");
        } else if (("ru").equals(to)) {
            tvTo.setText("俄语");
        } else if (("ara").equals(to)) {
            tvTo.setText("阿拉伯语");
        } else if (("spa").equals(to)) {
            tvTo.setText("西班牙语");
        } else if (("it").equals(to)) {
            tvTo.setText("意大利语");
        }
    }


    /**
     * Toast提示
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }


}