package junqigame.ezreal.junqiv1.junqi;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.HashMap;

import java.util.Random;


public class JunQiMap {

    public final static int MOVEMENT_INVALID=0;//无效动作
    public final static int MOVEMENT_TURN_PIECE=1;//翻棋动作
    public final static int MOVEMENT_MOVE=2;//移动动作
    public final static int MOVEMENT_KILL=3;//吃棋动作
    public final static int MOVEMENT_BOTH_DEATH=4;//碰棋动作


    //棋盘点类型
    public final static int BIG_CAMP=3;
    public final static int CAMP=2;
    public final static int RIALWAY=1;
    public final static int ROAD=0;

    public final static int BOARD_ROW=12;
    public final static int BOARD_COLUMN=5;
    //对局胜负标记
    final static int RED_CAMP_WIN=1;
    final static int BLUE_CAMP_WIN=2;
    final static int DRAW=0;//平局
    final static int NO_WINNER=3;//无结果


    //棋盘点布局
    final static  int [][] station ={
            {ROAD,BIG_CAMP,ROAD,BIG_CAMP,ROAD},
            {RIALWAY,RIALWAY,RIALWAY,RIALWAY,RIALWAY},
            {RIALWAY,CAMP,ROAD,CAMP,RIALWAY},
            {RIALWAY,ROAD,CAMP,ROAD,RIALWAY},
            {RIALWAY,CAMP,ROAD,CAMP,RIALWAY},
            {RIALWAY,RIALWAY,RIALWAY,RIALWAY,RIALWAY},

            {RIALWAY,RIALWAY,RIALWAY,RIALWAY,RIALWAY},
            {RIALWAY,CAMP,ROAD,CAMP,RIALWAY},
            {RIALWAY,ROAD,CAMP,ROAD,RIALWAY},
            {RIALWAY,CAMP,ROAD,CAMP,RIALWAY},
            {RIALWAY,RIALWAY,RIALWAY,RIALWAY,RIALWAY},
            {ROAD,BIG_CAMP,ROAD,BIG_CAMP,ROAD}

    };



    //军棋中所有棋子
    private byte [] mallPieces={0x0c,0x0b,0x0b,0x0b,0xa,0x0a,0x09,0x08,0x07,0x07,
            0x06,0x06,0x05,0x05,0x04,0x04,0x03,0x03,0x03,0x02,0x02,0x02,0x01,0x01,0x01,
                                 0x1c,0x1b,0x1b,0x1b,0x1a,0x1a,0x19,0x18,0x17,0x17,
            0x16,0x16,0x15,0x15,0x14,0x14,0x13,0x13,0x13,0x12,0x12,0x12,0x11,0x11,0x11};



    public final  static Point NONE_POSITION=new Point(0,0);


    JunQiImageResourcesData imageResourcesData;
    private byte[][] mpieceInMap;//储存棋子标志
    private HashMap<Point,JunQiPiece> mpiecesPosition; //位置对应的棋子映射

    //存储副本，以备与悔棋和存储
    private byte[][] pieceInMapCopy;
    private HashMap<Point,JunQiPiece> piecesPositionCopy;

    private byte whosTurn;//记录到哪一方下棋

    private Point touchedP;
    private Point fromP,toP;
    private Point fromP_succeed,toP_succeed;
    private boolean moveSucceed;


//构造函数
    public JunQiMap(JunQiImageResourcesData imageResourcesData){
        this.imageResourcesData=imageResourcesData;
        mpieceInMap =new byte[BOARD_ROW][BOARD_COLUMN];
        mpiecesPosition =new HashMap<>();

        fromP=toP=fromP_succeed=toP_succeed=touchedP=NONE_POSITION;
        whosTurn=JunQiPiece.NONE_CAMP;
        moveSucceed=false;

    }
//重新开始游戏
    public void restart(){
        //先清空资源
        mpieceInMap =null;
        mpiecesPosition.clear();

        mpieceInMap =new byte[BOARD_ROW][BOARD_COLUMN];

        fromP=toP=fromP_succeed=toP_succeed=touchedP=NONE_POSITION;
        whosTurn=JunQiPiece.NONE_CAMP;
        moveSucceed=false;
        initPieceInMap(getRandomPieceInMap());
    }

    //获取随机放棋的布局
    public byte[][] getRandomPieceInMap(){
        byte[] allPieces=mallPieces.clone();
        byte[][] pieceInMap=new byte[BOARD_ROW][BOARD_COLUMN];
        int len=50,index;
        for(int i=0;i<BOARD_ROW;i++){
            for(int j=0;j<BOARD_COLUMN;j++){
                if(station[i][j]==CAMP)
                    pieceInMap[i][j]=JunQiPiece.NONE_TAG;
                else{
                    Random random=new Random();
                    if(len>1) {
                        index = random.nextInt(len - 1);
                        pieceInMap[i][j] = allPieces[index];
                        allPieces[index] = allPieces[len - 1];
                        allPieces[len - 1] = mpieceInMap[i][j];
                    }
                    else
                        pieceInMap[i][j] = allPieces[0];
                    len--;

                }

            }
        }
        return  pieceInMap;
    }

    //测试用棋盘
    public byte[][] getTestMap(){
        byte[] allPieces=mallPieces.clone();
        byte[][] pieceInMap=new byte[BOARD_ROW][BOARD_COLUMN];
        int len=50,index;
        for(int i=0;i<BOARD_ROW;i++){
            for(int j=0;j<BOARD_COLUMN;j++){
                if(station[i][j]==CAMP)
                    pieceInMap[i][j]=JunQiPiece.NONE_TAG;
                else{
                    Random random=new Random();
                    if(len>1) {
                        index = random.nextInt(len - 1);
                        pieceInMap[i][j] = allPieces[index];
                        allPieces[index] = allPieces[len - 1];
                        allPieces[len - 1] = mpieceInMap[i][j];
                    }
                    else
                        pieceInMap[i][j] = allPieces[0];
                    len--;

                    if((pieceInMap[i][j]&0x0f)<JunQiPiece.SHILING_RED){
                        pieceInMap[i][j]=JunQiPiece.NONE_TAG;
                    }

                }

            }
        }
        return pieceInMap;
    }
    //根据布局初始化棋盘

    public void initPieceInMap(byte[][] pieceInMap){
        mpiecesPosition.clear();
        mpieceInMap=pieceInMap;
        for(int i=0;i<BOARD_ROW;i++){
            for(int j=0;j<BOARD_COLUMN;j++){
                if(mpieceInMap[i][j]!=JunQiPiece.NONE_TAG) {
                    JunQiPiece piece = new JunQiPiece(imageResourcesData, mpieceInMap[i][j], new Point(i + 1, j + 1));
                    mpiecesPosition.put(new Point(i + 1, j + 1), piece);//添加到哈希map中，以便于查找棋子
                    //注意：此处i，j不是对应棋盘中的坐标，i+1,j+1才是。。。。。
                }
            }
        }
    }

    //获取棋盘布局
    public byte[][] getPieceInMap(){
        return mpieceInMap;
    }
    //根据位置获取棋子
    public JunQiPiece getPiece(Point position){
        return mpiecesPosition.get(position);
    }



    /*****************
     * 屏幕触摸处理函数,得到from和to点的值
     * (需在每次触摸屏幕之后调用此函数)
     * *******************/
    public void dealWithMapTouched(byte camp){
        //未到次方下棋，不处理其触摸事件
        if(camp!=whosTurn && whosTurn!=JunQiPiece.NONE_CAMP) return;
        if(touchedP==NONE_POSITION)  fromP=toP=NONE_POSITION;  //若点击坐标无效，则from设置为未选中
        else{
            if(fromP==NONE_POSITION){
                if(getPiece(touchedP)==null) fromP=NONE_POSITION; //from为空时且没有选中棋子，则from设置为未选中
                else if(!getPiece(touchedP).getPieceKnownState()) fromP=touchedP;//若棋子未翻动，则from设置为touch点
                else if(getPiece(touchedP).getPurePieceType()==JunQiPiece.FLAG_RED ||
                        getPiece(touchedP).getPurePieceType()==JunQiPiece.MINE_RED)
                    fromP=NONE_POSITION;//若点击棋子为地雷或军棋，且from为空，不可选择移动，则from设置为未选中
                else {
                    if(whosTurn!=JunQiPiece.NONE_CAMP && whosTurn==getPiece(touchedP).getPieceCamp())
                        fromP = touchedP;//若from点为空且点击的棋子为己方已知棋子，则设置为touch点
                    else
                        fromP=NONE_POSITION;
                }
            }
            else{
            toP=touchedP; //from点以设置，且touch点有效，设置to点
            //from 和to 点均找齐，尝试处理该动作
            tryToMove(fromP,toP);
            }
        }
    }

    /***
     * 尝试去执行动作，成功则执行，失败则对坐标记录点处理
     * ***/
    public void tryToMove(Point from,Point to){
        int moveType=getMovementType(from,to);
        if(moveType!=MOVEMENT_INVALID) {
            executeMovement(from, to, moveType);//执行动作

            if(whosTurn==JunQiPiece.NONE_CAMP)//若为第一次翻棋，设置相关阵营
               whosTurn=getPiece(from).getPieceCamp();

            //记录玩家下的点
            fromP_succeed = from;
            toP_succeed = to;
            moveSucceed=true;

        }
        fromP=toP=NONE_POSITION;
    }
    /***
     *判断棋子移动的类型
     ***/
    public  int getMovementType(Point from,Point to) {
        if (from.x == to.x && from.y==to.y) {
          /***注意：from和to为不同的点对象，即使其值相等，但对象地址不同，则两者不等，除非改变其equal函数**/
            if (!getPiece(from).getPieceKnownState())
                return MOVEMENT_TURN_PIECE;//from和to为同一点，若棋子未翻起，动作有效
        }
        else {
            if (getPiece(to) == null) {
                if (!getPiece(from).getPieceKnownState()) return MOVEMENT_INVALID;//未知棋子不可移动
                if (allowMove(from, to))
                    return MOVEMENT_MOVE;//to位置坐标处无棋子，若可移动，则为移动动作
            }
            else{
                if (allowMove(from, to)) {//棋子损耗类动作执行需要满足可移动条件
                    if (!getPiece(from).getPieceKnownState() || !getPiece(to).getPieceKnownState())
                        return MOVEMENT_INVALID;//from和to有一个棋子未知，则动作无效
                    else {
                        JunQiPiece fromPiece = getPiece(from);
                        JunQiPiece toPiece = getPiece(to);
                        if (fromPiece.getPieceCamp() != toPiece.getPieceCamp()) {
                            if (fromPiece.canBothGone(toPiece.getPurePieceType()))
                                return MOVEMENT_BOTH_DEATH;//碰棋动作
                            else if (fromPiece.canDefeat(rivalMineIsExist(toPiece.getPieceCamp()),
                                    toPiece.getPurePieceType()))
                                return MOVEMENT_KILL;//吃棋动作
                        }//非同阵营
                    }
                }
            }
        }


        return MOVEMENT_INVALID;//无效动作
    }
    /**
     * 执行动作的函数，并修改相关参数
     * */
    public void executeMovement(Point from,Point to,int movementType){
        JunQiPiece fromPiece=getPiece(from);
        JunQiPiece toPiece=getPiece(to);
        switch(movementType){
            case MOVEMENT_TURN_PIECE: //翻棋动作执行
               fromPiece.setPieceKnownState(true);
                break;

            case MOVEMENT_KILL://吃棋子
                //把to位置的棋子删除
                toPiece.setPieceAliveState(false);
                mpieceInMap[to.x-1][to.y-1]=JunQiPiece.NONE_TAG;
                mpiecesPosition.remove(to);
                //后面操作相当于移动棋子，因此不需要break
            case MOVEMENT_MOVE://移动动作

                fromPiece.setPosition(to);

                mpiecesPosition.remove(from);//删除之前映射，并设置后面映射
                mpiecesPosition.put(to,fromPiece);

                mpieceInMap[to.x-1][to.y-1]= mpieceInMap[from.x-1][from.y-1];//处理tag数组
                mpieceInMap[from.x-1][from.y-1]=JunQiPiece.NONE_TAG;
                break;

            case MOVEMENT_BOTH_DEATH://碰棋子
                //两棋子均删除
                toPiece.setPieceAliveState(false);
                mpieceInMap[to.x-1][to.y-1]=JunQiPiece.NONE_TAG;
                mpiecesPosition.remove(to);

                fromPiece.setPieceAliveState(false);
                mpieceInMap[from.x-1][from.y-1]=JunQiPiece.NONE_TAG;
                mpiecesPosition.remove(from);
                break;


        }

    }

    /***
     * 重要的规则函数
     * 判断移动是否合理
     * **/
    public boolean allowMove(Point from,Point to){

        int fromType=station[from.x-1][from.y-1];
        int toType=station[to.x - 1][to.y - 1];
        if(fromType==CAMP  || (fromType==RIALWAY&&toType==CAMP) ) { //from为行营 或 from为铁路且to为行营
            if (Math.abs(from.x - to.x) > 1 || Math.abs(from.y - to.y) > 1) return false;//超出附近九格，不可移动；
            else if (getPiece(to) != null && toType == CAMP) return false;//移动到的行营有棋子，不可移动；
            else   return true;
        }
        else if(fromType==BIG_CAMP ||fromType==ROAD || (fromType==RIALWAY &&
                (toType==BIG_CAMP||toType==ROAD))){//from为大本营 或 道路 或 from为铁路且to为大本营或道路
            if (Math.abs(from.x - to.x) > 1 || Math.abs(from.y - to.y) > 1 ||
                    (from.x!=to.x && from.y!=to.y)) return false;//超出附近4格，不可移动
            else if (getPiece(to) != null && toType == CAMP) return false;//移动到的行营有棋子，不可移动；
            else if(toType==BIG_CAMP && getPiece(to)!=null &&
                    getPiece(to).getPurePieceType()!=JunQiPiece.MINE_RED &&
                    getPiece(to).getPurePieceType()!=JunQiPiece.FLAG_RED)
                return  false;//移动到大本营，且大本营上有棋子，且棋子不为地雷或炸弹，不可移动
            else   return true;
        }
        else {//from棋子在铁路上，且to也在铁路上

            if (getPiece(from).getPurePieceType() != JunQiPiece.GONGBING_RED) {//是否为工兵
                if (straightLineAccessible(from, to)) return true; //非工兵只可以走直线
                else return false;
            }
            else {
                if (straightLineAccessible(from, to)) return true;
                else return false;
            }
        }
    }

//判断直线路径是否通畅
    private boolean straightLineAccessible(Point from,Point to){
        if(from.x==to.x){//同一行
            if(from.y>to.y){
                int index=to.y+1;
                while(from.y>index){
                    if(mpieceInMap[from.x-1][index-1]!=JunQiPiece.NONE_TAG) return false;
                    index++;
                }
                return true;
            }
            else{
                int index=from.y+1;
                while(to.y>index){
                    if(mpieceInMap[from.x-1][index-1]!=JunQiPiece.NONE_TAG) return false;
                    index++;
                }
                return true;
            }
        }
        else if(from.y==to.y){//同一列
            if(from.x>to.x){
                int index=to.x+1;
                while(from.x>index){
                    if(mpieceInMap[index-1][from.y-1]!=JunQiPiece.NONE_TAG) return false;
                    index++;
                }
                return true;
            }
            else{
                int index=from.x+1;
                while(to.x>index){
                    if(mpieceInMap[index-1][from.y-1]!=JunQiPiece.NONE_TAG) return false;
                    index++;
                }
                return true;
            }
        }
        else return false;
    }
    //判断俩点之间是否有通路
    private boolean pointAccessibleToAnotherPoint(Point from,Point to){

        return true;
    }



//轮到下个玩家
    public void turnToNextPlayer(){
        whosTurn=(byte)(whosTurn==JunQiPiece.RED_CAMP?JunQiPiece.BLUE_CAMP:JunQiPiece.RED_CAMP);
        moveSucceed=false;
    }
    //是否有成功的行动
    public boolean hasSucceedMove(){
        return  moveSucceed;
    }
    //获取下棋的一方
    public byte getPlayerCamp(){
        return whosTurn;
    }
    //返回成功的下棋轨迹
    public Rect getSucceedMove(){

        Rect r=new Rect(fromP_succeed.x,fromP_succeed.y,toP_succeed.x,toP_succeed.y);
        return r;
    }


    //高亮显示下棋轨迹
    public void highlightTouchedPoint(Canvas canvas){
        if(fromP!=JunQiMap.NONE_POSITION)
            positionHighlight(canvas,fromP, Color.GREEN);
    }
    public void highlightPlayTrace(Canvas canvas){
        if(fromP_succeed!=NONE_POSITION)
            positionHighlight(canvas,fromP_succeed,getCampColor(getPlayerCamp()));
        if(toP_succeed!=NONE_POSITION)
            positionHighlight(canvas,toP_succeed,getCampColor(getPlayerCamp()));
    }

    private int getCampColor(byte camp){
        return camp!=JunQiPiece.RED_CAMP? Color.RED:Color.BLUE;
    }


    //返回 玩家阵营字符串
    public String getPlayerCampStr(byte player){
        if(whosTurn==JunQiPiece.NONE_CAMP)
            return "    ";
        return  player==JunQiPiece.RED_CAMP?"红方":"蓝方";
    }




    //判断玩家对局是否失败
    public boolean isGameFailed(byte camp){
        if(whosTurn==JunQiPiece.NONE_CAMP) return false;

        boolean  isFlagExit=false;
        boolean  isMovePieceExit=false;
        if(!allPieceIsKnown()) isMovePieceExit=true;
        for(int i=0;i<BOARD_ROW;i++){
            for(int j=0;j<BOARD_COLUMN;j++){
                if(mpieceInMap[i][j]!=JunQiPiece.NONE_TAG)
                    if( (mpieceInMap[i][j] & (byte)0xf0)==camp){

                        if(!isFlagExit && (mpieceInMap[i][j] & 0x0f)==JunQiPiece.FLAG_RED) {
                            isFlagExit = true;

                        }
                        if(!isMovePieceExit && (mpieceInMap[i][j]&0x0f)<=JunQiPiece.BOMB_RED) {
                            isMovePieceExit = true;

                        }
                    }
                    if(isFlagExit && isMovePieceExit)
                        return false;

            }
        }
        return true;

    }

    //判断所有棋子是否被翻起
    private boolean allPieceIsKnown() {
        for (int i = 0; i < BOARD_ROW; i++) {
            for (int j = 0; j < BOARD_COLUMN; j++) {
                {
                if ((mpieceInMap[i][j] & 0x0f) != JunQiPiece.NONE_TAG) //判断是否有棋子
                    if (!getPiece(new Point(i + 1, j + 1)).getPieceKnownState()) //判断是否是所查找方的阵营
                        return false;
                }
            }
        }
        return true;
    }
    //遍历数组，看所查看方是否还有地雷，若无则工兵可吃军旗
    public boolean rivalMineIsExist(byte camp){
        for(int i=0;i<BOARD_ROW;i++){
            for(int j=0;j<BOARD_COLUMN;j++){
                {
                    if((mpieceInMap[i][j] & 0x0f)==JunQiPiece.MINE_RED) {//判断是否是地雷
                        if ((mpieceInMap[i][j] & 0xf0) == camp) //判断是否是所查找方的阵营
                            return true;

                    }
                }
            }
        }
        return false;
    }



    //高亮显示所选位置
    public void positionHighlight(Canvas canvas,Point position,int color){
        int d=8;
        Rect pieceRectInScreen=imageResourcesData.getPieceDSTRect(position);

        Rect highlightRect=new Rect(pieceRectInScreen.left-d,pieceRectInScreen.top-d,
                pieceRectInScreen.right+d,pieceRectInScreen.bottom+d);
        Paint paint=new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(15.0f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(highlightRect,paint);
    }


    //判断点击的棋盘坐标,
    public Point getTouchPosition(Point touch){
        Point screenSize=imageResourcesData.getScreenSize();
        int row=0;
        int d=(int)(imageResourcesData.getScreenScale()*20);//增大检测范围，以增加灵敏度
        if(touch.y>screenSize.y/2)  row=6;//若触摸点在屏幕下半部，则只检测下半部分的点
        for(int i=row;i<6+row;i++){
            for(int j=0;j<5;j++){
                Point position=new Point(i+1,j+1);
                Rect pieceRect=imageResourcesData.getPieceDSTRect(position);
                Rect checkRect=new Rect(pieceRect.left-d,pieceRect.top-d,
                        pieceRect.right+d,pieceRect.bottom+d);
                if(isInRect(checkRect,touch))
                {
                    touchedP=position;
                    return touchedP;
                }

            }
        }
        touchedP=NONE_POSITION;
        return touchedP;
    }
    //检测点是否在矩形内
    private boolean isInRect(Rect r,Point p){
        if(p.x>r.left && p.x<r.right && p.y>r.top &&p.y<r.bottom)
            return true;
        return false;
    }
    //绘制所有棋子
    public void drawAllPieces(Canvas canvas){
        for(int i=0;i<BOARD_ROW;i++){
            for(int j=0;j<BOARD_COLUMN;j++){
                JunQiPiece piece=getPiece(new Point(i+1,j+1));
                if(piece!=null){

                  //  piece.setPieceKnownState(true);
                    piece.drawPiece(canvas);
                }
            }
        }
    }

}
