package cs.hku.accountbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.hku.dao.DBOpenHelper;
import cs.hku.util.pubFun;

/**
 * @programName: RegistActivity.java
 * @programFunction: the regiter page
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class RegistActivity extends AppCompatActivity {
    final Context context = this;
    private EditText editPhone;
    private EditText editPwd;
    private Button btnRegist;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        getSupportActionBar().hide();
        editPhone = (EditText) findViewById(R.id.editPhone);
        editPwd = (EditText) findViewById(R.id.editPwd);
        btnRegist = (Button) findViewById(R.id.btnRegist);
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
    public String parse_JSON_String_and_Switch_Activity(String JSONString) {
        String state="";
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            state = rootJSONObj.getString("s");

        } catch (JSONException e) {
            state="error";
            e.printStackTrace();
        }
        return state;
    }
    /**
     * register event
     * @param v
     */
    public void OnMyRegistClick(View v)
    {
        boolean isTrue = true;
        if(pubFun.isPhoneNumberValid(editPhone.getText().toString()) == false){
            isTrue = false;
            Toast.makeText(this, "phone number is wrong format！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pubFun.isEmpty(editPwd.getText().toString())){
            isTrue = false;
            Toast.makeText(this, "password can not be empty！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isTrue = true){
            //这里开始mysql
            //http://i.cs.hku.hk/~yxwang2/c7506/register.php?action=insert&userID=11122233344&pwd=123
            final String url = "https://i.cs.hku.hk/~yxwang2/c7506/register.php" +
                    ("?action=insert&userID=" + android.net.Uri.encode(editPhone.getText().toString(), "UTF-8") +
                            "&pwd="+ android.net.Uri.encode(editPwd.getText().toString(), "UTF-8")
                    );
            Log.i("url",url);
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
                        String state=parse_JSON_String_and_Switch_Activity(jsonString);
                        if(state.equals("success")){
                            Toast.makeText(context, "regist successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(context, "users has exit ", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                    }
                }
            }.execute("");
            //结束mysql
        }else{
            return;
        }
    }

}
