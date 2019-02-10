package com.github.wnameless.jpa.type.flattenedjson;

import com.fasterxml.jackson.core.type.TypeReference;

public class TestModelAttrConverter
    extends ToFlattenedJsonConverter<TestModelAttr> {

  @Override
  protected TypeReference<TestModelAttr> getAttributeTypeReference() {
    return new TypeReference<TestModelAttr>() {};
  }

}
