package com.nnv;

import com.nnv.core.context.ContextUtils;
import com.nnv.core.context.MyServletContext;
import com.nnv.core.context.StandardContext;
import com.nnv.loader.MyWebAppLoader;
import com.nnv.xml.*;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.FilterRegistration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class InitServlet {
    private List<Map<String, ServletXml>> parseXmlServlet = new ArrayList<>();
    private List<Map<String, FilterXml>> parseXmlFilter = new ArrayList<>();
    private List<Map<String, ContextParamXml>> parseXmlContextParam = new ArrayList<>();

    public void init() {
        try {
            List<WebXml> webXmlList = new ArrayList<>();

            String currentDir = Path.of("").toAbsolutePath().toString() + "/web-server/webapps";
            File[] dirs = new File(currentDir).listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
            for (File file : dirs) {
                File webinfo = new File(file.getPath() + "\\" + Configer.WEBINF + "\\" + Configer.WEBXML);
                WebXml webXml = new WebXml();
                webXml.setPath(file.getPath());

                webXml.setServlet(partServlet(webinfo, webXml.getAppName()));
                webXml.setFilter(partFilter(webinfo, webXml.getAppName()));
                webXml.setListener(partContextListener(webinfo));
                webXml.setContextParam(partContextParam(webinfo));
                webXmlList.add(webXml);
            }
            List<MyServletContext> contextList = new ArrayList<>();

            for (WebXml webXml : webXmlList) {
                Thread.currentThread().setContextClassLoader(new MyWebAppLoader(webXml.getPath()));

                StandardContext standardContext = new StandardContext();
                standardContext.setAppName(webXml.getAppName());
                standardContext.setUrlPattern(webXml.getPath());

                MyServletContext applicationContext = new MyServletContext(standardContext);

                for (ServletXml parseXml : webXml.getServlet()) {
                    String clazz = parseXml.servlet.getServletClass();

                    Map<String, String> param = new HashMap<>();
                    parseXml.servlet.getInitParam().forEach(p -> {
                        param.put(p.getParamName(), p.getGetParamValue());
                    });
                    applicationContext.addServlet(parseXml.servlet.getServletName(), clazz, parseXml.servletMapping.getUrlPattern(), null, param);
                }

                for (FilterXml parseXml : webXml.getFilter()) {
                    String clazz = parseXml.filter.getFilterClass();
                    FilterRegistration.Dynamic filter = applicationContext.addFilter(parseXml.filter.getFilterName(), clazz, null);

                    Map<String, String> paramMap = new HashMap<>();
                    for (FilterXml.Param param : parseXml.filter.getInitParam()) {
                        paramMap.put(param.getParamName(), param.getParamValue());
                    }
                    filter.setInitParameters(paramMap);
                }

                for (ContextParamXml contextParamXml : webXml.getContextParam()) {
                    applicationContext.getContext().setContextParamDefs(contextParamXml.getParamName(), contextParamXml);
                }
                for (ListenerXml listenerXml : webXml.getListener()) {
                    applicationContext.addListener(listenerXml.getListenerClass());
                }
                contextList.add(applicationContext);
            }
            ContextUtils.getInstance().addServletContext(contextList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<ListenerXml> partContextListener(File webinfo) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(webinfo);

        NodeList nl = doc.getElementsByTagName("listener");
        Map<String, ListenerXml> mapXml = new HashMap<>();
        for (int i = 0; i < nl.getLength(); i++) {
            String paramValue = getChildContent((Element) nl.item(i), "listener-class");
            mapXml.put(paramValue, new ListenerXml(paramValue));
        }
        return new ArrayList<>(mapXml.values());
    }

    private List<ServletXml> partServlet(File webinfo, String appName) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(webinfo);

        NodeList nl = doc.getElementsByTagName("servlet");
        Map<String, ServletXml> mapXml = new HashMap<>();
        for (int i = 0; i < nl.getLength(); i++) {
            ServletXml parseXml = new ServletXml();

            String servletName = getChildContent((Element) nl.item(i), "servlet-name");
            String clazz = getChildContent((Element) nl.item(i), "servlet-class");


            Element elementInitParam = getChild((Element) nl.item(i), "init-param");
            String paramName = getChildContent(elementInitParam, "param-name");
            String paramValue = getChildContent(elementInitParam, "param-value");

            parseXml.servlet.setServletName(servletName);
            parseXml.servlet.setServletClass(clazz);
            parseXml.servlet.setInitParam(Arrays.asList(parseXml.newParam(paramName, paramValue)));

            mapXml.put(servletName, parseXml);
        }

        NodeList nlmp = doc.getElementsByTagName("servlet-mapping");
        for (int i = 0; i < nlmp.getLength(); i++) {
            String servletName = getChildContent((Element) nlmp.item(i), "servlet-name");
            String urlPattern = getChildContent((Element) nlmp.item(i), "url-pattern");
            String url = String.format("/%s", appName) + urlPattern.replace("*", "([A-z-0-9])\\w+");

            ServletXml parseXml = mapXml.get(servletName);
            parseXml.servletMapping.setUrl(url);
            parseXml.servletMapping.setServletName(servletName);
            parseXml.servletMapping.setUrlPattern(urlPattern);
        }
        return new ArrayList<>(mapXml.values());
    }

    private List<ContextParamXml> partContextParam(File webinfo) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(webinfo);

        NodeList nl = doc.getElementsByTagName("context-param");
        ArrayList<ContextParamXml> mapXml = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            String paramName = getChildContent((Element) nl.item(i), "param-name");
            String paramValue = getChildContent((Element) nl.item(i), "param-value");
            mapXml.add(new ContextParamXml(paramName, paramValue));
        }
        return mapXml;
    }

    private List<FilterXml> partFilter(File webinfo, String appName) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(webinfo);

        NodeList nl = doc.getElementsByTagName("filter");
        Map<String, FilterXml> mapXml = new HashMap<>();
        for (int i = 0; i < nl.getLength(); i++) {
            FilterXml parseXml = new FilterXml();
            String filterName = getChildContent((Element) nl.item(i), "filter-name");
            String clazz = getChildContent((Element) nl.item(i), "filter-class");

            parseXml.filter.setFilterName(filterName);
            parseXml.filter.setFilterClass(clazz);

            Element elementInitParam = getChild((Element) nl.item(i), "init-param");
            String paramName = getChildContent(elementInitParam, "param-name");
            String paramValue = getChildContent(elementInitParam, "param-value");
            parseXml.filter.setInitParam(Arrays.asList(parseXml.newParam(paramName, paramValue)));
            mapXml.put(filterName, parseXml);
        }

        NodeList nlmp = doc.getElementsByTagName("filter-mapping");
        for (int i = 0; i < nlmp.getLength(); i++) {
            String filterName = getChildContent((Element) nl.item(i), "filter-name");
            String urlPattern = getChildContent((Element) nlmp.item(i), "url-pattern");
            String url = String.format("/%s", appName) + urlPattern.replace("*", "([A-z-0-9])\\w+");

            FilterXml parseXml = mapXml.get(filterName);
            parseXml.filterMapping.setUrl(url);
            parseXml.filterMapping.setFilterName(filterName);
            parseXml.filterMapping.setUrlPattern(urlPattern);
        }
        return new ArrayList<>(mapXml.values());
    }


    private String getChildContent(Element parent, String name) {
        Element child = getChild(parent, name);
        if (child == null) {
            return null;
        } else {
            String content = (String) getContent(child);
            return content;
        }
    }

    private Object getContent(Element element) {
        NodeList nl = element.getChildNodes();
        StringBuffer content = new StringBuffer();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    return node;
                case Node.CDATA_SECTION_NODE:
                case Node.TEXT_NODE:
                    content.append(node.getNodeValue());
                    break;
            }
        }
        return content.toString().trim();
    }

    private Element getChild(Element parent, String name) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element && name.equals(child.getNodeName())) {
                return (Element) child;
            }
        }
        return null;
    }
}
