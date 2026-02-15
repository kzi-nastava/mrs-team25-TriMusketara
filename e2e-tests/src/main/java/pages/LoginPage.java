package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver; // WebDriver

    private WebDriverWait wait; // Explicitly wait

    // Email input field
    @FindBy(name = "email")
    private WebElement emailInput;

    // Password input field
    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(className = "submit-button")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get("http://localhost:4200/login");
    }

    public void enterEmail(String email) {
        // Wait until visible
        wait.until(ExpectedConditions.visibilityOf(emailInput));

        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));

        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    // Click button
    public void logIn() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));

        loginButton.click();
    }
}

