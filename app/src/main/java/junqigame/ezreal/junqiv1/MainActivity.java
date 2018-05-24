package junqigame.ezreal.junqiv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_pvp;
//    private Button btn_pve;
    private Button btn_setting;
    private Button btn_exit;
    private Button btn_pvp_in_bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_layout);
        btn_pvp=findViewById(R.id.btn_pvp);
//        btn_pve=findViewById(R.id.btn_pve);
        btn_setting=findViewById(R.id.btn_setting);
        btn_exit=findViewById(R.id.btn_exit);
        btn_pvp_in_bluetooth=findViewById(R.id.btn_pvp_in_bluetooth);

        //两人对战
        btn_pvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TwoManJunQiActivity.class);
                startActivity(intent);
            }
        });
        //蓝牙对战
        btn_pvp_in_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,BluetoothActivity.class);
                startActivity(intent);
            }
        });
//        //人机对战按钮
//        btn_pve.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        //游戏设置
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //退出游戏
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
