package framework;

import org.testng.SkipException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utility {

    public Properties readPropertiesFile(String filePath) {
        if (!(filePath == null || filePath.trim() != "")) {
            System.out.println("****** framework.Utility : readPropertiesFile : Please provide file name");
            return null;
        }
        if (!(new File(filePath).exists())) {
            System.out.println("****** framework.Utility : readPropertiesFile : Can not find any file at " + filePath);
            return null;
        }

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(filePath));
        } catch (IOException e) {
            System.out.println("****** framework.Utility : readPropertiesFile : " + e.getMessage());
            return null;
        }
        return prop;
    }

    public Properties readOR() {
        Properties prop = readPropertiesFile("src\\main\\resources\\OR.properties");
        if (prop == null) {
            System.out.println("****** framework.Utility : readOR : Could not read OR");
            throw new SkipException("Skipping execution as could not read OR");
        }
        return prop;
    }

    public void createDirectory(String strPath){
        if(!(new File(strPath).exists()))
            new File("/path/directory").mkdirs();
    }

}
