package adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import model.FlightModel;
import ug.flights.huza.flightsapp.R;

/**
 * Created by Huzy_Kamz on 1/30/2018.
 */

public class FlightAdapter  extends RecyclerView.Adapter<FlightAdapter.FlightHolder>  {


    private List<FlightModel> itemList;
    private Context context;

    public FlightAdapter(List<FlightModel> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public FlightHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

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
        final String DepartureTime = sr.getDeparturTimee();
        final String ArrivalTime = sr.getArrivalTime();
        final String Stops = sr.getStops();
        final String DirectFlight = sr.getDirectFlights();

        holder.from_txt.setText(DepartureAirport);
        holder.to_txt.setText(ArrivalAirport);
        holder.duration_txt.setText(Duration);
        holder.depature_txt.setText(DepartureTime);
        holder.arrival_txt.setText(ArrivalTime);
        holder.stops_txt.setText(Stops);
        holder.dept_txt.setText(DepartureAirport);
        holder.arr_txt.setText(ArrivalAirport);


        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent i = new Intent(FlightAdapter.this.context, DetailsLocalLeader.class);
              //  context.startActivity(i);


            }
        });


    }

    @Override
    public int getItemCount() {
            return itemList.size();
    }

    public class FlightHolder  extends RecyclerView.ViewHolder
    {

        public View root;
        public TextView from_txt,to_txt,duration_txt,
                depature_txt,arrival_txt,stops_txt, dept_txt,arr_txt;

        public FlightHolder(final View itemView) {
            super(itemView);
            root = itemView;

            from_txt =(TextView) itemView.findViewById(R.id.from_txt);
            to_txt =(TextView) itemView.findViewById(R.id.to_txt);
            duration_txt =(TextView) itemView.findViewById(R.id.duration_txt);
            depature_txt =(TextView) itemView.findViewById(R.id.departure_txt);
            arrival_txt =(TextView) itemView.findViewById(R.id.arrival_txt);
            stops_txt =(TextView) itemView.findViewById(R.id.stop_txt);
            dept_txt =(TextView) itemView.findViewById(R.id.dept_txt);
            arr_txt =(TextView) itemView.findViewById(R.id.arr_txt);



        }
    }
}
