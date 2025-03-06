package cn.xpleaf.spider.hbase;

import cn.xpleaf.spider.constants.SpiderConstants;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.xpleaf.spider.utils.SpiderUtil.getTopDomain;

/**
 * 该类实现了 SourceFunction<String> 接口，作为 Apache Flink 流处理中的一个数据源，从 HBase 中读取数据。
 */
public class MyHBaseSource implements SourceFunction<String> {
    private boolean isRunning = true; // 控制数据读取的循环
    private Connection connection = null; // HBase 连接对象
    private Table table = null; // HBase 表对象
    private final long SLEEP_MILLION = 2000; // 每次读取数据后的延迟时间

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        Configuration config = HBaseConfiguration.create();
        try {

            // 创建 HBase 连接
            connection = ConnectionFactory.createConnection(config);
            // 获取表对象
            table = connection.getTable(TableName.valueOf(SpiderConstants.TABLE_NAME));

            // **检查 HBase 是否为空，若为空则插入初始 URL**
            if (isHBaseEmpty(table)) {
                insertSeedUrls(table);
            }

            while (isRunning) {
                String url = null;
                while (url == null) {
                    // **从 HBase 获取数据**
                    Scan scan = new Scan();
                    scan.addColumn(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_PRIORITY), Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_PRIORITY_VALUE));
                    scan.addColumn(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_URL_SEED), Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_URL));
                    ResultScanner scanner = table.getScanner(scan);

                    try {
                        for (Result result : scanner) {
                            byte[] priorityValue = result.getValue(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_PRIORITY), Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_PRIORITY_VALUE));
                            if (priorityValue != null) {
                                // 将 byte[] 转换为字符串，假设使用 UTF-8 编码
                                String priorityValueStr = new String(priorityValue, java.nio.charset.StandardCharsets.UTF_8);
                                System.out.println("转换后的字符串: " + priorityValueStr);
                            } else {
                                System.out.println("获取到的字节数组为 null，无法转换为字符串。");
                            }


                            if (priorityValue != null && !Bytes.toBoolean(priorityValue)) {
                                byte[] urlValue = result.getValue(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_URL_SEED), Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_URL));
                                if (urlValue != null) {
                                    url = Bytes.toString(urlValue);
                                    sourceContext.collect(url);

                                    // **旧的 Row Key**
                                    byte[] oldRowKey = result.getRow();
                                    String oldKeyStr = Bytes.toString(oldRowKey);

                                    // **创建新的 Row Key（可以根据业务逻辑生成新的 Key）**
                                    String newKeyStr = "new_key_" + oldKeyStr;
                                    byte[] newRowKey = Bytes.toBytes(newKeyStr);

                                    // **插入新的数据**
                                    Put put = new Put(newRowKey);
                                    put.addColumn(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_PRIORITY),
                                            Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_PRIORITY_VALUE),
                                            Bytes.toBytes(true));
                                    put.addColumn(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_URL_SEED),
                                            Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_URL),
                                            urlValue);
                                    table.put(put);

                                    // **删除旧的 Row Key**
                                    Delete delete = new Delete(oldRowKey);
                                    table.delete(delete);

                                    System.out.println("Updated Row Key from " + oldKeyStr + " to " + newKeyStr);

                                    break; // 找到符合条件的数据后退出循环
                                }
                            }
                        }
                    } finally {
//                        scanner.close();
//                        connection.close();
//                        connection.close();
                    }
                    // **模拟延迟**
//                    Thread.sleep(SLEEP_MILLION);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (table != null) {
                table.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * **检查 HBase 是否为空**
     */
    private boolean isHBaseEmpty(Table table) throws IOException {
        Scan scan = new Scan();
        scan.setCaching(1); // 只取 1 条数据检查是否为空
        try (ResultScanner scanner = table.getScanner(scan)) {
            return !scanner.iterator().hasNext();
        }
    }

    /**
     * **插入初始种子 URL**
     */
    private void insertSeedUrls(Table table) throws IOException {
        String[] seedUrls = {
                "https://www.hippopx.com/en/query?q=fruit",
        };

        List<Put> puts = new ArrayList<>();
        for (String url : seedUrls) {
            Put put = new Put(Bytes.toBytes(url.hashCode()));
            put.addColumn(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_URL_SEED),
                    Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_URL),
                    Bytes.toBytes(url));
            put.addColumn(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_DOMAIN),
                    Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_DOMAIN_NAME),
                    Bytes.toBytes(getTopDomain(url)));
            put.addColumn(Bytes.toBytes(SpiderConstants.COLUMN_FAMILY_PRIORITY),
                    Bytes.toBytes(SpiderConstants.COLUMN_QUALIFIER_PRIORITY_VALUE),
                    Bytes.toBytes(false));
            puts.add(put);
        }

        table.put(puts);
        System.out.println("✅ 已插入初始种子 URL 到 HBase");
    }

    @Override
    public void cancel() {
        isRunning = false;
        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
