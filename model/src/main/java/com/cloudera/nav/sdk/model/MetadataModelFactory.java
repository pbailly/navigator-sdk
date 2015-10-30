/*
 * Copyright (c) 2015 Cloudera, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.nav.sdk.model;

import com.cloudera.nav.sdk.model.annotations.MClass;
import com.cloudera.nav.sdk.model.annotations.MProperty;
import com.cloudera.nav.sdk.model.entities.Entity;
import com.cloudera.nav.sdk.model.entities.TagChangeSet;
import com.cloudera.nav.sdk.model.entities.UDPChangeSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Factory for generating MetadataModel instances from Entity sub-types
 * annotated with @MClass
 */
public class MetadataModelFactory {

  /**
   * Create a MetadataModel from a given Entity subclass
   * @param entityClass
   */
  public MetadataModel newModel(Class<? extends Entity> entityClass,
                                String namespace) {
    MClass ann = entityClass.getAnnotation(MClass.class);
    MetadataModel model = new MetadataModel(ann.model(), namespace);

    // get all @MProperty's (including inherited ones)
    Map<Field, Method> properties = MClassUtil.getAnnotatedProperties(
        entityClass, MProperty.class);

    Class<?> valueType;
    for (Map.Entry<Field, Method> entry : properties.entrySet()) {
      valueType = entry.getKey().getType();
      if (valueType != UDPChangeSet.class && valueType != TagChangeSet.class) {
        model.addField(ModelField.fromField(entry.getKey()));
      }
    }
    return model;
  }
}
