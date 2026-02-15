package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class NavBarPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // User profile
    @FindBy(css = ".nav-right .user-circle-btn")
    private WebElement profileButton;

    public NavBarPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    // Click
    public void clickProfileButton() {
        try {
            wait.until(ExpectedConditions.visibilityOf(profileButton));

            try {
                wait.until(ExpectedConditions.elementToBeClickable(profileButton));
                profileButton.click();

            } catch (Exception e) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", profileButton);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
