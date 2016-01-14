package net.morimekta.providence.gentests;

import net.morimekta.providence.Binary;
import net.morimekta.test.requirement.ExceptionFields;
import net.morimekta.test.requirement.Value;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 15.01.16.
 */
public class ExceptionTest {
    @Test
    public void testExceptionFields() {
        ExceptionFields ex = ExceptionFields.builder()
                                            .setBooleanValue(true)
                                            .setBinaryValue(Binary.wrap(new byte[]{0, 1, 2, 3, 4, 5}))
                                            .setByteValue((byte) 6)
                                            .setDoubleValue(7.8d)
                                            .setIntegerValue(9)
                                            .setLongValue(10L)
                                            .setStringValue("11")
                                            .setShortValue((short) 12)
                                            .setEnumValue(Value.FIFTEENTH)
                                            .build();

        assertEquals("{" +
                     "booleanValue:true," +
                     "byteValue:6," +
                     "shortValue:12," +
                     "integerValue:9," +
                     "longValue:10," +
                     "doubleValue:7.8," +
                     "stringValue:11," +
                     "binaryValue:b64(AAECAwQF)," +
                     "enumValue:FIFTEENTH" +
                     "}", ex.getMessage());
    }
}
