import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Description: TestFile
 * Author: DIYILIU
 * Update: 2018-02-06 09:30
 */
public class TestFile {

    @Test
    public void test() throws Exception {
        String filePath = "plc-functionSet.xml";
        Resource resource = new ClassPathResource(filePath);

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(resource.getFile());

        System.out.println(document.asXML());
    }
}
