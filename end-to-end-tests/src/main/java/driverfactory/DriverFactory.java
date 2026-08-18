package driverfactory;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory
{
    public WebDriver create() {
        // Проверяем переменную BROWSER. В Jenkinsfile мы можем передавать BROWSER=remote
        if(System.getenv("BROWSER") != null){
            if(System.getenv("BROWSER").equals("chrome")){
                return prepareChromeDriver();
            } else if (System.getenv("BROWSER").equals("remote")){
                return prepareRemoteDriver();
            } else {
                System.out.println("WARN: Browser option '" + System.getenv("BROWSER") + "' not recognised. Falling back to ChromeDriver");
                return prepareChromeDriver();
            }
        }

        // Если переменная не задана (например, запустили из IntelliJ IDEA), тоже проверяем системное свойство для Selenoid
        if (System.getProperty("remote.web.driver.url") != null && !System.getProperty("remote.web.driver.url").isEmpty()) {
            return prepareRemoteDriver();
        }

        System.out.println("WARN: No browser option detected. Defaulting to ChromeDriver.");
        return prepareChromeDriver();
    }

    private WebDriver prepareChromeDriver(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // Добавим базовые флаги для стабильности локального Chrome
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }

    private WebDriver prepareRemoteDriver(){
        // Берем URL Selenoid из системных свойств (флаг -Dremote.web.driver.url из Jenkinsfile)
        // Если свойство пустое (например, локальный запуск через профиль remote), берем дефолтный адрес Selenoid
        String selenoidUrl = System.getProperty("remote.web.driver.url", "http://localhost:4444/wd/hub");

        System.out.println("INFO: Connecting to Selenoid at: " + selenoidUrl);

        ChromeOptions chromeOptions = new ChromeOptions();

        // Обязательные аргументы для стабильного запуска Chrome внутри Linux-контейнеров Selenoid
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--remote-allow-origins=*");

        // Настройки Selenoid для отображения сессии в Selenoid UI (VNC)
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);
        chromeOptions.setCapability("selenoid:options", selenoidOptions);

        try {
            return new RemoteWebDriver(new URL(selenoidUrl), chromeOptions);
        } catch (MalformedURLException e) {
            throw new RuntimeException("WARN: An error occurred attempting to create a remote driver connection to Selenoid. See the following error: " + e);
        }
    }
}
