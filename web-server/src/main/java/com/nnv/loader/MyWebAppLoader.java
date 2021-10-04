package com.nnv.loader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MyWebAppLoader extends ClassLoader {
    private String webPath;

    /**
     * lib:Represents the loaded file in the jar package
     * Similar to tomcat is {PROJECT}/WEB-INF/lib/
     */
    private String lib;
    /**
     * classes:Represents that the loaded file is a simple class file
     * Similar to tomcat is {PROJECT}/WEB-INF/classes/
     */
    private String classes;
    /**
     * Take the class es in all jar packages and read them into memory
     * Then, if you need to read it, look it up from the map
     */
    private Map<String, byte[]> map;

    /**
     * Just specify the project path.
     * The default jar loading path is {PROJECT}/WEB-INF/lib under the directory/
     * The default class loading path is {PROJECT}/WEB-INF/classes under the directory/
     *
     * @param webPath
     * @throws MalformedURLException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public MyWebAppLoader(String webPath) {
        if (webPath == null) {
            return;
        }
        this.webPath = webPath;
        lib = webPath + "/WEB-INF/lib/";
        classes = webPath + "/WEB-INF/classes/";
        map = new HashMap<String, byte[]>(64);

        preReadJarFile();
    }

    /**
     * According to the mechanism of the parent class, if no class is found in the parent class
     * This findClass will be called to load
     * This will only load files placed in your own directory
     * The class es that the system needs are not loaded from this
     */
    @Override
    public Class<?> findClass(String name) {
        try {
            byte[] result = getClassFromFileOrMap(name);
            if (result == null) {
                System.out.println("Failed to load class " + name);
            } else {
                return defineClass(name, result, 0, result.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find the file from the specified classes folder
     *
     * @param name
     * @return
     */
    private byte[] getClassFromFileOrMap(String name) {
        String classPath = classes + name.replace('.', File.separatorChar) + ".class";
        File file = new File(classPath);
        if (file.exists()) {
            InputStream input = null;
            try {
                input = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];
                int bytesNumRead = 0;
                while ((bytesNumRead = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesNumRead);
                }
                return baos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            if (map.containsKey(name)) {
                //Remove references from map s to avoid GC failing to recycle useless class files
                return map.remove(name);
            }
        }
        return null;
    }

    /**
     * Pre-read the package under lib
     */
    private void preReadJarFile() {
        List<File> list = scanDir();
        for (File f : list) {
            JarFile jar;
            try {
                jar = new JarFile(f);
                readJAR(jar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read a class file in a jar package and store it in the map of the current loader
     *
     * @param jar
     * @throws IOException
     */
    private void readJAR(JarFile jar) throws IOException {
        Enumeration<JarEntry> en = jar.entries();
        while (en.hasMoreElements()) {
            JarEntry je = en.nextElement();
            String name = je.getName();
            if (name.endsWith(".class")) {
                String clss = name.replace(".class", "").replaceAll("/", ".");
                if (this.findLoadedClass(clss) != null) continue;

                InputStream input = jar.getInputStream(je);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];
                int bytesNumRead = 0;
                while ((bytesNumRead = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesNumRead);
                }
                byte[] cc = baos.toByteArray();
                input.close();
                map.put(clss, cc);//Preserve temporarily
            }
        }
    }

    /**
     * Scan all jar packages under lib
     *
     * @return
     */
    private List<File> scanDir() {
        List<File> list = new ArrayList<File>();
        File[] files = new File(lib).listFiles();
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".jar"))
                list.add(f);
        }
        return list;
    }

    /**
     * Add a jar package to the loader.
     *
     * @param jarPath
     * @throws IOException
     */
    public void addJar(String jarPath) throws IOException {
        File file = new File(jarPath);
        if (file.exists()) {
            JarFile jar = new JarFile(file);
            readJAR(jar);
        }
    }

    @Override
    public URL getResource(String name) {
        URL url = null;
        // (2) Search local repositories
        String directoryPath = classes + name.replaceAll("\\.", "/");
        if (!checkExit(directoryPath)) {
            directoryPath = webPath + "/WEB-INF/" + name;
        }
        Path path = Paths.get(directoryPath);
        boolean isDir = Files.isDirectory(path);
        boolean isFile = Files.exists(path);
        if (isDir || isFile) {
            try {
                url = new URL("file:///" + path);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return url;
        }

        return super.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream targetStream = null;
        // (2) Search local repositories
        String directoryPath = classes + name;

        if (!checkExit(directoryPath)) {
            directoryPath = webPath + "/WEB-INF/" + name;
        }

        Path path = Paths.get(directoryPath);
        boolean isFile = Files.exists(path);
        if (isFile) {
            File initialFile = path.toFile();
            try {
                targetStream = new FileInputStream(initialFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return targetStream;
        }


        return super.getResourceAsStream(name);
    }

    boolean checkExit(String directoryPath) {
        Path path = Paths.get(directoryPath);
        return Files.exists(path);
    }
}
