package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class AdminHistoryPage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(css = ".filters select.figma-select")
    private List<WebElement> selectElements;

    // Load button
    @FindBy(css = ".btn-search")
    private List<WebElement> searchButtons;

    // Date inputs
    @FindBy(css = "input[type='date']")
    private List<WebElement> dateInputs;

    // Table rows (rides)
    @FindBy(css = "tbody tr")
    private List<WebElement> tableRows;

    public AdminHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void waitForPage() {
        wait.until(ExpectedConditions.visibilityOf(selectElements.get(0)));
    }

    public void selectUser(int index) {
        Select select = new Select(selectElements.get(0));
        select.selectByIndex(index);
    }

    public void selectSortOption(String value) {
        Select select = new Select(selectElements.get(1));
        select.selectByValue(value);
    }

    public void clickLoad() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButtons.get(0)));
        searchButtons.get(0).click();
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButtons.get(1)));
        searchButtons.get(1).click();
    }

    public void setFromDate(String date) {
        WebElement fromInput = dateInputs.get(0);
        fromInput.clear();
        fromInput.sendKeys(date);
    }

    public void setToDate(String date) {
        WebElement toInput = dateInputs.get(1);
        toInput.clear();
        toInput.sendKeys(date);
    }

    public int getRideCount() {
        if (tableRows.size() == 1 &&
                tableRows.get(0).getText().contains("No rides found")) {
            return 0;
        }
        return tableRows.size();
    }

    public List<WebElement> getRideRows() {
        return tableRows;
    }

    public double getPriceFromRow(int index) {
        WebElement row = tableRows.get(index);
        WebElement priceCell = row.findElements(By.tagName("td")).get(4);
        return Double.parseDouble(priceCell.getText());
    }
}
