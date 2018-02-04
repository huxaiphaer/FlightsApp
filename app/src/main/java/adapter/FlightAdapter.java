package adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import model.FlightModel;
import ug.flights.huza.flightsapp.MapsActivity;
import ug.flights.huza.flightsapp.R;

/**
 * Created by Huzy_Kamz on 1/30/2018.
 */

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightHolder> {


    private List<FlightModel> itemList;
    private Context context;
    private String CompareDest;

    public FlightAdapter(List<FlightModel> itemList, Context context, String CompareDest) {
        this.itemList = itemList;
        this.context = context;
        this.CompareDest = CompareDest;
    }

    @Override
    public FlightHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        FlightHolder rcv = new FlightHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(FlightHolder holder, int position) {

        final FlightModel sr = itemList.get(position);
        final String DepartureAirport = sr.getDepartureAirport();
        final String ArrivalAirport = sr.getArrivalAirport();
        final String Duration = sr.getDuration();
        final String DepartureTime = sr.getDeparturTimee().replace("T", " / Time : ");
        final String ArrivalTime = sr.getArrivalTime().replace("T", " / Time : ");
        final String Stops = sr.getStops();


        holder.from_txt.setText(DepartureAirport);
        holder.to_txt.setText(ArrivalAirport);
        holder.duration_txt.setText(Duration.replace("PT", "")
                .replace("H", " Hours and ")
                .replace("M", " Minutes")
                .replace("P1DT", " 1 Day and ")
        );
        holder.depature_txt.setText(DepartureTime);
        holder.arrival_txt.setText(ArrivalTime);

        // Check for direct flights and non direct flight
        if (!(ArrivalAirport.trim().equals(CompareDest))) {
            holder.view_setColor.setBackgroundColor(Color.parseColor("#CC0B25"));
        } else {
            holder.view_setColor.setBackgroundColor(Color.parseColor("#034C0A"));

        }


        System.out.println("Adapter ---> : DES ONE  " + ArrivalAirport.trim() + " DEST TWO " + CompareDest);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(FlightAdapter.this.context, MapsActivity.class);
                i.putExtra("fromLocation", DepartureAirport);
                i.putExtra("toLocation", ArrivalAirport);
                context.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static final class FlightHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView from_txt, to_txt, duration_txt,
                depature_txt, arrival_txt, stops_txt;
        public View view_setColor;

        public FlightHolder(View itemView) {
            super(itemView);
            root = itemView;

            from_txt = (TextView) itemView.findViewById(R.id.from_txt);
            to_txt = (TextView) itemView.findViewById(R.id.to_txt);
            duration_txt = (TextView) itemView.findViewById(R.id.duration_txt);
            depature_txt = (TextView) itemView.findViewById(R.id.departure_txt);
            arrival_txt = (TextView) itemView.findViewById(R.id.arrival_txt);
            view_setColor = (View) itemView.findViewById(R.id.view_setColor);

        }
    }
}
