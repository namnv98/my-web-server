package com.nnv.core.context.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

public class HttpInputStream extends InputStream  {
    private Reader source;
    private int bytesRemaining;
    private boolean chunked = false;

    public HttpInputStream(Reader source, Map<String, String> headers) throws IOException  {
        this.source = source;

        String declaredContentLength = headers.get("Content-Length");
        if (declaredContentLength != null)  {
            try  {
                bytesRemaining = Integer.parseInt(declaredContentLength);
            } catch (NumberFormatException e)  {
                throw new IOException("Malformed or missing Content-Length header");
            }
        }  else if ("chunked".equals(headers.get("Transfer-Encoding")))  {
            chunked = true;
            bytesRemaining = parseChunkSize();
        }
    }

    private int parseChunkSize() throws IOException {
        int b;
        int chunkSize = 0;

        while ((b = source.read()) != '\r') {
            chunkSize = (chunkSize << 4) |
                    ((b > '9') ?
                            (b > 'F') ?
                                    (b - 'a' + 10) :
                                    (b - 'A' + 10) :
                            (b - '0'));
        }
        // Consume the trailing '\n'
        if (source.read() != '\n')  {
            throw new IOException("Malformed chunked encoding");
        }

        return chunkSize;
    }

    public int read() throws IOException  {
        if (bytesRemaining == 0)  {
            if (!chunked) {
                return -1;
            } else  {
                // Read next chunk size; return -1 if 0 indicating end of stream
                // Read and discard extraneous \r\n
                if (source.read() != '\r')  {
                    throw new IOException("Malformed chunked encoding");
                }
                if (source.read() != '\n')  {
                    throw new IOException("Malformed chunked encoding");
                }
                bytesRemaining = parseChunkSize();

                if (bytesRemaining == 0)  {
                    return -1;
                }
            }
        }

        bytesRemaining -= 1;
        return source.read();
    }
}