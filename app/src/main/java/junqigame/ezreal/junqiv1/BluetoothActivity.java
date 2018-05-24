package junqigame.ezreal.junqiv1;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import junqigame.ezreal.junqiv1.Bluetooth.BlueToothController;
import junqigame.ezreal.junqiv1.Bluetooth.Constant;
import junqigame.ezreal.junqiv1.Bluetooth.DeviceAdapter;
import junqigame.ezreal.junqiv1.Bluetooth.PlayerVsController;
import junqigame.ezreal.junqiv1.junqi.JunQiMap;
import junqigame.ezreal.junqiv1.junqi.JunQiView;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    final static int MY_PERMISSION_REQUEST_CONSTANT = 1;
    //startActivityForResult()的请求码
    public static final int REQUEST_CODE = 0;

    private BlueToothController mblueToothController;
    private List<BluetoothDevice> mfoundDevice;
    private DeviceAdapter mdeviceAdapter;

    private IntentFilter mintent;
    private BlueToothBroadCast mbroadcast;

    private Button btn_build_game, btn_find_game, btn_stop;
    private Button btn_popup_menu;
    private TextView tv_tip;
    private ProgressBar pb_wait;
    private ListView lv_device_list;
    private RelativeLayout bluetoothLayout, gameLayout;
    private JunQiView gameView;
    private Thread gamePlay;

//处理子线程传来的消息
    /**
     * 在此处处理子线程传来的消息
     */
    private MyHandler mhandler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_START_LISTENING://开始监听
                    pb_wait.setVisibility(View.VISIBLE);
                    tv_tip.setText("等待玩家加入");
                    break;
                case Constant.MSG_FINISH_LISTENING://监听结束

                    break;
                case Constant.MSG_GOT_DATA://获取到对方传入信息
                    byte[] data = (byte[]) msg.obj;
                    String str = PlayerVsController.getInstance().decodeMessage(data);
                    String msgType = getGameMsgType(str);
                    if (msgType.equals(PlayerVsController.MSG_PLAYER_MOVE)){
                        Rect  move = getGameMoveMsg(str);
                        gameView.setBluetoothPlayerMove(move);

                    }
                    else if(msgType.equals(PlayerVsController.MSG_MAP_INFO)){
                        //得到棋盘后才可以开始游戏
                        byte[][] map=getGameMapMsg(str);
                        gameView.getJunQiMap().initPieceInMap(map);
                        gameView.gameStart();
                        Toast.makeText(BluetoothActivity.this, "对局开始！", Toast.LENGTH_SHORT).show();

                        gamePlay.start();
                    }
                    break;
                case Constant.MSG_ERROR:

                    break;
                case Constant.MSG_CONNECTED_TO_SERVER://寻找到并进入对局
                    //后手下棋
                    gameView.setMyself(false);
                    gameView.getMyself().setPlayerName("我方");
                    gameView.getRivalPlayer().setPlayerName("敌方");
                    enterGame();

                    break;
                case Constant.MSG_GOT_A_CLINET://有玩家进入对局
                    pb_wait.setVisibility(View.GONE);
                    tv_tip.setText("有玩家加入");
                    gameView.initGame();
                    gameView.setMyself(true);//先手下棋
                    gameView.getMyself().setPlayerName("我方");
                    gameView.getRivalPlayer().setPlayerName("敌方");
                    //发送棋盘
                    PlayerVsController.getInstance().sendGameMessage(gameView.getJunQiMap().getPieceInMap());
                    enterGame();



                    gamePlay.start();
                    Toast.makeText(BluetoothActivity.this, "对局开始！", Toast.LENGTH_SHORT).show();
                    gameView.gameStart();
                    break;
            }
        }
    }


    public void enterGame() {
        bluetoothLayout.setVisibility(View.GONE);
        gameLayout.setVisibility(View.VISIBLE);
    }

    private AdapterView.OnItemClickListener deviceListenter = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = mfoundDevice.get(position);
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//未匹配过的设备需要匹配

                device.createBond();
            }
            //加入对局操作
            PlayerVsController.getInstance().connectToGame(device, mblueToothController.getAdapter(), mhandler);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buletooth);
        init();
        //打开蓝牙
        mblueToothController.turnOnBlueTooth(this, REQUEST_CODE);
        //打开网络定位
        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CONSTANT);
        }


    }

    //变量初始化
    private void init() {
        //初始化变量
        mblueToothController = new BlueToothController();
        mfoundDevice = new ArrayList<>();
        mdeviceAdapter = new DeviceAdapter(mfoundDevice, this);


        btn_build_game = findViewById(R.id.btn_build_game);
        btn_find_game = findViewById(R.id.btn_find_game);
        //设置点击事件
        btn_find_game.setOnClickListener(this);
        btn_build_game.setOnClickListener(this);

        btn_stop = findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);

        btn_popup_menu=findViewById(R.id.bluetooth_junqi_menu);
        btn_popup_menu.setOnClickListener(this);

        bluetoothLayout = findViewById(R.id.bluetooth_connect);
        gameLayout = findViewById(R.id.bluetooth_game);

        tv_tip = findViewById(R.id.tv_tip);
        pb_wait = findViewById(R.id.pb_wait);

        lv_device_list = findViewById(R.id.device_list);
        lv_device_list.setAdapter(mdeviceAdapter);
        lv_device_list.setOnItemClickListener(deviceListenter);

        gameView = findViewById(R.id.bluetooth_junqi_view);
        gameView.setGmaeMode(JunQiView.PVP_IN_BLUETOOTH);

        mbroadcast = new BlueToothBroadCast();
        mintent = new IntentFilter();
        mintent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mintent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mintent.addAction(BluetoothDevice.ACTION_FOUND);
        mintent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        gamePlay=new Thread(){
            @Override
            public void run() {
                while(true){
                    if(gameView.isBluetoothPlayerMoveEnd()){
                        //下棋完成后发送有效动作给对方
                        PlayerVsController.getInstance().sendGameMessage(gameView.getMyself().getMovement());
                        gameView.bluetoothPlayerMoveMsgGot();
                    }


                }
            }
        };
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
                    case R.id.bluetooth_give_up:

                        break;
                    case R.id.bluetooth_statistic:
                        break;
                    case R.id.bluetooth_exit:
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

    @Override
    protected void onResume() {
        super.onResume();
        //动态注册广播
        registerReceiver(mbroadcast, mintent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消广播注册
        unregisterReceiver(mbroadcast);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_build_game:
                //按键前先清空搜索到的设备
                mfoundDevice.clear();
                mdeviceAdapter.notifyDataSetChanged();
                //对外可见，等待设备匹配并连接
                mblueToothController.enableVisibly(this);

                //创建对局操作
                PlayerVsController.getInstance().buildGameAndWaitToJoin(mblueToothController.getAdapter(), mhandler);

                break;
            case R.id.btn_find_game:
                //按键前先清空搜索到的设备
                mfoundDevice.clear();
                mdeviceAdapter.notifyDataSetChanged();
                mblueToothController.findDevice();
                break;
            case R.id.btn_stop://停止按键
                //取消搜索
                mblueToothController.getAdapter().cancelDiscovery();
                pb_wait.setVisibility(View.GONE);
                tv_tip.setText("未加入对局");

                PlayerVsController.getInstance().stop();
                break;
            case R.id.bluetooth_junqi_menu:
                //弹出菜单
                showPopupMenu(btn_popup_menu);
        }
    }


    private class BlueToothBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //蓝牙搜索开始
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                pb_wait.setVisibility(View.VISIBLE);
                tv_tip.setText("搜寻对局中");
            }
            //蓝牙搜索结束
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                String str = "搜寻结束";
                if (mfoundDevice.size() == 0) {
                    str += "（无对局房间可加入）";
                }
                tv_tip.setText(str);
                pb_wait.setVisibility(View.GONE);
            }
            //搜索到设备
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mfoundDevice.add(device);
                mdeviceAdapter.notifyDataSetChanged();
            }
            //扫描模式改变，即设备在可见性之间切换
            else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                //可见性的模式
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                //本设备对其他设备可见
                if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

                }
                //本设备对其他设备隐藏
                else {

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //若用户不打算打开蓝牙功能，则activity直接被finish
            if (resultCode != RESULT_OK) {
                finish();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //permission granted!
                } else {
                    //无法请求定位，关闭活动
                    finish();
                }
                break;
            }
        }
    }


    public String getGameMsgType(String str) {
        String[] msg = str.split(",");
        return msg[0];
    }

    //获取游戏信息
    public byte[][] getGameMapMsg(String str) {
        String[] msg = str.split(",");

        byte[][] map = new byte[JunQiMap.BOARD_ROW][JunQiMap.BOARD_COLUMN];
        for (int i = 0; i < JunQiMap.BOARD_ROW; i++) {
            for (int j = 0; j < JunQiMap.BOARD_COLUMN; j++) {
                map[i][j] = (byte) Integer.parseInt(msg[1 + i * JunQiMap.BOARD_COLUMN + j]);
            }
        }
        return map;
    }

    public Rect getGameMoveMsg(String str) {
        Log.d("Mytag","get msg:"+str);
        String[] msg = str.split(",");
        Rect r = new Rect();
        r.left = Integer.parseInt(msg[1]);
        r.top = Integer.parseInt(msg[2]);
        r.right = Integer.parseInt(msg[3]);
        r.bottom = Integer.parseInt(msg[4]);
        return r;

    }
}

