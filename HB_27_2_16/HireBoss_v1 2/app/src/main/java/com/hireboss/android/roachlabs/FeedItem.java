package com.hireboss.android.roachlabs;

import java.io.Serializable;

/**
 * Created by Vishal on 2/1/2016.
 * modified by gurdev singh on 24-feb-2016
 */
public class FeedItem implements Serializable {
    private String title,desc;
    private String thumbnail;
    private String ids,
                    name,
                    number,
                    duration,
                    distance,
                    start_time,
                    end_time,
                    item_amount,
                    item_desc,
                    base_amount,
                    time_fare,
                    distance_fare,
                    convenience_charges,
                    amount_total;


    public String getTitle() {return title;}
    public void setTitle(String title) {
        this.title = title;
    }

    public void setConvenience_charges(String convenience_charges){this.convenience_charges = convenience_charges;}
    public String getConvenience_charges(){return convenience_charges;}

    public void setAmount_total(String amount_total){this.amount_total = amount_total;}
    public String getAmount_total(){return amount_total;}

    public void setId(String ids){this.ids = ids;}
    public String getId(){return ids;}

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setDesc(String desc){this.desc = desc;}
    public String getDesc(){return desc;}

    public void setName(String name){this.name = name;}
    public String getName(){return name;}

    public void setNumber(String number){this.number = number;}
    public String getNumber(){return number;}

    public void setDistance(String distance){this.distance = distance;}
    public String getDistance(){return distance;}

    public void setDuration(String duration){this.duration = duration;}
    public String getDuration(){return duration;}

    public void setStart_time(String start_time){this.start_time = start_time;}
    public String getStart_time(){return start_time;}

    public void setEnd_time(String end_time){this.end_time = end_time;}
    public String getEnd_time(){return end_time;}

    public void setItem_amount(String item_amount){this.item_amount = item_amount;}
    public String getItem_amount(){return item_amount;}

    public void setItem_desc(String item_desc){this.item_desc = item_desc;}
    public String getItem_desc(){return item_desc;}

    public void setBase_amount(String base_amount){this.base_amount = base_amount;}
    public String getBase_amount(){return base_amount;}

    public void setTime_fare(String time_fare){this.time_fare = time_fare;}
    public String getTime_fare(){return time_fare;}

    public void setDistance_fare(String distance_fare){this.distance_fare = distance_fare;}
    public String getDistance_fare(){return distance_fare;}


}