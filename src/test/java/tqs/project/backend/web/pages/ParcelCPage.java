package tqs.project.backend.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ParcelCPage {

    private WebDriver driver;

    @FindBy(xpath = "/html/body/div[1]/p[2]/button")
    private WebElement statusButton;

    @FindBy(xpath = "/html/body/div[1]/div/div/form/button")
    private WebElement actionButton;

    public ParcelCPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getStatus() {
        return statusButton.getText();
    }

    public boolean isActionButtonDisplayed() {
        return actionButton.isDisplayed();
    }

    public String getAction() {
        return actionButton.getText();
    }
}
