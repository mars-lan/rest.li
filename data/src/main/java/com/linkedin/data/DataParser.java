/*
   Copyright (c) 2020 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.data;

import java.io.IOException;


/**
 * Data parser interface invoked by stream decoder.
 *
 * This interface contains methods that are invoked when parsing a Data object.
 * Each method represents a different kind of event/read action
 *
 * Methods can throw IOException as a checked exception to
 * indicate parsing error.
 *
 * @author amgupta1
 */
public interface DataParser
{
  /**
   * Internal tokens, used to identify types of elements in data during decoding
   */
  enum Token
  {
    START_OBJECT,
    END_OBJECT,
    START_ARRAY,
    END_ARRAY,
    STRING,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    BOOL_TRUE,
    BOOL_FALSE,
    NULL,
    NOT_AVAILABLE
  }

  /**
   * Method that can be called to feed more data
   *
   * @param data Byte array that contains data to feed: caller must ensure data remains
   *    stable until it is fully processed
   * @param offset Offset where input data to process starts
   * @param end Offset after last byte contained in the input array
   *
   * @throws IOException if the state is such that this method should not be called
   *   (has not yet consumed existing input data, or has been marked as closed)
   */
  void feedInput(byte[] data, int offset, int end) throws IOException;

  /**
   * Method that should be called after last chunk of data to parse has been fed
   * (with {@link #feedInput(byte[], int, int)}). After calling this method,
   * no more data can be fed; and parser assumes no more data will be available.
   */
  void endOfInput();

  /**
   * Main iteration method, which will advance stream enough
   * to determine type of the next token, if any. If none
   * remaining (stream has no content other than possible
   * white space before ending), null will be returned.
   *
   * @return Next token from the stream, if any found, or null
   *   to indicate end-of-input
   */
  Token nextToken() throws IOException;

  /**
   * Method for accessing textual representation of the current token;
   * if no current token (before first call to {@link #nextToken}, or
   * after encountering end-of-input), returns null.
   * Method can be called for any token type.
   */
  String getString() throws IOException;

  /**
   * Numeric accessor that can be called when the current
   * token is of type {@link Token#INTEGER} and
   * it can be expressed as a value of Java int primitive type.
   */
  int getIntValue() throws IOException;

  /**
   * Numeric accessor that can be called when the current
   * token is of type {@link Token#LONG} and
   * it can be expressed as a Java long primitive type.
   */
  long getLongValue() throws IOException;

  /**
   * Numeric accessor that can be called when the current
   * token is of type {@link Token#FLOAT} and
   * it can be expressed as a Java float primitive type.
   */
  float getFloatValue() throws IOException;

  /**
   * Numeric accessor that can be called when the current
   * token is of type {@link Token#DOUBLE} and
   * it can be expressed as a Java double primitive type.
   */
  double getDoubleValue() throws IOException;
}