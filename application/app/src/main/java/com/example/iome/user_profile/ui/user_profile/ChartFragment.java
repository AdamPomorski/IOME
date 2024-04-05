package com.example.iome.user_profile.ui.user_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.iome.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.example.iome.AAChartCoreLib.AAChartCreator.AAChartView;
import com.example.iome.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.example.iome.AAChartCoreLib.AAChartEnum.AAChartAxisType;
import com.example.iome.AAChartCoreLib.AAChartEnum.AAChartType;
import com.example.iome.AAChartCoreLib.AAOptionsModel.AADataLabels;
import com.example.iome.AAChartCoreLib.AAOptionsModel.AADateTimeLabelFormats;
import com.example.iome.AAChartCoreLib.AAOptionsModel.AAOptions;
import com.example.iome.AAChartCoreLib.AAOptionsModel.AAStyle;
import com.example.iome.AAChartCoreLib.AAOptionsModel.AATitle;
import com.example.iome.AAChartCoreLib.AAOptionsModel.AATooltip;
import com.example.iome.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class ChartFragment extends Fragment implements ChartViewModel.OnDataLoadedListener {

    private ChartViewModel mViewModel;
    private AAChartView aaChartView;
    private EditText startDate, startHours, stopDate, stopHours;
    private Button confirmButton;
    private SimpleDateFormat formatter1, formatter2;

    private long unixTimeStart, unixTimeStop;
    private String dateStartStr, dateStopStr, hoursStartStr, hoursStopStr;
    private Object[][] chartData;
    private String dataType = null;
    private List<SongInformation> songInformationList;


    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartViewModel.class);
        mViewModel.setOnDataLoadedListener(this);
        if (getArguments() != null) {
            dataType = getArguments().getString("data_type");
        }

    }

    @SuppressLint("SimpleDateFormat")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chart, container, false);
        startDate = root.findViewById(R.id.start_date);
        startHours = root.findViewById(R.id.start_hours);
        stopDate = root.findViewById(R.id.stop_date);
        stopDate = root.findViewById(R.id.stop_date);
        stopHours = root.findViewById(R.id.stop_hours);
        confirmButton = root.findViewById(R.id.confirm_button_chart);
        aaChartView = root.findViewById(R.id.AAChartView);
        Toolbar toolbar = root.findViewById(R.id.toolbar_chart);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        if(dataType.equals("meditation")) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Meditation History");
        } else {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Attention History");
        }


        formatter1 = new SimpleDateFormat("dd-MM-yyyy");
        formatter2 = new SimpleDateFormat("HH-mm-ss");


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    dateStartStr = startDate.getText().toString();
                    Date dateStart1 = formatter1.parse(dateStartStr);
                    hoursStartStr = startHours.getText().toString();
                    Date dateStart2 = formatter2.parse(hoursStartStr);
                    dateStopStr = stopDate.getText().toString();
                    Date dateStop1 = formatter1.parse(dateStopStr);
                    hoursStopStr = stopHours.getText().toString();
                    Date dateStop2 = formatter2.parse(hoursStopStr);

                    TimeZone timeZone = TimeZone.getDefault();
                    int offset = timeZone.getOffset(System.currentTimeMillis());

                    unixTimeStart = dateStart1.getTime() + dateStart2.getTime()+offset;
                    unixTimeStop = dateStop1.getTime() + dateStop2.getTime()+offset;

                } catch (ParseException e) {
                    Toast.makeText(getContext(), "Wrong date format", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
                if (unixTimeStart >= unixTimeStop) {
                    Toast.makeText(getContext(), "Wrong dates values", Toast.LENGTH_LONG).show();
                    return;
                }

                mViewModel.getChartData(dataType, unixTimeStart, unixTimeStop);






            }
        });
        return root;
    }

    private String getColorForSongUri(String songUri) {

        String[] colors = {"#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", "#F08080", "#808080"};

        if(songUri == null) {
            return colors[0];
        }
        int index = Math.abs(songUri.hashCode()) % colors.length;


        return colors[index];
    }

    @Override
    public void onDataLoaded(Object[][] chartData, List<SongInformation> songInformations) {
        this.chartData = chartData;
        this.songInformationList = songInformations;



        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                List<Map<String, Object>> modifiedChartData = new ArrayList<>();
                String songName = null;
                List<String> artists = new ArrayList<>();
                String artistsString = null;

                for (int i = 0; i < chartData.length; i++) {
                    String songUri = (String) chartData[i][2];
                    String color = getColorForSongUri(songUri);
                    for (SongInformation songInformation : songInformationList) {
                        if (songInformation.getSongUri().equals(songUri)) {
                             songName = songInformation.getSongName();
                             artists = songInformation.getArtists();
                            artistsString = String.join(", ", artists);

                        }
                    }



                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("x", chartData[i][0]);
                    dataPoint.put("y", chartData[i][1]);
                    dataPoint.put("color", color);
                    dataPoint.put("songName", songName);
                    dataPoint.put("artists", artistsString);


                    modifiedChartData.add(dataPoint);
                }
                String chartTitle = dataType.toUpperCase(Locale.ROOT) + " CHART";

                AAChartModel aaChartModel = new AAChartModel()
                        .chartType(AAChartType.Area)
                        .title(chartTitle)
                        .titleStyle(new AAStyle()
                                .color("#ffffffff")
                                .fontSize(20f))
                        .backgroundColor("#e0a3ff")
                        .yAxisGridLineWidth(0f)
                        .tooltipEnabled(true)
                        .series(new AASeriesElement[]{
                                new AASeriesElement()
                                        .name(dataType)
                                        .data(modifiedChartData.toArray())
                                        .colorByPoint(true)
                        });


                AAOptions aaOptions = aaChartModel.aa_toAAOptions();

                aaOptions.xAxis
                        .type(AAChartAxisType.Datetime)
                        .dateTimeLabelFormats(new AADateTimeLabelFormats()
                                .month("%e. %b")
                                .year("%b")
                        );
                aaOptions.tooltip(new AATooltip()
                        .useHTML(true)
                        .pointFormat("Value: {point.y}<br> Song name: {point.songName}<br>Artists: {point.artists}")
                        .valueDecimals(2)
                        .backgroundColor("#000000")
                        .borderColor("#000000")
                        .style(new AAStyle()
                                .color("#FFD700")
                                .fontSize(12)
                        )
                );
                aaChartView.aa_drawChartWithChartOptions(aaOptions);






            }
        });


    }
}