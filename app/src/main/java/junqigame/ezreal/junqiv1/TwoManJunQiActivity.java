package junqigame.ezreal.junqiv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;

import junqigame.ezreal.junqiv1.junqi.JunQiView;

public class TwoManJunQiActivity extends AppCompatActivity {

    private JunQiView junQiView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.junqi_game_layout);
        junQiView=findViewById(R.id.junqi_view);
        final Button btn_menu=findViewById(R.id.junqi_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(btn_menu);
            }
        });
    }
    //弹出菜单设置
    private void showPopupMenu(View view){
        PopupMenu menu=new PopupMenu(this,view);
        //menu布局
        menu.getMenuInflater().inflate(R.menu.pvp_in_one_game_menu,menu.getMenu());
        //为menu的item设置事件
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.restart://重新开局
                       junQiView.restartGame();

                        break;
                    case R.id.return_fore_step:
                        break;
                    case R.id.statistic:
                        break;
                    case R.id.exit:
                        finish();//返回主菜单
                        break;

                }
                return true;
            }
        });
        //PopupMenu关闭事件
        menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        menu.show();
    }

}
