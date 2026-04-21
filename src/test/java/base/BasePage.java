package base;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import managers.DriverManager;
import config.ConfigLoader;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

import java.time.Duration;

public abstract class BasePage {

    protected WaitUtils waitUtils;
    protected utils.ElementActions elementActions;
    protected utils.DeviceActions deviceActions;

    public BasePage() {
        PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ZERO), this);

        this.waitUtils = new WaitUtils(DriverManager.getDriver(), Integer.parseInt(ConfigLoader.getProperty("explicit.wait")));
        this.elementActions = new utils.ElementActions(waitUtils, DriverManager.getDriver());
        this.deviceActions = new utils.DeviceActions(DriverManager.getDriver());
    }
}
