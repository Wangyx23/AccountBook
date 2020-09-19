package cs.hku.accountbook;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @programName: AboutUsActivity.java
 * @programFunction: About Us page
 * @createDate: 2020/04/28
 * @author: HKUCS COMP7506 group 8
 * @version:
 * xx.   yyyy/mm/dd   ver    author                     comments
 * 01.   2020/04/28   1.00   HKUCS COMP7506 group 8   New Create
 */
public class AboutUsActivity extends AppCompatActivity {
    private TextView tvAboutUs;
    private TextView tvIntroduce;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        getSupportActionBar().hide();
    }
}

