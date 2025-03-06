package cn.xpleaf.spider.utils;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
// 定义下载器类，用于下载图片
public class ImageDownloader {
    private static final Logger LOGGER = Logger.getLogger(ImageDownloader.class.getName());
    private static final int BUFFER_SIZE = 4096;

    public static byte[] downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;

        try {
            // 打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为 GET
            connection.setRequestMethod("GET");

            // 获取输入流
            inputStream = connection.getInputStream();
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            // 循环读取输入流中的数据，并写入到输出流中
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // 将输出流中的数据转换为字节数组并返回
            return outputStream.toByteArray();
        } catch (IOException e) {
            // 记录异常日志
            LOGGER.log(Level.SEVERE, "下载图片时发生错误: " + imageUrl, e);
            throw e;
        } finally {
            // 关闭输出流
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "关闭 ByteArrayOutputStream 时发生错误", e);
                }
            }
            // 关闭输入流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "关闭 InputStream 时发生错误", e);
                }
            }
            // 断开连接
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
//        } finally {
//            // 确保资源正确关闭
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//    }
//}