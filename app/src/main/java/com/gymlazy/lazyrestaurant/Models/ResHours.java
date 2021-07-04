package com.gymlazy.lazyrestaurant.Models;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-07-03
 * Description: ResHours
 */
public class ResHours {
    private String mHourStart;
    private String mHourEnd;

    public String getHourStart() {
        return mHourStart;
    }

    public void setHourStart(String hourStart) {
        mHourStart = hourStart;
    }

    public String getHourEnd() {
        return mHourEnd;
    }

    public void setHourEnd(String hourEnd) {
        mHourEnd = hourEnd;
    }

    /**
     * return working hours based on start and end time of the restaurants
     * @return
     * @throws ParseException
     */
    public String getWorkingHours() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");
        Date startDate = simpleDateFormat.parse(mHourStart);
        Date endDate = simpleDateFormat.parse(mHourEnd);

        SimpleDateFormat workingHourFormat = new SimpleDateFormat("HH:mm a");
        String sStartDate = workingHourFormat.format(startDate);
        String sEndDate = workingHourFormat.format(endDate);

        String sWorkingHour = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            sWorkingHour = String.join(" - ", sStartDate, sEndDate);
        } else {
            String[] arrWorkingHour = new String[]{sStartDate, sEndDate};
            sWorkingHour = TextUtils.join(" - ", arrWorkingHour);
        }

        return sWorkingHour;
    }
}
