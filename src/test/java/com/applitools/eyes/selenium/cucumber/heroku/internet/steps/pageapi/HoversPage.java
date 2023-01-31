package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HoversPage extends BasePage {
    
    private static final String RELATIVE_URL = "/hovers";
    
    By title = By.cssSelector("#content .example h3");
    By figure = By.className("figure");
    
    public HoversPage() {
        super(RELATIVE_URL);
    }
    
    public HoversPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }
    
    public String getTitle() {
        return find(title).getText();
    }
    
    /**
     * Hover over the figure at this index.
     * 
     * @param index starting with 1
     */
    public FigureCaption hoverOverFigure(int index) {
        return new FigureCaption(moveTo(figure, index - 1));
    }
    
    public class FigureCaption {
        
        By figureCaption = By.className("figcaption");
        By header = By.tagName("h5");
        By link = By.tagName("a");

        private WebElement caption;
        
        public FigureCaption(WebElement figure) {
           this.caption = figure.findElement(figureCaption); 
        }
        
        public boolean isDisplayed() {
            return caption.isDisplayed();
        }
        
        public String getTitle() {
            return caption.findElement(header).getText();
        }
        
        public WebElement getProfileLink() {
            return caption.findElement(link);
        }
        
        public String getProfileLinkURL() {
            return getProfileLink().getAttribute("href");
        }
        
        public String getProfileLinkText() {
            return getProfileLink().getText();
        }
        
        public String getPageUrl(String relativeUrl) {
            return site.pageUrlString(relativeUrl);
        }
    }

}
