package com.rt.tests;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.rt.base.Base;
import com.rt.pages.Page;
import com.rt.utils.Action;

public class Flipkart extends Base {

	// Test to verify opening Flipkart homepage
	@Test(priority = 1)
	public void goToFlipkart() {

		logger.info("open Flipkart website");
		logger.info("Verify homepage loads successfully");
		Action.explicitWait(getDriver(), Page.searchBox(getDriver()), Duration.ofSeconds(10));
		Assert.assertTrue(Action.isPageLoaded(), "Flipkart homepage did not load successfully");
	}

	// Test to search for a product and add it to the cart
	@Test(dependsOnMethods = "goToFlipkart")
	public void searchAndAddToCart() throws InterruptedException {

		logger.info("search product");
		Page.searchBox(getDriver()).sendKeys("Laptop");
		Page.searchBox(getDriver()).submit();

		String parentWindow = getDriver().getWindowHandle();

		logger.info("click on first product");
		Page.searchIteam(getDriver()).click();

		Set<String> windowHandles = getDriver().getWindowHandles();

		for (String handels : windowHandles) {
			if (!equals(parentWindow)) {
				getDriver().switchTo().window(handels);
			}
		}

		try {
			Action.explicitWait(getDriver(), Page.addToCart(getDriver()), Duration.ofSeconds(10));

		} catch (StaleElementReferenceException e) {
			getDriver().navigate().refresh();
			logger.info("add to cart");
			Page.addToCart(getDriver()).click();
		}

		Assert.assertTrue(Page.product(getDriver()).isDisplayed(), "Product was not added to the cart");
	}

	// Test to proceed to checkout from the shopping cart
	@Test(dependsOnMethods = "searchAndAddToCart", invocationCount = 10)
	public void proceedToCheckout() {

		logger.info("Navigate back to shopping cart");
		getDriver().navigate().back();
		logger.info("Refresh Page");
		getDriver().navigate().refresh();

		logger.info("click on cart");
		Page.cart(getDriver()).click();

		logger.info("verify product in cart");
		Assert.assertTrue(Page.product(getDriver()).getText().contains(
				"CHUWI Intel Celeron Quad Core 12th Gen N100 - (8 GB/256 GB SSD/Windows 11 Home) GemiBook X_Pro Laptop"));

	}

	// The following tests are for user authentication, shipping information,
	// payment information, and order review.
	// They are currently disabled and can be enabled when static OTP.

	@Test(priority = 4, enabled = false)
	public void userAuthentication() {
		// Enter valid credentials to log in
		// Verify user is successfully logged in

	}

	@Test(priority = 5, enabled = false)
	public void shippingInformation() {
		// Enter valid shipping information
		// Proceed to the next step
	}

	@Test(priority = 6, enabled = false)
	public void paymentInformation() {
		// Choose a payment method
	}

	@Test(priority = 7, enabled = false)
	public void reviewOrder() {
		// Verify order summary
		// Do not proceed to final order placement
	}
}
