package base;

import extensions.AfterTestExtension;
import extensions.TestExtension;
import managers.DriverManager;
import screens.login.LoginPage;
import screens.login.SuccessLoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Supplier;

@ExtendWith({TestExtension.class,
             AfterTestExtension.class})
public abstract class BaseAndroidTest {

    protected Supplier<LoginPage> createLoginPage = LoginPage::new;
    protected Supplier<SuccessLoginPage> createSuccessPage = SuccessLoginPage::new;

    @BeforeEach
    public void setUp() {
        DriverManager.getDriver();
    }

    @AfterEach
    public void cleanUp() {
        DriverManager.quitDriver();
    }

}
