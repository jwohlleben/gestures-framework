package com.jwohlleben.datarecorder;

import java.util.Calendar;
import java.util.Date;

public class DateTime
{
    public static String getTimestamp()
    {
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();

        int[] time = {
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)
        };

        for (int t : time)
        {
            sb.append(String.format("%02d", t));
        }

        return sb.toString();
    }
}
