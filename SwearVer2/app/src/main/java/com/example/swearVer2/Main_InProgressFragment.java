package com.example.swearVer2;

import android.support.v4.app.Fragment;

public class Main_InProgressFragment extends Fragment {

//    RecyclerView rcv;
//    LinearLayoutManager llm;
//    public static MainAdapter mainAdapter;
//    public static ArrayList<Item> itemArrayList;
//
//    public Main_InProgressFragment() { }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.main_fragment_inprogress, container, false);
//
//        Log.d("뷰페이저", "inprogressFragment onCreateView()");
//
//        rcv = (RecyclerView)layout.findViewById(R.id.main_recyclerView_inprogress);
//        llm = new LinearLayoutManager(getActivity());
//
//        rcv.setHasFixedSize(true); //리사이클러뷰 자체의 크기가 변하지 않을 때 - 성능개선(?) - 무슨 뜻?
//        rcv.setLayoutManager(llm); //리사이클러뷰의 레이아웃을 linear로 맞춘다
//
//        itemArrayList = new ArrayList<>(); //Item 클래스 형의 데이터를 리스트화 시킨다
//        initDataSet();
//
//
//        //현재 액티비티와 item리스트를 매개변수로 갖는 어댑터 객체를 만든다
//        mainAdapter = new MainAdapter(getActivity(), itemArrayList);
//
//        //어댑터 객체를 현 액티비티의 리사이클러뷰와 연결시킨다
//        rcv.setAdapter(mainAdapter);
//
//        return layout;
//    }
//
//
//    private String getCurrentUser(){
//        SharedPreferences infoPref = this.getActivity().getSharedPreferences(SignUp.userInfoPreference, this.getActivity().MODE_PRIVATE);
//        return infoPref.getString("currentUser","유저정보없음");
//    }
//
//    //현재 날짜, 시간을 구하기
//    public String getCurrentTime(){
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
//        String date = format.format(System.currentTimeMillis());
//        return date;
//    }
//
//    //두 날짜 사이의 차이 계산
//    public int calDateBetweenDates(String startDate){
//
//        int result = 0;
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            Date formattedStartDate = format.parse(startDate);
//            Date formattedEndDate = format.parse(getCurrentTime());
//
//            long calDate = formattedStartDate.getTime() - formattedEndDate.getTime();
//            long days = Math.abs(calDate / (24 * 60 * 60 * 1000));
//            result = (int)days;
//
//        }catch (Exception e){
//            StringWriter sw = new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String ex = sw.toString();
//
//            Log.d("날짜계산 오류",ex);
//        }
//
//        return result;
//    }
//
//    private void initDataSet(){
//
//        SharedPreferences sp = this.getActivity().getSharedPreferences(getCurrentUser()+";goalList", this.getActivity().MODE_PRIVATE);
//        int listSize = sp.getInt("size", 0);
//        if(listSize>0){
//            int j=0;
//            for(int i=0; i<listSize; i++){
//
//                String itemPreference = sp.getString(String.valueOf(i),""); //약속별 preference 이름을 불러오기
//
//                SharedPreferences sspp = this.getActivity().getSharedPreferences(itemPreference, this.getActivity().MODE_PRIVATE);
//
//                //결과가 확정된 약속이 아닐 때만 리스트에 넣는다
//                if(!sspp.contains("SuccessOrFailure")) {
//
//                    String goalTitle = sspp.getString("goalTitle", "제목없음"); //약속의 제목
//                    String startDate = sspp.getString("goalStartDate", "시작x"); //약속 시작날짜
//                    String endDate = sspp.getString("goalEndDate", "끝x"); //약속 종료날짜
//
//                    //진행률 계산
//                    int numberOfDays = sspp.getInt("numberOfDays", 0); //총 이행기간
//                    int passedDays = calDateBetweenDates(startDate); //오늘까지 지난 기간
//                    double progressRate = passedDays / (double) numberOfDays; //int끼리 계산하면 소수점자리를 잃는다 -> 결과값이 0이 나옴. 실수로 형변환하여 계산해야함
//                    int finalProgressRate = (int) (Math.round(progressRate * 100));
//                    if (finalProgressRate > 100) {
//                        finalProgressRate = 100;
//                    }
//                    Log.d("뷰페이저",i+"번째 아이템)"+goalTitle+";"+startDate+";"+endDate+";"+finalProgressRate);
//
//                    itemArrayList.add(j, new Item(finalProgressRate,
//                            goalTitle, startDate + "~" + endDate, itemPreference));
//                    j++;
//                }
//                //결과가 확정된 경우
//                else{
//                    Log.d("뷰페이저","inProgress)"+i+"번째 아이템은 Complete에 속합니다");
//                }
//            }
//        }
//
//    }

}
