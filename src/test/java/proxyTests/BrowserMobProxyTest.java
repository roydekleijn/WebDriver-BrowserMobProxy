package proxyTests;

import java.io.File;

import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class BrowserMobProxyTest {
  private WebDriver driver;
  private ProxyServer server;

  @BeforeClass
  public void setup() throws Exception {
    // start the proxy
    server = new ProxyServer(4444);
    server.start();
    // get the Selenium proxy object
    Proxy proxy = server.seleniumProxy();

    // configure it as a desired capability
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.PROXY, proxy);

    // start the browser up
    driver = new FirefoxDriver(capabilities);
  }

  @AfterClass
  public void teardown() {
    driver.quit();
  }

  public void basicTest() throws Exception {
    // create a new HAR with the label "amazon.com"
    server.newHar("amazon.com");
    server.blacklistRequests("http://ecx.images-amazon.com/.*", 404);
    server.blacklistRequests(
        "http://z-ecx.images-amazon.com/images/G/01/browser-scripts/.*", 404);

    driver.get("http://www.amazon.com");
    Thread.sleep(7500);

    // get the HAR data
    System.out.println(server.getHar().getLog().getEntries().size());
    server.getHar().writeTo(new File("har.json"));
  }
}
