package loadbalancer.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A client's HTTP request read fully off the wire: headers are parsed just
 * enough to find the request line and Content-Length, while the exact bytes
 * received are retained so they can be replayed verbatim to a backend.
 */
public final class RawHttpRequest {

    private final String requestLine;
    private final List<String> headerLines;
    private final byte[] rawBytes;

    private RawHttpRequest(String requestLine, List<String> headerLines, byte[] rawBytes) {
        this.requestLine = requestLine;
        this.headerLines = headerLines;
        this.rawBytes = rawBytes;
    }

    /** Returns empty if the client closed the connection before sending a full request. */
    public static Optional<RawHttpRequest> readFrom(InputStream in) throws IOException {
        byte[] headerBytes = readUntilBlankLine(in);
        if (headerBytes == null) {
            return Optional.empty();
        }

        String headerText = new String(headerBytes, StandardCharsets.ISO_8859_1);
        String[] lines = headerText.split("\r\n");
        String requestLine = lines.length > 0 ? lines[0] : "";

        List<String> headerLines = new ArrayList<>();
        int contentLength = 0;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                continue;
            }
            headerLines.add(line);
            int colon = line.indexOf(':');
            if (colon > 0 && line.substring(0, colon).equalsIgnoreCase("Content-Length")) {
                contentLength = parseContentLength(line.substring(colon + 1).trim());
            }
        }

        byte[] body = contentLength > 0 ? in.readNBytes(contentLength) : new byte[0];

        byte[] raw = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, raw, 0, headerBytes.length);
        System.arraycopy(body, 0, raw, headerBytes.length, body.length);

        return Optional.of(new RawHttpRequest(requestLine, headerLines, raw));
    }

    private static int parseContentLength(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Reads bytes up to and including the terminating "\r\n\r\n", or null on EOF before any bytes. */
    private static byte[] readUntilBlankLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int[] last4 = {-1, -1, -1, -1};
        int b;
        while ((b = in.read()) != -1) {
            buffer.write(b);
            last4[0] = last4[1];
            last4[1] = last4[2];
            last4[2] = last4[3];
            last4[3] = b;
            if (last4[0] == '\r' && last4[1] == '\n' && last4[2] == '\r' && last4[3] == '\n') {
                return buffer.toByteArray();
            }
        }
        return buffer.size() == 0 ? null : buffer.toByteArray();
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public String toLogString() {
        StringBuilder sb = new StringBuilder(requestLine);
        for (String header : headerLines) {
            sb.append('\n').append(header);
        }
        return sb.toString();
    }
}
