/*
 * Copyright (c) 2016, Providence Authors
 *
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
package net.morimekta.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link Base64InputStream} will read data from another
 * <tt>InputStream</tt>, given in the constructor,
 * and encode/decode to/from Base64 notation on the fly.
 *
 * @see Base64
 * @since 1.3
 */
public class Base64InputStream extends FilterInputStream {

    private boolean encode;         // Encoding or decoding
    private int     position;       // Current position in the buffer
    private byte[]  buffer;         // Small buffer holding converted data
    private int     bufferLength;   // Length of buffer (3 or 4)
    private int     numSigBytes;    // Number of meaningful bytes in the buffer
    private int     lineLength;
    private boolean breakLines;     // Break lines at less than 80 characters
    private int     options;        // Record options used to create the stream.
    private byte[]  decodabet;      // Local copies to avoid extra method calls


    /**
     * Constructs a {@link Base64InputStream} in DECODE mode.
     *
     * @param in the <tt>InputStream</tt> from which to read data.
     * @since 1.3
     */
    public Base64InputStream(InputStream in ) {
        this(in, Base64.DECODE );
    }   // end constructor


    /**
     * Constructs a {@link Base64InputStream} in
     * either ENCODE or DECODE mode.
     * <p>
     * Valid options:<pre>
     *   ENCODE or DECODE: Encode or Decode as data is read.
     *   DO_BREAK_LINES: break lines at 76 characters
     *     (only meaningful when encoding)</i>
     * </pre>
     * <p>
     * Example: <code>new Base64.InputStream( in, Base64.DECODE )</code>
     *
     *
     * @param in the <tt>InputStream</tt> from which to read data.
     * @param options Specified options
     * @see Base64#ENCODE
     * @see Base64#DECODE
     * @see Base64#DO_BREAK_LINES
     * @since 2.0
     */
    public Base64InputStream(InputStream in, int options ) {

        super( in );
        this.options      = options; // Record for later
        this.breakLines   = (options & Base64.DO_BREAK_LINES) > 0;
        this.encode       = (options & Base64.ENCODE) > 0;
        this.bufferLength = encode ? 4 : 3;
        this.buffer       = new byte[ bufferLength ];
        this.position     = -1;
        this.lineLength   = 0;
        this.decodabet    = Base64.getDecodabet(options);
    }   // end constructor

    /**
     * Reads enough of the input stream to convert
     * to/from Base64 and returns the next byte.
     *
     * @return next byte
     * @since 1.3
     */
    @Override
    public int read() throws IOException {

        // Do we need to get data?
        if( position < 0 ) {
            if( encode ) {
                byte[] b3 = new byte[3];
                int numBinaryBytes = 0;
                for( int i = 0; i < 3; i++ ) {
                    int b = in.read();

                    // If end of stream, b is -1.
                    if( b >= 0 ) {
                        b3[i] = (byte)b;
                        numBinaryBytes++;
                    } else {
                        break; // out of for loop
                    }   // end else: end of stream

                }   // end for: each needed input byte

                if( numBinaryBytes > 0 ) {
                    Base64.encode3to4(b3, 0, numBinaryBytes, buffer, 0, options );
                    position = 0;
                    numSigBytes = 4;
                }   // end if: got data
                else {
                    return -1;  // Must be end of stream
                }   // end else
            }   // end if: encoding

            // Else decoding
            else {
                byte[] b4 = new byte[4];
                int i = 0;
                for( i = 0; i < 4; i++ ) {
                    // Read four "meaningful" bytes:
                    int b = 0;
                    do{ b = in.read(); }
                    while( b >= 0 && decodabet[ b & 0x7f ] <= Base64.WHITE_SPACE_ENC );

                    if( b < 0 ) {
                        break; // Reads a -1 if end of stream
                    }   // end if: end of stream

                    b4[i] = (byte)b;
                }   // end for: each needed input byte

                if( i == 4 ) {
                    numSigBytes = Base64.decode4to3(b4, 0, buffer, 0, options );
                    position = 0;
                }   // end if: got four characters
                else if( i == 0 ){
                    return -1;
                }   // end else if: also padded correctly
                else {
                    // Must have broken out from above.
                    throw new IOException( "Improperly padded Base64 input." );
                }   // end

            }   // end else: decode
        }   // end else: get data

        // Got data?
        if( position >= 0 ) {
            // End of relevant data?
            if( /*!encode &&*/ position >= numSigBytes ){
                return -1;
            }   // end if: got data

            if( encode && breakLines && lineLength >= Base64.MAX_LINE_LENGTH ) {
                lineLength = 0;
                return '\n';
            }   // end if
            else {
                lineLength++;   // This isn't important when decoding
                                // but throwing an extra "if" seems
                                // just as wasteful.

                int b = buffer[ position++ ];

                if( position >= bufferLength ) {
                    position = -1;
                }   // end if: end

                return b & 0xFF; // This is how you "cast" a byte that's
                                 // intended to be unsigned.
            }   // end else
        }   // end if: position >= 0

        // Else error
        else {
            throw new IOException( "Error in Base64 code reading stream." );
        }   // end else
    }   // end read


    /**
     * Calls {@link #read()} repeatedly until the end of stream
     * is reached or <var>len</var> bytes are read.
     * Returns number of bytes read into array or -1 if
     * end of stream is encountered.
     *
     * @param dest array to hold values
     * @param off offset for array
     * @param len max number of bytes to read into array
     * @return bytes read into array or -1 if end of stream is encountered.
     * @since 1.3
     */
    @Override
    public int read( byte[] dest, int off, int len )
    throws IOException {
        int i;
        int b;
        for( i = 0; i < len; i++ ) {
            b = read();

            if( b >= 0 ) {
                dest[off + i] = (byte) b;
            }
            else if( i == 0 ) {
                return -1;
            }
            else {
                break; // Out of 'for' loop
            } // Out of 'for' loop
        }   // end for: each byte read
        return i;
    }   // end read

}   // end inner class InputStream
