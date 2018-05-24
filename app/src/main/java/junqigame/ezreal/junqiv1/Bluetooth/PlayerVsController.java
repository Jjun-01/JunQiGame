package junqigame.ezreal.junqiv1.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import junqigame.ezreal.junqiv1.junqi.JunQiMap;

public class PlayerVsController {

    //发送消息类型
    public final static String MSG_MAP_INFO="M";//军棋棋盘信息,用于开局棋盘一致
    public final static String MSG_PLAYER_MOVE="P";//玩家执行动作消息
    public final static String MSG_PLAYER_GIVEUP="G";//玩家投降
    public final static String MSG_PLAYER_CHAT="C";//玩家发送聊天



    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;

    /**
     * 网络协议的处理函数
     */
    private  class ChatProtocol implements ProtocolHandler<String> {

        private static final String CHARSET_NAME = "utf-8";



        //封包，以发送至网络传递
        @Override
        public byte[] encodePackage(String data) {
            if( data == null) {
                return new byte[0];
            }
            else {
                try {
                    return data.getBytes(CHARSET_NAME);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return new byte[0];
                }
            }
        }

        //解包，接收网络传递过来的数据
        @Override
        public String decodePackage(byte[] netData) {
            if( netData == null) {
                return "";
            }
            try {
                return new String(netData, CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * 协议处理
     */
    private ChatProtocol mProtocol = new ChatProtocol();


    /**
     * 与服务器连接进行对局
     * @param device
     * @param adapter
     * @param handler
     */
    public void connectToGame(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        mConnectThread = new ConnectThread(device,adapter,handler);
        mConnectThread.start();
    }

    /**
     * 等待客户端来连接
     * @param adapter
     * @param handler
     */
    public void buildGameAndWaitToJoin(BluetoothAdapter adapter, Handler handler) {
        mAcceptThread = new AcceptThread(adapter,handler);
        mAcceptThread.start();
    }



    /**
     * 发出消息
     * @param msg
     */
    public void sendMessage(String msg) {
        byte[] data = mProtocol.encodePackage(msg);
        if(mConnectThread != null) {
            mConnectThread.sendData(data);
        }
        else if( mAcceptThread != null) {
            mAcceptThread.sendData(data);
        }
    }
    //发送游戏消息
    public void sendGameMessage(byte[][] mapData){
        String str="",sp=",";
        str=str+MSG_MAP_INFO+",";
        for(int i=0;i<JunQiMap.BOARD_ROW;i++){
            for(int j=0;j<JunQiMap.BOARD_COLUMN;j++){
                str=str+mapData[i][j]+",";
            }
        }
        sendMessage(str);
        Log.d("Mytag","send msg:"+str);
    }
    public void sendGameMessage(Rect r){
        String str="",sp=",";
        str=str+MSG_PLAYER_MOVE+sp;
        str=str+r.left+sp+r.top+sp+r.right+sp+r.bottom+sp;
        sendMessage(str);

        Log.d("Mytag","send msg:"+str);
        //蓝牙发送的字节流是固定长度的，若消息不足会在后面填充，所以应该要在最末尾加逗号以隔开
    }
    public void sendGameMessage(){
        sendMessage(MSG_PLAYER_GIVEUP+",");
    }




    /**
     * 网络数据解码
     * @param data
     * @return
     */
    public String decodeMessage(byte[] data) {
        return  mProtocol.decodePackage(data);
    }




    /**
     * 停止聊天
     */
    public void stop() {
        if(mConnectThread != null) {
            mConnectThread.cancel();
        }
        if( mAcceptThread != null) {
            mAcceptThread.cancel();
        }
    }

    /**
     * 单例方式构造类对象
     */
    private static class PlayerVsControlHolder {
        private static PlayerVsController mInstance = new PlayerVsController();
    }

    public static PlayerVsController getInstance() {
        return PlayerVsControlHolder.mInstance;
    }


}

