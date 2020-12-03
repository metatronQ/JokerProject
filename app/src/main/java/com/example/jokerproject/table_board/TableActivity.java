package com.example.jokerproject.table_board;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.jokerproject.R;
import com.example.jokerproject.base.BaseActivity;
import com.example.jokerproject.base.BasePresent;
import com.example.jokerproject.custom_control.Croupier;
import com.example.jokerproject.custom_control.MessageJoker;
import com.example.jokerproject.util.Analyse;
import com.example.jokerproject.util.Transform;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.BindViews;

public class TableActivity extends BaseActivity{

    Context context = this;

    /**
     * 用来储存player固定的位置
     */
    private ArrayList<String> INIT_ORDER = new ArrayList<>();

    /**
     * 用于全局的储存信息体的数组
     */
    MessageJoker[] messageJokers;

    /**
     * 储存初始两张牌
     */
    MessageJoker[][] messageJokersTwo;

    /**
     * 本机的位置
     */
    private int ORDER;
    public final String TAG = "TableActivity";
    Socket socket;
    static InputStreamReader inputStreamReader;
    static OutputStreamWriter outputStreamWriter;
    Croupier croupier = null;
    ReentrantLock reentrantLock = new ReentrantLock();
    Condition condition = reentrantLock.newCondition();
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    StringBuffer betMes = new StringBuffer();
    int betCount = 0;
    int initFrequency = 0;
    int initCount = 0;
    int timeCount = 2;
    int allPlayers = 0;
    int winPlayers;
    int winCount = 0;
    ProgressDialog progressDialog;
    ArrayList<String> results = new ArrayList<>();
    public String player;

    Handler progresssHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    progressDialog = new ProgressDialog(TableActivity.this);
                    progressDialog.setTitle("您已连接上服务器,请耐心等待");
                    progressDialog.setMessage("正在寻找其他玩家...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    break;

                case 1:
                    progressDialog.dismiss();
                    break;

                case 2:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TableActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("玩家已全部准备就绪\n现在请您下注,按下注键下注结束");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //将下注要用到的按钮激活
                            setBetClickable();
                        }
                    });
                    dialog.show();
                    break;

                case 3:
                    progressDialog = new ProgressDialog(TableActivity.this);
                    progressDialog.setTitle("请等待其他玩家下注");
                    progressDialog.setMessage("您已下注" + msg.arg1);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    break;

                case 4:
                    fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            reentrantLock.lock();
                            condition.signalAll();
                            reentrantLock.unlock();
                        }
                    });
                    break;

                case 5:
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(TableActivity.this);
                    dialog2.setTitle("提示");
                    dialog2.setMessage("轮到您操作了");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //将要牌要用到的按钮激活
                            setMyClick();
                        }
                    });
                    dialog2.show();
                    break;

                case 6:
                    AlertDialog.Builder dialog3 = new AlertDialog.Builder(TableActivity.this);
                    dialog3.setTitle("提示");
                    dialog3.setMessage("确认不再要牌了吗？");
                    dialog3.setCancelable(false);
                    dialog3.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setDisclick();
                            fixedThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        outputStreamWriter.write("06@0000000");
                                        outputStreamWriter.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                    dialog3.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context,"请继续要牌",Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog3.show();
                    break;

                case 7:
                    croupier.showFirstJoker();
                    break;

                case 8:
                    ArrayList<ArrayList<MessageJoker>> m = croupier.getAllJoker();
                    int[] ints = new int[m.size()];
                    for (int i = 0; i < m.size(); i++){
                        ArrayList<MessageJoker> a = m.get(i);
                        int countBig = 0;
                        for (int j = 0; j < a.size();j++){
                             countBig += Transform.getTransform().toNumber(a.get(j));
                        }

                        //当牌值大于21时，如果有A，会将A的牌值减10
                        if (countBig > 21){
                            int counta = countA(a);
                            if (counta > 0){
                                countBig -= 10 * counta;
                            }
                        }
                        ints[i] = countBig;
                    }

                    StringBuffer stringBuffer = new StringBuffer();
                    for (int z = 0; z < m.size();z++){
                        if (ints[z] > 21){
                            stringBuffer.append(INIT_ORDER.get(z)).append("的牌爆了\n");
                        }else {
                            stringBuffer.append(INIT_ORDER.get(z)).append("的点数为").append(ints[z]).append("\n");
                        }
                    }

                    if (msg.arg1 == 0){
                        stringBuffer.append("\n可惜，只差一点就赢了，是平局");
                    }else {
                        stringBuffer.append("\n有" + msg.arg1 + "个玩家赢了\n");
                        for (int i = 0;i < results.size();i++){
                            stringBuffer.append("分别是" + Transform.getTransform().toPlayer(results.get(i)) + "赢了\n");
                        }
                        if (boolI(results)){
                            stringBuffer.append("\n您赢了！！666666！！");
                        }
                    }

                    AlertDialog.Builder dialog4 = new AlertDialog.Builder(TableActivity.this);
                    dialog4.setTitle("游戏结束");
                    dialog4.setMessage(stringBuffer);
                    dialog4.setCancelable(false);
                    dialog4.setPositiveButton("我要接着玩", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setDisclick();
                            setInitAll();
                        }
                    });
                    dialog4.setNegativeButton("溜了溜了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    dialog4.show();
                    break;
            }
        }
    };

    Handler toastHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            String s = (String) msg.obj;
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
        }
    };


    Handler initJoker = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            MessageJoker[][] mm = (MessageJoker[][]) msg.obj;

            croupier.getMessage(changeOrder(mm[0]),000);
            croupier.getMessage(changeOrder(mm[1]),000);

            messageJokers = mm[1];
            //为下一步每个玩家单独更新准备容量
            setFalse();
        }
    };

    Handler initAnimation = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            croupier.setInitAnimation(fixedThreadPool,reentrantLock,condition);
        }
    };

    Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 5:
                    croupier.addImage(messageJokers,020,msg.arg1);
                    setFalse();
                    break;

                case 6:
                    croupier.addImage(messageJokers,010,msg.arg1);
                    setFalse();
                    break;
            }
        }
    };

    Handler explosionHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 6:
                    croupier.setForkBurst(111,010,"my");
                    break;

                case 7:
                    croupier.setForkBurst(000,020,"server");
                    break;
                default:
                    croupier.setForkBurst(msg.what,020,"other");
                    break;
            }
        }
    };

    @BindViews({R.id.jetton_1,R.id.jetton_5,R.id.jetton_10,R.id.jetton_50,R.id.jetton_100,R.id.cancel,R.id.check,R.id.deal})
    List<ImageView> listBase;

    @OnClick({R.id.jetton_1,R.id.jetton_5,R.id.jetton_10,R.id.jetton_50,R.id.jetton_100,R.id.cancel,R.id.check,R.id.deal})
    public void onClick(ImageView view) {
            switch (view.getId()) {
                //TODO:提示下注后，本机进行点击下注，下完注后提示对话框确认，然后向服务器发送消息
                case R.id.jetton_1:
                    Toast.makeText(this, "下了1块钱的注", Toast.LENGTH_SHORT).show();
                    betCount += 1;
                    break;

                case R.id.jetton_5:
                    Toast.makeText(this, "下了5块钱的注", Toast.LENGTH_SHORT).show();
                    betCount += 5;
                    break;

                case R.id.jetton_10:
                    Toast.makeText(this, "下了10块钱的注", Toast.LENGTH_SHORT).show();
                    betCount += 10;
                    break;

                case R.id.jetton_50:
                    Toast.makeText(this, "下了50块钱的注", Toast.LENGTH_SHORT).show();
                    betCount += 50;
                    break;

                case R.id.jetton_100:
                    Toast.makeText(this, "下了100块钱的注", Toast.LENGTH_SHORT).show();
                    betCount += 100;
                    break;

                //下面三个按钮是需要向服务器请求数据的,请求的是本机数据
                case R.id.cancel:
                    Toast.makeText(this, "结束", Toast.LENGTH_SHORT).show();
                    //确认结束
                    Message messageOut = Message.obtain();
                    messageOut.what = 6;
                    progresssHandler.sendMessage(messageOut);
                    break;

                case R.id.check:
                    Toast.makeText(this, "下注结束", Toast.LENGTH_SHORT).show();

                    betMes.append("01@000").append(betCount/1000%10).append(betCount/100%10).append(betCount/10%10).append(betCount%10);
                    Message message = Message.obtain();
                    message.what = 4;
                    progresssHandler.sendMessage(message);

                    break;

                case R.id.deal:
                    Toast.makeText(this, "发牌", Toast.LENGTH_SHORT).show();

                    fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                outputStreamWriter.write("04@0000000");
                                outputStreamWriter.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                default:
                    break;
            }
    }

    /**
     * 初始化
     * @param savedInstanceState
     */
    @Override
    protected void initView(Bundle savedInstanceState) {
        //绑定相应的View
        ButterKnife.bind(this);

        //初始化 INIT_ORDER，为玩家顺序的确定做好准备
        setInitOrder();

        //获得荷官类，获取所有玩家类的控制中心
        croupier = findViewById(R.id.croupier);

        //游戏开始前，所有按钮默认不可点击
        setDisclick();

        //弹出一个等待的对话框，此对话框在获取服务器连接之后关闭
        progressDialog = new ProgressDialog(TableActivity.this);
        progressDialog.setMessage("等待服务器连接...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String host = "103.46.128.43";
        int post = 24286;

        //用线程池开启一个线程
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //将此线程上锁
                    reentrantLock.lock();

                    //协议约定服务器的一次消息大小为10个char类型大小，因此此为承载服务器消息的数组
                    char[] myOrder = new char[10];

                    //在子线程中对socket进行绑定，并进行相关操作
                    socket = new Socket(host,post);
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    inputStreamReader =  new InputStreamReader(inputStream);
                    outputStreamWriter = new OutputStreamWriter(outputStream);

                    //读取服务器信息的主循环，每次进行10char类型大小的读取，当未接收到服务器消息时，子线程被堵塞，直到接收到服务器消息
                    while (inputStreamReader.read(myOrder) > 0){

                        String s = new String(myOrder);

                        //解析消息，获得当前消息的消息载体
                        MessageJoker mes = Analyse.getAnalyse().toMesSection(s);

                        //协议约定以status段0 - 9 值代表不同的消息指令，此处对不同的消息指令分case进行
                        switch (mes.getStatus()){

                            //玩家成功连接至服务器指令，只有确定能够接受服务器的消息后才能算服务器连接成功
                            case "0":

                                //发送消息主线程的对应的Handler，dismiss等待服务器连接对话框
                                Message serveOk = Message.obtain();
                                serveOk.what = 1;
                                progresssHandler.sendMessage(serveOk);

                                //对myPlayer段信息进行提取，确定本机的player顺序
                                String myPlayer = mes.getMyPlayer();
                                StringBuffer stringBuffer = new StringBuffer();
                                stringBuffer.append("player").append(myPlayer);
                                player = new String(stringBuffer);

                                //TODO：对玩家顺序进行调整，固定本机的顺序在INIT_ORDER数组末尾，server玩家在倒数第二位，ORDER记录本机顺序
                                ORDER = INIT_ORDER.indexOf(player);
                                INIT_ORDER.remove(ORDER);
                                INIT_ORDER.add("server");
                                INIT_ORDER.add(player);

                                //通知服务器本机已准备完成
                                outputStreamWriter.write("00@0000000");
                                outputStreamWriter.flush();

                                //发送消息给Handler弹出等待其他玩家就绪的对话框
                                Message myOk = Message.obtain();
                                myOk.what = 0;
                                progresssHandler.sendMessage(myOk);
                                break;

                            //服务器通知开始游戏指令，消息会携带玩家的人数
                            case "1":
                                Log.d(TAG, "开始游戏");

                                //TODO：通过玩家的人数再次重新设置本轮游戏的排序，INIT_ORDER默认玩家人数为7人，当玩家人数不够7人时，会将server前的玩家相应的减去
                                //TODO;！注意，此时玩家顺序已经固定，当消息到来时，都应参照INIT_ORDER的顺序和下标
                                allPlayers = Integer.parseInt(mes.getPlayer());
                                messageJokers = new MessageJoker[allPlayers];
                                messageJokersTwo = new MessageJoker[2][allPlayers];
                                int delete = 7 - allPlayers;
                                if (delete == 0){
                                    break;
                                }else {
                                    for (int i = 4;  i > 4 - delete;i--){
                                        INIT_ORDER.remove(i);
                                    }
                                    ORDER = INIT_ORDER.size() - 1;
                                }

                                //确定玩家顺序后，dismiss等待就绪对话框
                                Message messageBegin = Message.obtain();
                                messageBegin.what = 1;
                                progresssHandler.sendMessage(messageBegin);

                                //提示下注对话框，同时将相应的功能按钮激活
                                Message messageBet = Message.obtain();
                                messageBet.what = 2;
                                progresssHandler.sendMessage(messageBet);

                                //此处进行线程等待，当点击下注完成的按钮时会将线程唤醒，从而进行接下来的操作
                                condition.await();

                                //将所有按钮锁定
                                setDisclick();

                                //提示等待其他玩家下注对话框，并将下注数传递给服务器
                                String bet = new String(betMes);
                                Log.d(TAG, "下注"+ bet + "点");
                                Message messageBetOver = Message.obtain();
                                messageBetOver.what = 3;
                                messageBetOver.arg1 = betCount;
                                progresssHandler.sendMessage(messageBetOver);

                                outputStreamWriter.write(bet);
                                outputStreamWriter.flush();
                                break;

                            //服务器通知所有人下注结束指令
                            case "2":
                                Log.d(TAG, "所有玩家已下注");

                                //dismiss等待其他人下注对话框
                                Message messageBetOverAll = Message.obtain();
                                messageBetOverAll.what = 1;
                                progresssHandler.sendMessage(messageBetOverAll);

                                Log.d(TAG, "开始发牌");

                                //向服务器请求发初始牌
                                outputStreamWriter.write("02@0000000");
                                outputStreamWriter.flush();
                                break;

                            //接收初始牌指令，共需接收 玩家数*2 次
                            case "3":
                                //initFrequency代表接收的是该玩家初始牌的第几张，initCount表示接收的是第几个玩家的牌，因为服务器的发牌顺序为接连发同一个玩家的两张初始牌

                                if (initFrequency == 0){
                                    messageJokersTwo[0][initCount] = mes;
                                    initFrequency++;

                                }else if (initFrequency == 1){
                                    messageJokersTwo[1][initCount] = mes;
                                    initCount++;
                                    initFrequency = 0;
                                }

                                //当初始牌发完后，通过Handler向Croupier输出初始牌，并通过其内部方法和INIT_ORDER顺序将牌分发给指定位置
                                if (allPlayers == initCount){
                                    Message message = Message.obtain();
                                    message.obj = messageJokersTwo;
                                    initJoker.sendMessage(message);

                                    //启动初始牌分发动画，并让此线程进行等待
                                    Message message1 = Message.obtain();
                                    initAnimation.sendMessage(message1);

                                    //当动画结束时，会唤醒此线程通知服务器接收初始牌完成
                                    condition.await();

                                    setToast("动画结束了");
                                    outputStreamWriter.write("03@0000000");
                                    outputStreamWriter.flush();
                                }
                                break;

                            //服务器指定玩家操作指令
                            case "4":
                                //每当更换玩家操作时，服务器都必须先发一条此消息，来告知所有玩家该谁操作

                                String ss = Transform.getTransform().toPlayer(mes.getPlayer());
                                Log.d(TAG, "该" + ss + "操作了");
                                setToast("该" + ss + "操作了");
                                //更换玩家操作时，要牌次数重置，默认要第三张牌（0，1，2）
                                timeCount = 2;

                                //判断是本机操作：通过比较接收的玩家顺序是否与ORDER相等
                                if (INIT_ORDER.indexOf(Transform.getTransform().toPlayer(mes.getPlayer())) == ORDER){

                                    //设置本机操作提示
                                    Message message = Message.obtain();
                                    message.what = 5;
                                    progresssHandler.sendMessage(message);
                                }

                                break;

                            //服务器发牌指令（要的牌）
                            case "5":
                                //当接收到其他玩家的牌时,会通过Handler通知相应的玩家类更新牌
                                // 注意，每次更新后需将消息体集合的消息exist全部设为false，以便于分发时的判断和messageJokers数组的重用
                                //timeCount会记录玩家要的牌数已对应相应的UI位置显示，当换人时，服务器发送的消息会令该变量重置

                                if (INIT_ORDER.indexOf(Transform.getTransform().toPlayer(mes.getPlayer())) != ORDER){
                                    //不是本机操作
                                    int order = INIT_ORDER.indexOf(Transform.getTransform().toPlayer(mes.getPlayer()));
                                    messageJokers[order] = mes;

                                    Message messageA = Message.obtain();
                                    messageA.what = 5;
                                    messageA.arg1 = timeCount;
                                    UIHandler.sendMessage(messageA);

                                    if (timeCount < 7){
                                        timeCount++;
                                    }
                                }else if (INIT_ORDER.indexOf(Transform.getTransform().toPlayer(mes.getPlayer())) == ORDER){
                                    messageJokers[ORDER] = mes;

                                    Message messageB = Message.obtain();
                                    messageB.what = 6;
                                    messageB.arg1 = timeCount;
                                    UIHandler.sendMessage(messageB);

                                    //当为本机操作时，得到要的牌后需向服务器询问本机是否爆牌
                                    outputStreamWriter.write("05@0000000");
                                    outputStreamWriter.flush();

                                    if (timeCount < 7){
                                        timeCount++;
                                    }
                                }
                                break;

                            //爆牌指令
                            case "6":
                                String explosionPlayer = Transform.getTransform().toPlayer(mes.getPlayer());
                                Log.d(TAG, explosionPlayer + "牌爆了");

                                //得到爆牌玩家的编号
                                int order = INIT_ORDER.indexOf(explosionPlayer);

                                if (order == ORDER){
                                    //本机爆牌

                                    setDisclick();

                                    //通知爆牌UI显示
                                    Message m = Message.obtain();
                                    m.what = 6;
                                    explosionHandler.sendMessage(m);

                                    Log.d(TAG, explosionPlayer + "因爆牌失去游戏资格");

                                    //停止向服务器要牌
                                    outputStreamWriter.write("06@0000000");
                                    outputStreamWriter.flush();

                                }else if (order == ORDER - 1){
                                    //服务器爆牌

                                    Message mm = Message.obtain();
                                    mm.what = 7;
                                    explosionHandler.sendMessage(mm);
                                }else {
                                    //其他玩家爆牌

                                    Message mmm = Message.obtain();
                                    mmm.what = order;
                                    explosionHandler.sendMessage(mmm);
                                }
                                break;

                            //未爆牌指令
                            case "7":
                                String notExplosionPlayer = Transform.getTransform().toPlayer(mes.getPlayer());
                                Log.d(TAG, notExplosionPlayer + "牌未爆");
                                break;

                            //玩家结束指令，即将结束本局游戏
                            case "8":
                                Log.d(TAG, "所有玩家要牌结束");
                                setToast("所有玩家显示暗牌");

                                //handler通知显示服务器的暗牌
                                Message messageDisplay = Message.obtain();
                                messageDisplay.what = 7;
                                progresssHandler.sendMessage(messageDisplay);
                                break;

                            //玩家结果指令
                            case"9":
                                Thread.sleep(5000);

                                //winPlayers 代表赢的人数，0为平局，1为一个人赢，接收一次消息，2为两个人赢，接收两次消息，以此类推
                                winPlayers =  Integer.parseInt(mes.getMyPlayer());
                                if (winPlayers == 0){
                                    //平局
                                    Message res = Message.obtain();
                                    res.what = 8;
                                    res.arg1 = 0;
                                    progresssHandler.sendMessage(res);
                                }else{
                                     results.add(mes.getPlayer());
                                     winCount++;
                                     if (winCount == winPlayers){
                                         //通知计算各玩家点数并弹出显示结果的对话框
                                         Message res = Message.obtain();
                                         res.what = 8;
                                         res.arg1 = winPlayers;
                                         progresssHandler.sendMessage(res);
                                     }
                                }

                        }
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Socket: Don't connect");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    reentrantLock.unlock();
                }
            }
        });
    }

    @Override
    protected BasePresent createPresenter() {
        return null;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_table;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        INIT_ORDER.clear();
        fixedThreadPool.shutdown();
    }

    /**
     * 玩家选择在玩后，将全部该初始化的信息初始化
     */
    public void setInitAll(){
        croupier.removeAllJokers(allPlayers);

        INIT_ORDER.clear();
        setInitOrder();
        messageJokers = null;
        messageJokersTwo = null;
        betMes = new StringBuffer();
        betCount = 0;
        initFrequency = 0;
        initCount = 0;
        timeCount = 2;
        allPlayers = 0;
        winCount = 0;
        results.clear();

        progressDialog = new ProgressDialog(TableActivity.this);
        progressDialog.setTitle("您已连接上服务器,请耐心等待");
        progressDialog.setMessage("正在寻找其他玩家...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //确定顺序
        ORDER = INIT_ORDER.indexOf(player);
        INIT_ORDER.remove(ORDER);
        //默认本机在6，server在5
        INIT_ORDER.add("server");
        INIT_ORDER.add(player);

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                //告诉服务器本机准备好了
                try {
                    outputStreamWriter.write("00@0000000");
                    outputStreamWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * 设置子线程中的Toast
     * @param s
     */
    public void setToast(String s){
        Message message = Message.obtain();
        message.obj = s;
        toastHandler.sendMessage(message);
    }

    /**
     * 按照INIT_ORDER改变消息顺序
     * @return
     */
    public MessageJoker[] changeOrder(MessageJoker[] mes){
        //临时固定格式
        MessageJoker[] mess = new MessageJoker[mes.length];
        //固定信息体集合的顺序
        for (int i = 0; i < mes.length; i++){
            MessageJoker messageJokerChange = mes[i];
            int order = INIT_ORDER.indexOf(Transform.getTransform().toPlayer(messageJokerChange.getPlayer()));
            mess[order] = messageJokerChange;
        }
        return mess;
    }

    /**
     * 初始化 INIT_ORDER
     */
    private void setInitOrder(){
        INIT_ORDER.add("player1");
        INIT_ORDER.add("player2");
        INIT_ORDER.add("player3");
        INIT_ORDER.add("player4");
        INIT_ORDER.add("player5");
        //p6为本机
        INIT_ORDER.add("player6");

    }

    /**
     *  其他玩家操作和默认时设置不可点击
     */
    public void setDisclick(){
        for (ImageView imageView: listBase){
            imageView.setClickable(false);
        }
    }

    /**
     *  下注时，设置指定按钮可点击
     */
    public void setBetClickable(){
        for (int i = 0;i < 5;i++){
            listBase.get(i).setClickable(true);
        }
        listBase.get(6).setClickable(true);
    }

    /**
     * 轮到本机时，设置三个功能按钮可点击
     */
    public void setMyClick(){
        for (int i = 0; i < 3;i++){
            listBase.get(i + 5).setClickable(true);
        }
    }

    /**
     * 将之后的消息集合体的现存的消息无效化，哪个消息来了就设置为true(消息转换得到的MessageJoker默认为true)
     */
    public void setFalse(){
        for (int i = 0; i < messageJokers.length;i++){
            messageJokers[i].setExist(false);
        }
    }

    /**
     * 用于最后结果判断本机是否获胜
     * @param strings
     * @return
     */
    public boolean boolI(ArrayList<String> strings){
        for (int i = 0; i < strings.size();i++){
            if (player.equals(Transform.getTransform().toPlayer(strings.get(i)))){
                return true;
            }
        }
        return false;
    }

    /**
     * 返回当前信息体集合的含A的个数
     * @param s
     * @return
     */
    public int countA(ArrayList<MessageJoker> s){
        int countA = 0;
        for (int i = 0;i < s.size();i++){
            if ("01".equals(s.get(i).getNumber())){
                countA++;
            }
        }
        return countA;
    }

}
