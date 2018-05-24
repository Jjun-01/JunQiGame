package junqigame.ezreal.junqiv1.Bluetooth;

//蓝牙设备相互匹配必须使用唯一的字符串，否则无法连接
public class Constant {
    public static final String CONNECTTION_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    /**
     * 开始监听
     */

    public static final int MSG_START_LISTENING = 1;

    /**
     * 结束监听
     */
    public static final int MSG_FINISH_LISTENING = 2;

    /****
     * 监听时间过长
     */
    public static final int MSG_LISTENING_TIMEOUT = 3;
    /**
     * 有客户端连接
     */
    public static final int MSG_GOT_A_CLINET = 4;

    /**
     * 连接到服务器
     */
    public static final int MSG_CONNECTED_TO_SERVER = 5;

    /**
     * 获取到数据
     */
    public static final int MSG_GOT_DATA = 6;

    /**
     * 出错
     */
    public static final int MSG_ERROR = -1;
}