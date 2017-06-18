package jpass;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SeleniumTest {

	private WebDriver driver = null;
	
	@Before
	public void setup() throws Exception {
		System.setProperty("webdriver.chrome.driver",
				"d:\\tools\\chromedriver.exe");
		
		//DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		// Add the WebDriver proxy capability.
		//Proxy proxy = new Proxy();
		//proxy.setHttpProxy("myhttpproxy:3337");
		//capabilities.setCapability("proxy", proxy);
		
		
		// Add ChromeDriver-specific capabilities through ChromeOptions.
		//ChromeOptions options = new ChromeOptions();
		//options.addExtensions(new File("/path/to/extension.crx"));
		//capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		
		driver = new ChromeDriver(/*capabilities*/);
		//driver.manage().window().maximize();
		//driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		//driver.manage().deleteAllCookies();
	}

	@After
	public void tearDown() throws Exception {
		//driver.quit();
	}

	@Test
	public void runBrowser() {
		
		//driver.get("https://compte.3suisses.fr/sso/login");
		driver.get("https://www.hotel-bb.com/fr/mot-de-passe-oublie/-/logClassicUser.htm");
		
		String html = driver.getPageSource();
        // Printing result here.
        System.out.println(html);
        
		// username
		driver.findElement(By.xpath("//input[@name='login']")).sendKeys(
				"bertrand.ave@gmail.com");
		// password
		driver.findElement(By.xpath("//input[@id='password-4']")).sendKeys(
				"jwk330");
		// FormIdentificationDejaClient
		driver.findElement(
				By.xpath("//form[@name='form-login-4']"))
				.submit();
	}
}
