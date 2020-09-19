package cs.hku.accountbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
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

/**
 * @programName: TwoFragment.java
 * @programFunction: Display of category reports
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class TwoFragment extends Fragment {

    protected boolean mHasLoadedOnce;
    protected boolean mIsvisible;

    boolean mIsdataprepared = false;
    //private TwoFragment.OnFragmentInteractionListener mListenertwo;
    //private OnFragmentInteractionListener mListenertwo;
    public String inputInfo="";

    double[] values = {500.00, 800.00, 1000.00, 900.00};
    double sumVal = values[0] + values[1] + values[2] + values[3];
    int[] colors = {};

    double[] allvalues = {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00};
    double allsumVal = 0.00;
    int[] allcolors = {Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.YELLOW,  Color.GRAY, Color.GREEN, Color.LTGRAY,  Color.WHITE,  Color.DKGRAY, Color.TRANSPARENT};
    int actual_expenseType = 0;

    private LinearLayout ll_expense_piechart;
    private GraphicalView graphicalView;
    private SpendingActivity activity;


    public TwoFragment() {
        // Required empty public constructor
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListenertwo = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListenertwo = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String infoString);
    }*/


    public String month="";
    public String year="";

    public float cost=0;

    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表

    HashMap<String, Integer> Cmap = new HashMap<>();
    HashMap<Integer, String> TypeMap = new HashMap<>();

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

        TypeMap.put(0,"Food");TypeMap.put( 1,"Drinks");
        TypeMap.put(2,"Traffic");TypeMap.put(3,"Communications");
        TypeMap.put(4,"Leisure");TypeMap.put(5,"Learning");
        TypeMap.put(6,"Fruits");TypeMap.put(7,"Sports");
        TypeMap.put(8,"Clothing accessories");TypeMap.put(9,"Film");
        TypeMap.put(10, "Others");
    }


    /*@Override
    public void onResume() {
        super.onResume();
        Log.v("onResume","ooooooooooooooooo");
        System.out.println("===>start===>");
        System.out.println(inputInfo);
        System.out.println("===>start====>!");

        Log.i("inputinfo",inputInfo);
        initPieChart(v);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_two, container, false);
        Init_Cmap();

        //get String from spendingactivity
        Bundle arguments = this.getArguments();
        if(arguments != null)
            inputInfo = arguments.getString("DATA");

        Log.i("inputinfo",inputInfo);

        analysisData(inputInfo);
        initPieChart(v,1);

        return v;
    }

    /**
     * 生成饼图
     */
    private void initPieChart(View v){
        CategorySeries dataset = buildCategoryDataset("PieChart", values, 4);
        DefaultRenderer renderer = buildCategoryRenderer(colors, 4);

        ll_expense_piechart = (LinearLayout) v.findViewById(R.id.ll_expense_piechart);
        ll_expense_piechart.removeAllViews();

        graphicalView = ChartFactory.getPieChartView(getContext()
                ,dataset, renderer);//饼状图
        graphicalView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        ll_expense_piechart.addView(graphicalView);
    }
    /**
     * 生成饼图,从mysql的数据更新，mysql数据由onefragment获取，然后传给spendingactivity，最终传给twofragment    */
    private void initPieChart(View v, int flag){

        CategorySeries dataset = buildCategoryDataset("PieChart", allvalues, 11);
        DefaultRenderer renderer = buildCategoryRenderer(allcolors, actual_expenseType);

        ll_expense_piechart = (LinearLayout) v.findViewById(R.id.ll_expense_piechart);
        ll_expense_piechart.removeAllViews();

        graphicalView = ChartFactory.getPieChartView(getContext()
                ,dataset, renderer);//饼状图
        graphicalView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        ll_expense_piechart.addView(graphicalView);
    }


    /**
     * 构建数据集
     * @param title
     * @param values
     * @return
     */
    protected CategorySeries buildCategoryDataset(String title, double[] values, int len) {
        CategorySeries series = new CategorySeries(title);
        float sumcost = (float) allsumVal;
        if (sumcost<=0.0) {
            for (int i = 0; i < len; i++) {
                sumcost += values[i];
            }
        }

        actual_expenseType = 0;
        for (int i = 0; i < len; i++){
            if (values[i]>0.0) {
                actual_expenseType++;
                double val =  Math.round(values[i]/allsumVal*100)/100.0;
                series.add(TypeMap.get(i) + ":" + val, val);
            }
        }

        //Log.i("getdata !!!!!!!!!!!!!!:",month);
        //System.out.println(values[0]);
        return series;
    }

    /**
     * 构建渲染器
     * @param colors
     * @return
     */
    protected DefaultRenderer buildCategoryRenderer(int[] colors, int len){
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setLegendTextSize(35);//设置左下角标注文字的大小
        renderer.setLabelsTextSize(25);//饼图上标记文字的字体大小
        renderer.setLabelsColor(Color.BLACK);//饼图上标记文字的颜色
        renderer.setPanEnabled(false);
        //renderer.setDisplayValues(true);//显示数据


        for(int i=0; i< len; i++){
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            //设置百分比
            //r.setChartValuesFormat(NumberFormat.getPercentInstance());
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    //similar with one Fragment
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
        List<Map<String, Object>> listCate = new ArrayList<Map<String, Object>>();
        try {
            Log.i("iiiiiiiiiiiii",month);
            Map<String, Object> map = new HashMap<String, Object>();
            JSONObject rootJSONObj = new JSONObject(JSONString);
            JSONArray jsType = rootJSONObj.optJSONArray("Type");
            JSONArray jsCategory = rootJSONObj.optJSONArray("category");
            JSONArray jsCost = rootJSONObj.optJSONArray("cost");
            JSONArray jsNote = rootJSONObj.optJSONArray("note");
            JSONArray jsCurr = rootJSONObj.optJSONArray("currency");
            JSONArray jsRate = rootJSONObj.optJSONArray("rate");
            JSONArray jsDate = rootJSONObj.optJSONArray("date");
            Integer length=jsType.length();
            String money="";

            cost=0;
            //init
            for(int i=0; i< 11;i++) {
                allvalues[i]=0;
            }

            for (int i=0; i<length; ++i) {

                String expense_category = jsCategory.getString(i);
                float rate=Float.parseFloat(jsRate.getString(i));
                float icost=Float.parseFloat(jsCost.getString(i));


                if(jsType.getString(i).equals("1")){
                    cost=cost+icost*rate;
                    money="";
                    money = money+icost*rate;

                    //categories expense
                    int costtypeid = 0;
                    costtypeid = Cmap.get(expense_category) -6;
                    //Log.i("costtypeid:","costtypeid");
                    //System.out.println(costtypeid);

                    if (costtypeid>=0 && costtypeid< 11) {

                        allvalues[costtypeid] += icost*rate;
                        //allsumVal +=cost;
                        listCate.add(map);
                    }
                }

            }

            allsumVal = cost;
            Log.i("allsumVal:","allsumVal");
            System.out.println(allsumVal);
            Log.i("allsumVal:","allsumVal");
            Log.i("allvalues[0]:","allvalues[0]");
            Log.i("category:","category");
            System.out.println(listCate);

            //处理values
            for(int i=0; i< 11;i++) {
                allvalues[i] = Math.round(allvalues[i]*100)/100.0;
                map.put("expense_category", TypeMap.get(i));
                map.put("expense_money", allvalues[i]);
                listCate.add(map);

                Log.i("value[]:","");
                System.out.println(allvalues[i]);
            }

            return listCate;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listCate;

    }

    /**
     * 生成饼图
     */
    private void analysisData(String inputInfo){

        //先判断用户是否登录
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", "");

        mIsdataprepared = false;

        //Log.i("info", "The user logged in this time is" + userID);


        if (userID.isEmpty()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Tips")
                    .setMessage("You have not logged in, please click ok button to log in!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            getActivity().setResult(-1);
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    })
                    .show();
        } else {

            listitem = parse_JSON_String_and_Switch_Activity(inputInfo);

            //System.out.println(listitem);
            mIsdataprepared = true;
        }
    }


}

