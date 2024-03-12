package com.rt.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Page {

	public static WebElement element = null;

	public static WebElement searchBox(WebDriver driver) {
		return element = driver.findElement(By.name("q"));
	}

	public static WebElement searchIteam(WebDriver driver) {
		return element = driver.findElement(By.xpath(
				"//body/div[@id='container']/div/div[@class='_36fx1h _6t1WkM _3HqJxg']/div[@class='_1YokD2 _2GoDe3']/div[@class='_1YokD2 _3Mn1Gg']/div[2]/div[1]/div[1]/div[1]/a[1]/div[2]/div[1]/div[2]"));
	}

	public static WebElement addToCart(WebDriver driver) {
		return element = driver.findElement(By.xpath("//button[@class='_2KpZ6l _2U9uOA _3v1-ww']"));
	}

	public static WebElement cart(WebDriver driver) {
		return element = driver.findElement(By.xpath("//*[name()='path' and contains(@class,'_1bS9ic')]"));
	}

	public static WebElement product(WebDriver driver) {
		return element = driver.findElement(By.xpath("//a[@class='_2Kn22P gBNbID']"));
	}

}
