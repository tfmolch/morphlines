/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.morphlines.refererparser;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import com.stratio.morphlines.refererparser.exception.RefererParserException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.google.common.io.Resources;
import com.typesafe.config.Config;

/**
 * Created by eambrosio on 10/06/15.
 */
@RunWith(JUnit4.class)
public class RefererParserBuilderTest {

    private static final String MORPH_CONF_FILE = "refererParser.conf";
    private static final String REFERER_PARSER = "refererParser";
    private static final String COMMANDS = "commands";
    private Command command;
    private Config config;
    final MorphlineContext context = new MorphlineContext.Builder().build();
    Collector collectorParent = new Collector();
    Collector collectorChild = new Collector();

    @Before
    public void setUp() throws Exception {

        config = parse(MORPH_CONF_FILE).getConfigList(COMMANDS).get(0).getConfig(REFERER_PARSER);
        command = new RefererParserBuilder().build(config, collectorParent, collectorChild, context);
    }

    @Test
    public void checkRefererParserName() {
        assertThat(new RefererParserBuilder().getNames()).containsOnly("refererParser");
    }

    @Test
    public void processValidRecord() throws IOException {
        final Record record = buildMockRecord(
                "http://www.google.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari",
                "www.example.com");
        command.process(record);
        assertThat(record.get("source")).isNotNull();
        assertThat(record.get("medium")).isNotNull();
        assertThat(record.get("term")).isNotNull();
        assertThat(record.get("source")).containsExactly("Google");
        assertThat(record.get("medium")).containsExactly("search");
        assertThat(record.get("term")).containsExactly("gateway oracle cards denise linn");
    }

    @Test
    public void processValidRecordWithOrganicLink() throws IOException {
        final Record record = buildMockRecord(
                "https://www.google.es/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&cad=rja&uact=8&ved"
                        + "=0CDoQFjAAahUKEwjjxPHszYXGAhVuWtsKHXN7ALk&url=http%3A%2F%2Fwww.iberia.es%2F%3Flanguage%3Den&ei=E254VaPvC-607Qbz9oHICw&usg=AFQjCNGjcrV05itDtZci0GfQeTk63G6Ssw",
                "www.example.com");
        command.process(record);
        assertThat(record.get("source")).isNotNull();
        assertThat(record.get("medium")).isNotNull();
        assertThat(record.get("term")).isNotNull();
        assertThat(record.get("source")).containsExactly("Google");
        assertThat(record.get("medium")).containsExactly("search");
        assertThat(record.get("term")).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void processValidRecordWithPaidLink() throws IOException {
        final Record record = buildMockRecord(
                "http://www.renault.es/gama-renault/renault-vehiculos-turismos/?utm_source=google&utm_medium=cpc"
                        + "&utm_campaign=ES-R-Car_Generics",
                "www.example.com");
        command.process(record);
        assertThat(record.get("source")).isNotNull();
        assertThat(record.get("medium")).isNotNull();
        assertThat(record.get("term")).isNotNull();
        assertThat(record.get("source")).containsExactly("google");
        assertThat(record.get("medium")).containsExactly("cpc");
        assertThat(record.get("campaign")).containsExactly("ES-R-Car_Generics");
        assertThat(record.get("term")).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void processValidRecordWithPaidLink_2() throws IOException {
        final Record record = buildMockRecord(
                "http://yourdomain.com/yourproduct"
                        + ".html?utm_source=yoursite&utm_medium=postfooter&utm_campaign=product&utm_term=term1+term2"
                        + "+term3",
                "www.example.com");
        command.process(record);
        assertThat(record.get("source")).isNotNull();
        assertThat(record.get("medium")).isNotNull();
        assertThat(record.get("term")).isNotNull();
        assertThat(record.get("source")).containsExactly("yoursite");
        assertThat(record.get("medium")).containsExactly("postfooter");
        assertThat(record.get("campaign")).containsExactly("product");
        assertThat(record.get("term")).containsExactly("term1 term2 term3");
    }

    @Test
    public void processValidRecordWithSameHosts() throws IOException {
        final Record record = buildMockRecord("http://www.example.com/about", "www.example.com");
        command.process(record);
        assertThat(record.get("medium")).isNotNull();
        assertThat(record.get("source")).isNotNull();
        assertThat(record.get("term")).isNotNull();
        assertThat(record.get("medium")).containsExactly("internal");
        assertThat(record.get("source")).isEqualTo(Collections.EMPTY_LIST);
        assertThat(record.get("term")).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void processValidRecordWithInternalHosts() throws IOException {
        final Record record = buildMockRecord(
                "http://www.example2.com/about",
                "www.example.com");
        command.process(record);
        assertThat(record.get("medium")).isNotNull();
        assertThat(record.get("source")).isNotNull();
        assertThat(record.get("term")).isNotNull();
        assertThat(record.get("medium")).containsExactly("internal");
        assertThat(record.get("source")).isEqualTo(Collections.EMPTY_LIST);
        assertThat(record.get("term")).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void processValidRecordWithUnknownReferer() throws IOException {
        final Record record = buildMockRecord("http://www.unknown.com/about", "www.example.com");
        command.process(record);
        assertThat(record.get("medium")).isNotNull();
        assertThat(record.get("source")).isNotNull();
        assertThat(record.get("term")).isNotNull();
        assertThat(record.get("medium")).containsExactly("unknown");
        assertThat(record.get("source")).isEqualTo(Collections.EMPTY_LIST);
        assertThat(record.get("term")).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test(expected = RefererParserException.class)
    public void processInvalidURI() throws IOException {
        final Record record = buildMockRecord("(%·&·%!·%$!·$%", "");
        command.process(record);
    }


    protected Config parse(String file, Config... overrides) throws URISyntaxException, IOException {
        Config config = new org.kitesdk.morphline.base.Compiler().parse(
                new File(Resources.getResource(file).toURI()), overrides);
        config = checkNotNull(config.getConfigList("morphlines").get(0));
        return config;
    }

    private Record buildMockRecord(String referer, String request) throws IOException {
        Record newRecord = new Record();
        newRecord.put("referer", referer);
        newRecord.put("request", request);

        return newRecord;
    }

}