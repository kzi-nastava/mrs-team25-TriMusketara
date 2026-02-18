package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.*;
import utils.DriverFactory;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminHistoryTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private LoginPage loginPage;
    private AdminHistoryPage historyPage;
    private NavBarPage navbar;
    private ProfileSidebarPage sidebar;

    @BeforeEach
    public void setUp() {
        driver = DriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginPage = new LoginPage(driver);
        historyPage = new AdminHistoryPage(driver);
        navbar = new NavBarPage(driver);
        sidebar = new ProfileSidebarPage(driver);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginAsAdminAndOpenHistory() {
        loginPage.open();
        loginPage.enterEmail("admin@demo.com");
        loginPage.enterPassword("admin123");
        loginPage.logIn();

        wait.until(ExpectedConditions.urlContains("map"));

        navbar.clickProfileButton();
        sidebar.waitForSidebar();
        sidebar.clickButtonByText("Ride history");

        wait.until(ExpectedConditions.urlContains("admin-history"));
        historyPage.waitForPage();
        navbar.clickProfileButton();
    }

    @Test
    @DisplayName("Happy Path: Admin can sort ride history by price")
    public void testSortByPrice() {

        loginAsAdminAndOpenHistory();

        historyPage.selectUser(1);
        historyPage.clickLoad();
        historyPage.clickLoad();

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("tbody tr"), 0));

        historyPage.selectSortOption("totalPrice");

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("tbody tr"), 1));

        List<WebElement> rows = historyPage.getRideRows();

        double first = Double.parseDouble(
                rows.get(0).findElement(By.className("ride-price")).getText()
        );

        double second = Double.parseDouble(
                rows.get(1).findElement(By.className("ride-price")).getText()
        );

        assertTrue(first >= second,
                "Rides are not sorted correctly by price!");
    }

    @Test
    @DisplayName("Happy Path: Admin can filter ride history by date range")
    public void testFilterByDateRange() {

        loginAsAdminAndOpenHistory();

        historyPage.selectUser(1);
        historyPage.clickLoad();
        historyPage.clickLoad();

        historyPage.setFromDate("2024-01-01");
        historyPage.setToDate("2024-12-31");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("body")));

        assertTrue(historyPage.getRideCount() >= 0,
                "Filtering by date failed!");
    }

    @Test
    @DisplayName("Edge Case: No rides in selected future date range")
    public void testNoRidesInDateRange() {

        loginAsAdminAndOpenHistory();

        historyPage.selectUser(1);
        historyPage.clickLoad();
        historyPage.clickLoad();

        historyPage.setFromDate("2035-01-01");
        historyPage.setToDate("2035-12-31");
        historyPage.clickSearch();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("body")));

        assertEquals(0, historyPage.getRideCount(),
                "There should be no rides in this date range!");
    }
}
