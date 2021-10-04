package com.nnv.application.controller;

import com.nnv.application.model.Employee;
import com.nnv.application.model.FileStream;
import com.nnv.application.model.FileVO;
import com.nnv.application.ultil.VideoThumbTaker;
import com.nnv.core.anotation.MyController;
import com.nnv.core.anotation.MyRequestMapping;
import com.nnv.core.anotation.MyRequestParam;
import com.nnv.application.config.TemplateEngineUtil;
import com.nnv.application.config.ThymeleafConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jcodec.api.JCodecException;
import org.json.JSONArray;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@MyController
@MyRequestMapping("/home")
public class TestController {
    ExecutorService executorService = Executors.newSingleThreadExecutor();


    @MyRequestMapping("/index")
    public void fileName(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws ServletException, IOException, InterruptedException, JCodecException {
//        final AsyncContext ctx = request.startAsync();
//        executorService.execute(new TestRun(ctx));


        response.setContentType("text/html");
        PrintWriter pwriter = response.getWriter();
        servletContext = request.getServletContext();
        ThymeleafConfig thymeleafConfig = new ThymeleafConfig();
        thymeleafConfig.contextInitialized(servletContext);
        thymeleafConfig.templateEngine(servletContext);
        thymeleafConfig.templateResolver(servletContext);
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(servletContext);
        WebContext context = new WebContext(request, response, servletContext);

        URL url = Thread.currentThread().getContextClassLoader().getResource("/media/");
        String filePath = url.getPath().substring(1);

        List<FileVO> fileList = new ArrayList<>();
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();

        for (
                int i = 0;
                i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String ext2 = FilenameUtils.getExtension(listOfFiles[i].getName()); // returns "exe"
                if (ext2.equals("mp4") || ext2.equals("webm")) {
                    FileVO fileVO = new FileVO();
                    fileVO.setName(listOfFiles[i].getName());

                    String fileSizeReadable = FileUtils.byteCountToDisplaySize(listOfFiles[i].length());

                    fileVO.setSize(fileSizeReadable);
                    fileVO.setUrl(listOfFiles[i].getPath());
                    fileVO.setType(ext2);
                    fileList.add(fileVO);

                    Path p = Paths.get(listOfFiles[i].getAbsolutePath());
                    BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
                    FileTime fileTime = view.lastModifiedTime();

                    fileVO.setCreateTime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((fileTime.toMillis())));


                    String fileName = fileVO.getName().substring(0, fileVO.getName().indexOf(".") + 1) + "png";
                    String pathThumbnail = filePath + "/thumbnail/" + fileName;
                    fileVO.setVideoThumbUrl(fileName);
                    if (!Files.exists(Path.of(pathThumbnail))) {
                        VideoThumbTaker.generatePreviewImage(listOfFiles[i].getPath(), pathThumbnail);
                    }
                }
            }
        }


        context.setVariable("fileList", fileList);
        context.setVariable("recipient", "World");
        engine.process("index.html", context, pwriter);
        pwriter.close();

    }

    @MyRequestMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("fileName") String fileName) throws IOException {
//        fileName = "/media/" + fileName;
//        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
//        String filePath = url.getPath().substring(1);
        fileName = "C:\\Users\\admin\\OneDrive\\Iphone\\" + fileName;
        String filePath = fileName;
        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);

        // if you want to use a relative path to context root:

        String mimeType = Files.probeContentType(Path.of(filePath));
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }

        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
        response.setHeader("Accept-Ranges", "bytes");
        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();

        FileStream.stream(filePath, 30000).forEach(b -> {
            try {
                outStream.write(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        inStream.close();
        outStream.close();
    }

    @MyRequestMapping("/list/employee")
    public void employees(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            List<Employee> employeeList = Arrays.asList(
                    new Employee("Nguyến Văn A", 23, "Nhân viên"),
                    new Employee("Nguyến Văn B", 32, "Nhân viên"),
                    new Employee("Nguyến Văn C", 45, "Giám đốc"),
                    new Employee("Nguyến Văn D", 23, "Nhân viên"),
                    new Employee("Nguyến Văn E", 22, "Nhân viên"),
                    new Employee("Nguyến Văn F", 34, "Nhân viên"));


            JSONArray documentObj = new JSONArray(employeeList);

            response.getWriter().println(documentObj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @MyRequestMapping("/employee/create")
    public void create(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("userName") String userName, @MyRequestParam("password") String password) throws IOException {
        System.out.println(userName + password);
    }

    @MyRequestMapping("/employee/get")
    public void createGet(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("name") String[] name) throws IOException {
        response.setContentType("text/html");
        PrintWriter pwriter = response.getWriter();
        ServletContext servletContext = request.getServletContext();
        ThymeleafConfig thymeleafConfig = new ThymeleafConfig();
        thymeleafConfig.contextInitialized(servletContext);
        thymeleafConfig.templateEngine(servletContext);
        thymeleafConfig.templateResolver(servletContext);
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(servletContext);
        WebContext context = new WebContext(request, response, servletContext);

        context.setVariable("employee", new Employee());
        engine.process("employee.html", context, pwriter);
        pwriter.close();
    }
}
