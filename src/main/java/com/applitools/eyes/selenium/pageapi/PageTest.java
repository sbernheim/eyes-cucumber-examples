package com.applitools.eyes.selenium.pageapi;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PageTest {
    private static final Logger log = LoggerFactory.getLogger(thisClass());
    
    protected final Site site;

    public PageTest(Site site) {
        this.site = site;
    }
    
    public <P extends Page> P load(P page) {
        log.info("loading page URL: {}", page.getURL());
        return site.load(page);
    }

}
