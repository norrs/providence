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

/**
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TDefaultValueProvider<V> implements TValueProvider<V> {
    private final V mValue;

    public TDefaultValueProvider(V value) {
        mValue = value;
    }

    public V get() {
        return mValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TValueProvider)) {
            return false;
        }
        TValueProvider<?> other = (TValueProvider<?>) o;
        return mValue.equals(other.get());
    }

    @Override
    public int hashCode() {
        return mValue.hashCode();
    }
}
