package cs.hku.accountbook;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @programName: CurrencyActivity.java
 * @programFunction: the wish wall page
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class CurrencyActivity extends AppCompatActivity {
    //private static String[] accountList = new String [] {"Cash", "Deposit", "AliPay", "WeChat", "Ant Check Later (AliPay)", "Credit Card"};
    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
    private String rate="";
    private String o_rate="";
    final Context context = this;
    private Button bt_rate;

    //private Listview
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency);
        getSupportActionBar().hide();


    }
    @Override
    public void onResume() {
        super.onResume();
        listitem.clear();
        ListView listView = findViewById(R.id.lv_account);
        SimpleAdapter adapter = new SimpleAdapter(CurrencyActivity.this
                , listitem
                , R.layout.account_item
                , new String[]{"account", "balance"}
                , new int[]{R.id.tv_account,R.id.tv_balance});
        getData();
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        //listView.setOnClickListener(new MyListener());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置监听器,点击显示类别名称
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                //Toast.makeText(CurrencyActivity.this, map.get("balance").toString(), Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putString("account", map.get("account").toString());
                bundle.putString("balance", map.get("balance").toString());
                bundle.putString("rate", o_rate);
                Log.i("ii",o_rate);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(CurrencyActivity.this, AccountSetActivity.class);
                startActivity(intent);
            }
        });

        bt_rate=findViewById(R.id.bt_rate);



    }

    public void OnChangeRate(View v) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.rate_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editRate);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                rate=userInput.getText().toString();
                                DecimalFormat df = new DecimalFormat("0.0000");
                                rate=df.format(Float.parseFloat(rate));
                                if(rate!=o_rate){
                                    bt_rate.setText("1 HKD = "+rate+" CNY");
                                    updateRate(rate);
                                    o_rate=rate;
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException
    {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }
    public String getJsonPage(String url) {
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
            return HTMLSource;
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
    }
    public List<Map<String, Object>> parse_JSON_String_and_Switch_Activity(String JSONString) {
        List<Map<String, Object>> listtem = new ArrayList<Map<String, Object>>();
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            o_rate = rootJSONObj.getString("rate");
            Log.d("pp",o_rate);
            bt_rate.setText("1 HKD = "+o_rate+" CNY");
            JSONArray jsAcc = rootJSONObj.optJSONArray("account");
            JSONArray jsRemain = rootJSONObj.optJSONArray("remaining");
            JSONArray jsCurr = rootJSONObj.optJSONArray("currency");
            Integer length=jsAcc.length();
            String balance="";
            for (int i=0; i<length; ++i) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("account", jsAcc.getString(i));
                balance="";
                if(jsCurr.getString(i).equals("HKD")){
                    balance=balance+"HK$";
                }else{
                    balance=balance+" ¥ ";
                }
                map.put("balance",balance+jsRemain.getString(i));
                listtem.add(map);
            }
            return listtem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listtem;



    }
    private void updateRate(String rate){
        SharedPreferences sharedPreferences= getSharedPreferences("setting",Activity.MODE_PRIVATE);
        final String userID =sharedPreferences.getString("userID", "");
        final String url = "https://i.cs.hku.hk/~yxwang2/c7506/rate.php" +
                ("?action=update&userID=" + android.net.Uri.encode(userID, "UTF-8") +
                        "&rate="+ android.net.Uri.encode(rate, "UTF-8")
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
                    if (conn_object != null) {
                        conn_object.disconnect();
                    }
                }
                return null;
            }

        }.execute("");
    }
    private void getData(){
        //先判断用户是否登录
        SharedPreferences sharedPreferences= getSharedPreferences("setting",Activity.MODE_PRIVATE);
        final String userID =sharedPreferences.getString("userID", "");

        //Log.i("info", "The user logged in this time is" + userID);

        if(userID.isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Tips")
                    .setMessage("You have not logged in, please click ok button to log in!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK);
                            Intent intent=new Intent(CurrencyActivity.this,LoginActivity.class);
                            CurrencyActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    })
                    .show();
        }else{
            //MYSQL开始
            final String url = "https://i.cs.hku.hk/~yxwang2/c7506/account.php" +
                    ("?action=select&userID=" + android.net.Uri.encode(userID, "UTF-8")
                    );
            AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                boolean success;
                String jsonString;

                @Override
                protected String doInBackground(String... arg0) {
                    // TODO Auto-generated method stub
                    success = true;
                    Log.d("url",url);
                    jsonString = getJsonPage(url);
                    //Log.d("json",jsonString);
                    if (jsonString.equals("Fail to login"))
                        success = false;
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (success) {
                        listitem=parse_JSON_String_and_Switch_Activity(jsonString);
                        System.out.println(listitem);

                        SimpleAdapter adapter = new SimpleAdapter(CurrencyActivity.this
                                , listitem
                                , R.layout.account_item
                                , new String[]{"account", "balance"}
                                , new int[]{R.id.tv_account,R.id.tv_balance});

                        ListView listView = findViewById(R.id.lv_account);
                        listView.setAdapter(adapter);


                    } else {

                    }
                }
            }.execute("");
            //MYSQL结束

        }
    }
}
