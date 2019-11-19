package framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.SkipException;

public class Gateway {
    static String strBrowserName;

    public Gateway(String strBrowserName) {
        this.strBrowserName = strBrowserName;
    }

    public WebDriver getDriver() {

        WebDriver driver;

        switch (strBrowserName.toLowerCase()) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "src//main//resources//drivers//chromedriver.exe");
                driver = new ChromeDriver();
                break;
            default:
                System.out.println("*****framework.Gateway : getDriver : Unsupported browser");
                throw new SkipException("Skipping execution as could not find supported browser");
        }
        driver.manage().window().maximize();
        return driver;
    }
}
