import Modules.*;
import com.relevantcodes.extentreports.ExtentReports;
import framework.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.ITestContext;
import org.testng.annotations.*;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;

public class TestScript {

    public static String testCaseName;

    @BeforeSuite
    public void setup() {
        MasterClass.driver = new Gateway("chrome").getDriver();
        MasterClass.utility = new Utility();
        MasterClass.uiactions = new UIActions();
        MasterClass.reporter = new Reporter();
        MasterClass.report = new ExtentReports(Reporter.reportPath + "//" + "TestAutomationReport.html");
    }

    @BeforeTest
    public static void getTestCase(ITestContext testContext) {
        testCaseName = testContext.getName();
        MasterClass.test = MasterClass.report.startTest(testCaseName);
        System.out.println("BEFORE TEST");
    }

    @Test(dataProvider = "input")
    public static void testFLow1(@Optional HashMap<String, String> data) {
        System.out.println("IN METHOD");
        Methods.launch(data.get("URL"), "Sign in to your account");
        Methods.login(data.get("UserName"), data.get("Password"));
        Methods.navigateToModule(data.get("ModulePath"));
        Methods.createNewRecordOpportunities(data);
    }

    @Test(dataProvider = "input")
    public static void testFLow2(@Optional HashMap<String, String> data) {
        Methods.launch(data.get("URL"), "Sign in to your account");
        Methods.login(data.get("UserName"), data.get("Password"));
        Methods.navigateToModule(data.get("ModulePath"));
        Methods.createNewRecordOpportunities(data);
        Methods.validateSalesQuotation(data);
    }

    @Test(dataProvider = "input")
    public static void testFLow3(@Optional HashMap<String, String> data) {
        Methods.launch(data.get("URL"), "Sign in to your account");
        Methods.login(data.get("UserName"), data.get("Password"));
        Methods.navigateToModule(data.get("ModulePath"));
        Methods.scheduleOrder(data);
    }

    @AfterTest
    public void getResult() {
        MasterClass.driver.close();
        MasterClass.driver.quit();
        MasterClass.report.endTest(MasterClass.test);
        MasterClass.report.flush();
    }

    @AfterClass
    public static void endTest() {

    }

    @AfterSuite
    public void windUP() {
    }

    @DataProvider(name = "input")
    public static Object[][] dataProvider(ITestContext testContext) {
        String strMethodName = testContext.getName();
        String basePath = "src\\test\\data\\" + strMethodName + ".json";
        LinkedList<HashMap<String, String>> dataMap = new LinkedList<>();
        if (!new File(basePath).exists())
            return null;
        try {
            Object obj = new JSONParser().parse(new FileReader(basePath));
            JSONArray jsonArray = (JSONArray) obj;
            Object[][] data = new Object[jsonArray.size()][1];
            int i = 0;
            for (Object ja : jsonArray) {
                JSONObject jo = (JSONObject) ja;
                data[i][0] = getMap(jo);
                i++;
            }
            return data;
        } catch (Exception e) {
            System.out.println("********Data Provider : Either " + strMethodName + ".json file is corrupted or could not be accessed");
            return null;
        }
    }

    public static HashMap<String, String> getMap(JSONObject jsonObject) {
        HashMap<String, String> tmpMap = new HashMap<>();
        for (Object key : jsonObject.keySet()) {
            tmpMap.put(key.toString(), jsonObject.get(key).toString());
        }
        return tmpMap;
    }
}
