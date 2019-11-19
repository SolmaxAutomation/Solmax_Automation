package Modules;

import framework.MasterClass;

import java.util.HashMap;

public class Methods {
    public static void launch(String strURL, String strTitle) {
        MasterClass.uiactions.launchApplication(strURL);
        MasterClass.uiactions.waitForPageLoad(60);
        MasterClass.reporter.passFail(MasterClass.driver.getTitle().equalsIgnoreCase(strTitle), "Application should be launched", "Application launched successfully", "Could not launch application");
    }

    public static void login(String userName, String passWord) {
        MasterClass.uiactions.setValue("Username", userName);
        MasterClass.uiactions.click("Submit");
        MasterClass.uiactions.setValue("Password", passWord);
        MasterClass.uiactions.click("Submit");
        if (MasterClass.uiactions.elementVisible("Submit", 5))
            MasterClass.uiactions.click("Submit");
        if (MasterClass.uiactions.elementVisible("Cancel", 3))
            MasterClass.uiactions.click("Cancel");
        if (MasterClass.uiactions.elementVisible("Submit", 3))
            MasterClass.uiactions.click("Submit");
        MasterClass.reporter.passFail(MasterClass.uiactions.elementVisible("Home.User", 5), "User should be logged in", "User has logged in", "User is not able to login");
    }

    public static void navigateToModule(String modulePath) {
        int itemCount = 0;
        String[] itemPath = modulePath.split(">");
        if (MasterClass.uiactions.getElementAttribute("Nav.Pane", "aria-expanded").equalsIgnoreCase("false"))
            MasterClass.uiactions.click("Nav.Pane");
        if (itemPath.length == 1) {
            MasterClass.uiactions.click("Nav.Single", itemPath[0]);
            return;
        }
        for (String item : itemPath) {
            if (itemCount == 0) {
                if (!MasterClass.uiactions.getElementAttribute("Nav.Parent", "class", item).contains("isExpanded"))
                    MasterClass.uiactions.click("Nav.Parent", item);
            } else {
                if (itemCount == 2)
                    MasterClass.uiactions.click("Nav.CollapseAll");
                MasterClass.uiactions.click("Nav.Child", item);
            }
            itemCount++;
        }
        MasterClass.reporter.pass("Navigated to target module", "Navigation successful to " + itemPath[itemCount - 1]);
    }

    public static void createNewRecordOpportunities(HashMap<String, String> data) {
        MasterClass.uiactions.wait(3);
        MasterClass.uiactions.click("General.AddNew");
        MasterClass.uiactions.setValue("NewOpportunityName", data.get("OpportunityName"));
        MasterClass.uiactions.setValue("InterestLevel", data.get("InterestLevel"));
        MasterClass.uiactions.wait(1);
        MasterClass.uiactions.setValue("Currency", data.get("Currency"));
        MasterClass.uiactions.wait(1);
//        MasterClass.uiactions.setValue("Language", data.get("Language"));
        MasterClass.uiactions.wait(1);
        MasterClass.uiactions.setValue("Estimate", data.get("Estimate"));
        MasterClass.uiactions.wait(1);
        MasterClass.uiactions.setValue("SourceType", data.get("SourceType"));
        MasterClass.uiactions.wait(1);
        MasterClass.uiactions.setValue("CountryRegion", data.get("CountryRegion"));
        MasterClass.uiactions.wait(1);
        MasterClass.uiactions.setValue("CountryState", data.get("CountryState"));
        MasterClass.uiactions.wait(1);
        MasterClass.uiactions.setValue("CityTown", data.get("CityTown"));
        MasterClass.uiactions.wait(5);
        MasterClass.uiactions.setValue("Owner", data.get("Owner"));
        MasterClass.uiactions.wait(1);
        if (MasterClass.uiactions.elementVisible("Data.Error", 5)) {
            if (MasterClass.uiactions.elementVisible("Data.Error.Expand", 1))
                MasterClass.uiactions.click("Data.Error.Expand");
            MasterClass.reporter.fail("For all valid details no error should be displayed", "Error displayed on page");
        } else {
            MasterClass.uiactions.click("Save");
            if (MasterClass.uiactions.elementVisible("Data.Error", 5)) {
                MasterClass.reporter.fail("For all valid details no error should be displayed", "Error displayed on page");
                return;
            }
            MasterClass.reporter.passFail(MasterClass.uiactions.elementVisible("Opportunities.Title", 10) && MasterClass.uiactions.getElementAttribute("Opportunities.Title", "text").contains(data.get("OpportunityName")),
                    "New opportunity should be saved and Opportunity ID should be displayed on page", "Opportunity saved and id is displayed", "Could not save opportunity");
        }
    }

    public static void validateSalesQuotation(HashMap<String, String> data) {
        SubMethods.createQuotation(data);
        SubMethods.addQuotationLine(data);

    }

    public static void scheduleOrder(HashMap<String, String> data) {
        if (MasterClass.uiactions.elementVisible("SO.Site", 20, data.get("Site"))) {
            MasterClass.uiactions.click("SO.Site", data.get("Site"));
            MasterClass.uiactions.click("SO.Validate");
            MasterClass.uiactions.wait(10);
            MasterClass.uiactions.click("SO.Order");
            if (MasterClass.uiactions.elementVisible("SO.SalesData.DragFrom.Table", 15)) {
                MasterClass.uiactions.dragAndDrop("SO.SalesData.DragFrom.Fixed", "SO.MyData.DragTO");
                MasterClass.uiactions.wait(10);
                MasterClass.reporter.pass("Service order should be scheduled", "Service order is scheduled");
            } else
                MasterClass.reporter.fail("Sales data table should be displayed", "Sales data table is displayed");
        } else
            MasterClass.reporter.fail("Site " + data.get("Site") + " should be displayed", "Site " + data.get("Site") + " is be displayed");
    }
}
