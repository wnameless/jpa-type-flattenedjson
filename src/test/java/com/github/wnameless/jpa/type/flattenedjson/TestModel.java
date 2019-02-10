package com.github.wnameless.jpa.type.flattenedjson;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Table(
    indexes = { @Index(columnList = "props"), @Index(columnList = "testAttr") })
@Data
@Entity
public class TestModel {

  @GeneratedValue
  @Id
  Long id;

  @Column(columnDefinition = "varchar(max)")
  @Convert(converter = JsonNodeConverter.class)
  JsonNode props;

  @Column(columnDefinition = "varchar(max)")
  @Convert(converter = TestModelAttrConverter.class)
  TestModelAttr testAttr;

}
