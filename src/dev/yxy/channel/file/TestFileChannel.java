package dev.yxy.channel.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

/**
 * Created by atom on 2021/2/28
 */
public class TestFileChannel {
    private static final String path = TestFileChannel.class.getResource("/").getPath() + "code.txt";

    public static void main(String[] args) throws IOException {
        create(path);
        long size = writeStringToFile(path, "Hello " + System.currentTimeMillis(), StandardCharsets.UTF_8);
        System.out.println("[写入文件] 结果：" + size);
        String content = readFileToString(path, StandardCharsets.UTF_8);
        System.out.println("[读取文件] 结果：" + content);
    }

    public static void create(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            boolean flag = file.createNewFile();
            System.out.println("[创建文件] 结果：" + flag);
        }
    }

    public static long writeStringToFile(String filePath, String content, Charset charset) throws IOException {
        long writeSize = 0;
        try (FileOutputStream out = new FileOutputStream(filePath);
             FileChannel channel = out.getChannel()
        ) {
            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes(charset));
            while (buffer.hasRemaining()) {
                writeSize += channel.write(buffer);
            }
            channel.force(false);
        }
        return writeSize;
    }

    public static String readFileToString(String filePath, Charset charset) throws IOException {
        try (FileInputStream in = new FileInputStream(filePath);
             FileChannel channel = in.getChannel()
        ) {
            long fileSize = channel.size();
            int bufferSize = 1024;
            if (fileSize < 1024) {
                bufferSize = (int) fileSize;
            }
            StringBuilder builder = new StringBuilder((int) (fileSize / 2));

            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
            CharBuffer charBuffer = CharBuffer.allocate(bufferSize / 2);
            CharsetDecoder decoder = charset.newDecoder();
            while (channel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                CoderResult rel;
                do {
                    rel = decoder.decode(byteBuffer, charBuffer, false);
                    charBuffer.flip();

                    builder.append(charBuffer.array(), 0, charBuffer.limit());
                    charBuffer.clear();
                } while (rel.isOverflow());
                byteBuffer.compact();
            }

            byteBuffer.flip();
            decoder.decode(byteBuffer, charBuffer, true);
            charBuffer.flip();
            builder.append(charBuffer.array(), 0, charBuffer.limit());
            charBuffer.clear();

            return builder.toString();
        }
    }
}
