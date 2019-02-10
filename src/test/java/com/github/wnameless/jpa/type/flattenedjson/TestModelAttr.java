package com.github.wnameless.jpa.type.flattenedjson;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TestModelAttr {

  private List<Integer> numbers = new ArrayList<>();

  private List<String> words = new ArrayList<>();

}
