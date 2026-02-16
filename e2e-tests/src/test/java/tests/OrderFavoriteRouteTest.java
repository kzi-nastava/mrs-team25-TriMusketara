package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.*;
import utils.DriverFactory;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderFavoriteRouteTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private LoginPage loginPage;
    private FavoriteRoutesPage favoritesPage;
    private MainPage mainPage;
    private RideOrderingForm rideForm;
    private NavBarPage navbar;
    private ProfileSidebarPage sidebar;

    // All pages initialized
    @BeforeEach
    public void setUp() {
        driver = DriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginPage = new LoginPage(driver);
        favoritesPage = new FavoriteRoutesPage(driver);
        mainPage = new MainPage(driver);
        rideForm = new RideOrderingForm(driver);
        navbar = new NavBarPage(driver);
        sidebar = new ProfileSidebarPage(driver);

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Successfully order a ride from favorites")
    public void testOrderFavoriteRouteHappyPath() throws InterruptedException {

        // Login
        loginPage.open();
        loginPage.enterEmail("passenger@demo.com");
        loginPage.enterPassword("passenger123");
        loginPage.logIn();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));

        wait.until(ExpectedConditions.urlToBe("http://localhost:4200/map"));

        // Open profile sidebar
        navbar.clickProfileButton();

        sidebar.waitForSidebar();

        // Click on button
        sidebar.clickButtonByText("Favorite routes");

        wait.until(ExpectedConditions.urlContains("/favorite-routes"));

        // Check if we are on the page
        assertTrue(driver.getCurrentUrl().contains("/favorite-routes"));
        favoritesPage.routesLoading();

        // Check if the page is loaded
        assertTrue(favoritesPage.isLoaded());

        int numberOfRoutes = favoritesPage.getNumberOfRoutes();
        assertTrue(numberOfRoutes > 0); // Passenger has one or more favorite routes

        // Save origin and destination of the favored route we will order
        // Later used for assertion
        String expectedOriginVal = favoritesPage.getRouteOrigin(0);
        String expectedDestinationVal = favoritesPage.getRouteDestination(0);

        // Click to order
        favoritesPage.orderRoute(0);

        wait.until(ExpectedConditions.urlContains("localhost:4200/"));

        // Check if we were brought to main page
        assertTrue(driver.getCurrentUrl().contains("localhost:4200/"));

        // Wait for popup to open
        mainPage.waitForPopupToOpen();
        assertTrue(mainPage.isRidePopupOpen());

        // Check form
        rideForm.formLoading();

        // Get prefilled values
        String actualOriginVal = rideForm.getOriginValue();
        String actualDestinationVal = rideForm.getDestinationValue();

        // Check if they are prefilled in form and the same
        assertEquals(expectedOriginVal, actualOriginVal);
        assertEquals(expectedDestinationVal, actualDestinationVal);

        // Fill out other form fields
        rideForm.selectVehicleType("STANDARD");

        String futureTime = getFutureTime(2);
        rideForm.enterTime(futureTime);

        rideForm.checkBabyFriendly();
        rideForm.checkPetFriendly();

        // Order route
        rideForm.orderRoute();

        wait.until(ExpectedConditions.urlContains("localhost:4200/"));

        assertTrue(driver.getCurrentUrl().contains("localhost:4200/"));
    }

    @Test
    @DisplayName("Edge Case: User has no favored routes")
    public void testOrderFavoriteRoutesNoFavoriteRoutes() throws  InterruptedException {

        // Login
        loginPage.open();
        loginPage.enterEmail("passenger@demo.com");
        loginPage.enterPassword("passenger123");
        loginPage.logIn();

        wait.until(ExpectedConditions.urlContains("localhost:4200/"));

        // Open profile sidebar
        navbar.clickProfileButton();
        sidebar.waitForSidebar();

        // Click on button
        sidebar.clickButtonByText("Favorite routes");

        wait.until(ExpectedConditions.urlContains("/favorite-routes"));

        // Check if we are on the page
        assertTrue(driver.getCurrentUrl().contains("/favorite-routes"));

        assertTrue(favoritesPage.isLoaded());

        // Get number of favored routes
        int numberOfRoutes = favoritesPage.getNumberOfRoutes();

        // Expecting zero
        assertEquals(0, numberOfRoutes);
    }

    @Test
    @DisplayName("Edge Case: Time has passed")
    public void testOrderFavoriteRouteInvalidTime() throws  InterruptedException {

        //Login
        loginPage.open();
        loginPage.enterEmail("passenger@demo.com");
        loginPage.enterPassword("passenger123");
        loginPage.logIn();

        wait.until(ExpectedConditions.urlContains("localhost:4200/"));

        // Click and wait for sidebar
        navbar.clickProfileButton();
        sidebar.waitForSidebar();

        sidebar.clickButtonByText("Favorite routes");

        wait.until(ExpectedConditions.urlContains("/favorite-routes"));
        // Wait for page to load
        favoritesPage.routesLoading();
        assertTrue(favoritesPage.isLoaded());

        favoritesPage.orderRoute(0);

        wait.until(ExpectedConditions.urlContains("localhost:4200/"));

        mainPage.waitForPopupToOpen();

        // Wait for form to load
        rideForm.formLoading();;

        // Fill form
        rideForm.selectVehicleType("STANDARD");
        String passedTime = getPassedTime(2); // Set passed time
        rideForm.enterTime(passedTime);

        // Order route
        rideForm.orderRoute();

        Thread.sleep(1000);

        boolean popupStillOpen = mainPage.isRidePopupOpen();
        // Expect for popup to still be open since the ride ordering process is not successful
        assertTrue(popupStillOpen);
    }

    // Helper
    private String getFutureTime(int hoursFromNow) {
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime future = now.plusHours(hoursFromNow);

        return String.format("%02d:%02d", future.getHour(), future.getMinute());
    }

    private String getPassedTime(int hoursAgo) {
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime past = now.minusHours(hoursAgo);

        return String.format("%02d:%02d", past.getHour(), past.getMinute());
    }
}
