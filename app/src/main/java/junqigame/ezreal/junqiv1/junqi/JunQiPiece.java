package junqigame.ezreal.junqiv1.junqi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class JunQiPiece {

    //棋子类型
    //红方
    final static byte FLAG_RED=0x0C;
    final static byte MINE_RED=0x0B;
    final static byte BOMB_RED=0x0A;
    final static byte SHILING_RED=0x09;
    final static byte JUNZHANG_RED=0x08;
    final static byte SHIZHANG_RED=0x07;
    final static byte LVZHANG_RED=0x06;
    final static byte TUANZHANG_RED=0x05;
    final static byte YINGZHANG_RED=0x04;
    final static byte LIANGZHANG_RED=0x03;
    final static byte PAIZHANG_RED=0x02;
    final static byte GONGBING_RED=0x01;

   //红方棋子
    final static byte FLAG_BLUE=0x1C;
    final static byte MINE_BLUE=0x1B;
    final static byte BOMB_BLUE=0x1A;
    final static byte SHILING_BLUE=0x19;
    final static byte JUNZHANG_BLUE=0x18;
    final static byte SHIZHANG_BLUE=0x17;
    final static byte LVZHANG_BLUE=0x16;
    final static byte TUANZHANG_BLUE=0x15;
    final static byte YINGZHANG_BLUE=0x14;
    final static byte LIANGZHANG_BLUE=0x13;
    final static byte PAIZHANG_BLUE=0x12;
    final static byte GONGBING_BLUE=0x11;

    //表示阵营，只看其高4位
    final static byte RED_CAMP=0x00;//红方
    final static byte BLUE_CAMP=0x10;//蓝方
    final static byte NONE_CAMP=0x20;//无阵营
    //无棋子
    final static byte NONE_TAG=0x00;

    private Point position;//棋子在棋盘中的位置（行，列）
    private boolean alive;//棋子是否存活
    private boolean known;//棋子是否被翻面
    private byte pieceTag;
    private JunQiImageResourcesData imageResourcesData;//资源数据

//    构造函数
    public JunQiPiece(JunQiImageResourcesData resourcesData,byte pieceTag,Point position){

        this.imageResourcesData=resourcesData;
        this.pieceTag=pieceTag;
        this.position=position;

        alive=true;
        known=false;

    }

    //在屏幕上绘制棋子，
    public void drawPiece(Canvas canvas){
        if(!alive)  return;//棋子不存活则退出绘制
        Bitmap piecesAll=imageResourcesData.getJunqiAllPiecesImage();
        Bitmap piecesBackground=imageResourcesData.getJunqiBackgroundImage();
        Paint paint=new Paint();
        if(known){//翻动后
            canvas.drawBitmap(piecesAll,imageResourcesData.getPieceSRCRect(this),
                    imageResourcesData.getPieceDSTRect(position),paint);
        }
        else{//未被翻动
            canvas.drawBitmap(piecesBackground,imageResourcesData.getPieceRect(),
                    imageResourcesData.getPieceDSTRect(position),paint);
        }

    }

    //判断棋子是否翻起
    public boolean getPieceKnownState(){
        return known;
    }

    //设置棋子是否被翻起
    public void setPieceKnownState(boolean known){
        this.known=known;
    }

    //获取棋子存活状态
    public boolean pieceIsAlive(){
        return alive;
    }
    //设置棋子存活状态
    public void setPieceAliveState(boolean alive){
        this.alive=alive;
    }

    //设置棋子位置
    public void setPosition(Point dstPosition){
        this.position=dstPosition;
    }

    //获取棋子标志
    public byte getPieceTag(){
        return  pieceTag;
    }

    //获取棋子阵营
    public byte getPieceCamp(){
       byte camp=(byte)(pieceTag & 0xf0);
       return camp;

    }

    public static byte getRivalCamp(byte camp){
        return (camp==RED_CAMP?BLUE_CAMP:RED_CAMP);
    }
    //返回棋子无阵营类型
    public byte getPurePieceType()
    {
        byte prue=(byte)(pieceTag & 0x0f);
        return prue;
    }
    //判断棋子大小
    public boolean canDefeat(boolean rivalMineisExist,byte pureTag){
        if(getPurePieceType()==GONGBING_RED && pureTag==MINE_RED)
            return true;
        else if(!rivalMineisExist && getPurePieceType()==GONGBING_RED &&pureTag==FLAG_RED)//无地雷，可吃军旗
            return true;
        else if(getPurePieceType()>pureTag)
            return true;
        return false;
    }
    //判断棋子是否可以碰棋
    public boolean canBothGone(byte pureTag){
        if(getPurePieceType()==pureTag || (getPurePieceType()==BOMB_RED && pureTag!=FLAG_RED)
                || pureTag==BOMB_RED)
            return true;

        return false;

    }

}
