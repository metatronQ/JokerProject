package com.example.jokerproject.custom_control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.example.jokerproject.R;
import com.example.jokerproject.util.Analyse;
import com.example.jokerproject.util.Transform;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Croupier extends RelativeLayout{
    public final String TAG = "Croupier";

    private final int INIT_TIME = 000;
    private final int MINE_TIME = 010;
    private final int OTHER_TIME = 020;

    private int startX;
    private int startY;

    /**
     * 储存代表其他玩家的ViewGRoup
     */
    ArrayList<MyTableUser> myUserViews;

    /**
     * 储存代表自己的ViewGroup
     */
    MyTableUserMine myUser;

    /**
     * 储存代表服务器的ViewGroup
     */
    MyTableUser server;


    private ArrayList<MessageJoker[]> messageListsAllRound = new ArrayList<>();

    boolean usedClick = false;
    ArrayList<ImageView> burstJoker = new ArrayList<>();

    public Croupier(Context context) {
        super(context);
    }

    public Croupier(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInitAnimation(ExecutorService executorService,ReentrantLock reentrantLock,Condition condition){
        if (!usedClick) {
            InitialAnimation(400,executorService,reentrantLock,condition);
        }
        usedClick = true;
    }

    /**
     * 接收从TableActivity传来的消息，外部每次调用此方法时相应的ImageView已经被创建
     */
    public void getMessage(MessageJoker[] messageListOneRound,int status){
        this.messageListsAllRound.add(messageListOneRound);
        if (messageListOneRound != null){
            dispatchMes(messageListOneRound,status);
        }
    }

    /**
     * 处理并分发消息,此消息是上层Activity点击后传进来的消息
     * @param message
     */
    private void dispatchMes(MessageJoker[] message,int status){

        if (myUserViews == null && myUser == null && server == null){
            myUserViews = new ArrayList<>();
            LinearLayout linearLayout = findViewById(R.id.users_other);
            for (int i = 0; i < linearLayout.getChildCount();i++){
                myUserViews.add((MyTableUser) linearLayout.getChildAt(i));
            }
            server = findViewById(R.id.server);
            myUser = findViewById(R.id.user_mine);
        }

        //对message进行分段
        if (status == INIT_TIME){
            if (message.length != 2){
                for (int i = 0; i < message.length - 2; i++){
                    myUserViews.get(i).addMesToGroup(message[i],status);
                }
            }
            server.addMesToGroup(message[message.length - 2],status);
            myUser.addMesToGroup(message[message.length - 1],status);

        }else if (status == MINE_TIME){
            myUser.addMesToGroup(message[message.length - 1],status);

        }else if (status == OTHER_TIME){
            if (message.length == 2 || message[message.length - 2].exist){
                server.addMesToGroup(message[message.length - 2],status);
            }else if (message.length > 2){
                for (int i = 0;i < message.length - 2; i++){
                    if (message[i].exist){
                        myUserViews.get(i).addMesToGroup(message[i],status);
                    }
                }
            }


        }
    }


    /**
     * 初始发牌
     * @param time
     */
    private void InitialAnimation(int time, ExecutorService executorService,ReentrantLock reentrantLock,Condition condition) {
        ImageView imageView = (ImageView) findViewById(R.id.dealer);
        startX = imageView.getLeft();
        startY = imageView.getTop();

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "x", startX, myUserViews.get(0).getLeft());
        objectAnimator.setRepeatCount(1);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(imageView, "y", startY, (float) (myUserViews.get(0).getTop() + 180 * 3.5));
        objectAnimator1.setRepeatCount(1);
        animatorSet.playTogether(objectAnimator,objectAnimator1);
        animatorSet.start();


        AnimatorSet animatorSet1 = new AnimatorSet();
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(imageView,"x",startX,myUserViews.get(1).getLeft());
        objectAnimator2.setRepeatCount(1);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(imageView,"y",startY,(float) (myUserViews.get(1).getTop() + 180 * 3.5));
        objectAnimator3.setRepeatCount(1);
        animatorSet1.play(objectAnimator2).with(objectAnimator3).after(animatorSet);
        animatorSet1.start();

        AnimatorSet animatorSet2 = new AnimatorSet();
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(imageView,"x",startX,myUserViews.get(2).getLeft());
        objectAnimator4.setRepeatCount(1);
        ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(imageView,"y",startY,(float) (myUserViews.get(2).getTop() + 180 * 3.5));
        objectAnimator5.setRepeatCount(1);
        animatorSet2.play(objectAnimator4).with(objectAnimator5).after(animatorSet1);
        animatorSet2.start();

        AnimatorSet animatorSet3 = new AnimatorSet();
        ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(imageView,"x",startX,myUserViews.get(3).getLeft());
        objectAnimator6.setRepeatCount(1);
        ObjectAnimator objectAnimator7 = ObjectAnimator.ofFloat(imageView,"y",startY,(float) (myUserViews.get(3).getTop() + 180 * 3.5));
        objectAnimator7.setRepeatCount(1);
        animatorSet3.play(objectAnimator6).with(objectAnimator7).after(animatorSet2);
        animatorSet3.start();

        AnimatorSet animatorSet4 = new AnimatorSet();
        ObjectAnimator objectAnimator8 = ObjectAnimator.ofFloat(imageView,"x",startX,myUserViews.get(4).getLeft());
        objectAnimator8.setRepeatCount(1);
        ObjectAnimator objectAnimator9 = ObjectAnimator.ofFloat(imageView,"y",startY, (float) (myUserViews.get(4).getTop() + 180 * 3.5));
        objectAnimator9.setRepeatCount(1);
        animatorSet4.play(objectAnimator8).with(objectAnimator9).after(animatorSet3);
        animatorSet4.start();

        AnimatorSet animatorSet5 = new AnimatorSet();
        ObjectAnimator objectAnimator10 = ObjectAnimator.ofFloat(imageView,"x",startX,myUser.getLeft());
        objectAnimator10.setRepeatCount(1);
        ObjectAnimator objectAnimator11 = ObjectAnimator.ofFloat(imageView,"y",startY,myUser.getTop());
        objectAnimator11.setRepeatCount(1);
        animatorSet5.play(objectAnimator10).with(objectAnimator11).after(animatorSet4);
        animatorSet5.setDuration(time);
        animatorSet5.start();

        AnimatorSet animatorSetBack = new AnimatorSet();
        ObjectAnimator vanish = ObjectAnimator.ofFloat(imageView,"alpha",1.0f,0f);
        animatorSetBack.play(vanish).after(animatorSet5);
        animatorSetBack.start();

        AnimatorSet animatorSetBack2 = new AnimatorSet();
        ObjectAnimator objectAnimator12 = ObjectAnimator.ofFloat(imageView,"x",myUser.getLeft(),startX);
        ObjectAnimator objectAnimator13 = ObjectAnimator.ofFloat(imageView,"y",myUser.getTop(),startY);
        animatorSetBack2.play(objectAnimator12).with(objectAnimator13).after(animatorSetBack);
        animatorSetBack2.start();

        AnimatorSet animatorSetBack3 = new AnimatorSet();
        ObjectAnimator objectAnimator14 = ObjectAnimator.ofFloat(imageView,"alpha",0f,1f);
        animatorSetBack3.play(objectAnimator14).after(animatorSetBack2);
        animatorSetBack3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                int length = messageListsAllRound.get(0).length;

                if (length == 2){
                    addImageInit(server,0,R.drawable.back);
                    addImageInit(server,1,Transform.getTransform().toUri(messageListsAllRound.get(1)[0]));

                    addImageInit(myUser,0,Transform.getTransform().toUri(messageListsAllRound.get(0)[1]));
                    addImageInit(myUser,1,Transform.getTransform().toUri(messageListsAllRound.get(1)[1]));
                }else {
                    for (int i = 0;i < length - 2;i++) {
                        addImageInit(myUserViews.get(i), 0, Transform.getTransform().toUri(messageListsAllRound.get(0)[i]));
                        addImageInit(myUserViews.get(i), 1, Transform.getTransform().toUri(messageListsAllRound.get(1)[i]));
                    }
                    addImageInit(server,0,R.drawable.back);
                    addImageInit(server,1,Transform.getTransform().toUri(messageListsAllRound.get(1)[length - 2]));

                    addImageInit(myUser,0,Transform.getTransform().toUri(messageListsAllRound.get(0)[length - 1]));
                    addImageInit(myUser,1,Transform.getTransform().toUri(messageListsAllRound.get(1)[length - 1]));
                }

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        reentrantLock.lock();
                        condition.signalAll();
                        reentrantLock.unlock();
                    }
                });
            }
        });
        animatorSetBack3.start();
    }

    /**
     * 对每次发的牌添加View加以控制-初始化
     * @param groupItem
     */
    private void addImageInit(ViewGroup groupItem,int times,int uri){
        ImageView imageView = new ImageView(groupItem.getContext());
        Drawable drawable = ContextCompat.getDrawable(this.getContext(),uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(drawable);
        }
        LayoutParams layoutParams = new LayoutParams(120,160);
        layoutParams.addRule(RelativeLayout.ALIGN_LEFT,groupItem.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_TOP,groupItem.getId());
        layoutParams.setMargins(times * 80, 0, 0, 0);
        imageView.setLayoutParams(layoutParams);
        groupItem.addView(imageView);
    }

    public void addImage(MessageJoker[] mes,int status,int times){
        if (status == MINE_TIME) {
            ImageView imageView = new ImageView(myUser.getContext());
            Drawable drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.back);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackground(drawable);
            }
            LayoutParams layoutParams = new LayoutParams(120, 160);
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT, myUser.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, myUser.getId());

            if (times == 2){
                layoutParams.setMargins(times * 80, 0, 0, 0);
            }else if (times > 2){
                layoutParams.setMargins((times - 3) * 80, 160, 0, 0);
            }

            imageView.setLayoutParams(layoutParams);
            myUser.addView(imageView);
        }else if (status == OTHER_TIME){
            if (mes.length == 2 || mes[mes.length - 2].exist){
                ImageView imageView = new ImageView(server.getContext());
                Drawable drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.back);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.setBackground(drawable);
                }
                LayoutParams layoutParams = new LayoutParams(120, 160);
                layoutParams.addRule(RelativeLayout.ALIGN_LEFT, server.getId());
                layoutParams.addRule(RelativeLayout.ALIGN_TOP, server.getId());

                if (times == 2){
                    layoutParams.setMargins(times * 80, 0, 0, 0);
                }else if (times > 2){
                    layoutParams.setMargins((times - 3) * 80, 160, 0, 0);
                }

                imageView.setLayoutParams(layoutParams);
                server.addView(imageView);
            } else {
                for (int i = 0; i < mes.length - 2; i++){
                    if (mes[i].exist) {
                        MyTableUser myTableUser = myUserViews.get(i);
                        ImageView imageView = new ImageView(myTableUser.getContext());
                        Drawable drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.back);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imageView.setBackground(drawable);
                        }
                        LayoutParams layoutParams = new LayoutParams(120, 160);
                        layoutParams.addRule(RelativeLayout.ALIGN_LEFT, myTableUser.getId());
                        layoutParams.addRule(RelativeLayout.ALIGN_TOP, myTableUser.getId());

                        if (times == 2){
                            layoutParams.setMargins(times * 80, 0, 0, 0);
                        }else if (times > 2){
                            layoutParams.setMargins((times - 3) * 80, 160, 0, 0);
                        }

                        imageView.setLayoutParams(layoutParams);
                        myTableUser.addView(imageView);
                        break;
                    }
                }
            }
        }

        getMessage(mes,status);
    }

    /**
     * 爆牌后的操作
     * @param item
     * @param status
     */
    public void setForkBurst(int item,int status,String player){
        if (status == OTHER_TIME){
            if (messageListsAllRound.get(0).length == 2 || "server".equals(player)){
                ImageView imageView = new ImageView(server.getContext());
                Drawable drawable = ContextCompat.getDrawable(this.getContext(),R.drawable.burst);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.setBackground(drawable);
                }
                LayoutParams layoutParams = new LayoutParams(300,300);
                layoutParams.addRule(RelativeLayout.ALIGN_LEFT,server.getId());
                layoutParams.addRule(RelativeLayout.ALIGN_TOP,server.getId());
                imageView.setLayoutParams(layoutParams);
                server.addView(imageView);
            }else {
                ImageView imageView = new ImageView(myUserViews.get(item).getContext());
                Drawable drawable = ContextCompat.getDrawable(this.getContext(),R.drawable.burst);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.setBackground(drawable);
                }
                LayoutParams layoutParams = new LayoutParams(300,300);
                layoutParams.addRule(RelativeLayout.ALIGN_LEFT,myUserViews.get(item).getId());
                layoutParams.addRule(RelativeLayout.ALIGN_TOP,myUserViews.get(item).getId());
                imageView.setLayoutParams(layoutParams);
                myUserViews.get(item).addView(imageView);
            }
        }else if (status == MINE_TIME){
            ImageView imageView = new ImageView(myUser.getContext());
            Drawable drawable = ContextCompat.getDrawable(this.getContext(),R.drawable.burst);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackground(drawable);
            }
            LayoutParams layoutParams = new LayoutParams(300,300);
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT,myUser.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_TOP,myUser.getId());
            imageView.setLayoutParams(layoutParams);
            myUser.addView(imageView);
        }
    }

    /**
     * 显示服务器的第一张牌
     */
    public void showFirstJoker(){
        server.informFirstJoker();
//        if (messageListsAllRound.get(0).length == 2){
//           server.informFirstJoker();
//           myUser.informFirstJoker();
//        }else {
//            for (int i = 0; i < messageListsAllRound.get(0).length - 2; i++){
//                myUserViews.get(i).informFirstJoker();
//            }
//            server.informFirstJoker();
//            myUser.informFirstJoker();
//        }
    }

    /**
     * 得到所有的牌值
     * @return
     */
    public ArrayList<ArrayList<MessageJoker>> getAllJoker(){
        ArrayList<ArrayList<MessageJoker>> m = new ArrayList<>();
        if (messageListsAllRound.get(0).length == 2){
            m.add(server.getJokerRecord());
            m.add(myUser.getJokerRecord());
        }else {
            for (int i = 0; i < messageListsAllRound.get(0).length - 2; i++){
                m.add(myUserViews.get(i).getJokerRecord());
            }
            m.add(server.getJokerRecord());
            m.add(myUser.getJokerRecord());
        }
        return m;
    }

    /**
     * 如果玩家选择要再玩，则根据人数清除掉对应的Jokers
     * @param count
     */
    public void removeAllJokers(int count){
        messageListsAllRound.clear();
//        setAllVisible();
        if (count == 2){
            myUser.removeAllViews();
            myUser.getJokerRecord().clear();
            server.removeAllViews();
            server.getJokerRecord().clear();
        }else {
            for (int i = 0;i < count - 2;i++){
                myUserViews.get(i).removeAllViews();
                myUserViews.get(i).getJokerRecord().clear();
            }
            myUser.removeAllViews();
            myUser.getJokerRecord().clear();
            server.removeAllViews();
            server.getJokerRecord().clear();
        }
        usedClick = false;
    }

//    /**
//     *  只能由{@method removeAllJokers()}调用,是当玩家选择继续下一局时，将所有的玩家爆牌操作归零（暂）
//     */
////    private void setAllVisible(){
////        for (int i = 0;i < myUserViews.size(); i++){
////            myUserViews.get(i).setVisibility(VISIBLE);
////        }
////        server.setVisibility(VISIBLE);
////        myUser.setVisibility(VISIBLE);
////    }
}
