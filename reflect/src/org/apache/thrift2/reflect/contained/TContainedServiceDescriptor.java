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

package org.apache.thrift2.reflect.contained;

import org.apache.thrift2.descriptor.TServiceMethod;
import org.apache.thrift2.descriptor.TServiceDescriptor;

import java.util.List;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 18.09.15
 */
public class TContainedServiceDescriptor
        extends TServiceDescriptor {
    public TContainedServiceDescriptor(String comment,
                                       String packageName,
                                       String name,
                                       List<TServiceMethod<?, ?, ?>> methods) {
        super(comment,
              packageName,
              name,
              methods);
    }
}