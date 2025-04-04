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

package io.github.azagniotov.stubby4j.stubs;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;

public class StubMatcherTest {

    private StubMatcher stubMatcher;

    @Before
    public void setUp() throws Exception {
        stubMatcher = new StubMatcher(new HashMap<>());
    }

    @Test
    public void arraysIntersect_ShouldReturnTrue_WhenDataStoreArrayEmpty() throws Exception {
        final boolean isArraysIntersect = stubMatcher.listsIntersect(new ArrayList<>(), new ArrayList<String>() {
            {
                add("apple");
            }
        });

        assertThat(isArraysIntersect).isTrue();
    }

    @Test
    public void arraysIntersect_ShouldReturnFalse_WhenAssertingArrayEmpty() throws Exception {
        final boolean isArraysIntersect = stubMatcher.listsIntersect(
                new ArrayList<String>() {
                    {
                        add("apple");
                    }
                },
                new ArrayList<>());

        assertThat(isArraysIntersect).isFalse();
    }

    @Test
    public void arraysIntersect_ShouldReturnTrue_WhenTwoArraysHaveTheSameElements() throws Exception {
        final boolean isArraysIntersect = stubMatcher.listsIntersect(
                new ArrayList<String>() {
                    {
                        add("apple");
                    }
                },
                new ArrayList<String>() {
                    {
                        add("apple");
                    }
                });

        assertThat(isArraysIntersect).isTrue();
    }

    @Test
    public void arraysIntersect_ShouldReturnFalse_WhenTwoArraysDontHaveTheSameElements() throws Exception {
        final boolean isArraysIntersect = stubMatcher.listsIntersect(
                new ArrayList<String>() {
                    {
                        add("apple");
                    }
                },
                new ArrayList<String>() {
                    {
                        add("orange");
                    }
                });

        assertThat(isArraysIntersect).isFalse();
    }

    @Test
    public void stringsMatch_ShouldReturnTrue_WhenDataStoreValueNull() throws Exception {
        final String dataStoreVlaue = null;
        final String assertingValue = "blah";
        final boolean isStringsMatch =
                stubMatcher.stringsMatch(dataStoreVlaue, assertingValue, "arbitrary template token name");

        assertThat(isStringsMatch).isTrue();
    }

    @Test
    public void stringsMatch_ShouldReturnTrue_WhenDataStoreValueEmpty() throws Exception {
        final String dataStoreVlaue = "";
        final String assertingValue = "blah";
        final boolean isStringsMatch =
                stubMatcher.stringsMatch(dataStoreVlaue, assertingValue, "arbitrary template token name");

        assertThat(isStringsMatch).isTrue();
    }

    @Test
    public void stringsMatch_ShouldReturnFalse_WhenAssertingValueNull() throws Exception {
        final String dataStoreVlaue = "stubbedValue";
        final String assertingValue = null;
        final boolean isStringsMatch =
                stubMatcher.stringsMatch(dataStoreVlaue, assertingValue, "arbitrary template token name");

        assertThat(isStringsMatch).isFalse();
    }

    @Test
    public void stringsMatch_ShouldReturnFalse_WhenAssertingValueEmpty() throws Exception {
        final String dataStoreVlaue = "stubbedValue";
        final String assertingValue = "";
        final boolean isStringsMatch =
                stubMatcher.stringsMatch(dataStoreVlaue, assertingValue, "arbitrary template token name");

        assertThat(isStringsMatch).isFalse();
    }

    @Test
    public void mapsMatch_ShouldReturnTrue_WhenDataStoreMapEmptyAndAssertingMapEmpty() throws Exception {
        final Map<String, String> dataStoreMap = new HashMap<>();
        final Map<String, String> assertingMap = new HashMap<>();
        final boolean isMapsMatch = stubMatcher.mapsMatch(dataStoreMap, assertingMap, "arbitrary template token name");

        assertThat(isMapsMatch).isTrue();
    }

    @Test
    public void mapsMatch_ShouldReturnTrue_WhenDataStoreMapEmpty() throws Exception {
        final Map<String, String> dataStoreMap = new HashMap<>();
        final Map<String, String> assertingMap = new HashMap<String, String>() {
            {
                put("key", "value");
            }
        };
        final boolean isMapsMatch = stubMatcher.mapsMatch(dataStoreMap, assertingMap, "arbitrary template token name");

        assertThat(isMapsMatch).isTrue();
    }

    @Test
    public void mapsMatch_ShouldReturnFalse_WhenDataStoreMapNotEmptyAndAssertingMapEmpty() throws Exception {
        final Map<String, String> dataStoreMap = new HashMap<String, String>() {
            {
                put("key", "value");
            }
        };
        final Map<String, String> assertingMap = new HashMap<>();
        final boolean isMapsMatch = stubMatcher.mapsMatch(dataStoreMap, assertingMap, "arbitrary template token name");

        assertThat(isMapsMatch).isFalse();
    }

    @Test
    public void mapsMatch_ShouldReturnFalse_WhenAssertingMapDoesNotContainDataStoreKey() throws Exception {
        final Map<String, String> dataStoreMap = new HashMap<String, String>() {
            {
                put("requiredKey", "requiredValue");
            }
        };
        final Map<String, String> assertingMap = new HashMap<String, String>() {
            {
                put("someKey", "someValue");
            }
        };
        final boolean isMapsMatch = stubMatcher.mapsMatch(dataStoreMap, assertingMap, "arbitrary template token name");

        assertThat(isMapsMatch).isFalse();
    }

    @Test
    public void mapsMatch_ShouldReturnFalse_WhenAssertingMapDoesNotContainDataStoreValue() throws Exception {
        final Map<String, String> dataStoreMap = new HashMap<String, String>() {
            {
                put("requiredKey", "requiredValue");
            }
        };
        final Map<String, String> assertingMap = new HashMap<String, String>() {
            {
                put("requiredKey", "someValue");
            }
        };
        final boolean isMapsMatch = stubMatcher.mapsMatch(dataStoreMap, assertingMap, "arbitrary template token name");

        assertThat(isMapsMatch).isFalse();
    }

    @Test
    public void mapsMatch_ShouldReturnTrue_WhenAssertingMapMatchesDataStoreMap() throws Exception {
        final Map<String, String> dataStoreMap = new HashMap<String, String>() {
            {
                put("requiredKey", "requiredValue");
            }
        };
        final Map<String, String> assertingMap = new HashMap<String, String>() {
            {
                put("requiredKey", "requiredValue");
            }
        };
        final boolean isMapsMatch = stubMatcher.mapsMatch(dataStoreMap, assertingMap, "arbitrary template token name");

        assertThat(isMapsMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenIsPostStubbedFalse() {
        final StubRequest stubbedRequest =
                new StubRequest.Builder().withMethod(HttpMethod.POST.asString()).build();
        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, null);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnFalse_WhenAssertingPostBodyNull() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("stubbed")
                .build();
        final StubRequest assertingRequest =
                new StubRequest.Builder().withMethod(HttpMethod.POST.asString()).build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isFalse();
    }

    @Test
    public void postBodiesMatch_ShouldReturnFalse_WhenAssertingBodyWhitespace() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("stubbed")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("  ")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isFalse();
    }

    @Test
    public void postBodiesMatch_ShouldReturnFalse_WhenDifferentJson() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"name\":\"tod\"}")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"name\":\"bob\"}")
                .withHeaderContentType("application/json")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isFalse();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentJson() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"c\":\"d\",\"a\":\"b\"}")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"a\":\"b\",\"c\":\"d\"}")
                .withHeaderContentType("application/json")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentJsonWithCustomContentType() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"c\":\"d\",\"a\":\"b\"}")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"a\":\"b\",\"c\":\"d\"}")
                .withHeaderContentType("something/vnd.com+json")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentJsonWithCustomContentTypeAndCharset() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"c\":\"d\",\"a\":\"b\"}")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("{\"a\":\"b\",\"c\":\"d\"}")
                .withHeaderContentType("something/vnd.com+json;charset=stuff")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnFalse_WhenDifferentXml() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml>two</xml>")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml>one</xml>")
                .withHeaderContentType("application/xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isFalse();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentXml() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml><b>b</b><a>a</a></xml>")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml><a>a</a><b>b</b></xml>")
                .withHeaderContentType("application/xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatchUsingVanillaRegex_ShouldReturnTrue() {
        final String stubbedXml = "<\\?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>"
                + ".*<idex:authority>(.+?)</idex:authority>.*<idex:startsWith>(.+?)</idex:startsWith>.*";

        final String postedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<idex:type xmlns:idex=\"http://idex.bbc.co.uk/v1\">\n"
                + "    <idex:authority>ALEX-1</idex:authority>\n"
                + "    <idex:name>ALEX-2</idex:name>\n"
                + "    <idex:startsWith>ALEX-3</idex:startsWith>\n"
                + "</idex:type>";

        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(stubbedXml)
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(postedXml)
                .withHeaderContentType("application/xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatchUsingComplexVanillaRegex_ShouldReturnTrue() {

        // Note:
        // 1. the '?' in <?xml are escaped as these are regex characters
        // 2. ths '[' in <![CDATA[(.*)]]> are escaped as these are regex characters
        final String stubbedXml = "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "     <VocabularyElement id=\"urn:epc:idpat:sgtin:(.*)\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">(.*)</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">(.*)</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">(.*)</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">(.*)</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">(.*)</attribute>\n"
                + "    </VocabularyElement>"
                + "    <name>(.*)</name>\n"
                + "    <age>(.*)</age>\n"
                + "    <!--\n"
                + "      Hello,\n"
                + "         I am a multi-line XML comment\n"
                + "         <staticText>\n"
                + "            <reportElement x=\"180\" y=\"0\" width=\"200\" height=\"20\"/>\n"
                + "            <text><!\\[CDATA\\[(.*)\\]\\]></text>\n"
                + "          </staticText>\n"
                + "      -->"
                + "    <homecity xmlns=\"(.*)cities\">\n"
                + "        <long>(.*)</long>\n"
                + "        <lat>(.*)</lat>\n"
                + "        <name>(.*)</name>\n"
                + "    </homecity>\n"
                + "    <one name=\"(.*)\" id=\"urn:company:namespace:type:id:one\">(.*)</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"(.*)\">(.*)</two>"
                + "    <three name=\"(.*)\" id=\"urn:company:namespace:type:id:(.*)\" >(.*)</three>"
                + "</person>";

        final String postedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "     <VocabularyElement id=\"urn:epc:idpat:sgtin:0652642.107340\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">Piramidon</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">Generic Pharma Co.</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">CAPSULE</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">125 mg</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">100</attribute>\n"
                + "    </VocabularyElement>"
                + "    <name>Rob</name>\n"
                + "    <age>37</age>\n"
                + "    <!--\n"
                + "      Hello,\n"
                + "         I am a multi-line XML comment\n"
                + "         <staticText>\n"
                + "            <reportElement x=\"180\" y=\"0\" width=\"200\" height=\"20\"/>\n"
                + "            <text><![CDATA[Hello World!]]></text>\n"
                + "          </staticText>\n"
                + "      -->"
                + "    <homecity xmlns=\"http://www.my.example.com/xml/cities\">\n"
                + "        <long>0.00</long>\n"
                + "        <lat>123.000</lat>\n"
                + "        <name>London has a lot of monkeys during summer</name>\n"
                + "    </homecity>\n"
                + "    <one name=\"elementOne\" id=\"urn:company:namespace:type:id:one\">ValueOne</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"elementTwo\">ValueTwo</two>"
                + "    <three name=\"elementThree\" id=\"urn:company:namespace:type:id:three\" >"
                + "        This       is a text   which span across a very     very Very       long line!"
                + "    </three>"
                + "</person>";

        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(stubbedXml)
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(postedXml)
                .withHeaderContentType("application/xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatchUsingXMLUnitRegexPlaceholder_ShouldReturnTrue() {
        final String stubbedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "     <VocabularyElement id=\"urn:epc:idpat:sgtin:0652642.107340\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">Piramidon</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">Generic Pharma Co.</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">CAPSULE</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">125 mg</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">100</attribute>\n"
                + "    </VocabularyElement>"
                + "    <name>Rob</name>\n"
                + "    <age>37</age>\n"
                + "    <!--\n"
                + "      Hello,\n"
                + "         I am a multi-line XML comment\n"
                + "         <staticText>\n"
                + "            <reportElement x=\"180\" y=\"0\" width=\"200\" height=\"20\"/>\n"
                + "            <text><![CDATA[Hello World!]]></text>\n"
                + "          </staticText>\n"
                + "      -->"
                + "    <homecity xmlns=\"http://www.my.example.com/xml/cities\">\n"
                + "        <long>0.00</long>\n"
                + "        <lat>123.000</lat>\n"
                + "        <name>${xmlunit.matchesRegex(.*monkeys.*)}</name>\n"
                + "    </homecity>\n"
                + "    <one name=\"elementOne\" id=\"urn:company:namespace:type:id:one\">ValueOne</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"elementTwo\">ValueTwo</two>"
                + "    <three name=\"elementThree\" id=\"urn:company:namespace:type:id:three\" >${xmlunit.matchesRegex(.*)}</three>"
                + "</person>";

        final String postedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "     <VocabularyElement id=\"urn:epc:idpat:sgtin:0652642.107340\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">Piramidon</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">Generic Pharma Co.</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">CAPSULE</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">125 mg</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">100</attribute>\n"
                + "    </VocabularyElement>"
                + "    <name>Rob</name>\n"
                + "    <age>37</age>\n"
                + "    <!--\n"
                + "      Hello,\n"
                + "         I am a multi-line XML comment\n"
                + "         <staticText>\n"
                + "            <reportElement x=\"180\" y=\"0\" width=\"200\" height=\"20\"/>\n"
                + "            <text><![CDATA[Hello World!]]></text>\n"
                + "          </staticText>\n"
                + "      -->"
                + "    <homecity xmlns=\"http://www.my.example.com/xml/cities\">\n"
                + "        <long>0.00</long>\n"
                + "        <lat>123.000</lat>\n"
                + "        <name>London has a lot of monkeys during summer</name>\n"
                + "    </homecity>\n"
                + "    <one name=\"elementOne\" id=\"urn:company:namespace:type:id:one\">ValueOne</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"elementTwo\">ValueTwo</two>"
                + "    <three name=\"elementThree\" id=\"urn:company:namespace:type:id:three\" >"
                + "        This       is a text   which span across a very     very Very       long line!"
                + "    </three>"
                + "</person>";

        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(stubbedXml)
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(postedXml)
                .withHeaderContentType("application/xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentXmlWithAttributes() {

        final String stubbedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "     <VocabularyElement id=\"urn:epc:idpat:sgtin:0652642.107340\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">Piramidon</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">Generic Pharma Co.</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">CAPSULE</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">125 mg</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">100</attribute>\n"
                + "    </VocabularyElement>"
                + "    <name>Rob</name>\n"
                + "    <age>37</age>\n"
                + "    <!--\n"
                + "      Hello,\n"
                + "         I am a multi-line XML comment\n"
                + "         <staticText>\n"
                + "            <reportElement x=\"180\" y=\"0\" width=\"200\" height=\"20\"/>\n"
                + "            <text><![CDATA[Hello World!]]></text>\n"
                + "          </staticText>\n"
                + "      -->"
                + "    <homecity xmlns=\"http://www.my.example.com/xml/cities\">\n"
                + "        <long>0.00</long>\n"
                + "        <lat>123.000</lat>\n"
                + "        <name>London</name>\n"
                + "    </homecity>\n"
                + "    <one name=\"elementOne\" id=\"urn:company:namespace:type:id:one\">ValueOne</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"elementTwo\">ValueTwo</two>"
                + "    <three name=\"elementThree\" id=\"urn:company:namespace:type:id:three\" >"
                + "        This       is a text   which span across a very     very Very       long line!"
                + "    </three>"
                + "</person>";

        final String postedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "    <age>37</age>\n"
                + "    <name>Rob</name>\n"
                + "    <one id=\"urn:company:namespace:type:id:one\" name=\"elementOne\" >ValueOne</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"elementTwo\">ValueTwo</two>"
                + "    <three id=\"urn:company:namespace:type:id:three\" name=\"elementThree\">"
                + "        This is a     text   which        span across a very very Very long line!"
                + "    </three>"
                + "    <homecity xmlns=\"http://www.my.example.com/xml/cities\">\n"
                + "        <name>London</name>\n"
                + "        <lat>123.000</lat>\n"
                + "        <long>0.00</long>\n"
                + "    </homecity>\n"
                + "    <VocabularyElement id=\"urn:epc:idpat:sgtin:0652642.107340\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">Piramidon</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">Generic Pharma Co.</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">CAPSULE</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">125 mg</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">100</attribute>\n"
                + "    </VocabularyElement>"
                + "</person>";

        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(stubbedXml)
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(postedXml)
                .withHeaderContentType("application/xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesDoNotMatch_ShouldReturnFalse_WhenDifferentXmlWithAttributes() {

        final String stubbedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "     <VocabularyElement id=\"urn:epc:idpat:sgtin:0652642.107340\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">Piramidon</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">Generic Pharma Co.</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">CAPSULE</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">125 mg</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">100</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:anotherProperty\">888</attribute>\n"
                + "    </VocabularyElement>"
                + "    <name>Rob</name>\n"
                + "    <age>37</age>\n"
                + "    <!--\n"
                + "      Hello,\n"
                + "         I am a multi-line XML comment\n"
                + "         <staticText>\n"
                + "            <reportElement x=\"180\" y=\"0\" width=\"200\" height=\"20\"/>\n"
                + "            <text><![CDATA[Hello World!]]></text>\n"
                + "          </staticText>\n"
                + "      -->"
                + "    <homecity xmlns=\"http://www.my.example.com/xml/cities\">\n"
                + "        <long>0.00</long>\n"
                + "        <lat>123.000</lat>\n"
                + "        <name>London</name>\n"
                + "    </homecity>\n"
                + "    <one name=\"elementOne\" id=\"urn:company:namespace:type:id:one\">ValueOne</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"elementTwo\">ValueTwo</two>"
                + "    <three name=\"elementThree\" id=\"urn:company:namespace:type:id:three\" >"
                + "        This       is a text   which span across a very     very Very       long line!"
                + "    </three>"
                + "</person>";

        final String postedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<person xmlns=\"http://www.your.example.com/xml/person\">\n"
                + "    <age>37</age>\n"
                + "    <name>Rob</name>\n"
                + "    <lastName>Schneider</lastName>\n"
                + "    <one id=\"urn:company:namespace:type:id:one\" name=\"elementOne\" >ValueOne</one>"
                + "    <two id=\"urn:company:namespace:type:id:two\" name=\"elementTwo\">ValueTwo</two>"
                + "    <three id=\"urn:company:namespace:type:id:three\" name=\"elementThree\">"
                + "        This is a     text   which        span across a very very Very long line!"
                + "    </three>"
                + "    <homecity xmlns=\"http://www.my.example.com/xml/cities\">\n"
                + "        <name>London</name>\n"
                + "        <lat>123.000</lat>\n"
                + "        <long>0.00</long>\n"
                + "    </homecity>\n"
                + "    <VocabularyElement id=\"urn:epc:idpat:sgtin:0652642.107340\">\n"
                + "         <attribute id=\"urn:epcglobal:product:drugName\">Piramidon</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:manufacturer\">Generic Pharma Co.</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:dosageForm\">CAPSULE</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:strength\">125 mg</attribute>\n"
                + "         <attribute id=\"urn:epcglobal:product:containerSize\">100</attribute>\n"
                + "    </VocabularyElement>"
                + "</person>";

        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(stubbedXml)
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost(postedXml)
                .withHeaderContentType("application/xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isFalse();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentXmlWithCustomContentType() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml><b>b</b><a>a</a></xml>")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml><a>a</a><b>b</b></xml>")
                .withHeaderContentType("something/vnd.com+xml")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentXmlWithCustomContentTypeAndCharset() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml><b>b</b><a>a</a></xml>")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("<xml><a>a</a><b>b</b></xml>")
                .withHeaderContentType("something/vnd.com+xml;charset=something")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenStringMatch() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wibble")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wibble")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnFalse_WhenStringNotMatch() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wobble")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wibble")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isFalse();
    }

    @Test
    public void postBodiesMatch_ShouldReturnTrue_WhenEquivalentStringWithInvalidContentType() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wibble")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wibble")
                .withHeaderContentType("whut")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isTrue();
    }

    @Test
    public void postBodiesMatch_ShouldReturnFalse_WhenNonEquivalentStringWithInvalidContentType() {
        final StubRequest stubbedRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wibble")
                .build();
        final StubRequest assertingRequest = new StubRequest.Builder()
                .withMethod(HttpMethod.POST.asString())
                .withPost("wobble")
                .withHeaderContentType("whut")
                .build();

        final boolean isBodiesMatch = stubMatcher.postBodiesMatch(stubbedRequest, assertingRequest);

        assertThat(isBodiesMatch).isFalse();
    }
}
