package framework.library.common.helper.date;

import org.junit.Assert;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by verdeny on 10/24/17.
 */
public class DateHelperTest {

    private String _strDate = "24102017";
    private String _expectedDate = "24/10/2017";
    private Date _date = new Date(1508778000000l);
    private String _fromFormat = "ddMMyyyy";
    private String _destFormat = "dd/MM/yyyy";


    @org.junit.Test
    public void convertToString() throws Exception {
        String res = DateHelper.ConvertToString(_date);
        Assert.assertEquals(res, _expectedDate);
    }

    @org.junit.Test
    public void convertToString1() throws Exception {
        String res = DateHelper.ConvertToString(_date, _destFormat);
        Assert.assertEquals(res, _expectedDate);
    }

    @org.junit.Test
    public void convertToDate() throws Exception {
        Date res = DateHelper.ConvertToDate(_strDate, _fromFormat);
        Assert.assertEquals(res, _date);
    }

    @org.junit.Test
    public void convertToStringDate() throws Exception {
        String res = DateHelper.ConvertToStringDate(_strDate, _fromFormat, _destFormat);
        Assert.assertEquals(res, _expectedDate);
    }

}