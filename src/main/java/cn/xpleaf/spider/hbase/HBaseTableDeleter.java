package cn.xpleaf.spider.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import java.io.IOException;

public class HBaseTableDeleter {
    public static void main(String[] args) {
        Configuration config = HBaseConfiguration.create();
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {
            // 获取所有表名
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                // 禁用表
                if (admin.isTableEnabled(tableName)) {
                    admin.disableTable(tableName);
                }
                // 删除表
                admin.deleteTable(tableName);
                System.out.println("Deleted table: " + tableName.getNameAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}