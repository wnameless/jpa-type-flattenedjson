package com.github.wnameless.jpa.type.flattenedjson;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQuery;

@ActiveProfiles(profiles = "h2")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
public class H2FlattenedJsonTypeTest {

  @Autowired
  TestModelRepository testModelRepo;

  @Autowired
  EntityManager em;

  ObjectMapper objectMapper =
      FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory().get();

  @Test
  public void testJsonNodeDeserialized() throws IOException {
    JsonNode node = objectMapper.readTree("{ \"abc\":123 }");

    assertEquals(1, testModelRepo.findAll().stream()
        .filter(tm -> tm.props.equals(node)).count());
  }

  @Test
  public void testQuerydslHelperLike() {
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(0, testModelRepo.count(
        QuerydslHelper.like(qTestModel.testAttr, "'%\"numbers[0]\":0,%'")));
    assertEquals(1, testModelRepo.count(
        QuerydslHelper.like(qTestModel.testAttr, "'%\"numbers[0]\":3,%'")));
  }

  @Test
  public void testQuerydslHelperLikeIgnoreCase() {
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(0, testModelRepo.count(
        QuerydslHelper.like(qTestModel.testAttr, "'%\"words[0]\":\"ABC\"%'")));
    assertEquals(1, testModelRepo.count(QuerydslHelper.like(qTestModel.testAttr,
        "'%\"words[1]\":\"xyz\"%'", true)));
  }

  @Test
  public void testQuerydslHelperFlattenedJsonLike() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(1,
        query
            .from(qTestModel).where(QuerydslHelper
                .flattenedJsonLike(qTestModel.testAttr, "numbers[0]", "3"))
            .fetchCount());

    query = new JPAQuery<TestModel>(em);
    assertEquals(1,
        query
            .from(qTestModel).where(QuerydslHelper
                .flattenedJsonLike(qTestModel.testAttr, "numbers[0]", "1"))
            .fetchCount());
  }

  @Test
  public void testQuerydslHelperFlattenedJsonLikeIgnoreCase() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(0,
        query
            .from(qTestModel).where(QuerydslHelper
                .flattenedJsonLike(qTestModel.testAttr, "words[0]", "\"ABC\""))
            .fetchCount());

    query = new JPAQuery<TestModel>(em);
    assertEquals(1,
        query.from(qTestModel)
            .where(QuerydslHelper.flattenedJsonLike(qTestModel.testAttr,
                "words[1]", "\"xyz\"", true))
            .fetchCount());
  }

  @Test
  public void testQuerydslHelperRegexpLike() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(2,
        query.from(qTestModel)
            .where(QuerydslHelper.regexpLike(qTestModel.testAttr,
                QuerydslHelper.REGEXP_PAIR_PREFIX
                    + QuerydslHelper.quoteRegExSpecialChars("numbers[0]")
                    + QuerydslHelper.REGEXP_PAIR_INFIX + "\\d+"
                    + QuerydslHelper.REGEXP_PAIR_SUFFIX))
            .fetchCount());
  }

  @Test
  public void testQuerydslHelperFlattenedJsonRegexpLike() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(2,
        query.from(qTestModel).where(QuerydslHelper
            .flattenedJsonRegexpLike(qTestModel.testAttr, "numbers[0]", "\\d+"))
            .fetchCount());
  }

  @Test
  public void testQuerydslHelperFlattenedJsonRegexpLikeQuoteKey() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(0,
        query.from(qTestModel)
            .where(QuerydslHelper.flattenedJsonRegexpLike(qTestModel.testAttr,
                "numbers[0]", "\\d+", false))
            .fetchCount());
  }

}
