//package com.example.swear;
//
//import android.content.Context;
//import android.graphics.Typeface;
//import android.text.style.CharacterStyle;
//import android.text.style.StyleSpan;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.common.data.DataBufferUtils;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.AutocompletePrediction;
//import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.tasks.RuntimeExecutionException;
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.tasks.Tasks;
//
//import java.util.ArrayList;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
///**
// * Google Places Api의 Geo Data Client로부터 오는 요청을 담당하는 어댑터이다
// */
//
//public class PlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
//
//    private static final String TAG = "AutocompleteAdapter";
//    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
//
//    //이 어댑터로부터 반환되는 결과를 담는다
//    private ArrayList<AutocompletePrediction> mResultList;
//
//    //클라이언트는 자동완성 요청을 담당한다
//    private GeoDataClient mGeoDataClient;
//
//    //바운드: 지도상의 직사각형 = 검색할 범위 = 위치기준을 충족하는 장소의 배열
//    private LatLngBounds mBounds;
//
//    //쿼리 = 검색할 텍스트 문자열(예: 음식점)
//    //특정 장소타입에 대한 쿼리를 제한한다 - 무슨말?
//    private AutocompleteFilter mPlaceFilter;
//
//
//    public PlaceAutocompleteAdapter(Context context, GeoDataClient geoDataClient,
//                                    LatLngBounds bounds, AutocompleteFilter filter) {
//        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
//        mGeoDataClient = geoDataClient;
//        mBounds = bounds;
//        mPlaceFilter = filter;
//    }
//
//
//    //이어지는 검색어에 대한 지역범위를 제한한다
//    public void setBounds(LatLngBounds bounds) {
//        mBounds = bounds;
//    }
//
//
//    //자동완성 결과의 개수를 반환한다
//    @Override
//    public int getCount() {
//        return mResultList.size();
//    }
//
//
//    //자동완성 아이템을 반환한다
//    @Override
//    public AutocompletePrediction getItem(int position) {
//        return mResultList.get(position);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = super.getView(position, convertView, parent);
//
//        //행의 첫번째, 두번째 텍스트에 글자를 넣는다
//        //텍스트에 스타일을 지정한다(BOLD)
//
//        AutocompletePrediction item = getItem(position);
//
//        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
//        TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
//        textView1.setText(item.getPrimaryText(STYLE_BOLD));
//        textView2.setText(item.getSecondaryText(STYLE_BOLD));
//
//        //내용이 갖추어진 행을 반환한다
//        return row;
//    }
//
//
//    //현재 자동완성 결과에 대한 필터를 반환한다 -- 이 메소드 뭐지?
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//
//                // We need a separate list to store the results, since
//                // this is run asynchronously.
//                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();
//
//                // Skip the autocomplete query if no constraints are given.
//                if (constraint != null) {
//                    // Query the autocomplete API for the (constraint) search string.
//                    filterData = getAutocomplete(constraint);
//                }
//
//                results.values = filterData;
//                if (filterData != null) {
//                    results.count = filterData.size();
//                } else {
//                    results.count = 0;
//                }
//
//                return results;
//            }
//
//
//            //결과 발행
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//
//                if (results != null && results.count > 0) {
//                    // The API returned at least one result, update the data.
//                    mResultList = (ArrayList<AutocompletePrediction>) results.values;
//                    notifyDataSetChanged();
//                } else {
//                    // The API did not return any results, invalidate the data set.
//                    notifyDataSetInvalidated();
//                }
//            }
//
//            @Override
//            public CharSequence convertResultToString(Object resultValue) {
//                // Override this method to display a readable result in the AutocompleteTextView
//                // when clicked.
//                if (resultValue instanceof AutocompletePrediction) {
//                    return ((AutocompletePrediction) resultValue).getFullText(null);
//                } else {
//                    return super.convertResultToString(resultValue);
//                }
//            }
//        };
//    }
//
//
//
//    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
//        Log.i(TAG, "Starting autocomplete query for: " + constraint);
//
//        // Submit the query to the autocomplete API and retrieve a PendingResult that will
//        // contain the results when the query completes.
//        Task<AutocompletePredictionBufferResponse> results =
//                mGeoDataClient.getAutocompletePredictions(constraint.toString(), mBounds,
//                        mPlaceFilter);
//
//        // This method should have been called off the main UI thread. Block and wait for at most
//        // 60s for a result from the API.
//        try {
//            Tasks.await(results, 60, TimeUnit.SECONDS);
//        } catch (ExecutionException | InterruptedException | TimeoutException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();
//
//            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
//                    + " predictions.");
//
//            // Freeze the results immutable representation that can be stored safely.
//            return DataBufferUtils.freezeAndClose(autocompletePredictions);
//        } catch (RuntimeExecutionException e) {
//            // If the query did not complete successfully return null
//            Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
//                    Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Error getting autocomplete prediction API call", e);
//            return null;
//        }
//    }
//}
