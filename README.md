[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.wnameless/jpa-type-flattenedjson/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.wnameless/jpa-type-flattenedjson)

jpa-type-flattenedjson
=============
Simulate a new database data type FlattenedJson based on the feature of @Converter since JPA 2.1

## Purpose
Turn arbitrary objects into flattened JSON string and store them into database as Character datatype
```java
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

public class TestModelAttr {

  private List<Integer> numbers = new ArrayList<>();

  private List<String> words = new ArrayList<>();

}
```

Query the stored data by Querydsl with LIKE and REGEXP_LIKE functions support
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

long count = query
  .from(qTestModel)
  .where(QuerydslHelper.flattenedJsonLike(qTestModel.testAttr, "numbers[0]", "3"))
  .fetchCount();
  
count = query
  .from(qTestModel)
  .where(QuerydslHelper.flattenedJsonRegexpLike(qTestModel.testAttr, "numbers[0]", "\\d+"))
  .fetchCount();
```

## Maven Repo
```xml
<dependency>
	<groupId>com.github.wnameless</groupId>
	<artifactId>jpa-type-flattenedjson</artifactId>
	<version>0.1.0</version>
</dependency>
```

## Quick Start
Annotates any field in JPA Entity class with @Convert and a converter class which extends ToFlattenedJsonConverter abstract class
```java
@Convert(converter = TestModelAttrConverter.class)
```
```java
public class TestModelAttrConverter
    extends ToFlattenedJsonConverter<TestModelAttr> {

  @Override
  protected TypeReference<TestModelAttr> getAttributeTypeReference() {
    return new TypeReference<TestModelAttr>() {};
  }

}
```

## Features
### FlattenedJsonTypeConfigurer
```java
```

### QuerydslHelper
```java
```

### ToFlattenedJsonConverter
```java
```
