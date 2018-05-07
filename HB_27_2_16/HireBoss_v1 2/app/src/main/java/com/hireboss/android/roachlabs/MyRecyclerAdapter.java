package com.hireboss.android.roachlabs;

/**
 * Created by Vishal on 2/1/2016.
 * modified by Gurdev Singh on 24-feb-2016
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.Serializable;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    private List<FeedItem> feedItemList;
    private Context mContext;


    public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);


        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();


        //Download image using picasso library
        Picasso.with(mContext).load(feedItem.getThumbnail())
                .error(R.drawable.usererror)
                .placeholder(R.drawable.usererror)
                .fit()
                .transform(transformation)
                .into(customViewHolder.imageView);

        //Setting text view title
        customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));
        customViewHolder.idi.setText(Html.fromHtml(feedItem.getId()));
        customViewHolder.amount.setText(Html.fromHtml("₹"+feedItem.getAmount_total()+"/-"));
        customViewHolder.dates.setText(Html.fromHtml(feedItem.getStart_time()));


        //Handle click event on both title and image click
        customViewHolder.textView.setOnClickListener(clickListener);
        customViewHolder.imageView.setOnClickListener(clickListener);

        customViewHolder.textView.setTag(customViewHolder);
        customViewHolder.imageView.setTag(customViewHolder);


    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView,idi,amount,dates;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnaill);
            this.textView = (TextView) view.findViewById(R.id.title);
            this.idi = (TextView) view.findViewById(R.id.idvalue);
            this.amount = (TextView) view.findViewById(R.id.amt);
            this.dates  = (TextView) view.findViewById(R.id.dated);


        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomViewHolder holder = (CustomViewHolder) view.getTag();
            int position = holder.getPosition();

            FeedItem feedItem = feedItemList.get(position);

            Intent viewIntent = new Intent(mContext,TaskDetails.class);
            viewIntent.putExtra("feeditem", (Serializable) feedItem);
            viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(viewIntent);



         //   Toast.makeText(mContext, feedItem.getTitle(), Toast.LENGTH_SHORT).show();
        }
    };

}
