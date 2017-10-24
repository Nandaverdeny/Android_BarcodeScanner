package framework.library.common.helper.date;

import android.support.annotation.Nullable;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date Helper function
 * Created by verdeny on 10/21/17.
 */

public final class DateHelper {

    private static Date _sourceDate;
    private static Date _destDate;

    private static String _strSourceDate;
    private static String _strDestDate;

    private static SimpleDateFormat _sourceDateFormat;
    private static SimpleDateFormat _destDateFormat;

    private static Locale locale = Locale.getDefault();

    private static SimpleDateFormat getDestDateFormat() { return _destDateFormat; }
    private static void setDestDateFormat(String _destFormat) { _destDateFormat = new SimpleDateFormat(_destFormat, locale); }

    private static SimpleDateFormat getSourceDateFormat() { return _sourceDateFormat; }
    private static void setSourceDateFormat(String _sourceFormat) { _sourceDateFormat = new SimpleDateFormat(_sourceFormat, locale); }


    /**
     * @param date a date object
     * @return String date
     */
    public static String ConvertToString(Date date) {
        _sourceDate = date;
        SimpleDateFormat sdf = new SimpleDateFormat(this.getDateTimeInstance(), locale);
        return sdf.format(_sourceDate);
    }

    /**
     * @param date a date object
     * @param destFormat string for destination date format
     * @return String date with destination format
     */
    public static String ConvertToString(Date date, String destFormat) {
        _sourceDate = date;
        setDestDateFormat(destFormat);
        try {
            return getDestDateFormat().format(_sourceDate);
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage(), ex);
            return null;
        }
    }


    /**
     * @param strDate a string of date
     * @param fromFormat string for source date format
     * @return Date object
     */
    @Nullable
    public static Date ConvertToDate(String strDate, String fromFormat) {
        _strSourceDate = strDate;
        setSourceDateFormat(fromFormat);
        try {
            return getSourceDateFormat().parse(_strSourceDate);
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * @param strDate a string date
     * @param fromFormat string for source date format
     * @param destFormat string for destination date format
     * @return String date (based on from format) with destination format
     */
    public static String ConvertToStringDate(String strDate, String fromFormat, String destFormat) {
        return DateHelper.ConvertToString(DateHelper.ConvertToDate(strDate, fromFormat), destFormat);
    }
}
