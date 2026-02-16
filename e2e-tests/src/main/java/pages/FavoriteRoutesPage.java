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

public class FavoriteRoutesPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // List of all cards
    // A card is a users favored route
    @FindBy(className = "card")
    private List<WebElement> routeCards;

    // Order button
    @FindBy(css = ".interactable button")
    private List<WebElement> orderButtons;

    // Page title
    // Favorite routes
    @FindBy(css = ".title p")
    private WebElement pageTitle;

    public FavoriteRoutesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get("http://localhost:4200/favorite-routes");
    }

    // In Angular .html we use @for so we have to wait for rendering of cards
    public void routesLoading() {
        wait.until(ExpectedConditions.visibilityOf(routeCards.get(0)));
    }

    public int getNumberOfRoutes() {
        return routeCards.size();
    }

    public void orderRoute(int index) {
        // Wait untill clickable
        wait.until(ExpectedConditions.elementToBeClickable(orderButtons.get(index)));

        orderButtons.get(index).click();
    }

    public String getRouteOrigin(int index) {
        // Find the card
        WebElement card = routeCards.get(index);

        WebElement originElement = card.findElement(By.cssSelector(".route-info p:nth-of-type(1)")); // First paragraph el in div

        String fullText = originElement.getText();
        return fullText.replace("Origin: ", "").trim();
    }

    public String getRouteDestination(int index) {
        // Find the card
        WebElement card = routeCards.get(index);

        WebElement destinationElement = card.findElement(By.cssSelector(".route-info p:nth-of-type(2)"));

        String fullText = destinationElement.getText();
        return fullText.replace("Destination: ", "");
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return pageTitle.getText().equals("Favorite routes");
        } catch (Exception e) {
            return false;
        }
    }
}

