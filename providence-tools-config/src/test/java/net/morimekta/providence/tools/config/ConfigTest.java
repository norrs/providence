package net.morimekta.providence.tools.config;

import net.morimekta.console.util.TerminalSize;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import static net.morimekta.providence.testing.util.ResourceUtils.getResourceAsStream;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test the providence converter (pvd) command.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TerminalSize.class)
public class ConfigTest {
    private static InputStream defaultIn;
    private static PrintStream defaultOut;
    private static PrintStream defaultErr;

    public TemporaryFolder temp;

    private OutputStream outContent;
    private OutputStream errContent;

    private int    exitCode;
    private Config config;
    private File   thriftFile;
    private String version;

    @BeforeClass
    public static void setUpIO() {
        defaultIn = System.in;
        defaultOut = System.out;
        defaultErr = System.err;
    }

    @Before
    public void setUp() throws IOException {
        mockStatic(TerminalSize.class);
        when(TerminalSize.get()).thenReturn(new TerminalSize(40, 100));
        when(TerminalSize.isInteractive()).thenReturn(true);

        Properties properties = new Properties();
        properties.load(getResourceAsStream("/build.properties"));
        version = properties.getProperty("build.version");

        temp = new TemporaryFolder();
        temp.create();
        thriftFile = temp.newFile("config.thrift");

        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/config.thrift"), file);
        file.flush();
        file.close();

        exitCode = 0;

        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        config = new Config() {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @After
    public void tearDown() {
        System.setErr(defaultErr);
        System.setOut(defaultOut);
        System.setIn(defaultIn);

        temp.delete();
    }

    @Test
    public void testHelp() {
        config.run("--help");

        assertEquals("", errContent.toString());
        assertEquals(
                "Providence Config Tool - v" + version + "\n" +
                "Usage: pvdcfg [-hVv] [-I dir] [-C dir] [-Pkey=value ...] [help | print | validate | params] [...]\n" + "\n" +
                " --help (-h, -?)    : This help listing.\n" +
                " --verbose (-V)     : Show verbose output and error messages.\n" +
                " --version (-v)     : Show program version.\n" +
                " --include (-I) dir : Read config definitions from these directories.\n" +
                " --config (-C) dir  : Config directory locations.\n" +
                " -Pkey=value        : Config parameter override.\n" +
                " cmd                : Config action.\n" +
                "\n" +
                "Available Commands:\n" +
                "\n" +
                " help     : Show help for sub-commands.\n" +
                " print    : Print the resulting config.\n" +
                " validate : Validate the file, print an error if not valid.\n" +
                " params   : Show params that can be applied on the config.\n",
                outContent.toString());
        assertEquals(0, exitCode);
    }

    @Test
    public void testHelpCmd() {
        config.run("help");

        assertEquals("", errContent.toString());
        assertEquals(
                "Providence Config Tool - v" + version + "\n" +
                "Usage: pvdcfg [-hVv] [-I dir] [-C dir] [-Pkey=value ...] [help | print | validate | params] [...]\n" + "\n" +
                " --help (-h, -?)    : This help listing.\n" +
                " --verbose (-V)     : Show verbose output and error messages.\n" +
                " --version (-v)     : Show program version.\n" +
                " --include (-I) dir : Read config definitions from these directories.\n" +
                " --config (-C) dir  : Config directory locations.\n" +
                " -Pkey=value        : Config parameter override.\n" +
                " cmd                : Config action.\n" +
                "\n" +
                "Available Commands:\n" +
                "\n" +
                " help     : Show help for sub-commands.\n" +
                " print    : Print the resulting config.\n" +
                " validate : Validate the file, print an error if not valid.\n" +
                " params   : Show params that can be applied on the config.\n",
                outContent.toString());
        assertEquals(0, exitCode);
    }


    @Test
    public void testHelpPrint() {
        config.run("help", "print");

        assertEquals("", errContent.toString());
        assertEquals(
                "Providence Config Tool - v" + version + "\n" +
                "Usage: pvdcfg [...] print ...\n" +
                "\n" +
                " --format (-f) fmt : the output format (default: pretty)\n" +
                " file              : Config file to parse and print\n",
                outContent.toString());
        assertEquals(0, exitCode);
    }
}
