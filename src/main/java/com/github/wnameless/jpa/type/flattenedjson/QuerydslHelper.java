/*
 *
 * Copyright 2019 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.jpa.type.flattenedjson;

import java.util.regex.Pattern;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

/**
 * 
 * {@link QuerydslHelper} can help users to make SQL LIKE or REGEXP_LIKE queries
 * based on QueryDSL to any filed which is annotated by
 * {@link javax.persistence.Convert @Convert}
 *
 */
public class QuerydslHelper {

  public static String REGEXP_PAIR_PREFIX = "[{,]" + "\"";
  public static String REGEXP_PAIR_INFIX = "\":";
  public static String REGEXP_PAIR_SUFFIX = "[,}]";

  public static String LIKE_PAIR_PREFIX = "'%\"";
  public static String LIKE_PAIR_INFIX = "\":";
  public static String LIKE_PAIR_SUFFIX1 = ",\"%'";
  public static String LIKE_PAIR_SUFFIX2 = "}'";

  private QuerydslHelper() {}

  public static <T> BooleanExpression like(Expression<T> path, String pattern) {
    return like(path, pattern, false);
  }

  public static <T> BooleanExpression like(Expression<T> path, String pattern,
      boolean ignoreCase) {
    Expression<T> exp = Expressions.simpleTemplate(path.getType(), pattern);
    return Expressions.predicate(ignoreCase ? Ops.LIKE_IC : Ops.LIKE, path,
        exp);
  }

  public static <T> BooleanExpression flattenedJsonLike(Expression<T> path,
      String key, String value) {
    return flattenedJsonLike(path, key, value, false);
  }

  public static <T> BooleanExpression flattenedJsonLike(Expression<T> path,
      String key, String value, boolean ignoreCase) {
    return likeExpression(path, key, value, ignoreCase);
  }

  private static <T> BooleanExpression likeExpression(Expression<T> path,
      String key, String value, boolean ignoreCase) {
    Expression<T> exp1 = Expressions.simpleTemplate(path.getType(),
        LIKE_PAIR_PREFIX + key + LIKE_PAIR_INFIX + value + LIKE_PAIR_SUFFIX1);
    Expression<T> exp2 = Expressions.simpleTemplate(path.getType(),
        LIKE_PAIR_PREFIX + key + LIKE_PAIR_INFIX + value + LIKE_PAIR_SUFFIX2);
    return Expressions
        .predicate(ignoreCase ? Ops.LIKE_IC : Ops.LIKE, path, exp1)
        .or(Expressions.predicate(ignoreCase ? Ops.LIKE_IC : Ops.LIKE, path,
            exp2));
  }

  public static <T> BooleanExpression regexpLike(Expression<T> path,
      String regexp) {
    return Expressions.booleanTemplate("regexp_like({0}, {1}) = 1", path,
        regexp);
  }

  public static <T> BooleanExpression flattenedJsonRegexpLike(
      Expression<T> path, String key, String valueRegex) {
    return flattenedJsonRegexpLike(path, key, valueRegex, true);
  }

  public static <T> BooleanExpression flattenedJsonRegexpLike(
      Expression<T> path, String key, String valueRegex, boolean quoteKey) {
    key = quoteKey ? Pattern.quote(key) : key;
    String regex = REGEXP_PAIR_PREFIX + key + REGEXP_PAIR_INFIX + valueRegex
        + REGEXP_PAIR_SUFFIX;
    return Expressions.booleanTemplate("regexp_like({0}, {1}) = 1", path,
        regex);
  }

}