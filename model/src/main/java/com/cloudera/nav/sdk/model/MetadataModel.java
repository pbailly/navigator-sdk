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

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Encapsulates relevant model information about a class annotated with @MClass
 * so the model can be registered with the Navigator server
 */
public class MetadataModel {

  private final String name;
  private final String namespace;
  private final Collection<ModelField> fields;

  public MetadataModel(String name, String namespace) {
    this.name = name;
    this.namespace = namespace;
    this.fields = Lists.newLinkedList();
  }

  /**
   * Name of the model. The combination of name and namespace should be unique
   */
  public String getName() {
    return name;
  }

  /**
   * Namespace for the model. The combination of name and namespace should be
   * unique
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * Custom fields
   */
  public Collection<ModelField> getFields() {
    return fields;
  }

  /**
   * Add a new custom field
   *
   * @param field
   */
  public void addField(ModelField field) {
    fields.add(field);
  }
}
