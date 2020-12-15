package org.helium.fastdfs.test;

import com.feinno.superpojo.util.FileUtil;
import org.helium.fastdfs.FastDFSClient;
import org.helium.fastdfs.spi.FastDFSManager;
import org.junit.Test;

/**
 * Created by wuzhiguo on 15-10-13.
 */
public class FastDFSTest {

    @Test
    public void testFastDFSClient() throws Exception {
        String path = "src/test/resources/fastdfs/TEST_DFS.properties";
        String str = FileUtil.read(path);
        FastDFSClient fastDFSClient = FastDFSManager.INSTANCE.getFastDFSClient("TEST_DFS", str);
        System.out.println("fastDFSClient delete: " + fastDFSClient.deleteFile("123"));
    }

}
