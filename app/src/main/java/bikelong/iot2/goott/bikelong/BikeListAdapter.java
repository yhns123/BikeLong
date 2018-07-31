package bikelong.iot2.goott.bikelong;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BikeListAdapter extends BaseAdapter {

    private List<Bike> mBikes;
    private Context mActivityContext;
    private int mResourceId;

    public BikeListAdapter(List<Bike> mBikes, Context mActivityContext, int mResourceId) {
        this.mBikes = mBikes;
        this.mActivityContext = mActivityContext;
        this.mResourceId = mResourceId;

    }

    @Override
    public int getCount() { return mBikes.size(); }

    @Override
    public Object getItem(int position) { return mBikes.get(position); }

    @Override
    public long getItemId(int position) { return mBikes.get(position).getBikeNo(); }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null) {
            view = View.inflate(mActivityContext, mResourceId, null);
        }

        Bike bike = mBikes.get(position);

        TextView bikeNo = view.findViewById(R.id.bike_no);
        bikeNo.setText("자전거 번호 : "+ bike.getBikeNo());

        TextView bikeName = view.findViewById(R.id.rentalShop_name);
        bikeName.setText("대여소 위치 : "+ bike.getRentalShopName());

        return view;
    }
}
