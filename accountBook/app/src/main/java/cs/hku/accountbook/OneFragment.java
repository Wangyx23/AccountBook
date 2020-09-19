package cs.hku.accountbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.hku.dao.DBOpenHelper;
import cs.hku.util.pubFun;

/**
 * @programName: OneFragment.java
 * @programFunction: Recording details of income and expenditure
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class OneFragment extends Fragment {
    public String month="";
    public String year="";
    public float income=0;
    public float cost=0;
    private OnFragmentInteractionListener mListener;
    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表

    //int[] image_expense = new int[]{R.mipmap.detail_income, R.mipmap.detail_payout }; //存储图片的数据
    int[] image_expense = new int[]{R.mipmap.ico_salary, R.mipmap.ico_investment,
            R.mipmap.ico_bonus, R.mipmap.ico_callback,
            R.mipmap.ico_interest, R.mipmap.ico_general,

            R.mipmap.ico_food, R.mipmap.ico_drinks,
            R.mipmap.ico_traffic, R.mipmap.ico_communications,
            R.mipmap.ico_leisure, R.mipmap.ico_learning,
            R.mipmap.ico_fruits, R.mipmap.ico_sports,
            R.mipmap.ico_clothing, R.mipmap.ico_film,R.mipmap.ico_general};
    HashMap<String,Integer> Cmap = new HashMap<>();
    //给map中添加元素
    private void Init_Cmap() {
        Cmap.put("Salary",0);Cmap.put("Investment",1);
        Cmap.put("Bonus",2);Cmap.put("Call-back Pay",3);
        Cmap.put("Interest",4);Cmap.put("Others",5);
        Cmap.put("Food",6);Cmap.put("Drinks",7);
        Cmap.put("Traffic",8);Cmap.put("Communications",9);
        Cmap.put("Leisure",10);Cmap.put("Learning",11);
        Cmap.put("Fruits",12);Cmap.put("Sports",13);
        Cmap.put("Clothing accessories",14);Cmap.put("Film",15);
        Cmap.put("Others",16);
    }

    /*
    int[] image_Outcategrory = new int[]{R.mipmap.ico_food, R.mipmap.ico_drinks,
            R.mipmap.ico_traffic, R.mipmap.ico_communications,
            R.mipmap.ico_leisure, R.mipmap.ico_learning,
            R.mipmap.ico_fruits, R.mipmap.ico_sports,
            R.mipmap.ico_clothing, R.mipmap.ico_film,R.mipmap.ico_general};
*/
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        listitem.clear();

        SimpleAdapter adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.fragment_one_item
                , new String[]{"expense_category", "date","expense_money", "image_expense"}
                , new int[]{R.id.tv_expense_category,R.id.tv_date, R.id.tv_expense_money, R.id.image_expense});
        // 第一个参数是上下文对象
        // 第二个是listitem
        // 第三个是指定每个列表项的布局文件
        // 第四个是指定Map对象中定义的两个键（这里通过字符串数组来指定）
        // 第五个是用于指定在布局文件中定义的id（也是用数组来指定）

        ListView listView = (ListView) v.findViewById(R.id.lv_expense);
        Log.i("rrrrrrrrr",month);
        getData();
        //System.out.println(listitem);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置监听器,点击显示类别名称
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), map.get("expense_category").toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Init_Cmap();
        if (v == null) {//
            v = inflater.inflate(R.layout.fragment_one, container, false);
        }
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeView(v);
        }
        Bundle arguments = this.getArguments();
        year = arguments.getString("year");
        month = arguments.getString("month");
        Log.i("ffffffffffff",month);
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(float income,float cost, String JSONString);
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
            JSONArray jsType = rootJSONObj.optJSONArray("Type");
            JSONArray jsCategory = rootJSONObj.optJSONArray("category");
            JSONArray jsCost = rootJSONObj.optJSONArray("cost");
            JSONArray jsNote = rootJSONObj.optJSONArray("note");
            JSONArray jsCurr = rootJSONObj.optJSONArray("currency");
            JSONArray jsRate = rootJSONObj.optJSONArray("rate");
            JSONArray jsDate = rootJSONObj.optJSONArray("makedate");
            Integer length=jsType.length();
            String money="";
            income=0;
            cost=0;
            for (int i=0; i<length; ++i) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image_expense", image_expense[Cmap.get(jsCategory.getString(i))]);
                map.put("expense_category",jsCategory.getString(i));
                map.put("date",jsDate.getString(i));
                money="";
                float rate=Float.parseFloat(jsRate.getString(i));
                float icost=Float.parseFloat(jsCost.getString(i));
                if(jsType.getString(i).equals("0")){
                    money=money+"+";
                    income=income+icost*rate;
                }else{
                    money=money+"-";
                    cost=cost+icost*rate;
                }
                if(jsCurr.getString(i).equals("HKD")){
                    money=money+"HK$";
                }else{
                    money=money+" ¥ ";
                }

                map.put("expense_money", money + jsCost.getString(i));
                listtem.add(map);

                }

            int t=JSONString.indexOf("}");
            String jsonInfo = JSONString.substring(0,t+1);
            System.out.println("================onFragmentInteraction");
            Log.e("eee",jsonInfo);
            mListener.onFragmentInteraction(income,cost, jsonInfo);
            System.out.println("=================onFragmentInteraction");

            return listtem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listtem;



    }
    /**
     * 从数据库获得适配器数据
     */
    private void getData(){
        //先判断用户是否登录
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        String userID =sharedPreferences.getString("userID", "");

        //Log.i("info", "The user logged in this time is" + userID);

        if(userID.isEmpty()){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Tips")
                    .setMessage("You have not logged in, please click ok button to log in!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            getActivity().setResult(-1);
                            Intent intent=new Intent(getActivity(),LoginActivity.class);
                            getActivity().startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    })
                    .show();
        }else{
            //MYSQL
            Log.d("mmm",month);
            final String url = "https://i.cs.hku.hk/~yxwang2/c7506/spending.php" +
                    ("?action=select&userID=" + android.net.Uri.encode(userID, "UTF-8") +
                            "&year="+ android.net.Uri.encode(year, "UTF-8")+
                            "&month="+android.net.Uri.encode(month, "UTF-8")
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
                        SimpleAdapter adapter = new SimpleAdapter(getActivity()
                                , listitem
                                , R.layout.fragment_one_item
                                , new String[]{"expense_category", "date","expense_money", "image_expense"}
                                , new int[]{R.id.tv_expense_category,R.id.tv_date, R.id.tv_expense_money, R.id.image_expense});

                        ListView listView = (ListView) v.findViewById(R.id.lv_expense);

                        listView.setAdapter(adapter);
                    } else {

                    }
                }
            }.execute("");
            //MYSQL

        }
    }
}

