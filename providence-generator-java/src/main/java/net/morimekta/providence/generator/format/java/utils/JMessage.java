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

package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.descriptor.PExceptionDescriptor;
import net.morimekta.providence.descriptor.PExceptionDescriptorProvider;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptorProvider;
import net.morimekta.providence.descriptor.PUnionDescriptor;
import net.morimekta.providence.descriptor.PUnionDescriptorProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class JMessage<T extends CMessage<T, CField>> {
    private final PStructDescriptor<?, ?> struct;
    private final JHelper                 helper;
    private final ArrayList<JField>       fields;

    public JMessage(PStructDescriptor<T, CField> struct, JHelper helper) {
        this.struct = struct;
        this.helper = helper;
        this.fields = new ArrayList<>(struct.getFields().length);

        CField[] fields = struct.getFields();
        for (int i = 0; i < fields.length; ++i) {
            this.fields.add(new JField(fields[i], helper, i));
        }
        Collections.sort(this.fields, (a, b) -> Integer.compare(a.id(), b.id()));
    }

    public PStructDescriptor<?, ?> descriptor() {
        return struct;
    }

    public PMessageVariant variant() {
        return struct.getVariant();
    }

    public boolean isException() {
        return struct.getVariant() == PMessageVariant.EXCEPTION;
    }

    public boolean isUnion() {
        return struct.getVariant() == PMessageVariant.UNION;
    }

    /**
     * The short class name of the message.
     *
     * @return The class short name.
     */
    public String instanceType() {
        return JUtils.getClassName(struct);
    }

    public List<JField> fields() {
        return fields;
    }

    public String getDescriptorClass() throws GeneratorException {
        switch (variant()) {
            case STRUCT:
                return PStructDescriptor.class.getName();
            case UNION:
                return PUnionDescriptor.class.getName();
            case EXCEPTION:
                return PExceptionDescriptor.class.getName();
            default:
                throw new GeneratorException("Unable to determine type class for " + variant());
        }
    }

    public String getProviderClass() throws GeneratorException {
        switch (variant()) {
            case STRUCT:
                return PStructDescriptorProvider.class.getName();
            case UNION:
                return PUnionDescriptorProvider.class.getName();
            case EXCEPTION:
                return PExceptionDescriptorProvider.class.getName();
            default:
                throw new GeneratorException("Unable to determine type class for " + variant());
        }
    }
}
