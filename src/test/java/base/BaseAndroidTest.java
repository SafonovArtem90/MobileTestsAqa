package base;

import extensions.AfterTestExtension;
import extensions.TestExtension;
import managers.DriverManager;
import screens.login.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({TestExtension.class,
             AfterTestExtension.class})
public abstract class BaseAndroidTest {

    protected LoginPage loginPage;

    @BeforeEach
    public void setUp() {
        DriverManager.getDriver();
        loginPage = new LoginPage();
    }

    @AfterEach
    public void cleanUp() {
        DriverManager.quitDriver();
    }

}
