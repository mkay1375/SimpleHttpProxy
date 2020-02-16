package ir.mkay;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException, URISyntaxException {
        /* TODO Tests:
             - timeout
             - unknown host
             - simple get/post/put...
         */

        Files.readAllLines(Paths.get(this.getClass().getClassLoader().getResource("config.properties").toURI())).forEach(System.out::println);
        assertTrue( true );
    }
}
