package tqs.project.backend.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ParcelRPage {

    private WebDriver driver;

    @FindBy(xpath = "/html/body/div[1]/p/button")
    private WebElement statusButton;

    public ParcelRPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getStatus() {
        return statusButton.getText();
    }
}
