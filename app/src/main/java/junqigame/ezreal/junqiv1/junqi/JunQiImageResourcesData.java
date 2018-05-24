package junqigame.ezreal.junqiv1.junqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

import junqigame.ezreal.junqiv1.R;
//军棋图类资源加载类，负责提供数据
public class JunQiImageResourcesData {

    //所有棋子位图的参数
    final static int ALL_PIECES_COLUMN=6;
    final static int ALL_PIECES_ROW=4;
    //以1080宽度为分辨率的尺寸
    public final static int  [] MAP_COLUMNS={0,130,335,540,745,950,1080};
    public final static int  [] MAP_ROWS={0,90,195,295,398,498,600,
            799,900,1002,1104,1205,1310,1400};
    final static int DEFAULT_SCREEN_WIDTH=1080;


    private Bitmap junqiMap;
    private Bitmap junqiAllPieces;
    private Bitmap junqiBackground;
    private float bitmapScale;
    private float screenScale;
    private int mapPositionY;

    private int pieceWidth;
    private int pieceHeight;
    private Point screenSize;

    public JunQiImageResourcesData(Context context, Point screenSize){
        initBitmapResource(context);
        this.screenSize=screenSize;
        screenScale=(float)screenSize.x/(float)DEFAULT_SCREEN_WIDTH;
        bitmapScale=(float)(screenSize.x)/(float)junqiMap.getWidth();
        mapPositionY=(screenSize.y-(int)(junqiMap.getHeight()*bitmapScale))/2;

        pieceWidth=junqiAllPieces.getWidth()/ALL_PIECES_COLUMN;
        pieceHeight=junqiAllPieces.getHeight()/ALL_PIECES_ROW;

    }

    private void initBitmapResource(Context context){
        junqiMap= BitmapFactory.decodeResource(context.getResources(), R.drawable.junqimap);
        junqiAllPieces=BitmapFactory.decodeResource(context.getResources(),R.drawable.junqipieces);
        junqiBackground=BitmapFactory.decodeResource(context.getResources(),R.drawable.piece_background);
    }

    public float getBitmapScale(){
        return bitmapScale;
    }

    public float getScreenScale(){
        return screenScale;
    }

    public int getMapPositionY(){
        return mapPositionY;
    }
    //获取屏幕大小
    public Point getScreenSize(){
        return screenSize;
    }
//获取所有棋子的位图
    public Bitmap getJunqiAllPiecesImage() {
        return junqiAllPieces;
    }
//获取棋子翻面前的位图
    public Bitmap getJunqiBackgroundImage() {
        return junqiBackground;
    }
    //获取棋盘位图
    public Bitmap getJunqiMapImage() {
        return junqiMap;
    }
    //返回棋盘位图矩形
    public Rect getMapSRCRect(){
        Rect src=new Rect(0,0,junqiMap.getWidth(),junqiMap.getHeight());
        return src;
    }
    //返回棋盘在屏幕中绘制的位置
    public Rect getMapDSTRect(){
        Rect dst=new Rect(0,mapPositionY,screenSize.x,
                mapPositionY+(int)(junqiMap.getHeight()*bitmapScale));
        return dst;
    }

    //获取棋子在位图中的下标
    private  int getImageIndex(JunQiPiece piece){
        int camp=0,index=0;
        if(piece.getPieceCamp()==0x10) camp=12;
        byte tag=(byte)(piece.getPieceTag() & 0x0f);
        switch(tag)
        {
            case 0x0c: index=camp+11; break;
            case 0x0b: index=camp+10; break;
            case 0x0a: index=camp+9; break;
            case 0x09: index=camp+0; break;
            case 0x08: index=camp+1; break;
            case 0x07: index=camp+2; break;
            case 0x06: index=camp+3; break;
            case 0x05: index=camp+4; break;
            case 0x04: index=camp+5; break;
            case 0x03: index=camp+6; break;
            case 0x02: index=camp+7; break;
            case 0x01: index=camp+8; break;
        }
        return index;

    }
    //获取相应下标的棋子的矩形
    public  Rect getPieceSRCRect(JunQiPiece piece){
        int index=getImageIndex(piece);
        int x=(index % ALL_PIECES_COLUMN)*pieceWidth;
        int y=(index / ALL_PIECES_COLUMN)*pieceHeight;
        Rect r=new Rect(x,y,x+pieceWidth,y+pieceHeight);
        return r;
    }
    //获取棋子在屏幕摆放位置的矩形 参数position.x指行（从1开始），position.y指列（从1开始）
    public  Rect getPieceDSTRect(Point position){
        Point p=new Point((int)(MAP_COLUMNS[position.y]*screenScale)-(int)(pieceWidth*bitmapScale/2),
                (int)(MAP_ROWS[position.x]*screenScale)+mapPositionY-(int)(pieceHeight*bitmapScale/2));
        Rect r=new Rect(p.x,p.y,p.x+(int)(pieceWidth*bitmapScale),p.y+(int)(pieceHeight*bitmapScale));
        return r;
    }
    //返回一个棋子位图大小的矩形
    public Rect getPieceRect(){
        Rect r=new Rect(0,0,pieceWidth,pieceHeight);
        return  r;
    }
}
