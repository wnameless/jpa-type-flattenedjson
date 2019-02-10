/*
 *
 * Copyright 2019 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.jpa.type.flattenedjson;

import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;

/**
 * 
 * {@link FlattenedJsonTypeConfigurer} provides options to configure the JPA
 * FlattenedJson type to the way user may want. <br>
 * <br>
 * Those options include JsonFlattenerCustomizer, JsonUnflattenerCustomizer and
 * ObjectMapperFactory.
 *
 */
public enum FlattenedJsonTypeConfigurer {

  INSTANCE;

  private Function<JsonFlattener, JsonFlattener> jsonFlattenerCustomizer =
      (jfc) -> jfc;

  private Function<JsonUnflattener, JsonUnflattener> jsonUnflattenerCustomizer =
      (juc) -> juc;

  private Supplier<ObjectMapper> objectMapperFactory = () -> new ObjectMapper();

  /**
   * Creates a JsonFlattener of given input which is configured by
   * JsonFlattenerCustomizer.
   * 
   * @param json
   *          a valid JSON string
   * @return a configured JsonFlattener
   */
  public JsonFlattener createFlattener(String json) {
    return jsonFlattenerCustomizer.apply(new JsonFlattener(json));
  }

  /**
   * Creates a JsonUnflattener of given input which is configured by
   * JsonUnflattenerCustomizer.
   * 
   * @param json
   *          a valid JSON string
   * @return a configured JsonFlattener
   */
  public JsonUnflattener createUnflattener(String json) {
    return jsonUnflattenerCustomizer.apply(new JsonUnflattener(json));
  }

  /**
   * Returns a {@link java.util.function.Function Function} to customize
   * {@link com.github.wnameless.json.flattener.JsonFlattener JsonFlattener}.
   * 
   * @return a {@link java.util.function.Function Function} to customize
   *         {@link com.github.wnameless.json.flattener.JsonFlattener
   *         JsonFlattener}
   */
  public Function<JsonFlattener, JsonFlattener> getJsonFlattenerCustomizer() {
    return jsonFlattenerCustomizer;
  }

  /**
   * Sets a {@link java.util.function.Function Function} to customize
   * {@link com.github.wnameless.json.flattener.JsonFlattener JsonFlattener}.
   */
  public void setJsonFlattenerCustomizer(
      Function<JsonFlattener, JsonFlattener> jsonFlattenerCustomizer) {
    this.jsonFlattenerCustomizer = jsonFlattenerCustomizer;
  }

  /**
   * Returns a {@link java.util.function.Function Function} to customize
   * {@link com.github.wnameless.json.unflattener.JsonUnflattener
   * JsonUnflattener}.
   * 
   * @return a {@link java.util.function.Function Function} to customize
   *         {@link com.github.wnameless.json.unflattener.JsonUnflattener
   *         JsonUnflattener}
   */
  public Function<JsonUnflattener, JsonUnflattener> getJsonUnflattenerCustomizer() {
    return jsonUnflattenerCustomizer;
  }

  /**
   * Sets a {@link java.util.function.Function Function} to customize
   * {@link com.github.wnameless.json.unflattener.JsonUnflattener
   * JsonUnflattener}.
   */
  public void setJsonUnflattenerCustomizer(
      Function<JsonUnflattener, JsonUnflattener> jsonUnflattenerCustomizer) {
    this.jsonUnflattenerCustomizer = jsonUnflattenerCustomizer;
  }

  /**
   * Returns a {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}
   * which handles JSON serialization and deserialization in FlattenedJsonType.
   * 
   * @return a {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}
   */
  public Supplier<ObjectMapper> getObjectMapperFactory() {
    return objectMapperFactory;
  }

  /**
   * Sets a {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper} to
   * handle JSON serialization and deserialization in FlattenedJsonType.
   */
  public void setObjectMapperFactory(
      Supplier<ObjectMapper> objectMapperFactory) {
    this.objectMapperFactory = objectMapperFactory;
  }

}