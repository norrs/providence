package net.morimekta.providence.thrift;

import org.apache.thrift.protocol.TCompactProtocol;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TCompactProtocolSerializer extends TProtocolSerializer {
    public static final String MIME_TYPE = "application/vnd.apache.thrift.compact";

    public TCompactProtocolSerializer() {
        this(true);
    }

    public TCompactProtocolSerializer(boolean readStrict) {
        super(readStrict, new TCompactProtocol.Factory(),
              true, MIME_TYPE);
    }
}
