package com.example.jokerproject.custom_control;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.example.jokerproject.util.Transform;
import java.util.ArrayList;

public class MyTableUser extends RelativeLayout {
    private final int INIT_TIME = 000;
    private final int MINE_TIME = 010;
    private final int OTHER_TIME = 020;

    private final String TAG = "MyTableUser";

    private ArrayList<MessageJoker> JokerRecord = new ArrayList<>();

    Context context = getContext();

    //用于更新初始化UI
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getChildAt(0).setBackground(ContextCompat.getDrawable(context,Transform.getTransform().toUri(JokerRecord.get(0))));
                    }
                    break;

                default:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        int uri = Transform.getTransform().toUri(JokerRecord.get(msg.what));
                        getChildAt(msg.what).setBackground(ContextCompat.getDrawable(context,uri));
                    }
                    break;
            }
        }
    };

    public MyTableUser(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTableUser(Context context) {
        super(context);
    }

    public ArrayList<MessageJoker> getJokerRecord(){
        return JokerRecord;
    }

    /**
     * 通过Message发送指定更新子ImageView,不包含初始化View的操作
     */
    private void transformUri(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (JokerRecord != null){
                    int len = JokerRecord.size();
                    for (int i = 2;i < len; i++){
                        Message message = Message.obtain();
                        message.what = i;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }

    /**
     * 游戏结束时，隐藏牌由外部调用
     */
    public void informFirstJoker(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 0;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * TODO：需要考虑三个状态：初始状态，自我状态，他人状态
     * @param mes
     */
    public void addMesToGroup(MessageJoker mes, int status){
        if (status == INIT_TIME){
            JokerRecord.add(mes);
        }else if (status == MINE_TIME){
            JokerRecord.add(mes);
            transformUri();
        }else if (status == OTHER_TIME){
            JokerRecord.add(mes);
            transformUri();
        }else {
            Log.d(TAG, "ERROR");
        }

    }
}
