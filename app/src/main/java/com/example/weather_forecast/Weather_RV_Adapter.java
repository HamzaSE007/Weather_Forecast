package com.example.weather_forecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Weather_RV_Adapter extends RecyclerView.Adapter<Weather_RV_Adapter.Holder> {

    private Context context;
   private ArrayList<WeatherModel> arrayList1;

    public Weather_RV_Adapter(Context context, ArrayList<WeatherModel> arrayList1) {
        this.context = context;
        this.arrayList1 = arrayList1;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        return new Holder(v);
    }


    @SuppressLint("SetTextI18n,SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        WeatherModel wm = arrayList1.get(position);
        holder.tempTV1.setText(wm.getTemperature()+"°c");
        Picasso.get().load("https://".concat(String.valueOf(wm.getIcon()))).into(holder.imgCondition1);
        holder.windTV1.setText(wm.getWindSpeed()+"Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(wm.getTime());
            holder.timeTV1.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return arrayList1.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView timeTV1, tempTV1, windTV1;
        private ImageView imgCondition1;
        public Holder(@NonNull View itemView) {
            super(itemView);
            timeTV1 = itemView.findViewById(R.id.timeTV);
            tempTV1 = itemView.findViewById(R.id.tempTV);
            windTV1 = itemView.findViewById(R.id.windTV);
            imgCondition1 = itemView.findViewById(R.id.imgCondition);
        }
    }
}
