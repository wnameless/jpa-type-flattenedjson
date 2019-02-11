[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.wnameless/jpa-type-flattenedjson/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.wnameless/jpa-type-flattenedjson)

jpa-type-flattenedjson
=============
Simulate a new database datatype FlattenedJson based on the feature of @Converter since JPA 2.1

## Maven Repo
```xml
<dependency>
	<groupId>com.github.wnameless</groupId>
	<artifactId>jpa-type-flattenedjson</artifactId>
	<version>0.1.0</version>
</dependency>
```

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

Query the stored data by Querydsl with SQL LIKE and REGEXP_LIKE functions supported <br>
QTestModel can be generated by [Querydsl APT](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02.html)
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

## Quick Start
Annotate any field in JPA Entity class with @Convert and a converter class which extends ToFlattenedJsonConverter abstract class
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
Because REGEXP_LIKE is not a standard SQL function, it is required a little configuration to support this feature <br>
So far, Hibernate is the only ORM supported
### REGEXP_LIKE
Spring application.properties
```
hibernate.metadata_builder_contributor=com.github.wnameless.jpa.type.flattenedjson.hibernate.RegexpLikeSqlFunctionContributor
```
Java persistence.xml
```xml
<property>
    name="hibernate.metadata_builder_contributor" 
    value="com.github.wnameless.jpa.type.flattenedjson.hibernate.RegexpLikeSqlFunctionContributor"
</property>
```

### FlattenedJsonTypeConfigurer
FlattenedJsonTypeConfigurer is an enum with a single vlaue INSTANCE which also implies it's a singleton
```java
FlattenedJsonTypeConfigurer.INSTANCE
```

FlattenedJsonType is powered by [JsonFlattener](https://github.com/wnameless/json-flattener)
```java
FlattenedJsonTypeConfigurer.INSTANCE.getJsonFlattenerCustomizer();
FlattenedJsonTypeConfigurer.INSTANCE.setJsonFlattenerCustomizer(Function<JsonFlattener, JsonFlattener> jsonFlattenerCustomizer);
FlattenedJsonTypeConfigurer.INSTANCE.getJsonUnflattenerCustomizer();
FlattenedJsonTypeConfigurer.INSTANCE.setJsonUnflattenerCustomizer(Function<JsonUnflattener, JsonUnflattener> jsonUnflattenerCustomizer);
```
FlattenedJsonType is powered by [jackson-databind](https://github.com/FasterXML/jackson-databind) as well
```java
FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory();
FlattenedJsonTypeConfigurer.INSTANCE.setJsonFlattenerCustomizer(Supplier<ObjectMapper> objectMapperFactory);
```

Any modification in FlattenedJsonTypeConfigurer will take effects on the entire library

### QuerydslHelper
#### FlattenedJson LIKE <br>
Just simply provide the JSON key and value, then the LIKE query pattern is created automatically
```java
@Autowired
TestModelRepository testModelRepo; // Spring Data
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.flattenedJsonlike(qTestModel.testAttr, "numbers[0]", "3");
testModelRepo.count(exp); 
```
Ignore case
```java
QuerydslHelper.flattenedJsonlike(qTestModel.testAttr, "numbers[0]", "3", true);
```

#### LIKE <br>
The LIKE query pattern need to be provide completely
```java
@Autowired
TestModelRepository testModelRepo; // Spring Data
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.like(qTestModel.testAttr, "'%\"numbers[0]\":0,%'");
testModelRepo.count(exp); 
```
Ignore case
```java
QuerydslHelper.like(qTestModel.testAttr, "'%\"numbers[0]\":0,%'", true);
```

#### FlattenedJson REGEXP_LIKE <br>
Just simply provide the JSON key and REGEXP of value, then the REGEXP_LIKE query pattern is created automatically
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.flattenedJsonRegexpLike(qTestModel.testAttr, "numbers[0]", "\\d+");
query.from(qTestModel).where(exp).fetchCount();
```
By default, the key is quoted <br>
This can be disable
```java
QuerydslHelper.flattenedJsonRegexpLike(qTestModel.testAttr, "numbers[0]", "\\d+", false);
```


#### REGEXP_LIKE <br>
The REGEXP_LIKE query pattern need to be provide completely
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.regexpLike(qTestModel.testAttr,
    QuerydslHelper.REGEXP_PAIR_PREFIX    // "[{,]" + "\""
    + Pattern.quote("numbers[0]")
    + QuerydslHelper.REGEXP_PAIR_INFIX   // "\":"
    + "\\d+"
    + QuerydslHelper.REGEXP_PAIR_SUFFIX); // "[,}]"

query.from(qTestModel).where(exp).fetchCount();
```

### ToFlattenedJsonConverter
A base class to create a new JPA Converter of arbitrary type for FlattenedJson
```java
@Converter
public class AnyTypeConverter extends ToFlattenedJsonConverter<AnyType> {

  @Override
  protected TypeReference<AnyType> getAttributeTypeReference() {
    return new TypeReference<AnyType>() {};
  }

}
```
JsonNodeConverter is already provided in library
