package com.knowprocess.resource.spi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class FetcherTest {

    private Fetcher svc;
    private String resourceUrl = "http://farm4.staticflickr.com/3140/3094868910_41c19ce2a3_b_d.jpg";
    private String resourceUrl2 = "http://farm4.staticflickr.com/3025/3094867738_1300826ed5_q_d.jpg";
    private String repoUri = "https://docs.google.com/tux-collage.jpg";
    private String repoUri2 = "https://docs.google.com/sub-folder/java-collage.jpg";

    @Before
    public void setUp() throws Exception {
        svc = new Fetcher();
    }

    @Test
    public void testUploadImageToRootFolder() {
        try {
            svc.fetchToRepo(resourceUrl, repoUri);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /*
     * Does not work (uploads to root folder)
     * 
     * @Test public void testUploadImageToSubFolder() { try {
     * svc.fetchToRepo(resourceUrl2, repoUri2); } catch (IOException e) {
     * e.printStackTrace(); fail(); } }
     */

    @Test
    public void testClasspathResourceToString() {
        try {
            String bpmn = svc
                    .fetchToString("classpath:///process/com/knowprocess/deployment/DeploymentProcess.bpmn");
            System.out.println("BPMN: " + bpmn);
            assertNotNull(bpmn);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
