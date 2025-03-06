package cn.xpleaf.spider.client;

import cn.xpleaf.spider.core.pojo.Page;

import java.sql.PreparedStatement;
//import java.sql.*;
//import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author hong
 * @date 2022年08月17日 22:47
 */
public class MysqlClient {
    // 定义了 MySQL 数据库的连接 URL、用户名和密码
    private static String URL = "jdbc:mysql://localhost:3306/ispider?useSSL=false&serverTimezone=Hongkong&characterEncoding=utf-8&autoReconnect=true";
    private static String NAME = "root";
    private static String PASS = "zzxx1215";
    private static PreparedStatement ps;
//    static {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
////            Connection conn = DriverManager.getConnection(URL, NAME, PASS);
//            ////stmt = conn.createStatement();
//            //ps = conn.prepareStatement();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, NAME, PASS);
            ////stmt = conn.createStatement();
            //ps = conn.prepareStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 向 image 表中插入图片相关信息
     * @param page 包含图片信息的 Page 对象
//     * @param imageData 图片的二进制数据
//     * @param imgUrl 图片的 URL
//     * @param image_size 图片的分类
//     * @param image_memory 图片的分类
//     * @param tags 图片的标签
     * @return 插入操作影响的行数
     */

    public static int insert(Page page) {
        int row = 0;
        String sql = "INSERT INTO image (id, img_url, image_data, image_size, image_memory, tags) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, page.getId());
            ps.setString(2, page.getImgUrl());
            ps.setBytes(3, page.getImageData());
            ps.setString(4, page.getImageSize());
            ps.setString(5, page.getImageMemory());

            String tagsString = String.join(",", page.getTags());
            ps.setString(6, tagsString);

            row = ps.executeUpdate();

            System.out.println("Inserted rows: " + row); // 添加日志

            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return row;
    }
//    public static int insert(Page page) {
//        int row = 0;
//        // 插入数据的 SQL 语句
//        String sql = "insert into image(id, img_url, image_data, image_size,image_memory, tags) values(?, ?, ?, ?, ?,?)";
//        // 获取数据库连接
//        Connection connection = getConnection();
//        Logger logger = LoggerFactory.getLogger(MysqlClient.class);
//        try {
//            // 创建 PreparedStatement 对象
//            PreparedStatement ps = connection.prepareStatement(sql);
//            // 设置 id 参数
//            String id = page.getId();
//            logger.info("Setting id parameter: {}", id);
////            ps.setString(1, id);
//            try {
//                ps.setString(1, page.getId());
//            } catch (SQLException e) {
//                logger.error("设置 id 参数时发生 SQL 异常", e);
//            }
//            // 设置 img_url 参数
//            String imgUrl = page.getImgUrl();
//            logger.info("Setting img_url parameter: {}", imgUrl);
//            ps.setString(2, imgUrl);
//
//            // 设置 image_data 参数
//            byte[] imageData = page.getImageData();
//            logger.info("Setting image_data parameter length: {}", imageData != null ? imageData.length : 0);
//            ps.setBytes(3, imageData);
//
//            // 设置 category 参数
//            String imageSize = page.getImageSize();
//            logger.info("Setting image_size parameter: {}", imageSize);
//            ps.setString(4, imageSize);
//
//            String imageMemory = page.getImageMemory();
//            logger.info("Setting image_memory parameter: {}", imageMemory);
//            ps.setString(5, imageMemory);
//
//            // 设置 tags 参数
//            String tagsString = String.join(",", page.getTags());
//            logger.info("Setting tags parameter: {}", tagsString);
//            ps.setString(6, tagsString);
//
//            // 执行插入操作，并返回影响的行数
//            row = ps.executeUpdate();
//            logger.info("Insert operation affected {} rows", row);
//
//        } catch (SQLException throwables) {
//            logger.error("SQLException occurred during insert operation", throwables);
//        }
//        return row;
//    }
//    public static int insert(Page page) {
//        int row = 0;
//        // 插入数据的 SQL 语句
//        String sql = "insert into image(id, img_url, image_data, image_size,image_memory, tags) values(?, ?, ?, ?, ?,?)";
//        // 获取数据库连接
//        Connection connection = getConnection();
//        try {
//            // 创建 PreparedStatement 对象
//            PreparedStatement ps = connection.prepareStatement(sql);
//            // 设置 id 参数
//
//            ps.setString(1, page.getId());
//            // 设置 img_url 参数
//            ps.setString(2, page.getImgUrl());
//            // 设置 image_data 参数
//            ps.setBytes(3, page.getImageData());
//            // 设置 category 参数
//            ps.setString(4, page.getImageSize());
//            ps.setString(5, page.getImageMemory());
//
//            // 设置 tags 参数
//            String tagsString = String.join(",", page.getTags());
//            ps.setString(6, tagsString);
//
//
//            // 执行插入操作，并返回影响的行数
//            row = ps.executeUpdate();
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } finally {
//            // 关闭连接，确保资源释放
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return row;
//    }
}

//    public static int insert(Page page){
//        int row = 0;
//        String sql = "insert into phone(id, source, brand, title, price, comment_count, good_rate, url, img_url, main, battery, interface, network, operating_system, basic_info, camera, screen) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        Connection connection = getConnection();
//        try {
//            ps = connection.prepareStatement(sql);
//            ps.setString(1,page.getId());
//            ps.setString(2,page.getSource());
//            ps.setString(3,page.getBrand());
//            ps.setString(4,page.getTitle());
//            ps.setFloat(5,page.getPrice());
//            ps.setString(6,page.getCommentCount());
//            ps.setFloat(7,page.getGoodRate());
//            ps.setString(8,page.getUrl());
//            ps.setString(9,page.getImgUrl());
////            ps.setString(9,page.getParams());
//            Map paramDetails =  page.getParamDetails();
//            ps.setString(10, (String) paramDetails.get("主体"));
//            ps.setString(11, (String) paramDetails.get("电池信息"));
//            ps.setString(12, (String) paramDetails.get("数据接口"));
//            ps.setString(13, (String) paramDetails.get("网络支持"));
//            ps.setString(14, (String) paramDetails.get("操作系统"));
//            ps.setString(15, (String) paramDetails.get("基本信息"));
//            ps.setString(16, (String) paramDetails.get("摄像头"));
//            ps.setString(17, (String) paramDetails.get("屏幕"));
//
//            row = ps.executeUpdate();
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return row;
//
//    }