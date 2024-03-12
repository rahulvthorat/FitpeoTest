package com.rt.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.rt.utils.Action;
import com.rt.utils.FileUtils;

public class Base {

	private static final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();
	private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
	private static final ThreadLocal<String> myThreadLocal = new ThreadLocal<>();
	public ExtentSparkReporter sparkReporter;
	public ExtentSparkReporter FailedsparkReporter;
	public ExtentReports extent;
	public static ExtentTest logger;
	public static Properties prop;

	@BeforeSuite(groups = { "Sanity", "Smoke", "Regression" })
	public void loadconfig() throws IOException {
		try {
			prop = new Properties();
			FileInputStream ip = new FileInputStream(".//configuration//config.Properties");
			prop.load(ip);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@BeforeTest
	public void extentReport() {
		sparkReporter = new ExtentSparkReporter(
				System.getProperty("user.dir") + File.separator + "Reports" + File.separator + "SDET1").viewConfigurer()
				.viewOrder().as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST, ViewName.CATEGORY, ViewName.DEVICE,
						ViewName.AUTHOR })
				.apply();

		FailedsparkReporter = new ExtentSparkReporter(
				System.getProperty("user.dir") + File.separator + "Reports" + File.separator + "SDET2").viewConfigurer()
				.viewOrder().as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST, ViewName.CATEGORY, ViewName.DEVICE,
						ViewName.AUTHOR })
				.apply();

		FailedsparkReporter.filter().statusFilter().as(new Status[] { Status.FAIL, }).apply();
		FailedsparkReporter.config().setDocumentTitle("Failed-Tests");

		extent = new ExtentReports();
		extent.attachReporter(sparkReporter, FailedsparkReporter);
		sparkReporter.config().setTheme(Theme.STANDARD);
		extent.setSystemInfo("QA", "Rahul Thorat");
		extent.setSystemInfo("Project", "Demo");
		extent.setSystemInfo("UserName", "*****");
		sparkReporter.config().setDocumentTitle("Automation Report");
		sparkReporter.config().setReportName("QA Extent Report");

	}

    @Parameters("browser")
	@BeforeClass       // String browser add for cross browser Testing
	public static void browserInitialization(String browser) { 
    	// for specific browser
    	//String browserName = prop.getProperty("browser"); 
		if (browser.equalsIgnoreCase("chrome")) {
			webDriverThreadLocal.set(new ChromeDriver());
		} else if (browser.equalsIgnoreCase("firefox")) {
			webDriverThreadLocal.set(new FirefoxDriver());
		} else if (browser.equalsIgnoreCase("edge")) {
			webDriverThreadLocal.set(new EdgeDriver());
		}

		getDriver().manage().window().maximize();
		getDriver().get(prop.getProperty("url"));
		Action.implicitWait(getDriver(), 20);
		Action.pageLoadTimeOut(getDriver(), 10);
	}

	public static WebDriver getDriver() {
		return webDriverThreadLocal.get();
	}

	@BeforeMethod
	public void setupInformations(Method testMethod) {
		logger = extent.createTest(testMethod.getName());
		extentTestThreadLocal.set(logger);
		myThreadLocal.set("Hello ThreadLocal");
		logger.assignAuthor("Rahul Thorat");
		logger.addScreenCaptureFromBase64String("Screenshots");
		logger.assignDevice("Chrome").assignDevice("Firefox").assignDevice("Edge");
		logger.assignCategory("Smoke").assignCategory("Sanity").assignCategory("Regression");
		logger.getModel().setDescription("This test verifies the functionality.");
		logger.pass(MarkupHelper.createUnorderedList(Arrays.asList(new String[] { "Selenium" })).getMarkup());
		logger.pass("screenshots", MediaEntityBuilder.createScreenCaptureFromBase64String(Action.getBase64()).build());
	}

	@AfterMethod
	public void setupColour(ITestResult result) {
		if (extent != null) {
			if (result.getStatus() == ITestResult.FAILURE) {
				logger.log(Status.FAIL,
						MarkupHelper.createLabel(result.getName() + " -Test Case Failed", ExtentColor.RED));
				File file = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(file, new File("QAutomations\\screenshots\\failure.png"));
				logger.log(Status.FAIL,
						MarkupHelper.createLabel(result.getThrowable() + " -Test Case Failed", ExtentColor.RED));
			} else if (result.getStatus() == ITestResult.SKIP) {
				logger.log(Status.SKIP,
						MarkupHelper.createLabel(result.getName() + " -Test Case Skipped", ExtentColor.ORANGE));
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				logger.log(Status.PASS,
						MarkupHelper.createLabel(result.getName() + " -Test Case Pass", ExtentColor.GREEN));
			}
		}
	}

	@AfterClass
	public void afterClass() {
		// Perform cleanup actions after the test class
	}

	@AfterTest
	public void closeBrowser() {
		if (getDriver() != null) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			getDriver().quit();
		}
	}

	@AfterSuite
	public void endReport() {
		if (extent != null) {
			extent.flush();
		}
	}

}
