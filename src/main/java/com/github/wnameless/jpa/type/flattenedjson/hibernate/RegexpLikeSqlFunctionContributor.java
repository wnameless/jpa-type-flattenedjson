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
package com.github.wnameless.jpa.type.flattenedjson.hibernate;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

/**
 * 
 * {@link RegexpLikeSqlFunctionContributor} is designed to let Hibernate ORM
 * support REFEXP_LIKE SQL function.
 *
 */
public class RegexpLikeSqlFunctionContributor
    implements MetadataBuilderContributor {

  @Override
  public void contribute(MetadataBuilder metadataBuilder) {
    metadataBuilder.applySqlFunction("regexp_like",
        new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN,
            "(case when (regexp_like(?1, ?2)) then 1 else 0 end)"));
  }

}