package cs.hku.accountbook;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cs.hku.util.pubFun;

/**
 * @programName: SpendingActivity.java
 * @programFunction: Recording of income and expenditure
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class SpendingActivity extends AppCompatActivity implements View.OnClickListener, OneFragment.OnFragmentInteractionListener{
    //private TextView title;
    private TextView tv_year;
    public TextView textIncome;
    public TextView textCost;
    private TextView item_detail, item_category_report;
    private Button btnAddRecord;
    private ViewPager vp;
    private OneFragment oneFragment;
    private TwoFragment twoFragment;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private List<Fragment> mFragmentList_forupdata = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    private Spinner month_spinner;
    private ArrayAdapter<String> month_adapter;

    private AlertDialog alertDialog_AddRecord;//点击记一笔按钮时弹出提示框

    String[] titles = new String[]{"Details", "Type category"};
    private static final String[] yearList = {String.valueOf(pubFun.getTime("Y"))};
    private static final String[] monthList = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    public String month = "";
    public String year = "";
    public String infoString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除工具栏
        getSupportActionBar().hide();
        setContentView(R.layout.spending);

        initViews();
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList);
        initSpinner();

        //mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        //vp.setOffscreenPageLimit(2);//ViewPager的缓存为2帧
        //vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);//初始设置ViewPager选中第一帧
        item_detail.setTextColor(Color.parseColor("#1ba0e1"));

        //ViewPager的监听事件
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*此方法在页面被选中时调用*/
                //title.setText(titles[position]);
                changeTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /*此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
                arg0==1的时辰默示正在滑动，
                arg0==2的时辰默示滑动完毕了，
                arg0==0的时辰默示什么都没做。*/
            }
        });
    }

    /**
     * 初始化布局View
     */
    private void initViews() {
        //title = (TextView) findViewById(R.id.title);
        tv_year = (TextView) findViewById(R.id.tv_year);
        tv_year.setText(yearList[0]);
        textIncome = (TextView) findViewById(R.id.tv_income_money);
        textCost = (TextView) findViewById(R.id.tv_outlay_money);
        btnAddRecord = (Button) findViewById(R.id.btnAddRecord);

        item_detail = (TextView) findViewById(R.id.item_detail);
        item_category_report = (TextView) findViewById(R.id.item_category_report);

        item_detail.setOnClickListener(this);
        item_category_report.setOnClickListener(this);

        vp = (ViewPager) findViewById(R.id.mainViewPager);

        oneFragment = new OneFragment();
        Bundle bundle = new Bundle();
        bundle.putString("year", yearList[0]);
        bundle.putString("month", month);
        oneFragment.setArguments(bundle);
        float a = oneFragment.cost;
        Log.d("cost", String.valueOf(a));
        twoFragment = new TwoFragment();

        //给FragmentList添加数据
        mFragmentList.add(oneFragment);
        mFragmentList.add(twoFragment);

    }

    /**
     * 初始化spinner
     */
    private void initSpinner() {
        month_spinner = (Spinner) findViewById(R.id.month_spinner);
        //将可选内容与ArrayAdapter连接起来
        month_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, monthList);
        //设置下拉列表的风格
        month_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter添加到spinner中
        month_spinner.setAdapter(month_adapter);
        //添加事件Spinner事件监听

        month_spinner
                .setOnItemSelectedListener(new month_spinnerSelectedListener());
        //设置默认值
        month_spinner.setSelection(pubFun.getTime("M"), true);
        month_spinner.setVisibility(View.VISIBLE);
        month = (String) month_spinner.getSelectedItem();
    }

    /**
     * 选择 月份 事件 监听器
     */
    class month_spinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            int pos = month_spinner.getSelectedItemPosition();
            month = (String) month_spinner.getSelectedItem();

            //mFragmentAdapter.notifyDataSetChanged();

            oneFragment = new OneFragment();
            Bundle bundle = new Bundle();
            bundle.putString("year", yearList[0]);
            bundle.putString("month", month);
            Log.i("mmmmmmmmmmmmonth", month);
            oneFragment.setArguments(bundle);
            List<Fragment> nFragmentList = new ArrayList<Fragment>();
            twoFragment = new TwoFragment();

            //给FragmentList添加数据
            nFragmentList.add(oneFragment);
            nFragmentList.add(twoFragment);

            mFragmentAdapter.setFragments(nFragmentList);
            vp.setOffscreenPageLimit(2);//ViewPager的缓存为2帧
            vp.setAdapter(mFragmentAdapter);
            mFragmentAdapter.notifyDataSetChanged();

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    /**
     * 点击头部Text 动态修改ViewPager的内容
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_detail:
                vp.setCurrentItem(0, true);
                break;
            case R.id.item_category_report:
                vp.setCurrentItem(1, true);
                if(infoString!="") {
                    //System.out.println("one fra===>sa");
                    //System.out.println(infoString);
                    System.out.println("one fra===>sa!");

                    List<Fragment> nFragmentList = new ArrayList<Fragment>();
                    oneFragment = new OneFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("year", yearList[0]);
                    bundle.putString("month", month);
                    Log.i("mmmmmmmmmmmmonth", month);

                    oneFragment.setArguments(bundle);
                    twoFragment = new TwoFragment();
                    Bundle bundletwo = new Bundle();

                    Log.d("dd",infoString);
                    //System.out.println("===>two!");
                    bundletwo.putString("DATA", infoString);
                    twoFragment.setArguments(bundletwo);

                    //给FragmentList添加数据
                    //nFragmentList.add(oneFragment);
                    nFragmentList.add(oneFragment);
                    nFragmentList.add(twoFragment);

                    mFragmentAdapter.setFragments(nFragmentList);
                    vp.setOffscreenPageLimit(2);//ViewPager的缓存为2帧
                    vp.setAdapter(mFragmentAdapter);
                    mFragmentAdapter.notifyDataSetChanged();
                    vp.setCurrentItem(1, true);

                }
                else {
                    System.out.println("one fra===>sa empty");
                    System.out.println("one fra===>sa empty!");
                }
                break;
        }
    }


    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        private FragmentManager fm;

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fm = fm;
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }


        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void setFragments(List<Fragment> fragments) {
            if (this.fragmentList != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.fragmentList) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            this.fragmentList = fragments;
            notifyDataSetChanged();
        }

    }

    /**
     * 由ViewPager的滑动修改头部导航Text的颜色
     *
     * @param position
     */
    private void changeTextColor(int position) {
        if (position == 0) {
            item_detail.setTextColor(Color.parseColor("#1ba0e1"));
            item_category_report.setTextColor(Color.parseColor("#000000"));
            /*
            gone：表示不可见并且不占用空间
            visible：表示可见
            invisible：表示不可见但是占用空间
             */
            btnAddRecord.setVisibility(View.VISIBLE);
        } else if (position == 1) {
            item_category_report.setTextColor(Color.parseColor("#1ba0e1"));
            item_detail.setTextColor(Color.parseColor("#000000"));
            btnAddRecord.setVisibility(View.GONE);
        }
    }

    /**
     * Add an income and expense record
     *
     * @param v
     */
    public void OnAddRecordClick(View v) {
        final String[] items = {"Income", "Cost"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Please choose the type");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(SpendingActivity.this, items[i], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SpendingActivity.this, ExpenseProcesActivity.class);
                intent.putExtra("strType", i);
                //SpendingActivity.this.startActivity(intent);
                SpendingActivity.this.startActivityForResult(intent, 23);
                alertDialog_AddRecord.dismiss();
            }
        });
        alertDialog_AddRecord = alertBuilder.create();
        alertDialog_AddRecord.show();
    }

    @Override
    public void onFragmentInteraction(float income, float cost, String jsonString) {

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String strIncome = "¥ "+decimalFormat.format(income);
        String strCost = "¥ "+decimalFormat.format(cost);
        textCost.setText(strCost);
        textIncome.setText(strIncome);
        infoString = jsonString;

    }

}
