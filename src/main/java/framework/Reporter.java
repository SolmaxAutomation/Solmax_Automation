package framework;

import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reporter implements IReporter {

    protected String basePath = "src/test/Reports/";
    public static String reportPath;

    public Reporter() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String folderName = "Report_" + sdf.format(d);
        reportPath = basePath + folderName;
        MasterClass.utility.createDirectory(reportPath);
        MasterClass.utility.createDirectory(reportPath + "/screenshots");
    }

    protected static void takeScreenshots() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileWithPath = reportPath + "/screenshots/" + sdf.format(d) + ".png";
        TakesScreenshot scrShot = ((TakesScreenshot) MasterClass.driver);
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File DestFile = new File(fileWithPath);
        try {
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pass(String strExpected, String strActual) {
        takeScreenshots();
        MasterClass.test.log(LogStatus.PASS, "Test Step Passed: " + strActual);
        Assert.assertTrue(true);
    }

    public static void fail(String strExpected, String strActual) {
        takeScreenshots();
        MasterClass.test.log(LogStatus.FAIL, "Test Step Failed: " + strActual);
        Assert.assertTrue(false);
    }

    public static void passFail(boolean bTest, String strExpected, String strActualPass, String strActualFail) {
        if (bTest) {
            pass(strExpected, strActualPass);
        } else {
            fail(strExpected, strActualFail);
        }
    }

}
