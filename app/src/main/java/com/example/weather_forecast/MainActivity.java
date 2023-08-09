package com.example.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout rlHome1;
    private ProgressBar pb1;
    private TextView cityNameTV1, temTv1, conditionTV1,rainTV1,tvRain;
    private TextInputEditText inputET1;
    private RecyclerView weatherRV1;
    private ImageView imgBG1, imgSearch1, imgTem1,rainIV;

    private ArrayList<WeatherModel> arrayList;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String nameOfCity;
    private  Weather_RV_Adapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        rlHome1 = findViewById(R.id.rlHome);
        pb1 = findViewById(R.id.pb);
        cityNameTV1 = findViewById(R.id.cityName);
        temTv1 = findViewById(R.id.temTV);
        conditionTV1 = findViewById(R.id.conditionTv);
        inputET1 = findViewById(R.id.inputET);
        weatherRV1 = findViewById(R.id.weatherRV);
        imgBG1 = findViewById(R.id.imgBG);
        imgTem1 = findViewById(R.id.imgTem);
        rainTV1 = findViewById(R.id.rainTV);
        tvRain = findViewById(R.id.tvRain);
        rainIV = findViewById(R.id.rainIV);

        arrayList = new ArrayList<>();


         adapter = new Weather_RV_Adapter(this,arrayList);
//         weatherRV1.setLayoutManager(new LinearLayoutManager(this));
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        weatherRV1.setLayoutManager(layoutManager);
        weatherRV1.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        nameOfCity = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(nameOfCity);
//
        inputET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String city = inputET1.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter City Name",Toast.LENGTH_SHORT).show();
                }
                else {
                    cityNameTV1.setText(nameOfCity);
                    getWeatherInfo(city);
                }
            }
        });






    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission is Granted Successfully...", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please provide Permissions...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName= "Not Found";
        Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(latitude,longitude,10);
            for (Address adr : addresses) {
                if (adr!=null){
                    String city = adr.getLocality();
                    if (city!=null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG", "City Not Found");
//                        Toast.makeText(MainActivity.this,"User City Not Found...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url = "https://api.weatherapi.com/v1/forecast.json?key=3c32101a5a1546ab8fa191538231707&q=" +cityName+ "&days=1&aqi=yes&alerts=yes\n";
        cityNameTV1.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(JSONObject response) {
                pb1.setVisibility(View.GONE);
                rlHome1.setVisibility(View.VISIBLE);
                arrayList.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temTv1.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:/"+"/".concat(conditionIcon)).into(imgTem1);
                    conditionTV1.setText(condition);
                    if (isDay==1){
                        Picasso.get().load("https://img.freepik.com/free-photo/sun-rays-cloudy-sky_23-2148824930.jpg?w=360&t=st=1689848229~exp=1689848829~hmac=97a63db8793d8e5ed27d68a0d380eaaa821341db93c60507b6e7cbba8594649a").into(imgBG1);
                    }else {
                        Picasso.get().load("https://www.freepik.com/free-photo/low-angle-shot-mesmerizing-starry-sky_12448591.htm#query=Night%20background&position=13&from_view=search&track=ais").into(imgBG1);
                    }
                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONObject forecastObj = forecastObject.getJSONArray("forecastday").getJSONObject(0);
                    JSONObject rainForecast = forecastObj.getJSONObject("day");
                    String rain = rainForecast.getString("daily_chance_of_rain");
                    rainTV1.setText(rain+"%");
                    JSONObject rainObj = rainForecast.getJSONObject("condition");
                    String rainCondition = rainObj.getString("text");
                    tvRain.setText(rainCondition);
                    String imgRain = rainObj.getString("icon");
                    Picasso.get().load("https:".concat(imgRain)).into(rainIV);

//                    int rainImg = rainObj.getInt("icon");
//                    imgRain.setImageResource(rainImg);
                    JSONArray hourArray = forecastObj.getJSONArray("hour");
                    for (int i = 0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String tem = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        arrayList.add(new WeatherModel(time,tem,img,wind));
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
//                    throw new RuntimeException(e);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please Enter Valid City Name",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }



}