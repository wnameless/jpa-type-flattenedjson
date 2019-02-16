package com.github.wnameless.jpa.type.flattenedjson;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
@Entity
public class TestModel {

  @GeneratedValue
  @Id
  Long id;

  @Column(length = 4000)
  @Convert(converter = JsonNodeConverter.class)
  JsonNode props;

  @Column(length = 4000)
  @Convert(converter = TestModelAttrConverter.class)
  TestModelAttr testAttr;

}
