import driverfactory.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class TestSetup {

    protected WebDriver driver;

    // Читаем переменные из Jenkinsfile флагов -D. Если их нет (запуск из IDEA) — берем дефолты для Windows
    protected static final String BASE_UI_URL = System.getProperty("ui.base.url", "http://localhost:3003");
    protected static final String SELENOID_URL = System.getProperty("remote.web.driver.url", ""); // Локально пусто, для Jenkins передадим урл

    @BeforeEach
    public void SetUp(){
        // Передаем SELENOID_URL в фабрику. Если строка пустая — фабрика создаст локальный браузер
        driver = new DriverFactory().create(SELENOID_URL);
        driver.manage().timeouts().implicitlyWait(Duration.of(5, SECONDS)); // Поставим 5 секунд для стабильности в Docker
        driver.manage().window().maximize();
    }

    @AfterEach
    public void TearDown(){
        if (driver != null) {
            driver.quit();
        }
    }

    void navigateToApplication(){
        if(System.getenv("TARGET") != null && System.getenv("TARGET").equals("production")){
            driver.navigate().to("https://automationintesting.online/admin");
            driver.manage().addCookie(new Cookie("welcome", "true"));
            driver.navigate().refresh();
        } else {
            // Теперь вместо хардкода localhost используется динамический UI URL (в Jenkins это http://rbp-assets:80)
            driver.navigate().to(BASE_UI_URL + "/admin");
        }
    }

}
