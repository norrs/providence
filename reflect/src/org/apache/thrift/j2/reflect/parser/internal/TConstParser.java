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

package org.apache.thrift.j2.reflect.parser.internal;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TSet;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.reflect.parser.TParseException;
import org.apache.thrift.j2.util.TBase64Utils;
import org.apache.thrift.j2.util.TStringUtils;
import org.apache.thrift.j2.util.json.JsonException;
import org.apache.thrift.j2.util.json.JsonToken;
import org.apache.thrift.j2.util.json.JsonTokenizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Parsing thrift constants from string to actual value. This uses the JSON
 * format with some allowed special references. So enum values are always
 * expected to be 'EnumName.VALUE' (quotes or no quotes), and map keys are not
 * expected to be string literals.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TConstParser {
    public TConstParser() {}

    public Object parse(InputStream inputStream, TDescriptor type) throws TParseException {
        try {
            JsonTokenizer tokenizer = new JsonTokenizer(inputStream);
            return parseTypedValue(tokenizer.expect(""), tokenizer, type);
        } catch (JsonException e) {
            throw new TParseException("Unable to parse JSON", e);
        } catch (IOException e) {
            throw new TParseException("Unable to read JSON from input", e);
        }
    }

    /**
     * Parse JSON object as a message.
     *
     * @param tokenizer The JSON tokenizer.
     * @param type The message type.
     * @param <T> Message generic type.
     * @return The parsed message.
     */
    private <T extends TMessage<T>> T parseMessage(JsonTokenizer tokenizer, TStructDescriptor<T> type) throws IOException, JsonException {
        TMessageBuilder<T> builder = type.factory().builder();

        JsonToken token = tokenizer.expect("parsing message field id");
        while (!JsonToken.CH.MAP_END.equals(token.getSymbol())) {
            TField<?> field;
            if (token.isLiteral()) {
                field = type.getField(token.literalValue());
            } else {
                field = type.getField(token.getToken());
            }
            if (field == null) {
                throw new JsonException("Not a valid field name: " + token.getToken());
            }
            tokenizer.expectSymbol(JsonToken.CH.MAP_KV_SEP, "");

            builder.set(field.getKey(), parseTypedValue(tokenizer.expect("parsing field value."), tokenizer, field.getDescriptor()));
            token = tokenizer.expect("parsing message fields list sep");
            if (JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                break;
            } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                throw new JsonException("Unexpected token " + token.getToken() + ", expected ',' list separator.");
            }
            token = tokenizer.expect("parsing message field id.");
        }

        return builder.build();
    }

    private Object parseTypedValue(JsonToken token, JsonTokenizer tokenizer, TDescriptor valueType) {
        try {
            switch (valueType.getType()) {
                case BOOL:
                    if (token.isBoolean()) {
                        return token.booleanValue();
                    } else if (token.isInteger()) {
                        return token.longValue() != 0;
                    }
                    throw new JsonException("Not boolean value for bool: " + token.getToken(),
                                            tokenizer,
                                            token);
                case BYTE:
                    if (token.isInteger()) {
                        return token.byteValue();
                    }
                    throw new JsonException(token.getToken() + " is not a valid byte value.",
                                            tokenizer,
                                            token);
                case I16:
                    if (token.isInteger()) {
                        return token.shortValue();
                    }
                    throw new JsonException(token.getToken() + " is not a valid short value.",
                                            tokenizer,
                                            token);
                case I32:
                    if (token.isInteger()) {
                        return token.intValue();
                    }
                    throw new JsonException(token.getToken() + " is not a valid int value.",
                                            tokenizer,
                                            token);
                case I64:
                    if (token.isInteger()) {
                        return token.longValue();
                    }
                    throw new JsonException(token.getToken() + " is not a valid long value.",
                                            tokenizer,
                                            token);
                case DOUBLE:
                    if (token.isInteger() || token.isReal()) {
                        return token.doubleValue();
                    }
                    throw new JsonException(token.getToken() + " is not a valid double value.",
                                            tokenizer,
                                            token);
                case STRING:
                    if (token.isLiteral()) {
                        return token.literalValue();
                    }
                    throw new JsonException("Not a valid string value.", tokenizer, token);
                case BINARY:
                    if (token.isLiteral()) {
                        return parseBinary(token.literalValue());
                    }
                    throw new JsonException("Not a valid binary value.", tokenizer, token);
                case ENUM:
                    TEnumBuilder<?> eb = ((TEnumDescriptor<?>) valueType).factory().builder();
                    String name = token.getToken();
                    if (name.startsWith(valueType.getName())) {
                        name = name.substring(valueType.getName().length() + 1);
                    }
                    return eb.setByName(name).build();
                case MESSAGE:
                    if (JsonToken.CH.MAP_START.equals(token.getSymbol())) {
                        return parseMessage(tokenizer, (TStructDescriptor<?>) valueType);
                    }
                    throw new JsonException("Not a valid message start.", tokenizer, token);
                case LIST:
                    TDescriptor itemType = ((TList<?>) valueType).itemDescriptor();
                    LinkedList<Object> list = new LinkedList<>();

                    if (!JsonToken.CH.LIST_START.equals(token.getSymbol())) {
                        throw new JsonException("Not a valid list start token.", tokenizer, token);
                    }
                    token = tokenizer.expect("parsing list item.");
                    while (!JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                        list.add(parseTypedValue(token, tokenizer, itemType));
                        token = tokenizer.expect("parsing list separator");
                        if (JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new JsonException("Unexpected symbol " + token + ". " +
                                                            "Expected list separator or end.",
                                                    tokenizer,
                                                    token);
                        }
                        token = tokenizer.expect("parsing list item.");
                    }
                    return list;
                case SET:
                    itemType = ((TSet<?>) valueType).itemDescriptor();
                    HashSet<Object> set = new HashSet<>();

                    if (!JsonToken.CH.LIST_START.equals(token.getSymbol())) {
                        throw new JsonException("Not a valid set list start token.",
                                                tokenizer,
                                                token);
                    }
                    token = tokenizer.expect("parsing set list item.");
                    while (!JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                        set.add(parseTypedValue(token, tokenizer, itemType));
                        token = tokenizer.expect("parsing set list separator");
                        if (JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new JsonException("Unexpected symbol " + token + ". " +
                                                            "Expected list separator or end.",
                                                    tokenizer,
                                                    token);
                        }
                        token = tokenizer.expect("parsing set list item.");
                    }
                    return set;
                case MAP:
                    itemType = ((TMap<?, ?>) valueType).itemDescriptor();

                    TDescriptor keyType = ((TMap<?, ?>) valueType).keyDescriptor();
                    HashMap<Object, Object> map = new HashMap<>();

                    if (!JsonToken.CH.MAP_START.equals(token.getSymbol())) {
                        throw new JsonException("Not a valid map start token.", tokenizer, token);
                    }
                    token = tokenizer.expect("parsing map key.");
                    while (!JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                        Object key;
                        if (token.isLiteral()) {
                            key = parsePrimitiveKey(token.literalValue(), keyType);
                        } else {
                            if (keyType.getType().equals(TType.STRING) || keyType.getType().equals(TType.BINARY)) {
                                throw new JsonException("Expected string literal for string key", tokenizer, token);
                            }
                            key = parsePrimitiveKey(token.getToken(), keyType);
                        }

                        tokenizer.expectSymbol(JsonToken.CH.MAP_KV_SEP, "parsing map (kv)");
                        map.put(key,
                                parseTypedValue(tokenizer.expect("parsing map value."),
                                                tokenizer,
                                                itemType));
                        token = tokenizer.expect("parsing map (sep)");
                        if (JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new JsonException("Unexpected symbol " + token + ". " +
                                                            "Expected list separator or map end.",
                                                    tokenizer,
                                                    token);
                        }
                        token = tokenizer.expect("parsing map key.");
                    }
                    return map;
            }
        } catch (JsonException je) {
            throw new IllegalArgumentException("Unable to parse type value " + token.toString(),
                                               je);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read type value " + token.toString(), e);
        }

        throw new IllegalArgumentException("Unhandled item type " + valueType.getQualifiedName(null));
    }

    private Object parsePrimitiveKey(String key, TDescriptor keyType) throws IOException {
        switch (keyType.getType()) {
            case ENUM:
                TEnumBuilder<?> eb = ((TEnumDescriptor<?>) keyType).factory().builder();
                if (TStringUtils.isInteger(key)) {
                    return eb.setByValue(Integer.parseInt(key)).build();
                } else {
                    // Check for qualified name ( e.g. EnumName.VALUE ).
                    if (key.startsWith(keyType.getName())) {
                        key = key.substring(keyType.getName().length() + 1);
                    }
                    return eb.setByName(key).build();
                }
            case BOOL:
                return Boolean.parseBoolean(key);
            case BYTE:
                return Byte.parseByte(key);
            case I16:
                return Short.parseShort(key);
            case I32:
                return Integer.parseInt(key);
            case I64:
                return Long.parseLong(key);
            case DOUBLE:
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(key.getBytes());
                    JsonTokenizer tokener = new JsonTokenizer(bais);
                    JsonToken token = tokener.expect("parsing double value");
                    return token.doubleValue();
                } catch (JsonException e) {
                    throw new IllegalArgumentException("Unable to parse double from key \"" +
                                                       key + "\"", e);
                }
            case STRING:
                return key;
            case BINARY:
                return parseBinary(key);
            default:
                throw new IllegalArgumentException("Illegal key type: " + keyType.getType());
        }
    }

    /**
     * Parse a string into binary format using the same rules as above.
     *
     * @param value The string to decode.
     * @return The decoded byte array.
     */
    private byte[] parseBinary(String value) {
        return TBase64Utils.decode(value);
    }
}
