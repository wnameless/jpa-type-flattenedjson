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

import java.io.IOException;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * {@link ToFlattenedJsonConverter} is a convenient abstract class for user to
 * create a JPA flattened JSON converter of arbitrary type simply by inheriting
 * it.
 *
 * @param <T>
 *          any Type
 * 
 */
public abstract class ToFlattenedJsonConverter<T>
    implements AttributeConverter<T, String> {

  protected abstract TypeReference<T> getAttributeTypeReference();

  protected ObjectMapper objectMapper =
      FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory().get();

  @Override
  public String convertToDatabaseColumn(T attribute) {
    String json;
    try {
      json = objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return FlattenedJsonTypeConfigurer.INSTANCE.createFlattener(json).flatten();
  }

  @Override
  public T convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(FlattenedJsonTypeConfigurer.INSTANCE
          .createUnflattener(dbData).unflatten(), getAttributeTypeReference());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}