/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.snap.utils;

import org.esa.snap.core.datamodel.ProductData;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Utility class for date manipulation
 *
 * @author Cosmin Cara
 */
public class DateHelper {

    /**
     * Computes the median (average) of two dates.
     * This method handles the possible overflow that can occur.
     *
     * @param startDate The first date
     * @param endDate   The second date
     * @return The date between the two input dates
     */
    public static Date average(Date startDate, Date endDate) {
        Date averageDate = null;
        if (startDate != null && endDate != null) {
            BigInteger averageMillis = BigInteger.valueOf(startDate.getTime())
                    .add(BigInteger.valueOf(endDate.getTime()))
                    .divide(BigInteger.valueOf(2L));
            averageDate = new Date(averageMillis.longValue());
        }
        return averageDate;
    }

    /**
     * Computes the median (average) of two <code>ProductData.UTC</code> data structures.
     *
     * @param startDate The first date
     * @param endDate   The second date
     * @return The date between the two input dates
     */
    public static ProductData.UTC average(ProductData.UTC startDate, ProductData.UTC endDate) {
        ProductData.UTC average = null;
        if (startDate != null && endDate != null) {
            BigInteger averageMillis = BigInteger.valueOf(startDate.getAsDate().getTime()).add(BigInteger.valueOf(endDate.getAsDate().getTime())).divide(BigInteger.valueOf(2L));
            Date averageDate = new Date(averageMillis.longValue());
            average = ProductData.UTC.create(averageDate, 0L);
        }
        return average;
    }

    /**
     * Utility method for returning a <code>ProductData.UTC</code> date from a string
     * using the given date format.
     * Why not using <code>ProductData.UTC.parse(text, pattern)</code> method?
     * Because it errors in the case of a format like dd-MM-yyyy'T'HH:mm:ss.SSSSSS (which should be
     * perfectly fine).
     *
     * @param stringData The string to be converted into a date
     * @param dateFormat The format of the string date
     * @return The UTC date representation.
     */
    public static ProductData.UTC parseDate(String stringData, String dateFormat) {
        ProductData.UTC parsedDate = null;
        if (stringData != null) {
            try {
                if (stringData.endsWith("Z"))
                    stringData = stringData.substring(0, stringData.length() - 1);
                if (!stringData.contains(".") && dateFormat.contains("."))
                    stringData = stringData + ".000000";
                Long microseconds = 0L;
                if (dateFormat.contains(".")) {
                    String stringMicroseconds = stringData.substring(stringData.indexOf(".") + 1);

                    //check the microseconds length
                    if (stringMicroseconds.length() > 6) {
                        //if there are more than 6 digits, the last ones are removed
                        stringMicroseconds = stringMicroseconds.substring(0, 6);
                    } else {
                        //fill until 6 digits
                        while (stringMicroseconds.length() < 6) stringMicroseconds = stringMicroseconds + "0";
                    }

                    microseconds = Long.parseLong(stringMicroseconds);
                    stringData = stringData.substring(0, stringData.lastIndexOf("."));
                    dateFormat = dateFormat.substring(0, dateFormat.lastIndexOf("."));
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                simpleDateFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                Date date = simpleDateFormat.parse(stringData);
                parsedDate = ProductData.UTC.create(date, microseconds);
            } catch (ParseException e) {
                Logger.getLogger(DateHelper.class.getName()).warning(String.format("Date not in expected format. Found %s, expected %s",
                                                                                   stringData,
                                                                                   dateFormat));
            }
        }
        return parsedDate;
    }
}
