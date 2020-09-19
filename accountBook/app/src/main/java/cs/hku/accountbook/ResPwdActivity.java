package cs.hku.accountbook;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cs.hku.dao.DBOpenHelper;
import cs.hku.util.pubFun;

/**
 * @programName: ResPwdActivity.java
 * @programFunction: the reset password page
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class ResPwdActivity extends AppCompatActivity {
    final Context context = this;
    private EditText editPhone;
    private EditText editPwd;
    private EditText editResPwd;
    private Button btnConfirm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.res_password);
        getSupportActionBar().hide();
        editPhone = (EditText) findViewById(R.id.editPhone);
        editPwd = (EditText) findViewById(R.id.editPwd);
        editResPwd = (EditText) findViewById(R.id.editResPwd);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
    }

    /**
     * confirm event
     * @param v
     */
    public void OnMyConfirmClick(View v) {
        confirmInfo();
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
     * confirm event
     */
    private void confirmInfo() {
        if(pubFun.isEmpty(editPhone.getText().toString()) || pubFun.isEmpty(editPwd.getText().toString()) || pubFun.isEmpty(editResPwd.getText().toString())){
            Toast.makeText(this, "phone number or password is not empty！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!editPwd.getText().toString().equals(editResPwd.getText().toString())){
            Toast.makeText(this, "second password is not correct！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "https://i.cs.hku.hk/~yxwang2/c7506/resPwd.php" +
                ("?&userID=" + android.net.Uri.encode(editPhone.getText().toString(), "UTF-8") +
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
                        Toast.makeText(context, "password res successful！", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        new AlertDialog.Builder(context)
                                .setTitle("tips")
                                .setMessage("Can not find user account，please register")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        setResult(RESULT_OK);
                                        Intent intent=new Intent(ResPwdActivity.this,RegistActivity.class);
                                        ResPwdActivity.this.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        return;
                                    }
                                })
                                .show();
                    }

                } else {

                }
            }
        }.execute("");
    }
}