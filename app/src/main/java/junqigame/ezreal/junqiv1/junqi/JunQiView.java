package junqigame.ezreal.junqiv1.junqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import junqigame.ezreal.junqiv1.junqi.JunQiImageResourcesData;
import junqigame.ezreal.junqiv1.junqi.JunQiMap;
import junqigame.ezreal.junqiv1.junqi.JunQiPiece;

public class JunQiView extends View {
    //游戏模式
    public final static int PVP_IN_ONEDEVICE=0;//两人对战
    public final static int PVP_IN_BLUETOOTH=1;//蓝牙对战
    public final static int PVE=2;//人机对战

    private JunQiImageResourcesData junqiData;
    private Paint paintDraw;
    private Paint paintFont;
    private Point touchPoint;
    private Point screenSize;

    private JunQiMap map;//棋盘
    JunQiPlayer player1,player2;//俩个玩家
    JunQiPlayer myself;
    Point touchedMapPoint;
    private int gameMode;
    boolean gameStart;
    boolean gameEnd;
    boolean bluetoothMoveEnd;

    public JunQiView(Context context){
        super(context);
        init(context);
    }
    //若要在xml文件中使用该自定义View，必须有此构造函数
    public JunQiView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);


    }

    //初始化工作
    private void init(Context context){
        DisplayMetrics metrics=getResources().getDisplayMetrics();
        screenSize=new Point(metrics.widthPixels,metrics.heightPixels);
        junqiData=new JunQiImageResourcesData(context,screenSize);
        paintDraw=new Paint();//画图格式
        paintFont=new Paint();//字体格式
        paintFont.setTextSize(screenSize.y/50);
        paintFont.setColor(Color.BLACK);

        touchPoint=new Point(0,0);//触摸点
        //获取位图资源
        gameStart=false;
        gameEnd=false;

        bluetoothMoveEnd=false;
        map=new JunQiMap(junqiData);

        touchedMapPoint=new Point(0,0);

        //默认模式为两人对战模式
        gameMode=PVP_IN_ONEDEVICE;
        //初始化玩家，玩家1先手
        player1=new JunQiPlayer(map,"player1",true);
        player2=new JunQiPlayer(map,"player2",false);
        //蓝牙或人机对战才需要
        myself=null;
        if(gameMode!=PVP_IN_BLUETOOTH) {
            initGame();
            gameStart=true;
        }

    }
    //初始化棋盘
    public void initGame(){
        map.initPieceInMap(map.getRandomPieceInMap());//初始化棋盘
//        map.initPieceInMap(map.getTestMap());//初始化测试棋盘

    }
    //绘制画面
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制棋盘
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(junqiData.getJunqiMapImage(),junqiData.getMapSRCRect(),
                junqiData.getMapDSTRect(),paintDraw);
        if(gameStart) {

            if(player1.getPlayerCamp()==JunQiPiece.NONE_CAMP &&
                    map.getPlayerCamp()!=JunQiPiece.NONE_CAMP){//第一次翻棋时确定阵营
                player2.setCamp(map.getPlayerCamp());
                player1.setCamp(JunQiPiece.getRivalCamp(map.getPlayerCamp()));
            }
            map.drawAllPieces(canvas);

            //高亮玩家下棋轨迹
            map.highlightPlayTrace(canvas);

            map.highlightTouchedPoint(canvas);

            //显示信息
            int space = (int) paintFont.getTextSize();
            int y = 60;

            canvas.drawText(getGameModeStr() +"  :  "+ player1.getPlayerName() + "  VS  " + player2.getPlayerName(), 50, y, paintFont);
            y += (int) (space * 1.5);

            canvas.drawText("TouchPoint : ( " + touchedMapPoint.x + " , " + touchedMapPoint.y + " )", 50, y, paintFont);
            y += (int) (space * 1.5);
            String str1 = player1.isPlaying() ? "   (下棋中)" : "    ";

            canvas.drawText(player1.getPlayerName() +"  :  "+
                    map.getPlayerCampStr(player1.getPlayerCamp()) + str1, 50, y, paintFont);
            y += (int) (space * 1.5);
            String str2 = player2.isPlaying() ? "   (下棋中)" : "    ";
            canvas.drawText(player2.getPlayerName()+"  :  " +
                    map.getPlayerCampStr(player2.getPlayerCamp()) + str2, 50, y, paintFont);

            //提示玩家回合
            Paint paint=new Paint();
            paint.setColor(Color.rgb(255,0,255));
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(junqiData.getScreenSize().y/30);
            String str3=(player1.isPlaying()?player1.getPlayerName():player2.getPlayerName());
            canvas.drawText(str3 + "回合", screenSize.x/2, screenSize.y - 100, paint);


            setGameResult(canvas);


        }
    }
    //根据棋盘判断结果
    private void setGameResult(Canvas canvas){
        //判断输赢
        boolean player1Failed=map.isGameFailed(player1.getPlayerCamp());
        boolean player2Failed=map.isGameFailed(player2.getPlayerCamp());
        if(player1Failed || player2Failed){
            gameEnd=true;
            JunQiPlayer winner=player1Failed?player2:player1;
            Paint winFont=new Paint();

            winFont.setColor(Color.argb(220,66,0xcc,0xff));
            canvas.drawRoundRect(0+80,screenSize.y/2-screenSize.y/6,
                    screenSize.x-80,screenSize.y/2+screenSize.y/6,100,100,winFont);
            winFont.setColor(Color.YELLOW);
            winFont.setTextSize(screenSize.y/15);
            winFont.setTextAlign(Paint.Align.CENTER);
            String showStr="";
            if(gameMode==PVP_IN_ONEDEVICE){//若为两人对战，则只显示胜利的一方
                //显示胜利的一方
                showStr=map.getPlayerCampStr(winner.getPlayerCamp())+" 胜利 ";

            }
            else
            {
                if(winner==myself){
                    //我方胜利
                    showStr="胜 利";
                }
                else{
                    //我方失败
                    showStr="失 败";
                }
            }
            canvas.drawText(showStr,screenSize.x/2,screenSize.y/2,winFont);


        }
    }
    //触摸屏幕事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    //获取触摸点
        if(!gameEnd) {
            touchPoint.x = (int) event.getX();
            touchPoint.y = (int) event.getY();
            touchedMapPoint = map.getTouchPosition(touchPoint);//获取在棋盘上点击的坐标


            if (gameMode == PVP_IN_ONEDEVICE)
                pvpInOneDevice();
            else if (gameMode == PVP_IN_BLUETOOTH)
                pvpInBluetooth(myself);
            else if (gameMode == PVE)
                pve();
            ///重新绘制画布函数，再次调用onDraw函数
            invalidate();//不可在子线程中循环调用执行
            //postInvalidate();//可在子线程中循环调用执行
        }
        return super.onTouchEvent(event);
    }

    public JunQiPlayer getPlayer1() {
        return player1;
    }

    public JunQiPlayer getPlayer2() {
        return player2;
    }

    public JunQiMap getJunQiMap(){
        return map;
    }
    //设置游戏模式
    public void setGmaeMode(int mode){
        this.gameMode=mode;
    }

    public void restartGame(){
        map.restart();//重新布局
        player2.restart(false);
        player1.restart(true);
        gameEnd=false;
        gameStart=true;
        invalidate();//并且更新画面
    }

    private String getGameModeStr(){
        switch (gameMode){
            case PVP_IN_ONEDEVICE:
                return "两人对战";
            case PVP_IN_BLUETOOTH:
                return "蓝牙对战";
            case PVE:
                return "人机对战";
        }
        return "";
    }
    //两人对战模式
    private void pvpInOneDevice(){
        if(player1.isPlaying())
        {
            map.dealWithMapTouched(player1.getPlayerCamp());
            if(map.hasSucceedMove()){
                player1.setMovement(map.getSucceedMove());
//                if(player1.getPlayerCamp()==JunQiPiece.NONE_CAMP &&
//                        map.getPlayerCamp()!=JunQiPiece.NONE_CAMP){//第一次翻棋时确定阵营
//                    player1.setCamp(map.getPlayerCamp());
//                    player2.setCamp(JunQiPiece.getRivalCamp(map.getPlayerCamp()));
//                }
                player1.turnToNextPlayer();
                player2.turnToNextPlayer();
                map.turnToNextPlayer();
            }
        }
        else if(player2.isPlaying()){
            map.dealWithMapTouched(player2.getPlayerCamp());
            if(map.hasSucceedMove()) {
                player2.setMovement(map.getSucceedMove());
                player1.turnToNextPlayer();
                player2.turnToNextPlayer();
                map.turnToNextPlayer();
            }

        }
    }

    //蓝牙或人机模式时需指定自己的玩家身份，是否玩家1,（先手）
    public void setMyself(boolean isplayer1){
        if(isplayer1)
            myself=player1;
        else
            myself=player2;
    }

    public JunQiPlayer getMyself(){
        return myself;
    }
    //蓝牙对战

    private void pvpInBluetooth(JunQiPlayer player){
        if(player.isPlaying())
        {
            //若阵营未确定
//            if(player2.getPlayerCamp()==JunQiPiece.NONE_CAMP && player==player2  ) {
//                player.setCamp(map.getPlayerCamp());
//                player1.setCamp(JunQiPiece.getRivalCamp(map.getPlayerCamp()));
//            }
            map.dealWithMapTouched(player.getPlayerCamp());
            if(map.hasSucceedMove()){
                player.setMovement(map.getSucceedMove());
//                if(player1.getPlayerCamp()==JunQiPiece.NONE_CAMP &&
//                        map.getPlayerCamp()!=JunQiPiece.NONE_CAMP){//第一次翻棋时确定阵营
//                    player1.setCamp(map.getPlayerCamp());
//                    player2.setCamp(JunQiPiece.getRivalCamp(map.getPlayerCamp()));
//                }
                player1.turnToNextPlayer();
                player2.turnToNextPlayer();
                map.turnToNextPlayer();
                bluetoothMoveEnd=true;
            }
        }
    }

    public void  gameStart(){
        gameStart=true;
    }
    //处理对方玩家的行动
    public void setBluetoothPlayerMove(Rect r){
        map.tryToMove(new Point(r.left,r.top),new Point(r.right,r.bottom));
        player1.turnToNextPlayer();
        player2.turnToNextPlayer();
        map.turnToNextPlayer();
        invalidate();//更新画面
    }
    //查询是否完成下棋动作
    public boolean isBluetoothPlayerMoveEnd()
    {
        return bluetoothMoveEnd;
    }
    //动作消息发送完成
    public void  bluetoothPlayerMoveMsgGot(){
        bluetoothMoveEnd=false;
    }
    //人机对战
    private void pve(){

    }

    public  JunQiPlayer getRivalPlayer(){
        if(myself!=null)
             return myself==player1?player2:player1;
        return null;

    }

}
