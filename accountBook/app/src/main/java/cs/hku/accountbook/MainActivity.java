package cs.hku.accountbook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import cs.hku.accountbook.CircleMenuLayout.OnMenuItemClickListener;



/**
 * @programName: MainActivity.java
 * @programFunction: the home page
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class MainActivity extends AppCompatActivity {

    //第一次点击事件发生的时间
    private long mExitTime;

    private CircleMenuLayout mCircleMenuLayout;

    private String[] mItemTexts = new String[] { "login&register", "about us", "currency option", "income&cost", "add new one" };
    //private String[] mItemTexts = new String[] { "登录&注册", "关于我们", "币种选择",
      //      "更换背景", "收入&支出", "统计"  };
    private int[] mItemImgs = new int[] { R.mipmap.home_mbank_1_normal,
            R.mipmap.home_mbank_2_normal, R.mipmap.home_mbank_3_normal,
             R.mipmap.home_mbank_5_normal};
    //private int[] mItemImgs = new int[] { R.mipmap.home_mbank_1_normal,
      //      R.mipmap.home_mbank_2_normal, R.mipmap.home_mbank_3_normal,
        //    R.mipmap.home_mbank_4_normal, R.mipmap.home_mbank_5_normal, R.mipmap.home_mbank_6_normal };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
        mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);

        mCircleMenuLayout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public void itemClick(View view, int pos) {
                //if (mItemTexts[pos] == "change bg") {
                //    openSettingWind(view);
                //} else
                if (mItemTexts[pos] == "income&cost") {
                    openSpendingWind(view);
                } else if (mItemTexts[pos] == "login&register") {
                    openLoginWind(view);
                }
                //else if (mItemImgs[pos] == "add new one") {
                  //  openCountWind(view);}
                 else if (mItemTexts[pos] == "about us") {
                    openAboutUsAddWind(view);
                } else if (mItemTexts[pos] == "currency option") {
                     openCurrencyWind(view);
                }
                ;
            }

            public void itemCenterClick(View view) {
                //Toast.makeText(MainActivity.this, "you can do something just like login or register", Toast.LENGTH_SHORT).show();
                OnAddRecordClick(view);
                //OnAddRecordWind(view);
            }
        });
    }

    /**
     * 跳转至登录&注册界面
     * @param v
     */
    private void openLoginWind(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        this.startActivity(intent);
    }

    /**
     * 跳转至开销界面，记录收入与支出
     * @param v
     */
    private void openSpendingWind(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SpendingActivity.class);
        this.startActivity(intent);
    }
    /**
     * 跳转至记账界面
     * @param v
     */
   // private void OnAddRecordWind(View v){
     //   Intent intent=new Intent();
       // intent.setClass(MainActivity.this, AddRecordActivity.class);
        //this.startActivity(intent);
    public void OnAddRecordClick(View v){
        final String[] items = {"Income", "Cost"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Please choose the type");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(SpendingActivity.this, items[i], Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,ExpenseProcesActivity.class);
                intent.putExtra("strType", i);
                MainActivity.this.startActivity(intent);
                DialogInterface alertDialog_AddRecord;
                //alertDialog_AddRecord.dismiss();
            }
        });
        AlertDialog alertDialog_AddRecord = alertBuilder.create();
        alertDialog_AddRecord.show();
    }

    //}
    /**
     * 跳转至统计界面
     * @param v
     */
    private void openCountWind(View v){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this, CountActivity.class);
        this.startActivity(intent);
    }

    /**
     * 跳转至更换背景界面
     * @param
     */
    private void openSettingWind(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingActivity.class);
        this.startActivity(intent);
    }

    /**
     * 跳转至币种选择界面
     * @param v
     */
    private void openCurrencyWind(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, CurrencyActivity.class);
        this.startActivity(intent);
    }

    /**
     * 跳转至关于我们界面
     * @param v
     */
    private void openAboutUsAddWind(View v){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this, AboutUsActivity.class);
        this.startActivity(intent);
    }

    /**
     * 点击两次返回退出app
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                //System.currentTimeMillis()系统当前时间
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
