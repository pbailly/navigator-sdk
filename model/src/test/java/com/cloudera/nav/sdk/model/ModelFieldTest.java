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

import static org.junit.Assert.*;

import com.cloudera.nav.sdk.model.annotations.MProperty;
import com.cloudera.nav.sdk.model.entities.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.junit.*;

public class ModelFieldTest {

  @Test
  public void testFromField() {
    ModelField modelField;
    for (Field field : TestModel.class.getDeclaredFields()) {
      modelField = ModelField.fromField(field);
      String expectedName = field.getAnnotation(MProperty.class).attribute();
      if (StringUtils.isEmpty(expectedName)) {
        expectedName = field.getName();
      }
      assertEquals(expectedName, modelField.getName());

      Class<?> valueType = field.getType();
      if (Collection.class.isAssignableFrom(valueType)) {
        Type typeArg = ((ParameterizedType)field.getGenericType())
            .getActualTypeArguments()[0];
        assertEquals(typeArg, modelField.getType().getValueType());
        assertTrue(modelField.isMultiValued());
      } else if (Enum.class.isAssignableFrom(valueType)) {
        assertEquals(String.class, modelField.getType().getValueType());
        assertFalse(modelField.isMultiValued());
      } else {
        assertTrue(valueType == modelField.getType().getValueType() ||
            isPrimitiveFor(valueType, modelField.getType().getValueType()));
        assertFalse(modelField.isMultiValued());
      }
    }
  }

  private boolean isPrimitiveFor(Class<?> primType, Class<?> objType) {
    return (primType == boolean.class && objType == Boolean.class) ||
        (primType == int.class && objType == Integer.class) ||
        (primType == long.class && objType == Long.class) ||
        (primType == float.class && objType == Float.class) ||
        (primType == double.class && objType == Double.class);
  }

  private static class TestModel {
    @MProperty
    private Instant dateField;
    @MProperty
    private Boolean booleanField;
    @MProperty
    private Double doubleField;
    @MProperty
    private Float floatField;
    @MProperty
    private Integer integerField;
    @MProperty
    private Long longField;
    @MProperty
    private boolean booleanField2;
    @MProperty
    private double doubleField2;
    @MProperty
    private float floatField2;
    @MProperty
    private int integerField2;
    @MProperty
    private long longField2;
    @MProperty
    private String stringField;
    @MProperty(fieldType="TEXT")
    private String textField;
    @MProperty
    private EntityType enumField;
    @MProperty(attribute = "foo")
    private String namedField;
    @MProperty
    private Collection<Instant> collectionField;
  }
}
