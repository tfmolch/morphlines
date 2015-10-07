/**
 * Copyright (C) 2014 Stratio (http://stratio.com)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.morphlines.refererparser;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by eambrosio on 10/06/15.
 */
@RunWith(JUnit4.class)
public class ParserTest {

    private static final String URI_SAMPLE = "http://www.google.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari";
    private static final String PAGE = "www.example.com";
    private Parser parser;

    @Before
    public void setup() throws IOException {
        parser = new Parser();
    }

    @Test
    public void parseValidURI() throws Exception {
        final Referer parse = parser.parse(URI_SAMPLE, PAGE);
        assertThat(parse.medium).isEqualTo(Medium.SEARCH.toString());
        assertThat(parse.source).isEqualTo("Google");
        assertThat(parse.term).isEqualTo("gateway oracle cards denise linn");

    }

    @Test
    public void validParseWithInternalDomain() throws Exception {
        final Referer parse = parser.parse(new URI(URI_SAMPLE), PAGE, Arrays.asList("www.google.com"));

        assertThat(parse.medium).isEqualTo(Medium.INTERNAL.toString());
        assertThat(parse.source).isNull();
        assertThat(parse.term).isNull();

    }

    @Test
    public void validParseWithInternalDomainURI() throws Exception {
        final Referer parse = parser.parse("http://www.example.com/about", PAGE);

        assertThat(parse.medium).isEqualTo(Medium.INTERNAL.toString());
        assertThat(parse.source).isNull();
        assertThat(parse.term).isNull();
        assertThat(parse.toString()).isEqualTo("{medium: internal, source: null, term: null, campaign: null, content: null}");
    }

    @Test
    public void testLookupUtmParameters() throws Exception {

        final Referer parse = parser
                .parse("http://www.renault.es/gama-renault/renault-vehiculos-turismos/?utm_source=google&utm_medium=cpc&utm_campaign=ES-R-Car_Generics",
                        PAGE);

        assertThat(parse.source).isEqualTo("google");
        assertThat(parse.medium).isEqualTo(Medium.CPC.toString());
        assertThat(parse.campaign).isEqualToIgnoringCase("ES-R-Car_Generics");


    }
}