package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProfileSidebarPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Profile sidebar
    @FindBy(className = "profile-sidebar")
    private WebElement sidebar;

    @FindBy(id = "profile-name")
    private WebElement profileName;

    // All buttons
    @FindBy(css = ".buttons-frame button")
    private List<WebElement> sidebarButtons;

    public ProfileSidebarPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        PageFactory.initElements(driver, this);
    }

    public void waitForSidebar() {
        wait.until(ExpectedConditions.visibilityOf(sidebar));
        wait.until(ExpectedConditions.visibilityOf(profileName));
    }

    public void clickButtonByText(String buttonText) {
        // Wait untill sidebar is visible
        wait.until(ExpectedConditions.visibilityOf(sidebar));

        wait.until(ExpectedConditions.visibilityOfAllElements(sidebarButtons));

        for (WebElement button : sidebarButtons) {
            wait.until(ExpectedConditions.visibilityOf(button));

            if (button.getText().trim().equals(buttonText)) {
                wait.until(ExpectedConditions.elementToBeClickable(button));

                button.click();
                return;
            }
        }
        throw new RuntimeException("Button with text '" + buttonText + "' not found in sidebar");
    }
}
