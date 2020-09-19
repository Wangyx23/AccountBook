package cs.hku.accountbook;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cs.hku.dao.DBOpenHelper;
import cs.hku.util.pubFun;

/**
 * @programName: ExpenseProcesActivity.java
 * @programFunction: Add an income and expense record
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class ExpenseProcesActivity extends AppCompatActivity {
    private int type = 0;//0:income   1:payout
    final static int EDIT_MODE = 2;

    private String date="";
    private String month="";
    private String year="";
    private String day="";

    private String[] str = null;
    private String[] accountId = null;
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog datePicker = null;
    private AlertDialog dialog = null;
    private ArrayAdapter<String> adapter;
    private List<String> list = null;


    private TextView title_tv = null;
    private RadioGroup trans_type_tab_rg = null;
    private RadioButton rb1=null;
    private RadioButton rb2=null;

    private FrameLayout corporation_fl = null;
    private FrameLayout empty_fl = null;
    private Button cost_btn = null;
    private String  value="0";
    private Spinner first_level_category_spn = null;
    private Spinner sub_category_spn = null;
    private int type_sub_id = 0;
    private Spinner account_spn = null;
    private Spinner corporation_spn = null;
    private Button trade_time_btn = null;
    private Spinner project_spn = null;
    private Button memo_btn = null;
    private Button save_btn = null;
    private Button cancel_btn = null;

    private EditText edit = null;
    private int isInitOnly = 0;

    private Context context;

    //类别
    private static String[] bigCategoryList = { "" };
    private static String[] defaultSubCategory_info = { "" };
    //子类别
    private static String[][] subCategory_info = new String[][] {{ "" }, { "" }};
    //账户
    private static String[] accountList = { "" };
    //商家
    private static String[] shopList = { "" };
    //备注
    private static String[] noteList = { "" };

    private TextView txtBigCategory_view;
    private Spinner BigCategory_spinner;
    private ArrayAdapter<String> BigCategory_adapter;



    private TextView txtAccount_view;
    private Spinner account_spinner;
    private ArrayAdapter<String> account_adapter;

    private TextView txtShop_view;
    private Spinner shop_spinner;
    private ArrayAdapter<String> shop_adapter;

    private TextView txtNote_view;
    private Spinner note_spinner;
    private ArrayAdapter<String> note_adapter;

    private String txtBigCategory = "";
    private String txtAccount = "";
    private String txtNote = "";

    private TextView txtDate;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_proces);
        //去除工具栏
        getSupportActionBar().hide();
        //接收传递过来的参数
        final Intent intent = getIntent();
        type = intent.getIntExtra("strType", 0);

        context = this;

        initSpinner();//设置几个下拉选项

        loadingFormation();//设置金额和日期按钮

        trade_time_btn.setText(pubFun.format(calendar.getTime()));

        cost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(ExpenseProcesActivity.this, KeyPad.class);
                i.putExtra("value", value);
                startActivityForResult(i, 0);
            }
        });
        trade_time_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                openDate();
            }
        });
    }

    private void loadingFormation(){
        cost_btn=(Button)findViewById(R.id.cost_btn);
        trade_time_btn=(Button)findViewById(R.id.trade_time_btn);
    }

    private void openDate() {
        datePicker = new DatePickerDialog(this, mDateSetListenerSatrt,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    @Override
    /**
     * return money
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            Bundle extras = data.getExtras();
            value = extras.getString("value");
            //cost_btn.setText(DecimalFormat.getCurrencyInstance().format(Double.parseDouble(value)));
            DecimalFormat df = new DecimalFormat("0.00");
            cost_btn.setText(df.format(Float.parseFloat(value)));
        }
    }

    /**
     * return date
     */
    private DatePickerDialog.OnDateSetListener mDateSetListenerSatrt = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.YEAR, year);
            trade_time_btn.setText(pubFun.format(calendar.getTime()));
        }
    };

    /**
     * 初始化spinner
     */
    private void initSpinner(){
        if(type == 0){
            //类别
            //bigCategoryList = new String[] { "工资收入", "投资收入" ,"奖金收入","加班收入","利息收入","其他收入" };
            bigCategoryList = new String[] {"Salary","Investment","Bonus","Call-back Pay","Interest","Others"};
            //账户
            //accountList = new String[] { "现金", "储蓄卡","支付宝","微信","花呗","信用卡" };
            accountList = new String [] {"Cash", "Deposit", "AliPay", "WeChat", "Ant Check Later (AliPay)", "Credit Card"};
            //备注
            //noteList = new String[] { "公司报销","出差","其他" };
            noteList = new String[] {" Company Expense "," Business Trip "," Others "};
        }else{
            //bigCategoryList = new String[] { "服饰", "餐饮","购物","交通","水果","娱乐","美容","社交","旅行","运动","其他" };
            //bigCategoryList = new String [] {" Clothes ", "Food", "Shopping", "Traffic", "Fruits", "Entertainment", "Beauty","Social","Travel","Sports","other"};
            bigCategoryList = new String[] { "Food", "Drinks", "Traffic",
                    "Communications", "Leisure", "Learning", "Fruits", "Sports", "Clothing accessories", "Film"," Others"};
            //账户
            //accountList = new String[] { "现金", "储蓄卡","支付宝","花呗","微信","信用卡","其他" };
            accountList = new String [] {"Cash", "Deposit", "AliPay", "WeChat", "Ant Check Later (AliPay)", "Credit Card"};
            //备注
            //noteList = new String[] { "零食", "旅游","休闲","通讯","其他" };
            noteList = new String [] {"Snacks", "Travel", "Leisure", "Communication", "Others"};
        }

        /**
         * 1、定义类别下拉菜单
         */
        txtBigCategory_view = (TextView) findViewById(R.id.txtBigCategory);
        BigCategory_spinner = (Spinner) findViewById(R.id.BigCategory_spinner);
        //将可选内容与ArrayAdapter连接起来
        BigCategory_adapter = new ArrayAdapter<String>(this,
                R.layout.item, bigCategoryList);
        //设置下拉列表的风格
        BigCategory_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter添加到spinner中
        BigCategory_spinner.setAdapter(BigCategory_adapter);
        // 添加事件Spinner事件监听
        BigCategory_spinner
                .setOnItemSelectedListener(new BigCategory_spinnerSelectedListener());
        // 设置默认值
        BigCategory_spinner.setVisibility(View.VISIBLE);



        /**
         * 3、定义账户下拉菜单
         */
        txtAccount_view = (TextView)findViewById(R.id.txtAccount);
        account_spinner = (Spinner) findViewById(R.id.account_spinner);
        account_adapter = new ArrayAdapter<String>(this,
                R.layout.item, accountList);
        account_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_spinner.setAdapter(account_adapter);
        account_spinner
                .setOnItemSelectedListener(new account_spinnerSelectedListener());
        account_spinner.setVisibility(View.VISIBLE);



        /**
         * 5、定义备注下拉菜单
         */
        txtNote_view = (TextView)findViewById(R.id.txtNote);
        note_spinner = (Spinner) findViewById(R.id.note_spinner);
        note_adapter = new ArrayAdapter<String>(this,
                R.layout.item, noteList);
        note_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        note_spinner.setAdapter(note_adapter);
        note_spinner
                .setOnItemSelectedListener(new note_spinnerSelectedListener());
        note_spinner.setVisibility(View.VISIBLE);
    }

    /**
     * 选择 类别 事件 监听器
     */
    class BigCategory_spinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            txtBigCategory = bigCategoryList[arg2];
            int pos = BigCategory_spinner.getSelectedItemPosition();
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }



    /**
     * 选择 账户事件 监听器
     */
    class account_spinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            txtAccount = accountList[arg2];
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }



    /**
     * 选择 商家事件 监听器
     */
    class note_spinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            txtNote = noteList[arg2];
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    public void OnMySaveClick(View v) throws IOException {
        saveInfo();
    }
    public void OnMyCancelClick(View v) {
        exit();
    }

    /**
     * cancel event
     */
    private void exit() {
        if(type != EDIT_MODE){
            /*
            Intent intent = new Intent(this,SpendingActivity.class);
            startActivity(intent);
            finish();*/
            setResult(RESULT_CANCELED, getIntent());
            finish();
        }else{
            this.setResult(RESULT_OK, getIntent());
            this.finish();
        }
    }

    /**
     * save event
     * @return
     */
    private String saveInfo() throws IOException {
        //Save之前先判断用户是否登录
        SharedPreferences sharedPreferences= getSharedPreferences("setting",Activity.MODE_PRIVATE);
        final String userID =sharedPreferences.getString("userID", "");

        Log.i("info", "The user logged in this time is" + userID);

        if(userID.isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Tips")
                    .setMessage("You have not logged in, please click ok button to log in!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK);
                            Intent intent=new Intent(ExpenseProcesActivity.this,LoginActivity.class);
                            ExpenseProcesActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    })
                    .show();
        }else{
            if(value.equals("") || value == null || Double.parseDouble(value) <= 0){
                Toast.makeText(getApplicationContext(), getString(R.string.input_message),
                        Toast.LENGTH_SHORT).show();
                return userID;
            }
            /*
            //调用DBOpenHelper
            DBOpenHelper helper = new DBOpenHelper(this,"qianbao.db",null,1);
            SQLiteDatabase db = helper.getWritableDatabase();
            //插入数据
            ContentValues values= new ContentValues();
            values.put("userID",userID);
            values.put("Type",type);
            values.put("incomeWay",txtAccount);
            values.put("category",txtBigCategory);
            values.put("cost", value);
            values.put("note", txtNote);
            values.put("makeDate",pubFun.format(calendar.getTime()));
            long rowid = db.insert("basic_tb",null,values);

            //Test
            Cursor c = db.query("basic_tb",null,"userID=?",new String[]{userID},null,null,null);
            if(c!=null && c.getCount() >= 1){
                String[] cols = c.getColumnNames();
                while(c.moveToNext()){
                    for(String ColumnName:cols){
                        Log.i("infoTest",ColumnName+":"+c.getString(c.getColumnIndex(ColumnName)));
                    }
                }
                c.close();
            }
            db.close();
            */


            date=pubFun.format(calendar.getTime());
            year=date.substring(0,4);
            month=date.substring(5,7);
            day=date.substring(8);
            //这里开始mysql
            final String url = "https://i.cs.hku.hk/~yxwang2/c7506/addExpense.php" +
                    ("?action=insert&userID=" + android.net.Uri.encode(userID, "UTF-8") +
                    "&type="+ android.net.Uri.encode(String.valueOf(type), "UTF-8")+
                    "&account="+android.net.Uri.encode(txtAccount, "UTF-8")+
                            "&category="+android.net.Uri.encode(txtBigCategory, "UTF-8")+
                            "&cost="+android.net.Uri.encode(value, "UTF-8")+
                            "&note="+android.net.Uri.encode(txtNote, "UTF-8")+
                            "&y="+android.net.Uri.encode(year, "UTF-8")+
                            "&m="+android.net.Uri.encode(month, "UTF-8")+
                            "&d="+android.net.Uri.encode(day, "UTF-8")+
                            "&makedate="+android.net.Uri.encode(pubFun.format(calendar.getTime()), "UTF-8")
                            );
            Log.i("url",url);
            AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                boolean success;
                String jsonString;

                @Override
                protected String doInBackground(String... arg0) {
                    // TODO Auto-generated method stub
                    success = true;
                    HttpURLConnection conn_object = null;
                    final int HTML_BUFFER_SIZE = 2*1024*1024;
                    char htmlBuffer[] = new char[HTML_BUFFER_SIZE];

                    try {
                        URL url_object = new URL(url);
                        conn_object = (HttpURLConnection) url_object.openConnection();
                        conn_object.setInstanceFollowRedirects(true);

                        BufferedReader reader_list = new BufferedReader(new InputStreamReader(conn_object.getInputStream()));
                        String HTMLSource = ReadBufferedHTML(reader_list, htmlBuffer, HTML_BUFFER_SIZE);
                        reader_list.close();
                    } catch (Exception e) {
                        return "Fail to login";
                    } finally {
                        // When HttpClient instance is no longer needed,
                        // shut down the connection manager to ensure
                        // immediate deallocation of all system resources
                        if (conn_object != null) {
                            conn_object.disconnect();
                        }
                    }
                    return null;
                }

            }.execute("");
            //结束mysql

            Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, getIntent());
            this.finish();

        }
        return userID;
    }
    public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException
    {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
            } else {
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }
}
