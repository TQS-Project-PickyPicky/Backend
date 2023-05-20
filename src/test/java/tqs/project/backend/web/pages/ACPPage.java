package tqs.project.backend.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ACPPage {

    private WebDriver driver;

    @FindBy(xpath = "/html/body/div/table")
    private WebElement parcelTable;

    @FindBy(xpath = "/html/body/div/table/tbody/tr[1]/td[3]/button")
    private WebElement inTransitButton;

    @FindBy(xpath = "/html/body/div/table/tbody/tr[2]/td[3]/button")
    private WebElement deliveredButton;

    @FindBy(xpath = "/html/body/div/table/tbody/tr[3]/td[3]/button")
    private WebElement collectedButton;

    @FindBy(xpath = "/html/body/div/table/tbody/tr[4]/td[3]/button")
    private WebElement returnedButton;

    public ACPPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isParcelTableDisplayed() {
        return parcelTable.isDisplayed();
    }

    public void clickInTransitButton() {
        inTransitButton.click();
    }

    public void clickDeliveredButton() {
        deliveredButton.click();
    }

    public void clickCollectedButton() {
        collectedButton.click();
    }

    public void clickReturnedButton() {
        returnedButton.click();
    }
}
