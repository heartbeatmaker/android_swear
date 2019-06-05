package com.example.swearVer2;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Main extends AppCompatActivity {

    private SensorService sensorService;
    Intent serviceIntent;
    Context context;

    public static boolean isFreindMode; //친구의 약속을 열람중인지 표시

    private TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;

    RecyclerView rcv;
    LinearLayoutManager llm;
    public static MainAdapter mainAdapter;

    LinearLayout recommendBar;
    TextView recommend_textView, recommend_title_textView, userName_textView, unreadMessage_textView, unreadAlarmMessage_textView;
    public static ArrayList<Item> itemArrayList;

    private boolean isDestroyed;
    RecommendAd recAd;

    private String currentTime = getCurrentTime();//


    public Context getContext(){
        return context;
    }

    public String getData(String key){

        String data = "데이터 없음";

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");

        //Preference에서 현재 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(currentUser, "");
        Log.d("마이페이지", "json 파싱함. userInfo에 있는 정보 전부 출력: "+userInfo);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);
            Log.d("마이페이지", "json 파싱함. object에 있는 정보 전부 출력: " + jsonObject);

            if(jsonObject.containsKey(key)) {
                data = (String) jsonObject.get(key);
            }

        }catch(Exception e){

        }
        return data;
    }

    public int getIntData(String key){

        int data = 0;

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");

        //Preference에서 현재 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(currentUser, "");
        Log.d("마이페이지", "json 파싱함. userInfo에 있는 정보 전부 출력: "+userInfo);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);
            Log.d("마이페이지", "json 파싱함. object에 있는 정보 전부 출력: " + jsonObject);

            if(jsonObject.containsKey(key)) {
                data = ((Long) jsonObject.get(key)).intValue();
            }

        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }
        return data;
    }

    public void setData(String key, String newValue){

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            //기존에 해당 키가 있으면 삭제한다
            if(jsonObject.containsKey(key)){
                jsonObject.remove(key);
            }

            //새로운 값을 넣어준다
            jsonObject.put(key, newValue);

            //값을 string 형으로 변환한 후, sharedPreference에 저장한다
            editor.putString(currentUser, jsonObject.toString());
            Log.d("마이페이지", "닉네임 변경함. object에 있는 정보 전부 출력: " + jsonObject);

            editor.commit();

            Map<String, ?> allEntries = sp.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("마이페이지", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
            }



        }catch(Exception e){

        }
    }

    //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
    private void setProfileImage(){

        ImageView profileImage = (ImageView)findViewById(R.id.main_profile_ImageView);

        try {
            String imagePath = getData("profileImagePath");

            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                if (myBitmap != null) {
                    ExifInterface ei = new ExifInterface(imagePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation){
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(myBitmap,90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(myBitmap,180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(myBitmap,270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = myBitmap;
                    }
                    profileImage.setImageBitmap(rotatedBitmap);
                }
            }
        }catch(Exception e){

            Log.d("마이페이지","onCreate(), 사진없음");
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
    }

    private void setUsername(){
        TextView username_textView = (TextView)findViewById(R.id.main_username_textView);
        username_textView.setText(getData("username"));
    }


    //현재 날짜, 시간을 구하기
    public String getCurrentTime(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
        String date = format.format(System.currentTimeMillis());
        return date;
    }


    //두 날짜 사이의 차이 계산
    public int calDateBetweenDates(String startDate){

        int result = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date formattedStartDate = format.parse(startDate);
            Date formattedEndDate = format.parse(currentTime);

            long calDate = formattedStartDate.getTime() - formattedEndDate.getTime();
            long days = Math.abs(calDate / (24 * 60 * 60 * 1000));
            result = (int)days;

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("날짜계산 오류",ex);
        }

        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("알람","메인화면 onCreate()");
        context = this;
        setContentView(R.layout.activity_main);

        //--------------------뷰 페이저----------------------> 동작 안 함
//        initViewPager();
        //---------------------------------------------------

        sensorService = new SensorService(getContext());
        serviceIntent = new Intent(getContext(), SensorService.class);

        //지금 서비스가 실행되고 있지 않다면 -> 서비스를 실행한다
        if(!isMyServiceRunning(sensorService.getClass())){
            startService(serviceIntent);
        }

        requestRecordAudioPermission();
        initializeTextToSpeech();

        isDestroyed = false;

        //약속 추천창
        recommendBar = (LinearLayout)findViewById(R.id.recommendBar_linearLayout);
        recommend_textView = (TextView)findViewById(R.id.rec_text);
        recommend_title_textView = (TextView)findViewById(R.id.recommend_title_textView);
        recommendBar.setVisibility(View.GONE);

        userName_textView = (TextView)findViewById(R.id.main_username_textView);
        unreadMessage_textView = (TextView)findViewById(R.id.main_unreadMessage_textView);
        unreadAlarmMessage_textView = (TextView)findViewById(R.id.main_unreadAlarmMessage_textView);

        //사용자가 읽지 않은 메시지, 알람메시지의 개수 - 안 읽은 메시지가 생길 때까지 화면에서 숨겨놓는다
        unreadMessage_textView.setVisibility(View.GONE);
        unreadAlarmMessage_textView.setVisibility(View.GONE);

        setUnreadMessage();


        //onCreate()에서 asynctask가 실행되면, textToSpeech가 동작하지 않음 -> onStop()으로 옮김
        //첫순간에는 랜덤으로 뿌리고, 2초 뒤에 execute되도록 설정할 것!! --고치기@@@@@@
//        recAd = new RecommendAd();
//        recAd.execute();


        //리사이클러뷰
        rcv = (RecyclerView)findViewById(R.id.main_recycler);
        llm = new LinearLayoutManager(this);

        rcv.setHasFixedSize(true); //리사이클러뷰 자체의 크기가 변하지 않을 때 - 성능개선(?) - 무슨 뜻?
        rcv.setLayoutManager(llm); //리사이클러뷰의 레이아웃을 linear로 맞춘다

        itemArrayList = new ArrayList<>(); //Item 클래스 형의 데이터를 리스트화 시킨다

        //현재 액티비티와 item리스트를 매개변수로 갖는 어댑터 객체를 만든다
        mainAdapter = new MainAdapter(this, itemArrayList);

        //어댑터 객체를 현 액티비티의 리사이클러뷰와 연결시킨다
        rcv.setAdapter(mainAdapter);


        SharedPreferences sp = getSharedPreferences(getCurrentUser()+";goalList", MODE_PRIVATE);
        int listSize = sp.getInt("size", 0);
        Log.d("저장", "리스트 사이즈를 불러옴. 리스트 사이즈 : "+listSize);

        if(listSize>0){
            for(int i=0; i<listSize; i++){

                String itemPreference = sp.getString(String.valueOf(i),""); //약속별 preference 이름을 불러오기
                Log.d("저장", "약속별 preference 이름을 불러옴."+i+"번째 아이템의 xml이름: "+sp.getString(String.valueOf(i),""));

                SharedPreferences sspp = getSharedPreferences(itemPreference, MODE_PRIVATE);
                String goalTitle = sspp.getString("goalTitle","제목없음"); //약속의 제목

                String startDate = sspp.getString("goalStartDate","시작x"); //약속 시작날짜
                String endDate = sspp.getString("goalEndDate","끝x"); //약속 종료날짜

                //진행률 계산
                int numberOfDays = sspp.getInt("numberOfDays", 0); //총 이행기간
                int passedDays = calDateBetweenDates(startDate); //오늘까지 지난 기간
                double progressRate = passedDays / (double)numberOfDays; //int끼리 계산하면 소수점자리를 잃는다 -> 결과값이 0이 나옴. 실수로 형변환하여 계산해야함
                int finalProgressRate = (int)(Math.round(progressRate*100));
                if(finalProgressRate > 100){
                    finalProgressRate = 100;
                }

                itemArrayList.add(i,new Item(finalProgressRate,
                        goalTitle, startDate+"~"+endDate, itemPreference));
            }
        }

        //-----------------------//


        initializeSpeechRecognition();

        FloatingActionButton voiceAssistantBtn = (FloatingActionButton)findViewById(R.id.mainActivity_floatingActionButton);
        voiceAssistantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                speechRecognizer.startListening(intent);

            }
        });


        //화면 중간에 있는 'create'버튼을 누른다 -> 약속 새로만들기 화면이 나타난다
        Button b = (Button) findViewById(R.id.mainCreateBtn);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateNewGoals.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

        });


        //화면 상단에 있는 프로필 사진을 클릭하면 -> 회원정보 설정 화면이 나타난다
        ImageView img = (ImageView)findViewById(R.id.main_profile_ImageView);
        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }

        });


        //화면 상단 바에 있는 '메시지'모양 아이콘을 누르면 -> 친구가 나에게 보낸 메시지를 확인할 수 있다
        ImageButton readMessage_btn = (ImageButton)findViewById(R.id.main_readMessage_ImageBtn);
        readMessage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReadMessage.class);
                startActivity(intent);
            }
        });


        //화면 상단 바에 있는 '사람 모양' 아이콘을 누르면 -> 친구의 약속을 검색할 수 있는 대화상자가 나타난다
//        final TextView search_result = (TextView)findViewById(R.id.search_result);
        ImageButton search = (ImageButton)findViewById(R.id.searchbtn);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             Intent intent = new Intent(getApplicationContext(), FriendManagingActivity.class);
             startActivity(intent);


//                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
//
//                final EditText editText = new EditText(Main.this);
//                editText.setText("111@111.com");
//
//                builder.setTitle("친구찾기").setMessage("친구의 Email을 입력하세요").setView(editText)
//                        .setPositiveButton("검색", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                String friendsEmail = editText.getText().toString();
//
//                                //해당 이메일로 등록된 사용자가 있는지 확인한다
//                                SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
//
//                                //있으면 -> 인텐트로 이메일을 담아서 보낸다
//                                if(sp.contains(friendsEmail)){
//                                    Intent intent = new Intent(getApplicationContext(), CheckFriendsGoal.class);
//                                    intent.putExtra("friendsEmail", friendsEmail);
//                                    startActivity(intent);
//
//                                    //친구가 열람중인지 표시 - CheckFriendsGoal화면에서 backpress 눌렀을 때 false로 바뀜
//                                    isFreindMode = true;
//                                    dialog.dismiss();
//                                }else{
//                                    Toast.makeText(Main.this, "존재하지 않는 회원입니다", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();


//                Search customDialog = new Search(Main.this); //search 클래스의 객체 생성
//                customDialog.callFunction(search_result); //search 클래스의 callFunction 메소드 사용
            }
        });


        //화면 상단 바에 있는 '종 모양' 아이콘을 누르면 -> 알림메시지 확인 창이 나타난다
        ImageButton alert = (ImageButton)findViewById(R.id.alertbtn);
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Alert.class);
                startActivity(intent);
            }
        });


        //왼쪽에서 오른쪽으로 밀면 메뉴 화면이 나타난다
        final View drawerView = (View)findViewById(R.id.drawer);
        ImageButton openDrawer = (ImageButton)findViewById(R.id.btn_openDrawer);

        openDrawer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawerLayout);
                drawer.openDrawer(drawerView);
            }

        });

        //메뉴 화면(drawer layout)에서 일어나는 일
        whatHappensIndrawer();

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                Log.d("알람", "Main.class - isMyServiceRunning? "+true);
                return true;
            }
        }
        Log.d("알람", "Main.class - isMyServiceRunning? "+false);
        return false;
    }


    private void initializeSpeechRecognition(){
        Log.d("음성인식","initializeSpeechRecognition() 메소드 들어옴");

        if(SpeechRecognizer.isRecognitionAvailable(this)){
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Log.d("음성인식","onReadyForSpeech 들어옴");
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d("음성인식","onBeginningOfSpeech() 들어옴");
                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {
                    Log.d("음성인식","onEndOfSpeech() 들어옴");
                }

                @Override
                public void onError(int error) {
                    Log.d("음성인식","onError(int error) 들어옴. error="+error);
                }

                @Override
                public void onResults(Bundle bundle) {
                    Log.d("음성인식","onResults(Bundle bundle) 들어옴");

                    List<String> results = bundle.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                    );
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });

        }


    }

    private void processResult(String command) {
        Log.d("음성인식","processResult(String command) 들어옴");

        command = command.toLowerCase();

        if(command.indexOf("check") != -1){
            if(command.indexOf("promises") != -1){

                int itemArrayListSize = itemArrayList.size();

                //진행중인 약속의 갯수를 센다
                int inProgressCount = 0;
                for(int i=0; i<itemArrayList.size(); i++){
                    if(itemArrayList.get(i).getRate() < 100) {
                        inProgressCount++;
                    }
                }

                if(inProgressCount == 0) {
                    textToSpeech.speak("Yes Ma'am. Well, unfortunately there is no promise that you are currently carrying out" +
                            ". Why don't you make one?", TextToSpeech.QUEUE_ADD, null);
                }
                else{

                    if (inProgressCount == 1) {
                        textToSpeech.speak("Yes Ma'am. You are now carrying out one promise. " +
                                "Here is the title of it.", TextToSpeech.QUEUE_ADD, null);
                    } else {
                        textToSpeech.speak("Yes Ma'am. You are now carrying out " + inProgressCount + "promises. " +
                                "Here is the title of them.", TextToSpeech.QUEUE_ADD, null);
                    }

                    //한박자 쉼
                    textToSpeech.playSilence(750, TextToSpeech.QUEUE_ADD, null);

                    //진행중인 약속의 제목을 이야기해준다
                    for (int i = 0; i < itemArrayListSize; i++) {
                        if (itemArrayList.get(i).getRate() < 100) {
                            String expiredGoal_title = itemArrayList.get(i).getTitle();
                            textToSpeech.speak(expiredGoal_title, TextToSpeech.QUEUE_ADD, null);
                            textToSpeech.playSilence(500, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                }
                //한박자 쉼
                textToSpeech.playSilence(750, TextToSpeech.QUEUE_ADD, null);


                //약속 이행기간이 끝났지만 최종결과를 제출하지 않은 약속의 개수를 센다
                int inCompleteCount = 0;
                for(int i=0; i<itemArrayList.size(); i++){

                    //이행기간이 끝났는지 확인한다
                    if (itemArrayList.get(i).getRate() == 100) {
                        SharedPreferences sp = getSharedPreferences(itemArrayList.get(i).getPreferenceName(), MODE_PRIVATE);

                        //finalReport를 제출하였는지 확인한다
                        if (!sp.contains("SuccessOrFailure")) {
                            inCompleteCount++;
                        }
                    }
                }

                if(inCompleteCount != 0) {
                    textToSpeech.speak("and There is one more thing.", TextToSpeech.QUEUE_ADD, null);

                    //그런 약속이 있다면, 개수를 말해준다
                    if (inCompleteCount == 1) {
                        textToSpeech.speak("You have one expired promise. which is,", TextToSpeech.QUEUE_ADD, null);
                    } else if (inCompleteCount > 1) {
                        textToSpeech.speak("You have " + inCompleteCount + " expired promises. which is,", TextToSpeech.QUEUE_ADD, null);
                    }

                    //그런 약속의 제목을 말해준다
                    for (int i = 0; i < itemArrayListSize; i++) {

                        //이행기간이 끝났는지 확인한다
                        if (itemArrayList.get(i).getRate() == 100) {
                            SharedPreferences sp = getSharedPreferences(itemArrayList.get(i).getPreferenceName(), MODE_PRIVATE);

                            //finalReport를 제출하였는지 확인한다
                            if (!sp.contains("SuccessOrFailure")) {
                                String expiredGoal_title = itemArrayList.get(i).getTitle();
                                textToSpeech.speak(expiredGoal_title, TextToSpeech.QUEUE_ADD, null);
                                textToSpeech.playSilence(500, TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    }

                    //한박자 쉼
                    textToSpeech.playSilence(750, TextToSpeech.QUEUE_ADD, null);
                    textToSpeech.speak("Don't forget to submit a final report.", TextToSpeech.QUEUE_ADD, null);
                }

                if(inProgressCount != 0) {
                    textToSpeech.speak("and That is all, Ma'am.", TextToSpeech.QUEUE_ADD, null);
                }

            }
        }
        else if(command.indexOf("what") != -1){
            if(command.indexOf("name") != -1){
                if(command.indexOf("your") != -1) {

                    textToSpeech.speak("My name is Jarvis.", TextToSpeech.QUEUE_ADD, null);
                }
                else if(command.indexOf("my") != -1){
                    String userName = userName_textView.getText().toString();
                    textToSpeech.speak("Your name is "+userName, TextToSpeech.QUEUE_ADD, null);
                    textToSpeech.playSilence(750, TextToSpeech.QUEUE_ADD, null);
                    textToSpeech.speak("Are you testing me?", TextToSpeech.QUEUE_ADD, null);
                }
            }
        }

        else{ //사용자가 말한 단어를 인식할 수 없으면, 다시 말해달라고 요청한다
            speak("I beg your pardon?");
        }
    }

    private void requestRecordAudioPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            if(checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (textToSpeech.getEngines().size() == 0) {
                    Toast.makeText(Main.this, "no engine", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    textToSpeech.setLanguage(Locale.UK);
                    speak("Welcome to swear, my lord");
                }
            }
        });

    }

    private void speak(String message) {
        Log.d("음성인식","speak 메소드에 들어옴");
        if(Build.VERSION.SDK_INT>=21){

            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, bundle, null);
            Log.d("음성인식","speak 메소드, sdk21이상, message 말하라고 시킴");
        }
        else{
            textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    class UpdateRecyclerView implements Runnable{


        String currentTime = getCurrentTime();


        @Override
        public void run() {

            SharedPreferences sp = getSharedPreferences(getCurrentUser()+";goalList", MODE_PRIVATE);
            int listSize = sp.getInt("size", 0);

            if(listSize>0){
                for(int i=0; i<listSize; i++){
                    String itemPreference = sp.getString(String.valueOf(i),""); //약속별 preference 이름을 불러오기

                    SharedPreferences sspp = getSharedPreferences(itemPreference, MODE_PRIVATE);
                    String goalTitle = sspp.getString("goalTitle","제목없음"); //약속의 제목

                    String startDate = sspp.getString("goalStartDate","시작x"); //약속 시작날짜
                    String endDate = sspp.getString("goalEndDate","끝x"); //약속 종료날짜

                    //진행률 계산
                    int numberOfDays = sspp.getInt("numberOfDays", 0); //총 이행기간
                    int passedDays = calDateBetweenDates(startDate); //오늘까지 지난 기간
                    double progressRate = passedDays / (double)numberOfDays; //int끼리 계산하면 소수점자리를 잃는다 -> 결과값이 0이 나옴. 실수로 형변환하여 계산해야함
                    int finalProgressRate = (int)(Math.round(progressRate*100));
                    if(finalProgressRate > 100){
                        finalProgressRate = 100;
                    }

                    itemArrayList.add(i,new Item(finalProgressRate,
                            goalTitle, startDate+"~"+endDate, itemPreference));
                }
            }
            else{ }

        }

        //현재 날짜, 시간을 구하기
        public String getCurrentTime(){

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
            String date = format.format(System.currentTimeMillis());
            return date;
        }


        //두 날짜 사이의 차이 계산
        public int calDateBetweenDates(String startDate){
            Log.d("날짜계산","calDateBetweenDates 메소드 안으로 들어옴");

            int result = 0;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Log.d("날짜계산","SimpleDateFormat format = new SimpleDateFormat;");
                Date formattedStartDate = format.parse(startDate);
                Log.d("날짜계산","Date startDate = format.parse(s_date.toString()); startDate: " + formattedStartDate);
                Date formattedEndDate = format.parse(currentTime);
                Log.d("날짜계산","Date startDate = format.parse(e_date.toString()); endDate: " + formattedEndDate);

                long calDate = formattedStartDate.getTime() - formattedEndDate.getTime();
                Log.d("날짜계산","long calDate = startDate.getTime() - endDate.getTime(); calDate= "+calDate);
                long days = Math.abs(calDate / (24 * 60 * 60 * 1000));
                Log.d("날짜계산","Math.abs(calDate / (24 * 60 * 60 * 1000)); days= "+days);
                result = (int)days;
                Log.d("날짜계산","result = (int)days; result= "+result);

            }catch (Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String ex = sw.toString();

                Log.d("날짜계산 오류",ex);
            }

            return result;
        }
    }


    public void whatHappensIndrawer(){

        //drawer 화면에서 'My page' 를 클릭하면 -> 회원정보 설정 화면이 나타난다
        TextView setting = (TextView)findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }

        });


        //drawer 화면에서 'How it works' 를 클릭하면 -> 앱 소개 화면이 나타난다
        TextView text_howItWorks = (TextView) findViewById(R.id.how_it_works);
        text_howItWorks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Intro.class);
                startActivity(intent);

                //사용법을 알려주는 유투브 페이지로 연결 -- 가짜 페이지
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/yzsnrRl-0ig")));

                //회사 소개 페이지로 이동 -- 가짜 페이지
//                Uri uri = Uri.parse("https://johnthornton204.wixsite.com/ewhafrance2017");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
            }

        });


        //drawer 화면에서 'Customer Service'를 클릭하면 -> 앱 회사에 메일을 보낼 수 있는 창이 나타난다
        TextView text_cs = (TextView) findViewById(R.id.cs_text);
        text_cs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CustomerService.class);
                startActivity(intent);
            }

        });


        //drawer 화면에서 'LogOut'을 클릭하면 -> 로그인 창이 나타난다
        TextView logout = (TextView)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //currentUser: 현재 사용자가 누구인지 나타냄(로그인, 회원가입 성공 시 저장한 값)
                //로그아웃 했으니 이 값을 지워야함
                SharedPreferences sspp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);

                SharedPreferences.Editor editor = sspp.edit();

                //로그인상태값과 현재 사용자 이름을 삭제한다
                editor.putBoolean("isLogin",false);
                editor.remove("currentUser");
                editor.commit();

                Toast.makeText(Main.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                finish();
            }

        });
    }


    private String getCurrentUser(){
        SharedPreferences infoPref = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        return infoPref.getString("currentUser","유저정보없음");
    }




    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(getApplicationContext(), "onStart()호출", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("알람", "메인화면 onStop()");
//        Toast.makeText(getApplicationContext(), "onStop()호출", Toast.LENGTH_LONG).show();

        recommendBar.setVisibility(View.VISIBLE);
        recAd = new RecommendAd();
        recAd.execute();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("알람", "메인화면 onDestroy()");

        try {
            stopService(serviceIntent);
        }catch (Exception e){

        }

        textToSpeech.shutdown();
        isDestroyed = true;

        try {
            recAd.cancel(true);
        }catch (Exception e){ }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("알람", "메인화면 onPause()");


    }


    //관리자가 추천하는 약속을 랜덤으로 출력한다
      class RecommendAd extends AsyncTask<Void, String, Void>{


          @Override
          protected Void doInBackground(Void... voids) {

              List<String> rec = new ArrayList<String>(); //더 구체적인 예시 추가하기
              rec.add("한 달 동안 금연하기");
              rec.add("하루에 만원 씩 저축하기");
              rec.add("일주일에 책 한 권씩 읽기");
              rec.add("한 달 동안 배우자에게 화내지 않기");
              rec.add("2주동안 회사에 지각하지 않기");
              rec.add("한 달 안에 블랙잭 마스터하기");
              rec.add("일 주일 동안 플라스틱 용기 사용하지 않기");
              rec.add("하루에 2km씩 조깅하기");
              rec.add("일주일 동안 모르는 이웃에게 인사하기");

           while(!isDestroyed) {

               int ran = (int)(Math.random()*8+1);
               String text = rec.get(ran);
               publishProgress(text);

                   try {
                       Thread.sleep(3000);
                   } catch (Exception e) {
                       StringWriter sw = new StringWriter();
                       e.printStackTrace(new PrintWriter(sw));
                       String ex = sw.toString();

                       Log.d("날짜계산, recAd 오류",ex);
                   }

               }
              return null;
          }

          @Override
          protected void onProgressUpdate(String... values) {
              super.onProgressUpdate(values);

              String text = values[0];
              recommend_textView.setText(text);

          }

          @Override
          protected void onPostExecute(Void aVoid) {
              super.onPostExecute(aVoid);

          }

          @Override
          protected void onCancelled(Void aVoid) {
              super.onCancelled(aVoid);
              Log.d("쓰레드","onCancelled"+aVoid);
          }
      }



        @Override
        protected void onResume() {
            super.onResume();

            Log.d("알람","메인화면 onResume()");

            try {
                //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
                setProfileImage();
            }catch(Exception e){
                Log.d("메인화면","프로필 사진이 없음");
            }

            //저장소에서 닉네임을 불러옴 -> 화면에 표시
            setUsername();


            //목록 업데이트하기
            //onStop()상태에서 약속이 추가 되었음. 변화를 목록에 반영
//            mainAdapter.notifyDataSetChanged();
            rcv.scrollToPosition(0);
        }


        private void setUnreadAlarmMessage(){
            //안 읽은 메시지가 있을 경우, 그것을 화면 상단의 아이콘 위에 표시한다
            //숫자가 실시간으로 바뀌어야 하기 때문에, onResume()에 넣음
            try {
                int unreadAlarmMessage = getIntData("unreadAlarmMessage");
                Log.d("알람","메인화면) unreadAlarmMessage: "+unreadAlarmMessage);

                if (unreadAlarmMessage > 0) {
                    Log.d("알람","메인화면) unreadAlarmMessage가 1개 이상임");
                    unreadAlarmMessage_textView.setVisibility(View.VISIBLE);
                    unreadAlarmMessage_textView.setText(String.valueOf(unreadAlarmMessage));
                    Log.d("알람","unreadAlarmMessage_textView.setText");
                }else{
                    unreadAlarmMessage_textView.setVisibility(View.GONE);
                }
            }catch (Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String ex = sw.toString();

                Log.d("알람",ex);
            }
        }

        private void setUnreadMessage(){
        //안 읽은 메시지가 있을 경우, 그것을 화면 상단의 아이콘 위에 표시한다
        //숫자가 실시간으로 바뀌어야 하기 때문에, onResume()에 넣음
        try {
            int unreadMessage = getIntData("unreadMessage");
            Log.d("알람","메인화면) unreadMessage: "+unreadMessage);

            if (unreadMessage > 0) {
                Log.d("알람","메인화면) unreadMessage가 1개 이상임");
                unreadMessage_textView.setVisibility(View.VISIBLE);
                unreadMessage_textView.setText(String.valueOf(unreadMessage));
                Log.d("알람","unreadMessage_textView.setText");
            }else{
                unreadMessage_textView.setVisibility(View.GONE);
            }
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }
    }


        @Override
        protected void onRestart() {
            super.onRestart();
            Log.d("알람","메인화면 onRestart()");

//            setUnreadAlarmMessage();
            setUnreadMessage();
        }


        private long back_time = 0;

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();

        long result = now - back_time;

        if(result<2000){
            ActivityCompat.finishAffinity(this);
            System.runFinalization();
            System.exit(0);
        }
        else
        {
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
            back_time = System.currentTimeMillis();
        }
    }


}