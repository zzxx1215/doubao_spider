package cn.xpleaf.spider;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Query {
    private static String URL = "jdbc:mysql://localhost:3306/ispider?useSSL=false&serverTimezone=Hongkong&characterEncoding=utf-8&autoReconnect=true";
    private static String NAME = "root";
    private static String PASS = "zzxx1215";
    private static PreparedStatement ps;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, NAME, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            query();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, NAME, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void query() throws IOException {
        Scanner scan = new Scanner(System.in);
        Connection connection = getConnection();

        while (true) {
            // 定义 image 表列映射
            Map<String, String> column_map = new HashMap<>();
            column_map.put("图片唯一标识", "id");
            column_map.put("图片地址", "img_url");
            column_map.put("图片数据", "image_data");
            column_map.put("图片尺寸", "image_size");
            column_map.put("图片内存", "image_memory");
            column_map.put("图片标签", "tags");

            // 查询 image 表的总记录数
            String cnt_sql_all = "select count(*) from image;";
            Statement statement = null;
            String count = "";
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(cnt_sql_all);
                while (resultSet.next()) {
                    count = resultSet.getString(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            System.out.println("欢迎来到爬虫系统查询模块！当前 image 表内包含" + count + "条数据，其中每一条数据包含的可查询条目如下：");
            int cnt = 0;
            for (Map.Entry<String, String> entry : column_map.entrySet()) {
                String mapKey = entry.getKey();
                cnt++;
                System.out.print(cnt + " " + mapKey + "; ");
            }

            System.out.println("\n请输入查询条目：");
            String column = column_map.get(scan.nextLine());
            System.out.println("已接收查询条目：" + column);
            String sql = "SELECT * FROM image WHERE " + column + " LIKE ?;";
            System.out.println("请输入查询关键词：");
            String keyword = "%" + scan.nextLine() + "%";
            System.out.println("已接收查询关键词：" + keyword);

            // 查询符合条件的记录数
            String cnt_sql = "select count(*) from image where " + column + " LIKE '" + keyword + "';";
            String res_count = "";
            try {
                ResultSet resultSet = statement.executeQuery(cnt_sql);
                while (resultSet.next()) {
                    res_count = resultSet.getString(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            System.out.println("查询结束！已找到" + res_count + "条数据，请输入需要显示的条目（以空格分开，默认显示图片唯一标识和图片地址）：");
            String[] col_set = scan.nextLine().split(" ");

            try {
                ps = connection.prepareStatement(sql);
                ps.setString(1, keyword);
                ResultSet res = ps.executeQuery();
                while (res.next()) {
                    System.out.println("图片唯一标识: " + res.getString("id"));
                    System.out.println("图片地址: " + res.getString("img_url"));
                    for (String col : col_set) {
                        System.out.println(col + ": " + res.getString(column_map.get(col)));
                    }
                }
                System.out.println("输出结束！");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}