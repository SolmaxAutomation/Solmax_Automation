package framework;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class UIActions {

    Properties OR;
    static WebDriver driver;
    static int std_wait_time = 30;
    static JavascriptExecutor js_executor;
    static int page_load_timeout = 60;

    public UIActions() {
        driver = MasterClass.driver;
        OR = MasterClass.utility.readOR();
        js_executor = (JavascriptExecutor) driver;
    }

    // Fetching list of all elements that match with provided locator type
    public List<WebElement> findElements(String strLocator) {
        List<WebElement> elements = Collections.emptyList();
        try {
            elements = driver.findElements(By.id(strLocator));
        } catch (Exception e) {
            ;
        }
        if (elements.size() > 0) return elements;
        try {
            elements = driver.findElements(By.className(strLocator));
        } catch (Exception e) {
            ;
        }
        if (elements.size() > 0) return elements;
        try {
            elements = driver.findElements(By.name(strLocator));
        } catch (Exception e) {
            ;
        }
        if (elements.size() > 0) return elements;
        try {
            elements = driver.findElements(By.linkText(strLocator));
        } catch (Exception e) {
            ;
        }
        if (elements.size() > 0) return elements;
        try {
            elements = driver.findElements(By.tagName(strLocator));
        } catch (Exception e) {
            ;
        }
        if (elements.size() > 0) return elements;
        try {
            elements = driver.findElements(By.xpath(strLocator));
        } catch (Exception e) {
            ;
        }
        return elements;
    }

    // Locating a single element by utilizing findElements method
    public WebElement findElement(String strLocator, String... args) {
        if (strLocator == null) {
            System.out.println("*****framework.UIActions : findElement : No locator provided");
            return null;
        }
        if (getLocator(strLocator) != null)
            strLocator = getLocator(strLocator);
        strLocator = String.format(strLocator, args);
        if (!elementVisible(strLocator, std_wait_time)) {
            System.out.println("*****framework.UIActions : findElement : Could not find any element on '" + strLocator + "'");
            return null;
        } else if (findElements(strLocator).size() == 0)
            return null;
        else
            return findElements(strLocator).get(0);
    }

    public String getLocator(String OR) {
        if (this.OR.containsKey(OR))
            return this.OR.getProperty(OR);
        else {
            System.out.println("*****framework.UIActions : getLocator : Could not find any porperty for '" + OR + "'");
            return null;
        }
    }

    /* Overloading click method with two argument types. First method takes element name from OR as String,
       and Second method takes element object as WebElement. First method calls second method. Same approach is established for other
       essential actions such as setValue, selectValue, selectCheckbox, etc.*/

    public void click(String OR, String... args) {
        WebElement obj = findElement(OR, args);
        if (obj == null || !obj.isDisplayed() || !obj.isEnabled()) {
            System.out.println("*****framework.UIActions : click : Could not find any element for '" + OR + "'");
            Reporter.fail("", "Could not find/click element " + OR);
        } else {
            click(obj);
        }
    }

    public void click(WebElement element) {
        if (element == null) {
            System.out.println("*****framework.UIActions : click : Could not click on null element.");
        } else {
            try {
                element.click();
                waitForPageLoad(page_load_timeout);
            } catch (Exception e) {
                scollInView(element);
                jsClick(element);
                waitForPageLoad(page_load_timeout);
            }
        }
    }

    public void setValue(String OR, String value, String... args) {
        WebElement obj = findElement(OR, args);
        if (obj == null || !obj.isDisplayed() || !obj.isEnabled()) {
            System.out.println("*****framework.UIActions : setValue : Could not find any element for '" + OR + "'");
            Reporter.fail("", "Could not find element " + OR);
        } else {
            try {
                setValue(obj, value);
            } catch (Exception e) {
                js_executor.executeScript("arguments[0].scrollIntoView(true);", obj);
                wait(1);
                setValue(obj, value);
            }
        }
    }

    public void setValueAndEnter(String OR, String value, String... args) {
        WebElement obj = findElement(OR, args);
        if (obj == null || !obj.isDisplayed() || !obj.isEnabled()) {
            System.out.println("*****framework.UIActions : setValue : Could not find any element for '" + OR + "'");
            Reporter.fail("", "Could not find element " + OR);
        } else {
            setValueAndEnter(obj, value);
        }
    }

    public void setValueAndEnter(WebElement element, String value) {
        if (element == null) {
            System.out.println("*****framework.UIActions : setValue : Could not set value on null element.");
            return;
        }
        if (value.trim().equals(""))
            System.out.println("*****framework.UIActions Warning : setValue : Setting up blank value for element.");
        element.clear();
        element.sendKeys(value);
        element.sendKeys(Keys.chord(Keys.ENTER));
    }

    // Overloading setValue method with WebElement parameter so that previous setValue can use the basic component of this method.
    public void setValue(WebElement element, String value) {
        if (element == null) {
            System.out.println("*****framework.UIActions : setValue : Could not set value on null element.");
            return;
        }
        if (value.trim().equals(""))
            System.out.println("*****framework.UIActions Warning : setValue : Setting up blank value for element.");
        element.clear();
        element.sendKeys(value);
        wait(2);
        element.sendKeys(Keys.chord(Keys.TAB));
    }

    public void selectValue(String OR, String value, String by) {
        WebElement obj = findElement(OR);
        if (obj == null || !obj.isDisplayed() || !obj.isEnabled()) {
            System.out.println("*****framework.UIActions : selectValue : Could not find/select any element for '" + OR + "'");
            Reporter.fail("", "Could not find element " + OR);
        } else {
            if (value.trim().equals(""))
                System.out.println("*****framework.UIActions Warning : selectValue : Setting up blank value for  '" + OR + "'");
            Select select = new Select(obj);
            List<WebElement> availableOptions = select.getOptions();
            LinkedList<String> options = new LinkedList<>();
            for (WebElement temp : availableOptions) {
                options.add(temp.getText());
                options.add(temp.getAttribute("value"));
            }
            if (by.equalsIgnoreCase("value")) {
                if (options.contains(value))
                    select.selectByValue(value);
                else
                    System.out.println("*****framework.UIActions : selectValue : Could not find any value '" + value + "' for '" + OR + "'");
            } else if (by.equalsIgnoreCase("text")) {
                if (options.contains(value))
                    select.selectByVisibleText(value);
                else
                    System.out.println("*****framework.UIActions : selectValue : Could not find any text '" + value + "' for '" + OR + "'");
            } else if (by.equalsIgnoreCase("index")) {
                int index;
                try {
                    index = Integer.parseInt(value);
                    select.selectByIndex(index);
                } catch (Exception e) {
                    System.out.println("*****framework.UIActions : selectValue : Could not find any option or could not parse '" + value + "' for '" + OR + "'");
                }
            }
        }
    }

    public void selectValue(WebElement element, String value, String by) {
        if (element == null) {
            System.out.println("*****framework.UIActions : selectValue : No Element present matching with criteria");
        } else {
            if (value.trim().equals(""))
                System.out.println("*****framework.UIActions Warning : selectValue : Setting up blank value for dropdown element");
            Select select = new Select(element);
            List<WebElement> availableOptions = select.getOptions();
            LinkedList<String> options = new LinkedList<>();
            for (WebElement temp : availableOptions) {
                options.add(temp.getText());
                options.add(temp.getAttribute("value"));
            }
            if (by.equalsIgnoreCase("value")) {
                if (options.contains(value))
                    select.selectByValue(value);
                else
                    System.out.println("*****framework.UIActions : selectValue : Could not find any value '" + value + "' for '" + " dropdown");
            } else if (by.equalsIgnoreCase("text")) {
                if (options.contains(value))
                    select.selectByVisibleText(value);
                else
                    System.out.println("*****framework.UIActions : selectValue : Could not find any text '" + value + "' for '" + " dropdown");
            } else if (by.equalsIgnoreCase("index")) {
                int index;
                try {
                    index = Integer.parseInt(value);
                    select.selectByIndex(index);
                } catch (Exception e) {
                    System.out.println("*****framework.UIActions : selectValue : Could not find any option or could not parse '" + value + "' for '" + " dropdown");
                }
            }
        }
    }

    /*One logic is applicable for Radio buttons and checkboxes, hence one method, toggleBoxButton is required for both operations
    fieldType can have any of these value {radio, checkbox}
     */
    public void toggleBoxButton(String OR, String fieldType, boolean bValue) {
        WebElement obj = findElement(OR);
        if (obj == null) {
            System.out.println("*****framework.UIActions : selectCheckbox : Could not find any element for '" + OR + "', of field type: " + fieldType);
        } else {
            toggleBoxButton(obj, fieldType, bValue);
        }
    }

    public void toggleBoxButton(WebElement element, String fieldType, boolean bValue) {
        if (element == null) {
            System.out.println("*****framework.UIActions : selectCheckbox : Could not find any element for field type: " + fieldType);
        } else {
            if (element.isSelected() ^ bValue)
                element.click();
        }
    }

    public static void launchApplication(String strURL) {
        if (strURL == null) {
            System.out.println("*****framework.UIActions : launchURL : URL is not provided");
        } else {
            try {
                driver.get(strURL);
                driver.manage().window().maximize();
            } catch (InvalidArgumentException e) {
                System.out.println("*****framework.UIActions : launchURL : Invalid URL provided: " + strURL);
            }
        }
    }

    public void browserNavigationAction(String actionName) {
        switch (actionName.toLowerCase()) {
            case "back":
                driver.navigate().back();
                break;
            case "forward":
                driver.navigate().forward();
                break;
            case "refresh":
                driver.navigate().refresh();
                break;
            default:
                System.out.println("*****framework.UIActions : browserNavigationAction : Incorrect navigation option provided");
        }
    }

    public String getElementProperty(String OR, String value) {
        if (getLocator(OR) == null) {
            System.out.println("******framework.UIActions : getElementProperty : No such locator present in Object Repository");
            return null;
        } else {
            return getLocator(OR).replace("%s", value);
        }
    }

    public String getElementAttribute(String OR, String strAttribute, String... args) {
        WebElement obj = findElement(OR, args);
        return getElementAttribute(obj, strAttribute);
    }

    public String getElementAttribute(WebElement element, String strAttribute) {
        String strTemp = null;
        if (element == null) {
            System.out.println("*****framework.UIActions : getElementAttribute : Could not find any element for '" + OR + "'");
        } else {
            if (strAttribute.equalsIgnoreCase("text"))
                strTemp = element.getText();
            else
                strTemp = element.getAttribute(strAttribute);

            if (strTemp != null)
                strTemp = strTemp.trim();
        }
        return strTemp;
    }

    public boolean elementVisible(String locator, int intSeconds, String... args) {
        if (getLocator(locator) != null)
            locator = String.format(getLocator(locator), args);
        try {
            WebDriverWait oWait = new WebDriverWait(driver, intSeconds);
            String finalLocator = locator;
            Boolean element = (Boolean) oWait.until(new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    if (findElements(finalLocator).size() == 0) {
                        return false;
                    } else {
                        List<WebElement> oList = findElements(finalLocator);
                        return ((WebElement) oList.get(oList.size() - 1)).isDisplayed();
                    }
                }
            });
            return element;
        } catch (Exception e) {
            return false;
        }
    }

    public static void wait(int waitTimeInSec) {
        try {
            Thread.sleep(waitTimeInSec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForPageLoad(int timeOut) {
        int wait_counter = 0;
        wait(1);
        try {
            if ((Boolean) (js_executor.executeScript("return window.jQuery != undefined"))) {
                while (!(((Boolean) js_executor.executeScript("return jQuery.active==0")) && ((Boolean) js_executor.executeScript("return document.readyState").toString().equals("complete")))) {
                    wait(1);
                    if (wait_counter > timeOut) break;
                    wait_counter++;
                }
            }
        } catch (Exception e) {
            ;
        }
    }

    //    Pass label name and value, to select dropdown values
    public static void selectComboBox(String strLabel, String strValue) {

        MasterClass.uiactions.click("General.Select", strLabel);
        MasterClass.uiactions.click("General.Select.Option", strValue);
    }

    public static void setValueToInputBox(String strLabel, String strValue) {
        if (MasterClass.uiactions.elementVisible("General.Inputbox", 1, strLabel)) {
            MasterClass.uiactions.setValue("General.Inputbox", strValue, strLabel);
        } else {
        }
    }

    public static void scollInView(WebElement obj) {
        try {
            js_executor.executeScript("arguments[0].scrollIntoView(true);", obj);
            wait(1);
        } catch (Exception e) {
            System.out.println("Can not scroll in to view as element is null");
        }
    }

    public static void jsClick(WebElement obj) {
        try {
            js_executor.executeScript("arguments[0].click();", obj);
        } catch (Exception e) {
            System.out.println("Can not perform JS Click");

        }
    }

    public void dragAndDrop(String dragFrom, String dragTo) {
        if (getLocator(dragFrom) != null)
            dragFrom = getLocator(dragFrom);

        if (getLocator(dragTo) != null)
            dragTo = getLocator(dragTo);

        WebElement objFrom = findElement(dragFrom);
        WebElement objTo = findElement(dragTo);
        if (objFrom == null || objTo == null) {
            MasterClass.reporter.fail("", "Either drag from or drag to locator is null");
            return;
        }
        this.dragAndDrop(objFrom, objTo);
    }

    public void dragAndDrop(WebElement objFrom, WebElement objTo) {
        Actions act = new Actions(MasterClass.driver);
        act.keyDown(Keys.CONTROL).click(objFrom).dragAndDrop(objFrom, objTo).keyUp(Keys.CONTROL);
        Action dragDrop = act.build();
        this.wait(2);
        dragDrop.perform();
        this.wait(3);
    }
}