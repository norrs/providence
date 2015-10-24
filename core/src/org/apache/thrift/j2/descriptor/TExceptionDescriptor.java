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

package org.apache.thrift.j2.descriptor;

import java.util.List;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TMessageVariant;

/**
 * The definition of a thrift structure.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TExceptionDescriptor<T extends TMessage<T>>
        extends TStructDescriptor<T> {
    public TExceptionDescriptor(String comment,
                                String packageName,
                                String name,
                                TField<?>[] fields,
                                TMessageBuilderFactory<T> provider) {
        this(comment, packageName, name, fieldList(fields), provider);
    }


    public TExceptionDescriptor(String comment,
                                String packageName,
                                String name,
                                List<TField<?>> fields,
                                TMessageBuilderFactory<T> provider) {
        super(comment, packageName, name, fields, provider, false);
    }

    @Override
    public TMessageVariant getVariant() {
        return TMessageVariant.EXCEPTION;
    }
}
