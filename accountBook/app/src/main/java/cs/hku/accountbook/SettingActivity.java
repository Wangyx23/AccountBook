package cs.hku.accountbook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @programName: SettingActivity.java
 * @programFunction: the setting page
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class SettingActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.setting);
    }
}
