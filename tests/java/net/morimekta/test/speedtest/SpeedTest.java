package net.morimekta.test.speedtest;

import net.morimekta.test.j2.Containers;

import org.apache.thrift.TException;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.protocol.TBinaryProtocolSerializer;
import org.apache.thrift.j2.protocol.TCompactProtocolSerializer;
import org.apache.thrift.j2.protocol.TJsonProtocolSerializer;
import org.apache.thrift.j2.protocol.TTupleProtocolSerializer;
import org.apache.thrift.j2.serializer.TBinarySerializer;
import org.apache.thrift.j2.serializer.TJsonSerializer;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;
import org.apache.thrift.j2.util.io.CountingOutputStream;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.utils.FormatString;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by steineldar on 22.12.15.
 */
public class SpeedTest {
    public static class Options {
        @Option(name = "--entries",
                usage = "Expected number of entries to in input files")
        public int entries = 10000;

        @Argument(usage = "Base input directory of data",
                  metaVar = "DIR",
                  required = true)
        public String in;
    }

    public enum Format {          //   J2    thrift
        binary_protocol("bin"),   //  1.82    1.99
        tuple_protocol("tuples"), //  1.92    2.02
        compact_protocol("bin"),  //  2.13    2.43

        binary("bin"),            //  2.32

        json_protocol("json"),    // 10.64   10.92

        json("json"),             // 18.72
        json_named("json"),       // 21.88
        ;

        String suffix;

        Format(String s) {
            suffix = s;
        }
    }

    public static void main(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);

        try {
            parser.parseArgument(args);

            File in = new File(opts.in);
            if (!in.exists()) {
                throw new CmdLineException(parser, new FormatString("Input folder does not exist: %s"), opts.in);
            }
            if (!in.isDirectory()) {
                throw new CmdLineException(parser, new FormatString("Output is not a directory: %s"), opts.in);
            }

            File out = File.createTempFile("thrift-j2-", "-speed-test");
            if (out.exists()) {
                out.delete();
            }
            out.mkdirs();

            System.out.println("OUT: " + out.getAbsolutePath());

            System.out.println();
            System.out.println(" --- thrift ---");
            System.out.println();

            for (Format f : Format.values()) {
                File in_dir = new File(in, f.name());
                if (!in_dir.exists()) {
                    throw new CmdLineException(parser, new FormatString("Target is not a directory: %s"), in_dir.getAbsolutePath());
                }
                if (!in_dir.isDirectory()) {
                    throw new CmdLineException(parser, new FormatString("Target is not a directory: %s"), in_dir.getAbsolutePath());
                }

                File out_dir = new File(out, f.name());
                out_dir.mkdir();

                File in_file = new File(in_dir, String.format("data.%s", f.suffix));

                File out_file = new File(out_dir, String.format("data-v1.%s", f.suffix));
                out_file.createNewFile();

                FileInputStream fis = new FileInputStream(in_file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                FileOutputStream fos = new FileOutputStream(out_file, false);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                CountingOutputStream os = new CountingOutputStream(bos);

                TProtocolFactory in_prot;
                TProtocolFactory out_prot;

                switch (f) {
                    case binary_protocol:
                        in_prot = new TBinaryProtocol.Factory();
                        out_prot = new TBinaryProtocol.Factory();
                        break;
                    case compact_protocol:
                        in_prot = new TCompactProtocol.Factory();
                        out_prot = new TCompactProtocol.Factory();
                        break;
                    case json_protocol:
                        in_prot = new TJSONProtocol.Factory();
                        out_prot = new TJSONProtocol.Factory();
                        break;
                    case tuple_protocol:
                        in_prot = new TTupleProtocol.Factory();
                        out_prot = new TTupleProtocol.Factory();
                        break;
                    default:
                        System.out.format("%20s: [skipped]\n", f.name());
                        continue;
                }

                long in_size = in_file.length();

                long start = System.currentTimeMillis();

                net.morimekta.test.Containers containers;
                int num = 0;

                long rtime = 0;
                long wtime = 0;

                while (num < opts.entries) {
                    ++num;

                    long rstart = System.nanoTime();

                    TTransport tin = new TIOStreamTransport(bis);
                    containers = new net.morimekta.test.Containers();
                    containers.read(in_prot.getProtocol(tin));
                    bis.read();  // the separating newline.

                    long rend = System.nanoTime();

                    TTransport tout = new TIOStreamTransport(os);
                    containers.write(out_prot.getProtocol(tout));
                    tout.flush();
                    os.write('\n');

                    long wend = System.nanoTime();

                    rtime += (rend - rstart);
                    wtime += (wend - rend);
                }

                bis.close();
                os.flush();

                int out_size = os.getByteCount();

                os.close();

                long end = System.currentTimeMillis();

                wtime /= 1000000;
                rtime /= 1000000;

                System.out.format("%20s: %3d in %5.2fs  (r: %5.2fs, w: %5.2fs) # %,9d kB -> %,9d kB\n",
                                  f.name(), num,
                                  (double) (end - start) / 1000,
                                  (double) rtime / 1000,
                                  (double) wtime / 1000,
                                  in_size / 1024, out_size / 1024);
            }

            System.out.println();
            System.out.println(" --- thrift-j2 ---");
            System.out.println();

            for (Format fmt : Format.values()) {
                File in_dir = new File(in, fmt.name());
                if (!in_dir.exists()) {
                    throw new CmdLineException(parser, new FormatString("Target is not a directory: %s"), in_dir.getAbsolutePath());
                }
                if (!in_dir.isDirectory()) {
                    throw new CmdLineException(parser, new FormatString("Target is not a directory: %s"), in_dir.getAbsolutePath());
                }

                File out_dir = new File(out, fmt.name());
                out_dir.mkdir();

                File in_file = new File(in_dir, String.format("data.%s", fmt.suffix));

                File out_file = new File(out_dir, String.format("data-j2.%s", fmt.suffix));
                out_file.createNewFile();

                TSerializer serializer;

                FileInputStream fis = new FileInputStream(in_file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                FileOutputStream fos = new FileOutputStream(out_file, false);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                CountingOutputStream cos = new CountingOutputStream(bos);

                switch (fmt) {
                    case binary:
                        serializer = new TBinarySerializer();
                        break;
                    case binary_protocol:
                        serializer = new TBinaryProtocolSerializer();
                        break;
                    case compact_protocol:
                        serializer = new TCompactProtocolSerializer();
                        break;
                    case json:
                        serializer = new TJsonSerializer(TJsonSerializer.IdType.ID);
                        break;
                    case json_named:
                        serializer = new TJsonSerializer(TJsonSerializer.IdType.NAME);
                        break;
                    case json_protocol:
                        serializer = new TJsonProtocolSerializer();
                        break;
                    case tuple_protocol:
                        serializer = new TTupleProtocolSerializer();
                        break;
                    default:
                        System.out.format("%20s: [skipped]\n", fmt.name());
                        continue;
                }

                final long in_size = in_file.length();
                final long start = System.currentTimeMillis();

                int num;

                long rtime = 0;
                long wtime = 0;

                for (num = 0; num < opts.entries; ++num) {
                    try {
                        long rstart = System.nanoTime();
                        TMessage<?> message = serializer.deserialize(bis, Containers.kDescriptor);
                        bis.read();  // the separating newline.

                        long rend = System.nanoTime();

                        serializer.serialize(cos, message);
                        cos.write('\n');

                        long wend = System.nanoTime();

                        rtime += (rend - rstart);
                        wtime += (wend - rend);
                    } catch (TSerializeException e) {
                        throw new TSerializeException(e, "Error in message " + num);
                    }
                }

                cos.flush();

                final long out_size = cos.getByteCount();
                final long end = System.currentTimeMillis();

                cos.close();

                wtime /= 1000000;
                rtime /= 1000000;

                System.out.format("%20s: %3d in %5.2fs  (r: %5.2fs, w: %5.2fs) # %,9d kB -> %,9d kB\n",
                        fmt.name(), num,
                                  (double) (end - start) / 1000,
                                  (double) rtime / 1000,
                                  (double) wtime / 1000,
                                  in_size / 1024, out_size / 1024);
            }
        } catch (TException|TSerializeException|IOException | CmdLineException e) {
            System.out.flush();
            System.err.println();
            e.printStackTrace();
            System.err.println();
            parser.printSingleLineUsage(System.err);
            System.err.println();
            parser.printUsage(System.err);
            System.exit(-1);
        }
    }
}
