/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumBuilderFactory;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptorProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CEnumValue;
import net.morimekta.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class JEnumFormat {
    private final JOptions options;

    JEnumFormat(JOptions options) {
        this.options = options;
    }

    public void format(IndentedPrintWriter writer, CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        if (type.getComment() != null) {
            new BlockCommentBuilder(writer)
                    .comment(type.getComment())
                    .finish();
        }
        if (JAnnotation.isDeprecated((CAnnotatedDescriptor) type)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }

        writer.formatln("public enum %s implements %s<%s> {",
                        simpleClass,
                        PEnumValue.class.getName(),
                        simpleClass)
              .begin();

        for (CEnumValue v : type.getValues()) {
            if (v.getComment() != null) {
                new BlockCommentBuilder(writer)
                        .comment(v.getComment())
                        .finish();
            }
            if (JAnnotation.isDeprecated(v)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
            writer.formatln("%s(%d, \"%s\"),",
                            JUtils.enumConst(v),
                            v.getValue(),
                            v.getName());
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mValue;")
              .appendln("private final String mName;")
              .newline()
              .formatln("%s(int value, String name) {", simpleClass)
              .begin()
              .appendln("mValue = value;")
              .appendln("mName = name;")
              .end()
              .appendln("}")
              .newline();

        writer.appendln("@Override")
              .appendln("public int getValue() {")
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public String getName() {")
              .begin()
              .appendln("return mName;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public int asInteger() {")
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public String asString() {")
              .begin()
              .appendln("return mName;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s forValue(int value) {", simpleClass)
              .begin()
              .appendln("switch (value) {")
              .begin();
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case %d: return %s.%s;",
                            value.getValue(),
                            simpleClass,
                            JUtils.enumConst(value));
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s forName(String name) {", simpleClass)
              .begin()
              .appendln("switch (name) {")
              .begin();
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case \"%s\": return %s.%s;",
                            value.getName(),
                            simpleClass,
                            JUtils.enumConst(value));
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        appendBuilder(writer, type);

        appendDescriptor(writer, type);

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendDescriptor(IndentedPrintWriter writer, PEnumDescriptor<?> type) {
        String simpleClass = JUtils.getClassName(type);

        writer.formatln("public static final %s<%s> kDescriptor;",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .newline();

        writer.appendln("@Override")
              .formatln("public %s<%s> descriptor() {",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s<%s> provider() {",
                        PEnumDescriptorProvider.class.getName(),
                        simpleClass)
              .begin()
              .formatln("return new %s<%s>(kDescriptor);",
                        PEnumDescriptorProvider.class.getName(),
                        simpleClass)
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Factory")
              .begin()
              .formatln("    extends %s<%s> {",
                        PEnumBuilderFactory.class.getName(),
                        simpleClass)
              .appendln("@Override")
              .formatln("public %s._Builder builder() {", simpleClass)
              .begin()
              .formatln("return new %s._Builder();", simpleClass)
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Descriptor")
              .formatln("        extends %s<%s> {",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .begin()
              .appendln("public _Descriptor() {")
              .begin()
              .formatln("super(\"%s\", \"%s\", new _Factory());",
                        type.getPackageName(),
                        type.getName(),
                        simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s[] getValues() {", simpleClass)
              .begin()
              .formatln("return %s.values();", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s getValueById(int id) {", simpleClass)
              .begin()
              .formatln("return %s.forValue(id);", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s getValueByName(String name) {", simpleClass)
              .begin()
              .formatln("return %s.forName(name);", simpleClass)
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("static {", simpleClass)
              .begin();
        writer.appendln("kDescriptor = new _Descriptor();")
              .end()
              .appendln('}');
    }

    private void appendBuilder(IndentedPrintWriter writer, PEnumDescriptor<?> type) {
        String simpleClass = JUtils.getClassName(type);
        writer.formatln("public static class _Builder extends %s<%s> {",
                        PEnumBuilder.class.getName(),
                        simpleClass)
              .begin()
              .formatln("%s mValue;", simpleClass)
              .newline();

        writer.appendln("@Override")
              .appendln("public _Builder setByValue(int value) {")
              .begin()
              .formatln("mValue = %s.forValue(value);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public _Builder setByName(String name) {")
              .begin()
              .formatln("mValue = %s.forName(name);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public boolean isValid() {")
              .begin()
              .appendln("return mValue != null;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public %s build() {", simpleClass)
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();
    }
}
