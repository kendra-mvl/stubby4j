/*
 * Copyright (c) 2012-2024 Alexander Zagniotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.azagniotov.stubby4j.stubs.proxy;

import static io.github.azagniotov.stubby4j.utils.StringUtils.toLower;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum StubProxyStrategy {
    /**
     * No changes/modifications will be applied to the request before proxying it.
     * <p>
     * See: https://github.com/azagniotov/stubby4j/blob/master/docs/REQUEST_PROXYING.md#supported-yaml-properties
     */
    AS_IS("as-is"),

    /**
     * Additive changes will be applied to the request before proxying it. The additive changes
     * currently supported are setting of additional HTTP headers using the `headers` property on the
     * `proxy-config` object.
     * <p>
     * See: https://github.com/azagniotov/stubby4j/blob/master/docs/REQUEST_PROXYING.md#supported-yaml-properties
     */
    ADDITIVE("additive");

    private static final Map<String, StubProxyStrategy> PROPERTY_NAME_TO_ENUM_MEMBER;

    static {
        PROPERTY_NAME_TO_ENUM_MEMBER = new HashMap<>();
        for (final StubProxyStrategy enumMember : EnumSet.allOf(StubProxyStrategy.class)) {
            PROPERTY_NAME_TO_ENUM_MEMBER.put(enumMember.toString(), enumMember);
        }
    }

    private final String value;

    StubProxyStrategy(final String value) {
        this.value = value;
    }

    public static boolean isUnknownProperty(final String stubbedProperty) {
        return !PROPERTY_NAME_TO_ENUM_MEMBER.containsKey(toLower(stubbedProperty));
    }

    public static Optional<StubProxyStrategy> ofNullableProperty(final String stubbedProperty) {
        return Optional.ofNullable(PROPERTY_NAME_TO_ENUM_MEMBER.get(toLower(stubbedProperty)));
    }

    @Override
    public String toString() {
        return toLower(this.value);
    }
}
