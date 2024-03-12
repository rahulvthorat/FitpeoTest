package com.rt.utils;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IAnnotationTransformer;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.rt.base.Base;

public class SuiteListener implements ITestListener, IAnnotationTransformer {

	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String filename = System.getProperty("user.dir") + File.separator + "screenshots" + File.separator+ testName + "Failure.png"
				+ result.getMethod().getMethodName();
		File file = ((TakesScreenshot) Base.getDriver()).getScreenshotAs(OutputType.FILE);
		
		 try {
		        // Copy the screenshot to the destination file
		        FileUtils.copyFile(file, new File(filename));
		        System.out.println("Screenshot captured: " + filename);
		    } catch (IOException e) {
		        System.err.println("Failed to capture screenshot: " + e.getMessage());
		        e.printStackTrace();
		    } finally {
		        // Quit the WebDriver session after capturing the screenshot
		        Base.getDriver().quit();
		    }
		}
	    

	    

	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}

}

   

