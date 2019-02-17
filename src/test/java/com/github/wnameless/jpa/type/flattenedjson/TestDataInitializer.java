package com.github.wnameless.jpa.type.flattenedjson;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataInitializer {

  @Autowired
  TestModelRepository testModelRepo;

  @SuppressWarnings({ "unchecked", "serial", "rawtypes" })
  @PostConstruct
  void after() throws IOException {
    TestModel model = new TestModel();
    model.setProps(FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory()
        .get().readTree("{ \"abc\": { \"CBA\": 123 } }"));

    TestModelAttr tma = new TestModelAttr();
    tma.getNumbers().add(3);
    tma.getNumbers().add(2);
    tma.getNumbers().add(1);
    model.setTestAttr(tma);

    tma.getWords().add(new HashMap() {
      {
        put("abc", "XYZ");
      }
    });
    tma.getWords().add(new HashMap() {
      {
        put("DEF", "uvw");
      }
    });

    testModelRepo.save(model);

    model = new TestModel();
    model.setProps(FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory()
        .get().readTree("{}"));

    tma = new TestModelAttr();
    tma.getNumbers().add(1);
    tma.getNumbers().add(2);
    tma.getNumbers().add(3);
    model.setTestAttr(tma);

    testModelRepo.save(model);
  }

}
