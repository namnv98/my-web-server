package com.nnv.application.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

/**
 * Thymeleaf configuration.
 */
@WebListener
public class ThymeleafConfig {

    public void contextInitialized(ServletContext sce) {
        TemplateEngine engine = templateEngine(sce);
        TemplateEngineUtil.storeTemplateEngine(sce, engine);
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

    public TemplateEngine templateEngine(ServletContext servletContext) {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver(servletContext));
        return engine;
    }

    public ITemplateResolver templateResolver(ServletContext servletContext) {
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(servletContext);
        resolver.setPrefix("/templates/");
        resolver.setTemplateMode(TemplateMode.HTML);
        return resolver;
    }

}
