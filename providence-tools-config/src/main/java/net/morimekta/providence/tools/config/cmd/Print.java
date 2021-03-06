package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.tools.common.options.Format;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Print the resulting config.
 */
public class Print implements Command {
    private Serializer serializer = new PrettySerializer("  ", " ", "\n", "", true, false);
    private File       file       = null;

    @Override
    public void execute(ProvidenceConfig config) throws SerializerException {
        try {
            serializer.serialize(System.out, (PMessage) config.load(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " print", parent.getVersion(), "");
        parser.add(new Option("--format", "f", "fmt", "the output format", this::setSerializer, "pretty"));
        parser.add(new Argument("file", "Config file to parse and print", Parser.file(this::setFile), null, null, false, true, false));
        return parser;
    }

    private void setFile(File file) {
        this.file = file;
    }

    private void setSerializer(String name) {
        Format format = Format.forName(name);
        serializer = format.createSerializer(false);
    }
}
