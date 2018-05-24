package junqigame.ezreal.junqiv1.junqi;

import android.graphics.Point;
import android.graphics.Rect;

public class JunQiPlayer {

    private JunQiMap map;
    private Point fromP,toP;//玩家有效动作的两坐标
    private String playerName;
    private boolean isMyTurn; //是否到该玩家回合
    private byte camp;//玩家阵营

    public JunQiPlayer(JunQiMap map, String name, Boolean isMyTurn){
        this.map=map;
        this.playerName=name;
        this.isMyTurn=isMyTurn;
        fromP=toP=JunQiMap.NONE_POSITION;
        camp=JunQiPiece.NONE_CAMP;//无阵营

    }
    public void restart(Boolean isMyTurn){
        this.isMyTurn=isMyTurn;
        fromP=toP=JunQiMap.NONE_POSITION;
        camp=JunQiPiece.NONE_CAMP;//无阵营
    }

    public JunQiMap getMap(){
        return map;
    }
    //获取玩家的行动轨迹
    public Rect getMovement(){
        Rect r=new Rect(fromP.x,fromP.y,toP.x,toP.y);
        return r;
    }
    //获取玩家阵营
    public  byte getPlayerCamp(){
        return camp;
    }
    //判断玩家是否下棋中
    public boolean isPlaying(){
        return isMyTurn;
    }

    public String getPlayerName(){
        return playerName;
    }

    public void setMap(JunQiMap map){
        this.map=map;
    }
    public void setMovement(Rect r){
        fromP=new Point(r.left,r.top);
        toP=new Point(r.right,r.bottom);

    }
    public void setCamp(byte camp){
        this.camp=camp;
    }
    public void setPlayerName(String name){
        playerName=name;
    }
    //轮到下个玩家
    public void turnToNextPlayer(){
        isMyTurn=!isMyTurn;
    }


}
