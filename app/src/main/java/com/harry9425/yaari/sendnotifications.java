package com.harry9425.yaari;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class sendnotifications {

    public sendnotifications(String message,String topic,String key)
    {
       try {
            JSONObject noticontent= new JSONObject("{'contents':{'en':'"+message+"'},'include_player_ids':['"+key +"'],"+
                    "'headings':{'en':'"+topic+"'}}");
            OneSignal.postNotification(noticontent,null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
