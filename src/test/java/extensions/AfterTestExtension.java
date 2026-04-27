package extensions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

import static managers.DriverManager.getDriver;

public class AfterTestExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            takeScreenshot();
        }
    }

    private void takeScreenshot() {
        Allure.addAttachment("Screenshot of test failure",
                             new ByteArrayInputStream((getDriver()).getScreenshotAs(OutputType.BYTES)));
    }
}
