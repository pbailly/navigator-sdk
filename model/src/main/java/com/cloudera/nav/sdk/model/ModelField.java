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

import com.cloudera.nav.sdk.model.annotations.MProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

/**
 * Encapsulated information about a custom metadata model field associated with
 * an @MProperty annotation.
 */
public class ModelField {

  /**
   * Type info for the field
   */
  public static enum FieldType {
    BOOLEAN(Boolean.class),
    DATE(Instant.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    INTEGER(Integer.class),
    LONG(Long.class),
    STRING(String.class),
    TEXT(String.class, false);

    private static final LinkedListMultimap<Class<?>, FieldType> typeMap =
        LinkedListMultimap.create();
    static {
      for (FieldType fieldType : FieldType.values()) {
        typeMap.put(fieldType.getValueType(), fieldType);
      }
      // primitives
      typeMap.put(boolean.class, BOOLEAN);
      typeMap.put(long.class, LONG);
      typeMap.put(int.class, INTEGER);
      typeMap.put(float.class, FLOAT);
      typeMap.put(double.class, DOUBLE);
    }

    /**
     * Get the FieldType that correspond with given Class
     * @param valueType
     */
    public static Collection<FieldType> getFieldTypes(Class<?> valueType) {
      return typeMap.get(valueType);
    }

    private final Class<?> valueType;
    private final boolean defaultFieldType;

    private FieldType(Class<?> valueType) {
      this(valueType, true);
    }

    /**
     * @param valueType
     * @param defaultFieldType is this the default FieldType for given valueType
     */
    private FieldType(Class<?> valueType, boolean defaultFieldType) {
      this.valueType = valueType;
      this.defaultFieldType = defaultFieldType;
    }

    public Class<?> getValueType() {
      return valueType;
    }

    public boolean isDefaultFieldType() {
      return defaultFieldType;
    }
  }

  /**
   * Create a ModelField instance from a Field annotated with @MProperty
   * @param field
   */
  public static ModelField fromField(Field field) {
    Preconditions.checkArgument(field.getAnnotation(MProperty.class) != null,
        "Could not find @MProperty annotation from field %s", field);
    MProperty ann = field.getAnnotation(MProperty.class);
    Class<?> valueType = field.getType();
    boolean isMultiValued = false;
    if (valueType.isAssignableFrom(Collection.class) ||
        valueType.isAssignableFrom(Iterable.class)) {
      isMultiValued = true;
      valueType = (Class)((ParameterizedType)field.getGenericType())
          .getActualTypeArguments()[0];
    }
    Collection<FieldType> fieldTypes = FieldType.getFieldTypes(valueType);
    FieldType fType = null;
    if (fieldTypes.size() == 1) {
      fType = Iterables.getOnlyElement(fieldTypes);
    } else if (StringUtils.isNotEmpty(ann.fieldType())) {
      fType = FieldType.valueOf(ann.fieldType());
      Preconditions.checkArgument(fieldTypes.contains(fType),
          "MProperty.fieldType attribute for %s does not match declared " +
              "type %s", fType, valueType);
    } if (CollectionUtils.isEmpty(fieldTypes)) {
      // default to String field if no matching type found and no override
      // fieldType provided in annotation
      fType = FieldType.STRING;
    } else {
      for (FieldType ft : fieldTypes) {
        if (ft.isDefaultFieldType()) {
          fType = ft;
          break;
        }
      }
      Preconditions.checkNotNull(fType, "No default FieldType found for %s",
          valueType);
    }

    String name = StringUtils.isEmpty(ann.attribute()) ? field.getName() :
        ann.attribute();
    return new ModelField(name, fType, isMultiValued);
  }

  private final String name;
  private final FieldType type;
  private final boolean multiValued;

  public ModelField(String name, FieldType type, boolean multiValued) {
    this.name = name;
    this.type = type;
    this.multiValued = multiValued;
  }

  public String getName() {
    return name;
  }

  public FieldType getType() {
    return type;
  }

  public boolean isMultiValued() {
    return multiValued;
  }
}
