package com.nnv.core.servlet;

import java.io.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileStream implements Iterator<byte[]>, Iterable<byte[]>, Spliterator<byte[]> {

    private InputStream stream;
    private int bufferSize;
    private long blockCount;


    /**
     * Create a FileStreamReader
     * @param stream the input stream containing the content to be read
     * @param bufferSize size of the buffer that should be read at once from the stream
     */
    private FileStream(InputStream stream, long fileSize, int bufferSize) {
        this.bufferSize = bufferSize;
        //calculate how many blocks will be generated by this stream
        this.blockCount = (long) Math.ceil((float)fileSize / (float)bufferSize);
        this.stream = stream;
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = false;
        try {
            hasNext = stream.available() > 0;
            return hasNext;
        } catch (IOException e) {
            return false;
        } finally {
            //close the stream if there is no more to read
            if (!hasNext) {
                close();
            }
        }
    }

    @Override
    public byte[] next() {
        try {
            byte[] data = new byte[Math.min(bufferSize, stream.available())];
            stream.read(data);
            return data;
        } catch (IOException e) {
            //Close the stream if next causes an exception
            close();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Close the stream
     */
    public void close() {
        try {
            stream.close();
        } catch (IOException e) { }
    }

    @Override
    public boolean tryAdvance(Consumer<? super byte[]> action) {
        action.accept(next());
        return hasNext();
    }

    @Override
    public Spliterator<byte[]> trySplit() {
        return this;
    }

    @Override
    public long estimateSize() {
        return blockCount;
    }

    @Override
    public int characteristics() {
        return Spliterator.IMMUTABLE;
    }

    @Override
    public Iterator<byte[]> iterator() {
        return this;
    }

    @Override
    public void forEachRemaining(Consumer<? super byte[]> action) {
        while(hasNext())
            action.accept(next());
    }

    /**
     * Create a java stream
     * @param inParallel if true then the returned stream is a parallel stream; if false the returned stream is a sequential stream.
     * @return stream with the data
     */
    private Stream<byte[]> stream(boolean inParallel) {
        return StreamSupport.stream(this, inParallel);
    }

    /**
     * Create a File Stream reader
     * @param fileName Name of the file to stream
     * @param bufferSize size of the buffer that should be read at once from the stream
     * @return Stream representation of the file
     */
    public static Stream<byte[]> stream(String fileName, int bufferSize) {
        return stream(new File(fileName), bufferSize);
    }

    /**
     * Create a FileStream reader
     * @param file The file to read
     * @param bufferSize the size of each read
     * @return the stream
     */
    public static Stream<byte[]> stream(File file, int bufferSize) {
        try {
            return stream(new FileInputStream(file), bufferSize);
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Create a file stream reader
     * @param stream the stream to read from (note this process will close the stream)
     * @param bufferSize size of each read
     * @return the stream
     */
    public static Stream<byte[]> stream(InputStream stream, int bufferSize) {
        try {
            return new FileStream(stream, stream.available(), bufferSize).stream(false);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Calculate the number of segments that will be created
     * @param sourceSize the size of the file
     * @param bufferSize the buffer size (or chunk size for each segment to be)
     * @return the number of packets that will be created
     */
    public static long caculateEstimatedSize(long sourceSize, Integer bufferSize) {
        return (long) Math.ceil((float)sourceSize / (float)bufferSize);
    }
}