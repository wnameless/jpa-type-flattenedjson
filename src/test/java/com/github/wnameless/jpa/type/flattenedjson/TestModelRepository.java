package com.github.wnameless.jpa.type.flattenedjson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TestModelRepository extends JpaRepository<TestModel, Long>,
    QuerydslPredicateExecutor<TestModel> {}
