[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.wnameless/jpa-type-flattenedjson/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.wnameless/jpa-type-flattenedjson)

jpa-type-flattenedjson
=============
Simulate a new database datatype FlattenedJson based on the feature of AttributeConverter since JPA 2.1.

# Goal
- Make all kinds of relational databases to support JSON data format with as little effort as possible. <br>
- Allow user to search arbitrary JSON data through JPA without using database special functions(Ex: JSON_CONTAINS).

## Maven Repository
```xml
<dependency>
	<groupId>com.github.wnameless</groupId>
	<artifactId>jpa-type-flattenedjson</artifactId>
	<version>0.2.0</version>
</dependency>
```

## Concept Brief
Normally JSON format can not be queried directly with SQL, it's required the database to provide special functions to search JSON data. For example, the JSON_CONTAINS function in MySQL database. <br>

However, all those special functions are not well supported by all RDBMS and it tends to break the SQL convention somehow. <br>

AttributeConverter was introduced in JPA 2.1. It allows any field of an entity class to be converted to JSON string which can also be stored as Varchar in all databases. <br>

Applying [JsonFlattener](https://github.com/wnameless/json-flattener) on stored JSON strings makes us possible to search a flattened JSON data by regular SQL LIKE or REGEXP related functions without losing performance.

## HowTo 
Turn arbitrary objects into flattened JSON string and store them into database as Character datatype.
```java
@Entity
public class TestModel {

  @GeneratedValue
  @Id
  Long id;

  @Column(length = 4000)
  @Convert(converter = JsonNodeConverter.class)
  JsonNode props; // JsonNode is from jackson-databind library

  @Column(length = 4000)
  @Convert(converter = TestModelAttrConverter.class) // Implemented by extending the abstract ToFlattenedJsonConverter class
  TestModelAttr testAttr;

}

public class TestModelAttr {

  private List<Integer> numbers = new ArrayList<>();

  private List<Map<String, String>> words = new ArrayList<>();

}
```
```java
@Autowired
TestModelRepository testModelRepo; // Spring Data

TestModel testModel = new TestModel();
testModel.setProps(FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory()
  .get().readTree("{\"abc\":123}"));

TestModelAttr tma = new TestModelAttr();
tma.getNumbers().add(3);
tma.getNumbers().add(2);
tma.getNumbers().add(1);

tma.getWords().add(new HashMap() {{ put("abc", "XYZ"); }});
tma.getWords().add(new HashMap() {{ put("DEF", "uvw"); }});
model.setTestAttr(tma);

testModelRepo.save(model);

// The actual data stored in database:
// | id | props       | test_attr                                                                                |
// |----|-------------|------------------------------------------------------------------------------------------|
// | 1  | {"abc":123} | {"numbers[0]":3,"numbers[1]":2,"numbers[2]":1,"words[0].abc":"XYZ","words[1].DEF":"uvw"} |
```

Query the stored data by [Querydsl](https://github.com/querydsl/querydsl) with SQL LIKE and REGEXP_LIKE functions supported. <br>
QTestModel can be generated by [Querydsl APT](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02.html).
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
Annotate any field in JPA Entity class with @Convert and a converter class which extends ToFlattenedJsonConverter abstract class.
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
Because REGEXP of databases is supported in different ways, it is required a little configuration to enable this feature. <br>
So far, Hibernate is the only ORM supported. <br>

The following table shows all tested databases: <br>

| Database   |  REGEXP_LIKE  |  REGEXP_MATCHES | SUBSTRING
|------------|---------------|-----------------|----------|
| H2         |  &#9745;      |  &#9744;        |  &#9744; |
| HSQLDB     |  &#9744;      |  &#9745;        |  &#9744; |
| MySQL      |  &#9745;      |  &#9744;        |  &#9744; |
| PostgreSQL |  &#9744;      |  &#9744;        |  &#9745; |

### Configuration (Since v0.2.0, REGEXP_MATCHES and SUBSTRING are also supported.)
Pick either of the configurations listed below which fits your database: <br>

Spring application.properties
```javascript
// Add REGEX_LIKE function support to Hibernate
hibernate.metadata_builder_contributor=com.github.wnameless.jpa.type.flattenedjson.hibernate.RegexpLikeSqlFunctionContributor
```
```javascript
// Add REGEX_MATCHES function support to Hibernate
hibernate.metadata_builder_contributor=com.github.wnameless.jpa.type.flattenedjson.hibernate.RegexpMatchesSqlFunctionContributor
```
```javascript
// Add SUBSTRING function support to Hibernate
hibernate.metadata_builder_contributor=com.github.wnameless.jpa.type.flattenedjson.hibernate.SubstringSqlFunctionContributor
```
Java persistence.xml
```xml
<property>
    name="hibernate.metadata_builder_contributor" 
    value="com.github.wnameless.jpa.type.flattenedjson.hibernate.RegexpLikeSqlFunctionContributor"
</property>
```
```xml
<property>
    name="hibernate.metadata_builder_contributor" 
    value="com.github.wnameless.jpa.type.flattenedjson.hibernate.RegexpMatchesSqlFunctionContributor"
</property>
```
```xml
<property>
    name="hibernate.metadata_builder_contributor" 
    value="com.github.wnameless.jpa.type.flattenedjson.hibernate.SubstringSqlFunctionContributor"
</property>
```

### QuerydslHelper
#### LIKE
This query pattern need to be provide completely.
```java
@Autowired
TestModelRepository testModelRepo; // Spring Data
QTestModel qTestModel = QTestModel.testModel;

// BooleanExpression is also a Querysdsl Predicate
BooleanExpression exp = QuerydslHelper.like(qTestModel.testAttr, "'%\"numbers[0]\":3,%'");
// Spring Repository interface can accept Predicate by extending QueryDslPredicateExecutor interface
testModelRepo.count(exp); 
```
Ignore case
```java
QuerydslHelper.like(qTestModel.testAttr, "'%\"NUMBERS[0]\":3,%'", true);
```

#### FlattenedJson LIKE
Just simply provide the JSON key and value, then the LIKE query pattern is created automatically.
```java
@Autowired
TestModelRepository testModelRepo; // Spring Data
QTestModel qTestModel = QTestModel.testModel;

// BooleanExpression is also a Querysdsl Predicate
BooleanExpression exp = QuerydslHelper.flattenedJsonlike(qTestModel.testAttr, "numbers[0]", "3");
// Spring Repository interface can accept Predicate by extending QueryDslPredicateExecutor interface
testModelRepo.count(exp); 
```
Ignore case
```java
QuerydslHelper.flattenedJsonlike(qTestModel.testAttr, "NUMBERS[0]", "3", true);
```

#### REGEXP_LIKE
This query pattern need to be provide completely.
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.regexpLike(qTestModel.testAttr,
    QuerydslHelper.REGEXP_PAIR_PREFIX    // "[{,]" + "\""
    + QuerydslHelper.quoteRegExSpecialChars("numbers[0]")
    + QuerydslHelper.REGEXP_PAIR_INFIX   // "\":"
    + "\\d+"
    + QuerydslHelper.REGEXP_PAIR_SUFFIX); // "[,}]"

query.from(qTestModel).where(exp).fetchCount();
```

#### FlattenedJson REGEXP_LIKE
Just simply provide the JSON key and REGEXP of value, then the query pattern is created automatically.
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.flattenedJsonRegexpLike(qTestModel.testAttr, "numbers[0]", "\\d+");
query.from(qTestModel).where(exp).fetchCount();
```
By default, the key is quoted. This can be disable by doing this:
```java
QuerydslHelper.flattenedJsonRegexpLike(qTestModel.testAttr, "numbers\\[0\\]", "\\d+", false);
```

#### REGEXP_MATCHES
This query pattern need to be provide completely.
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.regexpMatches(qTestModel.testAttr,
    QuerydslHelper.REGEXP_PAIR_PREFIX    // "[{,]" + "\""
    + QuerydslHelper.quoteRegExSpecialChars("numbers[0]")
    + QuerydslHelper.REGEXP_PAIR_INFIX   // "\":"
    + "\\d+"
    + QuerydslHelper.REGEXP_PAIR_SUFFIX); // "[,}]"

query.from(qTestModel).where(exp).fetchCount();
```

#### FlattenedJson REGEXP_MATCHES
Just simply provide the JSON key and REGEXP of value, then the query pattern is created automatically.
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.flattenedJsonRegexpMatches(qTestModel.testAttr, "numbers[0]", "\\d+");
query.from(qTestModel).where(exp).fetchCount();
```
By default, the key is quoted. This can be disable by doing this:
```java
QuerydslHelper.flattenedJsonRegexpMatches(qTestModel.testAttr, "numbers\\[0\\]", "\\d+", false);
```

#### SUBSTRING_MATCHES
Because we only care if the SUBSTRING MATCHES regexp pattern, not actually want to aquire the substring itself. The function is named as **#substringMatches** intead of **#substring** to avoid misunderstanding.

This query pattern need to be provide completely.
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.substringMatches(qTestModel.testAttr,
    QuerydslHelper.REGEXP_PAIR_PREFIX    // "[{,]" + "\""
    + QuerydslHelper.quoteRegExSpecialChars("numbers[0]")
    + QuerydslHelper.REGEXP_PAIR_INFIX   // "\":"
    + "\\d+"
    + QuerydslHelper.REGEXP_PAIR_SUFFIX); // "[,}]"

query.from(qTestModel).where(exp).fetchCount();
```

#### FlattenedJson SUBSTRING_MATCHES
Just simply provide the JSON key and REGEXP of value, then the query pattern is created automatically.
```java
JPAQuery<TestModel> query = new JPAQuery<TestModel>(entityManager);
QTestModel qTestModel = QTestModel.testModel;

BooleanExpression exp = QuerydslHelper.flattenedJsonSubstringMatches(qTestModel.testAttr, "numbers[0]", "\\d+");
query.from(qTestModel).where(exp).fetchCount();
```
By default, the key is quoted. This can be disable by doing this:
```java
QuerydslHelper.flattenedJsonSubstringMatches(qTestModel.testAttr, "numbers\\[0\\]", "\\d+", false);
```

### ToFlattenedJsonConverter
A base class to create a new JPA Converter of arbitrary type for FlattenedJson.
```java
@Converter
public class AnyTypeConverter extends ToFlattenedJsonConverter<AnyType> {

  @Override
  protected TypeReference<AnyType> getAttributeTypeReference() {
    return new TypeReference<AnyType>() {};
  }

}
```
JsonNodeConverter is already provided in library.

### FlattenedJsonTypeConfigurer
FlattenedJsonTypeConfigurer is an enum with a single vlaue INSTANCE which also implies it's a singleton.
```java
FlattenedJsonTypeConfigurer.INSTANCE
```

FlattenedJsonType is powered by [JsonFlattener](https://github.com/wnameless/json-flattener).
```java
FlattenedJsonTypeConfigurer.INSTANCE.getJsonFlattenerCustomizer();
FlattenedJsonTypeConfigurer.INSTANCE.setJsonFlattenerCustomizer(Function<JsonFlattener, JsonFlattener> jsonFlattenerCustomizer);
FlattenedJsonTypeConfigurer.INSTANCE.getJsonUnflattenerCustomizer();
FlattenedJsonTypeConfigurer.INSTANCE.setJsonUnflattenerCustomizer(Function<JsonUnflattener, JsonUnflattener> jsonUnflattenerCustomizer);
```
FlattenedJsonType is powered by [jackson-databind](https://github.com/FasterXML/jackson-databind) as well.
```java
FlattenedJsonTypeConfigurer.INSTANCE.getObjectMapperFactory();
FlattenedJsonTypeConfigurer.INSTANCE.setJsonFlattenerCustomizer(Supplier<ObjectMapper> objectMapperFactory);
```

Any modification in FlattenedJsonTypeConfigurer will take effects on the entire library.
