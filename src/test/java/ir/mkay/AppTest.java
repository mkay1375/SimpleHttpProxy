package ir.mkay;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        Files.readAllLines(Paths.get(this.getClass().getClassLoader().getResource("config.properties").toURI())).forEach(System.out::println);
        assertTrue( true );
    }
}
