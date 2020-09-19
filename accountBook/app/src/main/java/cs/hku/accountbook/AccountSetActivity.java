package cs.hku.accountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class AccountSetActivity extends AppCompatActivity {
    final Context context = this;
    private TextView tv_acc=null;
    private TextView tv_hkd=null;
    private Button money_btn = null;
    private String  value="0";
    private String account="";
    private String currency="";
    private String o_currency="";
    private String rate="";
    private androidx.appcompat.app.AlertDialog alertDialog_Currency;//点击记一笔按钮时弹出提示框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_set);
        getSupportActionBar().hide();
        /*
        btn_money
                btn_save
        btn_cancel
        */
        money_btn=findViewById(R.id.btn_money);


        Bundle bundle=getIntent().getExtras();
        account=bundle.getString("account");
        rate=bundle.getString("rate");
        Log.i("ii",rate);
        String balance=bundle.getString("balance");
        Log.i("balance",balance);
        tv_acc=findViewById((R.id.tv_acc));
        tv_acc.setText(account);
        Log.i("ii",account);
        value=balance.substring(3);
        money_btn.setText(value);
        tv_hkd=findViewById(R.id.tv_hkd);
        if (balance.substring(0,2).equals("HK")){
            o_currency="HKD";
        }else{
            o_currency="CNY";
        }
        Log.i("ii",o_currency);
        tv_hkd.setText(o_currency);
        money_btn.setOnClickListener(new MyListener());
        tv_hkd.setOnClickListener(new MyListener());

    }
    public class MyListener implements View.OnClickListener {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(v.getId() == R.id.tv_hkd) {
                final String[] items = {"HKD", "CNY"};
                androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
                alertBuilder.setTitle("Please select the currency type");
                alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(SpendingActivity.this, items[i], Toast.LENGTH_SHORT).show();
                        tv_hkd.setText(items[i]);
                        currency=items[i];
                        if(o_currency!=currency){
                            if(currency=="HKD"){
                                Log.i("ii",rate);
                                float v=Float.parseFloat(value)/Float.parseFloat(rate);
                                DecimalFormat df = new DecimalFormat("0.00");
                                value=df.format(v);
                                Log.d("dd",value);
                                money_btn.setText(value);
                            }else{
                                float v=Float.parseFloat(value)*Float.parseFloat(rate);
                                DecimalFormat df = new DecimalFormat("0.00");
                                value=df.format(v);
                                money_btn.setText(value);
                            }
                        }

                        alertDialog_Currency.dismiss();
                    }
                });
                alertDialog_Currency = alertBuilder.create();
                alertDialog_Currency.show();
            } else if(v.getId() == R.id.btn_money) {
                Intent i = new Intent(AccountSetActivity.this, KeyPad.class);
                i.putExtra("value", value);
                startActivityForResult(i, 0);
            }
        }
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
            money_btn.setText(df.format(Float.parseFloat(value)));
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
        this.setResult(RESULT_CANCELED, getIntent());
        this.finish();
    }

    /**
     * save event
     * @return
     */
    private String saveInfo() throws IOException {
        //Save之前先判断用户是否登录
        SharedPreferences sharedPreferences= getSharedPreferences("setting", Activity.MODE_PRIVATE);
        final String userID =sharedPreferences.getString("userID", "");

        Log.i("info", "The user logged in this time is" + userID);

        if(userID.isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Tips")
                    .setMessage("You have not logged in, please click ok button to log in!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK);
                            Intent intent=new Intent(AccountSetActivity.this,LoginActivity.class);
                            AccountSetActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    })
                    .show();
        }else{
            if(value.equals("") || value == null){
                Toast.makeText(getApplicationContext(), getString(R.string.input_message),
                        Toast.LENGTH_SHORT).show();
                return userID;
            }

            //这里开始mysql
            //https://i.cs.hku.hk/~yxwang2/c7506/account.php?action=update&userID=12323231128&account=Cash&remain=980&currency=CNY
            final String url = "https://i.cs.hku.hk/~yxwang2/c7506/account.php" +
                    ("?action=update&userID=" + android.net.Uri.encode(userID, "UTF-8") +
                            "&account="+android.net.Uri.encode(account, "UTF-8")+
                            "&remain="+android.net.Uri.encode(value, "UTF-8")+
                            "&currency="+android.net.Uri.encode(currency, "UTF-8")
                    );
            //Log.i("url",url);
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
