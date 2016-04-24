package net.morimekta.providence.serializer;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PStructDescriptor;

import java.io.IOException;

/**
 * An interface for reading messages and service calls.
 */
public interface MessageReader {
    <T extends PMessage<T>, TF extends PField> T read(PStructDescriptor<T, TF> descriptor)
            throws IOException, SerializerException;

    <T extends PMessage<T>> PServiceCall<T> read(PService service)
            throws IOException, SerializerException;
}
