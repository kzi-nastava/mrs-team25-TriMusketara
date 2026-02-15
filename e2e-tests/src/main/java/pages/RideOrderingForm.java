package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RideOrderingForm {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "origin")
    private WebElement originInput;

    @FindBy(id = "destination")
    private WebElement destinationInput;

    @FindBy(id = "type")
    private WebElement typeSelect;

    @FindBy(id = "time")
    private WebElement timeInput;

    @FindBy(id = "baby-friendly")
    private WebElement babyFriendlyCheck;

    @FindBy(id = "pet-friendly")
    private WebElement petFriendlyCheck;

    @FindBy(className = "finish-ride-ordering")
    private WebElement showRouteButton;

    public RideOrderingForm(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public void formLoading() {
        wait.until(ExpectedConditions.visibilityOf(originInput));
    }

    public String getOriginValue() {
        wait.until(ExpectedConditions.visibilityOf(originInput));
        return originInput.getAttribute("value");
    }

    public String getDestinationValue() {
        wait.until(ExpectedConditions.visibilityOf(destinationInput));
        return destinationInput.getAttribute("value");
    }

    public void selectVehicleType(String type) {
        wait.until(ExpectedConditions.visibilityOf(typeSelect));
        try {
            Select select = new Select(typeSelect);
            select.selectByVisibleText(type);
        } catch (Exception e) {
            typeSelect.sendKeys(type);
        }
    }

    public void enterTime(String time) {
        wait.until(ExpectedConditions.visibilityOf(timeInput));
        timeInput.clear();
        timeInput.sendKeys(time);
    }

    // Check baby friendly
    public void checkBabyFriendly() {
        wait.until(ExpectedConditions.elementToBeClickable(babyFriendlyCheck));

        if (!babyFriendlyCheck.isSelected()) {
            babyFriendlyCheck.click();
        }
    }

    // Check pet friendly
    public void checkPetFriendly() {
        wait.until(ExpectedConditions.elementToBeClickable(petFriendlyCheck));

        if (!petFriendlyCheck.isSelected()) {
            petFriendlyCheck.click();
        }
    }

    public void orderRoute() {
        wait.until(ExpectedConditions.elementToBeClickable(showRouteButton));
        showRouteButton.click();
    }
}
