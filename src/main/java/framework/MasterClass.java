package framework;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MasterClass {

    public static UIActions uiactions;
    public static Utility utility;
    public static WebDriver driver;
    public static Reporter reporter;
    public static ExtentTest test;
    public static ExtentReports report;

    public static void main(String[] args) {
        driver = new Gateway("chrome").getDriver();
        utility = new Utility();
        uiactions = new UIActions();
        reporter = new Reporter();
    }

}
