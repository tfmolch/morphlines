/**
 * Copyright 2012-2013 Snowplow Analytics Ltd
 *
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.ingestion.morphline.refererparser;

import java.util.Arrays;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public enum Medium {
    UNKNOWN,
    INTERNAL,
    SEARCH,
    SOCIAL,
    EMAIL,
    CPC;

    static public Medium fromString(String field) {
        Medium medium;
        try {
            medium = Medium.valueOf(field.toUpperCase());

        } catch (IllegalArgumentException e) {
            medium = Medium.UNKNOWN;
        }
        return medium;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static  boolean contains(String value) {
        final ImmutableSet<Medium> set = Sets.immutableEnumSet(Arrays.asList(Medium.values()));
        return set.contains(Medium.fromString(value));
    }
}
