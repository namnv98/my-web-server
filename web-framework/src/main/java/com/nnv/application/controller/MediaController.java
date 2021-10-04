package com.nnv.application.controller;

import com.nnv.application.model.FileStream;
import com.nnv.core.anotation.MyController;
import com.nnv.core.anotation.MyRequestMapping;
import com.nnv.core.anotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@MyController
@MyRequestMapping("/media")
public class MediaController {

    @MyRequestMapping("/view")
    public void download(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("fileName") String fileName) throws IOException {
        String path = "/media/";
        if (fileName.endsWith("png")) {
            path += "thumbnail/";
        }

        fileName = path + fileName;
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        String filePath = url.getPath().substring(1);


        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);


        String mimeType = Files.probeContentType(Path.of(filePath));

        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
        response.setHeader("Accept-Ranges", "bytes");

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
}
