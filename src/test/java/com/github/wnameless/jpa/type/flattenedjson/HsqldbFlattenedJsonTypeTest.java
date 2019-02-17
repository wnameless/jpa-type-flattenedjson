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

@ActiveProfiles(profiles = "hsqldb")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
public class HsqldbFlattenedJsonTypeTest {

  @Autowired
  TestModelRepository testModelRepo;

  @Autowired
  EntityManager em;

  ObjectMapper objectMapper =
      FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory().get();

  @Test
  public void testJsonNodeDeserialized() throws IOException {
    JsonNode node = objectMapper.readTree("{\"abc\":{\"CBA\":123}}");

    assertEquals(1, testModelRepo.findAll().stream()
        .filter(tm -> tm.props.equals(node)).count());
  }

  @Test
  public void testQuerydslHelperLike() {
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(0, testModelRepo.count(
        QueryDslHelper.like(qTestModel.testAttr, "'%\"numbers[0]\":0,%'")));
    assertEquals(1, testModelRepo.count(
        QueryDslHelper.like(qTestModel.testAttr, "'%\"numbers[0]\":3,%'")));
  }

  @Test
  public void testQuerydslHelperLikeIgnoreCase() {
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(0, testModelRepo.count(QueryDslHelper.like(qTestModel.testAttr,
        "'%\"words[0].abc\":\"xyz\"%'")));
    assertEquals(1, testModelRepo.count(QueryDslHelper.like(qTestModel.testAttr,
        "'%\"words[0].abc\":\"xyz\"%'", true)));
  }

  @Test
  public void testQuerydslHelperFlattenedJsonLike() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(1,
        query
            .from(qTestModel).where(QueryDslHelper
                .flattenedJsonLike(qTestModel.testAttr, "numbers[0]", "3"))
            .fetchCount());

    query = new JPAQuery<TestModel>(em);
    assertEquals(1,
        query
            .from(qTestModel).where(QueryDslHelper
                .flattenedJsonLike(qTestModel.testAttr, "numbers[0]", "1"))
            .fetchCount());
  }

  @Test
  public void testQuerydslHelperFlattenedJsonLikeIgnoreCase() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(1,
        query.from(qTestModel).where(QueryDslHelper
            .flattenedJsonLike(qTestModel.testAttr, "words[1].DEF", "\"uvw\""))
            .fetchCount());

    query = new JPAQuery<TestModel>(em);
    assertEquals(1,
        query.from(qTestModel)
            .where(QueryDslHelper.flattenedJsonLike(qTestModel.testAttr,
                "words[1].def", "\"UVW\"", true))
            .fetchCount());
  }

  @Test
  public void testQuerydslHelperRegexpMatches() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(2,
        query.from(qTestModel)
            .where(QueryDslHelper.regexpMatches(qTestModel.testAttr,
                QueryDslHelper.REGEXP_PAIR_PREFIX
                    + QueryDslHelper.quoteRegExSpecialChars("numbers[0]")
                    + QueryDslHelper.REGEXP_PAIR_INFIX + "\\d+"
                    + QueryDslHelper.REGEXP_PAIR_SUFFIX))
            .fetchCount());
  }

  @Test
  public void testQuerydslHelperFlattenedJsonRegexpMatches() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(2, query.from(qTestModel).where(QueryDslHelper
        .flattenedJsonRegexpMatches(qTestModel.testAttr, "numbers[0]", "\\d+"))
        .fetchCount());
  }

  @Test
  public void testQuerydslHelperFlattenedJsonRegexpMatchesQuoteKey() {
    JPAQuery<TestModel> query = new JPAQuery<TestModel>(em);
    QTestModel qTestModel = QTestModel.testModel;

    assertEquals(0,
        query.from(qTestModel)
            .where(QueryDslHelper.flattenedJsonRegexpMatches(
                qTestModel.testAttr, "numbers[0]", "\\d+", false))
            .fetchCount());
  }

}
