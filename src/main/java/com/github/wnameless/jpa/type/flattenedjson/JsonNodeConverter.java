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

import javax.persistence.Converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * {@link JsonNodeConverter} is a {@link javax.persistence.Converter Converter}
 * which converts attributes from {@link com.fasterxml.jackson.databind.JsonNode
 * JsonNode} to FlattenedJsonType.
 *
 */
@Converter
public class JsonNodeConverter extends ToFlattenedJsonConverter<JsonNode> {

  @Override
  protected TypeReference<JsonNode> getAttributeTypeReference() {
    return new TypeReference<JsonNode>() {};
  }

}