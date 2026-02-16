package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(className = "overlay")
    private WebElement popupOverlay;

    // Ride ordering form
    @FindBy(tagName = "app-ride-ordering")
    private WebElement rideOrderingForm;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get("http://localhost:4200/");
    }

    public boolean isRidePopupOpen() {
        try {
            // Wait for overlay to be visible
            wait.until(ExpectedConditions.visibilityOf(popupOverlay));

            String classes = popupOverlay.getAttribute("class");
            return classes.contains("open");
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForPopupToOpen() {
        wait.until(ExpectedConditions.visibilityOf(popupOverlay));

        // If overlay = open then the form is displayed
        wait.until(driver -> {
            String classes = popupOverlay.getAttribute("class");
            return classes.contains("open");
        });
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
