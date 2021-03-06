package com.github.wnameless.jpa.type.flattenedjson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TestModelAttr {

  private List<Integer> numbers = new ArrayList<>();

  private List<Map<String, String>> words = new ArrayList<>();

}
