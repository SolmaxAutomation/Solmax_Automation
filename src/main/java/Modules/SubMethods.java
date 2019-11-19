package Modules;

import framework.MasterClass;
import org.openqa.selenium.Keys;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import java.util.HashMap;

public class SubMethods {

    @Test(dataProvider = "input")
    public static void createQuotation(@Optional HashMap<String, String> data) {
        MasterClass.uiactions.click("General.Button", "Sales quotation");
        MasterClass.uiactions.selectComboBox("Account type", data.get("AccountType"));
        MasterClass.uiactions.setValueToInputBox("Legal Entity", data.get("LegalEntity"));
        MasterClass.uiactions.setValueToInputBox("Customer name", data.get("CustomerName"));
        MasterClass.uiactions.setValue("Opportunity.CustomerAccount", data.get("CustomerAccount"));
        MasterClass.uiactions.wait(3);
        MasterClass.uiactions.setValueToInputBox("Contact", data.get("Contact"));
        MasterClass.uiactions.setValueToInputBox("Company name", data.get("CompanyName"));
        MasterClass.uiactions.setValue("Opportunity.Sales.CX", data.get("CX"));
        MasterClass.uiactions.setValueToInputBox("Site", data.get("SiteNo"));
        MasterClass.uiactions.click("General.Ok");
        if (MasterClass.uiactions.elementVisible("General.Message.Popup", 5)) {
            MasterClass.uiactions.click("General.Message.Popup.Close");
            if (MasterClass.uiactions.elementVisible("Data.Error.Expand", 5))
                MasterClass.uiactions.click("Data.Error.Expand");
            MasterClass.reporter.fail("For all valid details no error should be displayed", "Error displayed on page");
        } else {
            MasterClass.reporter.passFail(MasterClass.uiactions.elementVisible("Opportunity.Sales.Header", 10)
                            && MasterClass.uiactions.getElementAttribute("Opportunity.Sales.Header", "text").contains(data.get("CustomerName")),
                    "Sales Quotation should be created and Quotation should be displayed on page", "Quotation crated and id is displayed", "Could not Sales Quotation");
        }
    }


    @Test(dataProvider = "input")
    public static void addQuotationLine(@Optional HashMap<String, String> data) {
        MasterClass.uiactions.wait(5);
        MasterClass.uiactions.selectComboBox("System of units", data.get("SystemOfUnits"));
        MasterClass.uiactions.setValue("Opportunity.SalesQuotation.poNumber", data.get("CustomerPONumber"));
        MasterClass.uiactions.setValueToInputBox("Item", data.get("ItemNo"));
        MasterClass.uiactions.wait(4);
        MasterClass.uiactions.waitForPageLoad(10);
        if (MasterClass.uiactions.elementVisible(MasterClass.uiactions.getElementProperty("Opportunity.Sales.loadingMsg", "is not Applied"), 3)) {
            MasterClass.uiactions.scollInView(MasterClass.uiactions.findElement("Opportunity.Sales.lineQuantity"));
            MasterClass.uiactions.click("Opportunity.Sales.lineQuantity");
            MasterClass.uiactions.wait(3);
            MasterClass.uiactions.findElement("Opportunity.Sales.lineQuantity").clear();
            MasterClass.uiactions.setValue("Opportunity.Sales.lineQuantity", data.get("LineQuantity"));
            addNewTransportation(data);
            MasterClass.uiactions.click("Opportunity.SalesQuote.calculate");
            //TODO: getAttribute text does not work in below items, so will have to remove if else validation from here
            if (!MasterClass.uiactions.findElement("Opportunity.Sales.adjQuantity").isDisplayed()) {
                MasterClass.reporter.fail("Sale Quotation values should be calculated", "Sale Quotation values are not calculated");
            } else {
                MasterClass.reporter.pass("Sale Quotation values should be calculated", "Sale Quotation values are calculated");
                MasterClass.uiactions.click("Opportunity.SalesQuote.showLoading");
                MasterClass.uiactions.wait(2);
                if (MasterClass.uiactions.getElementAttribute("Opportunity.Loading.itemNumber", "title").equals(data.get("ItemNo"))) {
                    MasterClass.reporter.pass("Sale Quotation should be displayed in Loading", "Sale Quotation is displayed in Loading");
                } else {
                    MasterClass.reporter.fail("Sale Quotation should be displayed in Loading", "Sale Quotation is NOT displayed in Loading");
                }
                MasterClass.uiactions.click("Opportunity.Loading.applyBtn");
                MasterClass.reporter.passFail(MasterClass.uiactions.elementVisible("Opportunity.Sales.loadingMsg", 4, "Applied"),
                        "Loading should be applied to Sales Quotation", "Loading is applied to Sales Quotation", "Loading is NOT applied to Sales Quotation");

                String strQuoteHeader = MasterClass.uiactions.getElementAttribute("Opportunity.Quotation.header", "text");
                strQuoteHeader = strQuoteHeader.substring(0, 5);
                MasterClass.uiactions.click("Opportunity.GenerateQuotation");

                if (MasterClass.uiactions.elementVisible("Opportunity.GenerateQuotation.quotation", 10)) {
                    String strText = MasterClass.uiactions.getElementAttribute("Opportunity.GenerateQuotation.quotation", "title").split("\n")[0];
                    MasterClass.reporter.passFail(strText.equals(strQuoteHeader), "Sale Quotation should be displayed in Send Quotation page", "Sale Quotation is displayed in Send Quotation page", "Unexpected Sales Quotation ID " + strText + " is displayed");
                } else {
                    MasterClass.reporter.fail("Sale Quotation should be displayed in Send Quotation page", "Sale Quotation is NOT displayed in Send Quotation page");
                }

                MasterClass.uiactions.click("Opportunity.GenerateQuotation.OK");
                MasterClass.reporter.passFail(MasterClass.uiactions.elementVisible("Opportunity.QuotationStatus", 7, "Sent"),
                        "Sales Quotation should be generated", "Sales Quotation is generated and status is Sent", "Sales Quotation is NOT generated");

                MasterClass.uiactions.click("Opportunity.followUpTab");
                MasterClass.uiactions.click("Opportunity.followUpTab.convertToSales");
                if (MasterClass.uiactions.elementVisible("Opportunity.GenerateQuotation.quotation", 10)) {
                    String strText = MasterClass.uiactions.getElementAttribute("Opportunity.GenerateQuotation.quotation", "title").split("\n")[0];
                    MasterClass.reporter.passFail(strText.equals(strQuoteHeader), "Sale Quotation should be displayed in Send Quotation page", "Sale Quotation is displayed in Send Quotation page", "Unexpected Sales Quotation ID " + strText + " is displayed");
                } else {
                    MasterClass.reporter.fail("Sale Quotation should be displayed in Confirm Quotation page", "Sale Quotation is NOT displayed in Confirm Quotation page");
                }
                MasterClass.uiactions.click("Opportunity.GenerateQuotation.OK");
                MasterClass.reporter.passFail(MasterClass.uiactions.elementVisible("Opportunity.QuotationStatus", 10, "Confirmed"),
                        "Sales Quotation should be converted to Sales Order", "Sales Quotation is converted to Sales Order and status is Confirmed", "Sales Quotation is NOT converted to Sales Order");
            }
        }
    }

    @Test(dataProvider = "input")
    public static void addNewTransportation(@Optional HashMap<String, String> data) {
        MasterClass.uiactions.click("Opportunity.Quotation");
        MasterClass.uiactions.click("Opportunity.Sales.transportation");
        MasterClass.uiactions.waitForPageLoad(10);
        MasterClass.uiactions.wait(3);
        if (MasterClass.uiactions.elementVisible("Opportunity.transportation.newBtn", 10)) {
            MasterClass.uiactions.click("Opportunity.transportation.newBtn");
            MasterClass.uiactions.setValueToInputBox("Site", data.get("SiteNo"));
            MasterClass.uiactions.setValueToInputBox("Departure Id", data.get("DepartureId"));
            MasterClass.uiactions.setValueToInputBox("Transport type", data.get("TransportType"));
            MasterClass.uiactions.setValueToInputBox("Distance", data.get("Distance"));
            MasterClass.uiactions.setValueToInputBox("Incoterm Code", data.get("IncotermCode"));

            MasterClass.uiactions.click("Opportunity.transportation.saveBtn");
            MasterClass.uiactions.wait(4);
            MasterClass.uiactions.click("Opportunity.transportation.closeBtn");
            MasterClass.uiactions.wait(3);
            MasterClass.reporter.pass("New Transportation should be added to Sales Quotation", "New Transportation is added to Sales Quotation");
        } else {
            MasterClass.reporter.fail("New Transportation screen should be displayed", "New Transportation screen did not display");
        }
    }
}
