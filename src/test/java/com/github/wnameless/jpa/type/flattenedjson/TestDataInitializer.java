package com.github.wnameless.jpa.type.flattenedjson;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataInitializer {

  @Autowired
  TestModelRepository testModelRepo;

  @PostConstruct
  void after() throws IOException {
    TestModel model = new TestModel();
    model.setProps(FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory()
        .get().readTree("{\"abc\":123}"));

    TestModelAttr tma = new TestModelAttr();
    tma.getNumbers().add(3);
    tma.getNumbers().add(2);
    tma.getNumbers().add(1);
    model.setTestAttr(tma);

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

    model = new TestModel();
    tma = new TestModelAttr();
    tma.getWords().add("abc");
    tma.getWords().add("XYZ");
    model.setTestAttr(tma);

    testModelRepo.save(model);
  }

}
