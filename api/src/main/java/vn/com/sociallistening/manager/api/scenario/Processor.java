package vn.com.sociallistening.manager.api.scenario;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import vn.com.sociallistening.manager.api.pojos.social.SocialNetwork;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class Processor {
    private static final long DEFAULT_WAIT_TIMEOUT = 3000;
    private static final long DEFAULT_SLEEP_TIME = 200;
    public static String CRAWL_PROFILE_TYPE;

    private Map<String, Object> map;
    private Map<Object, String> mapConvert;
    private final WebDriver webDriver;
    private final ApplicationContext applicationContext;

    public Processor(WebDriver webDriver, ApplicationContext applicationContext) {
        this.webDriver = webDriver;
        this.applicationContext = applicationContext;
    }

    public void process(List<Definition> definitions) throws InterruptedException {
        WebElement element;
        List<WebElement> elements;
        int size = definitions.size();
        Definition definition;

        for (int i = 0; i < size; ++i) {
            definition = definitions.get(i);
            switch (definition.getAction().toLowerCase()) {
                case Definition.ACTION_LOAD_PAGE:
                    webDriver.get(definition.getValue());
                    break;

                case Definition.ACTION_GOTO_PAGE:
                    webDriver.navigate().to(definition.getValue());
                    break;

                case Definition.ACTION_BACK_PAGE:
                    webDriver.navigate().back();
                    break;

                case Definition.ACTION_FORWARD_PAGE:
                    webDriver.navigate().forward();
                    break;

                case Definition.ACTION_SET:
                    element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                    if (element == null) {
                        log.warn("No element found with xpath {}", definition.getXpath());
                        return;
                    } else element.sendKeys(definition.getValue());
                    break;

                case Definition.ACTION_CLICK:
                    element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                    if (element == null) {
                        log.warn("No element found with xpath {}", definition.getXpath());
                        if (!definition.isIgnore_exception())
                            return;
                        break;
                    } else element.click();
                    break;

                case Definition.ACTION_BEGIN_CLICK_LIST: {
                    elements = waitListLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                    if (elements == null) {
                        log.warn("No element found with xpath {}", definition.getXpath());
                        if (!definition.isIgnore_exception())
                            return;
                        break;
                    }
                    if (definition.getChild_xpath() != null) {
                        List<Definition> definitionList = new ArrayList<>();
                        int indexAction = i + 1;
                        if (definition.getIndex() == 0) {
                            while (!definitions.get(indexAction).getAction().equalsIgnoreCase(Definition.ACTION_END_CLICK_LIST))
                                definitionList.add(definitions.get(indexAction++));
                        } else
                            while (indexAction < size) {
                                if (definitions.get(indexAction).getAction().equalsIgnoreCase(Definition.ACTION_END_CLICK_LIST) &&
                                        definitions.get(indexAction).getIndex() == definition.getIndex())
                                    break;
                                definitionList.add(definitions.get(indexAction++));
                            }
                        String url;
                        for (WebElement item : elements) {
                            element = findSingleChildElement(item, By.xpath(definition.getChild_xpath()));
                            if (element != null) {
                                url = "window.open('" + element.getAttribute(definition.getAttribute()) + "', '-blank')";
                                ((JavascriptExecutor) webDriver).executeScript(url);
                                //focus new tab
                                List<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
                                webDriver.switchTo().window(tabs.get(1));
                                this.process(definitionList);
                                webDriver.close();
                                webDriver.switchTo().window(tabs.get(0));
                            }
                        }
                        i = indexAction - 1;
                    }
                    break;
                }

                case Definition.ACTION_END_CLICK_LIST:
                    break;

                case Definition.ACTION_SCROLL_DOWN: {
                    element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                    if (element == null) {
                        log.warn("No element found with xpath {}", definition.getXpath());
                        return;
                    }
                    element.sendKeys(Keys.CONTROL, Keys.END);
                    break;
                }

                case Definition.ACTION_SLEEP:
                    try {
                        Thread.sleep(Long.parseLong(definition.getValue()));
                    } catch (NumberFormatException ex) {
                        log.warn("Cannot convert String to Long : {}", definition.getValue());
                        return;
                    }
                    break;

                case Definition.ACTION_SCREEN_MAX:
                    webDriver.manage().window().maximize();
                    break;

                case Definition.ACTION_GET: {
                    element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                    if (element == null) {
                        log.warn("No element found with xpath {}", definition.getXpath());
                        if (!definition.isIgnore_exception())
                            return;
                        break;
                    }
                    List<Object> list = new ArrayList<>();
                    String attr = definition.getAttribute();
                    List<WebElement> elementsTemp;

                    if (definition.getChild_xpath() != null) {
                        element = findSingleChildElement(element, By.xpath(definition.getChild_xpath()));
                        map.put(
                                definition.getFieldName(),
                                element == null ?
                                        null :
                                        (StringUtils.equalsIgnoreCase("text", attr) ?
                                                element.getText() :
                                                element.getAttribute(attr)));

                    } else if (definition.getList_child_xpath() != null) {
                        elementsTemp = findListChildElement(element, By.xpath(definition.getList_child_xpath()));
                        if (elementsTemp != null) {
                            elementsTemp.forEach((value) -> list.add(value == null ?
                                    null :
                                    (attr.equalsIgnoreCase("text") ?
                                            value.getText() :
                                            value.getAttribute(attr))));
                            map.put(definition.getFieldName(), list);
                        } else map.put(definition.getFieldName(), null);
                    } else
                        map.put(
                                definition.getFieldName(),
                                StringUtils.equalsIgnoreCase("text", attr) ?
                                        element.getText() :
                                        element.getAttribute(attr));
                    break;
                }

                case Definition.ACTION_GET_LIST: {
                    elements = waitListLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                    if (elements == null) {
                        log.warn("No element found with xpath {}", definition.getXpath());
                        if (!definition.isIgnore_exception())
                            return;
                        break;
                    }
                    List<Object> list = new ArrayList<>();
                    String attr = definition.getAttribute();
                    List<WebElement> elementsTemp;
                    for (WebElement item : elements) {
                        if (definition.getChild_xpath() != null) {
                            element = findSingleChildElement(item, By.xpath(definition.getChild_xpath()));
                            list.add(element == null ?
                                    null :
                                    (attr.equalsIgnoreCase("text") ?
                                            element.getText() :
                                            element.getAttribute(attr)));
                        } else if (definition.getList_child_xpath() != null) {
                            elementsTemp = findListChildElement(item, By.xpath(definition.getList_child_xpath()));
                            if (elementsTemp != null)
                                elementsTemp.forEach((value) -> list.add(value == null ?
                                        null :
                                        (attr.equalsIgnoreCase("text") ?
                                                value.getText() :
                                                value.getAttribute(attr))));
                            else list.add(null);
                        } else
                            list.add(attr.equalsIgnoreCase("text") ? item.getText() :
                                    item.getAttribute(attr));
                    }
                    map.put(definition.getFieldName(), list);
                    break;
                }

                case Definition.ACTION_BEGIN_UNLIMITED_LOOP: {

                    List<Definition> definitionList = new ArrayList<>();
                    int indexActionInLoop = i + 1;
                    //check for loop
                    if (definition.getIndex() == 0) {
                        /**
                         * count & add actions in loop
                         */
                        while (!definitions.get(indexActionInLoop).getAction().equalsIgnoreCase(Definition.ACTION_END_LOOP)) {
                            definitionList.add(definitions.get(indexActionInLoop++));
                        }
                    } else {
                        while (indexActionInLoop < size) {
                            if (definitions.get(indexActionInLoop).getAction().equalsIgnoreCase(Definition.ACTION_END_LOOP) &&
                                    definition.getIndex() == definitions.get(indexActionInLoop).getIndex())
                                break;
                            definitionList.add(definitions.get(indexActionInLoop++));
                        }
                    }

                    if (definition.getXpath() != null) {
                        element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                        if (element == null) {
                            if (log.isDebugEnabled())
                                log.warn("No element found with xpath {}", definition.getXpath());
                            i = indexActionInLoop - 1;
                            break;
                        }
                    }

                    while (true) {
                        /**
                         * recursive
                         */
                        this.process(definitionList);
                        element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getNext_page_element_xpath()));
                        if (element == null) {
                            i = indexActionInLoop - 1; // jump : end unlimited loop
                            break;
                        }
                        try {
                            switch (definition.getNext_page_action()) {
                                case "click":
                                    element.click();
                                    break;
                                case "submit":
                                    element.submit();
                                    break;
                                default: {
                                    log.warn("UnKnow next page action: {}", definition.getNext_page_action());
                                    return;
                                }
                            }
                        } catch (StaleElementReferenceException ex) {
                            break;
                        }
                    }
                    break;
                }

                case Definition.ACTION_BEGIN_LIMITED_LOOP: {
                    List<Definition> definitionList = new ArrayList<>();
                    int indexActionInLoop = i + 1;
                    //check for loop
                    if (definition.getIndex() == 0) {
                        /**
                         * count & add actions in loop
                         */
                        while (!definitions.get(indexActionInLoop).getAction().equalsIgnoreCase(Definition.ACTION_END_LOOP)) {
                            definitionList.add(definitions.get(indexActionInLoop++));
                        }
                    } else {
                        while (indexActionInLoop < size) {
                            if (definitions.get(indexActionInLoop).getAction().equalsIgnoreCase(Definition.ACTION_END_LOOP) &&
                                    definition.getIndex() == definitions.get(indexActionInLoop).getIndex())
                                break;
                            definitionList.add(definitions.get(indexActionInLoop++));
                        }
                    }

                    if (definition.getXpath() != null) {
                        element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getXpath()));
                        if (element == null) {
                            if (log.isDebugEnabled())
                                log.warn("No element found with xpath {}", definition.getXpath());
                            i = indexActionInLoop - 1;
                            break;
                        }
                    }

                    int limit;
                    try {
                        limit = Integer.parseInt(definition.getValue());
                    } catch (NumberFormatException ex) {
                        log.warn("Cannot convert String to Integer : {}", definition.getValue());
                        return;
                    }
                    for (int k = 0; k < limit; ++k) {
                        /**
                         * recursive
                         */
                        this.process(definitionList);
                        element = waitSingleLoaderCompleted(webDriver, By.xpath(definition.getNext_page_element_xpath()));
                        if (element == null) {
                            i = indexActionInLoop - 1; // jump : end limited loop
                            break;
                        }
                        try {
                            switch (definition.getNext_page_action()) {
                                case "click":
                                    element.click();
                                    break;
                                case "submit":
                                    element.submit();
                                    break;
                                case "none":
                                    break;
                                default: {
                                    log.warn("UnKnow next page action: {}", definition.getNext_page_action());
                                    return;
                                }
                            }
                        } catch (StaleElementReferenceException ex) {
                            break;
                        }
                    }
                    break;
                }

                case Definition.ACTION_END_LOOP: {
                    break;
                }

                case Definition.ACTION_DECLARE_LIST_OBJECT: {
                    mapConvert = new LinkedHashMap<>();
                    break;
                }

                case Definition.ACTION_ADD_TO_MAP_OBJECT: {
                    mapConvert.put(map, definition.getType());
                    break;
                }

                case Definition.ACTION_DECLARE_SINGLE_OBJECT:
                    map = new HashMap<>();
                    break;

                case Definition.ACTION_CONVERT_OBJECT:
                    // save db
                    SocialNetwork social = applicationContext.
                            getBean(definition.getType(), SocialNetwork.class);
                    CRAWL_PROFILE_TYPE = definition.getType();
                    social.saveCrawlProfile(social.dataFilter(mapConvert), definition.getValue());
                    break;

                default:
                    log.warn("UnKnow action {}", definition.getAction());
                    break;
            }
        }
    }

    private WebElement findSingleChildElement(WebElement element, By locator) {
        try {
            return element.findElement(locator);
        } catch (WebDriverException ex) {
            if (log.isDebugEnabled())
                log.warn("{}", locator);
            return null;
        }
    }

    private List<WebElement> findListChildElement(WebElement element, By locator) {
        try {
            return element.findElements(locator);
        } catch (WebDriverException ex) {
            if (log.isDebugEnabled())
                log.warn("{}", locator);
            return null;
        }
    }

    private WebElement waitSingleLoaderCompleted(WebDriver driver, By locator) {
        try {
            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            wait.withTimeout(Duration.ofMillis(DEFAULT_WAIT_TIMEOUT));
            wait.pollingEvery(Duration.ofMillis(DEFAULT_SLEEP_TIME));
            wait.ignoring(NoSuchElementException.class);
            wait.ignoring(StaleElementReferenceException.class);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (WebDriverException e) {
            if (log.isDebugEnabled())
                log.warn("{}", locator);
            return null;
        }
    }

    private List<WebElement> waitListLoaderCompleted(WebDriver driver, By locator) {
        try {
            FluentWait<WebDriver> wait = new FluentWait<>(driver);
            wait.withTimeout(Duration.ofMillis(DEFAULT_WAIT_TIMEOUT));
            wait.pollingEvery(Duration.ofMillis(DEFAULT_SLEEP_TIME));
            wait.ignoring(NoSuchElementException.class);
            wait.ignoring(StaleElementReferenceException.class);
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (WebDriverException e) {
            if (log.isDebugEnabled())
                log.warn("{}", locator);
            return null;
        }
    }
}
