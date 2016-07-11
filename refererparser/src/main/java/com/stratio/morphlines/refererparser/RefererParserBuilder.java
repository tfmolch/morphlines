/*
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.stratio.morphlines.refererparser.exception.RefererParserException;
import com.typesafe.config.Config;

/**
 * Created by eambrosio@stratio.com
 */
public class RefererParserBuilder implements CommandBuilder {
    private static final String COMMAND_NAME = "refererParser";

    public Collection<String> getNames() {
        return Collections.singletonList(COMMAND_NAME);
    }

    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new RefererParser(this, config, parent, child, context);
    }

    private static final class RefererParser extends AbstractCommand {

        private static final String URI_INPUT_FIELD = "uri";
        private static final String PAGE_HOST_INPUT_FIELD = "pageHost";
        private static final String INTERNAL_DOMAINS_INPUT_FIELD = "internalDomains";
        private static final String SOURCE_OUTPUT_FIELD = "source";
        private static final String MEDIUM_OUTPUT_FIELD = "medium";
        private static final String TERM_OUTPUT_FIELD = "term";
        private static final String CAMPAIGN_OUTPUT_FIELD = "campaign";
        private static final String CONTENT_OUTPUT_FIELD = "content";
        private static final String DEFAULT_SOURCE_OUTPUT_FIELD = "source";
        private static final String DEFAULT_MEDIUM_OUTPUT_FIELD = "medium";
        private static final String DEFAULT_TERM_OUTPUT_FIELD = "term";
        private static final String DEFAULT_CAMPAIGN_OUTPUT_FIELD = "campaign";
        private static final String DEFAULT_CONTENT_OUTPUT_FIELD = "content";

        private String uriInputField;
        private String pageHostInputField;
        private String sourceOutputFiled;
        private String mediumOutputField;
        private String termOutputField;
        private String campaignOutputField;
        private String contentOutputField;
        private List<String> internalDomainsInputField;
        private Parser parser;

        protected RefererParser(CommandBuilder builder, Config config, Command parent,
                Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            this.uriInputField = getConfigs().getString(config, URI_INPUT_FIELD);
            this.pageHostInputField = getConfigs().getString(config, PAGE_HOST_INPUT_FIELD);
            this.sourceOutputFiled = getConfigs().getString(config, SOURCE_OUTPUT_FIELD, DEFAULT_SOURCE_OUTPUT_FIELD);
            this.mediumOutputField = getConfigs().getString(config, MEDIUM_OUTPUT_FIELD, DEFAULT_MEDIUM_OUTPUT_FIELD);
            this.termOutputField = getConfigs().getString(config, TERM_OUTPUT_FIELD, DEFAULT_TERM_OUTPUT_FIELD);
            this.campaignOutputField = getConfigs()
                    .getString(config, CAMPAIGN_OUTPUT_FIELD, DEFAULT_CAMPAIGN_OUTPUT_FIELD);
            this.contentOutputField = getConfigs()
                    .getString(config, CONTENT_OUTPUT_FIELD, DEFAULT_CONTENT_OUTPUT_FIELD);
            this.internalDomainsInputField = getConfigs().getStringList(config,
                    INTERNAL_DOMAINS_INPUT_FIELD, Collections.<String>emptyList());
            try {
                parser = new Parser();
            } catch (IOException e) {
                throw new RefererParserException("Resources file must be provided.", e);
            }
            validateArguments();
        }

        @Override
        protected boolean doProcess(Record record) {
            final Referer parse = parser.parse(getUri(record), getPageHost(record), internalDomainsInputField);
            putFieldIfNotNull(record, sourceOutputFiled, parse.source);
            putFieldIfNotNull(record, mediumOutputField, parse.medium);
            putFieldIfNotNull(record, termOutputField, parse.term);
            putFieldIfNotNull(record, campaignOutputField, parse.campaign);
            putFieldIfNotNull(record, contentOutputField, parse.content);
            return super.doProcess(record);
        }

        private void putFieldIfNotNull(Record record, String outputField, String fieldValue) {
            if (StringUtils.isNotBlank(fieldValue)) {
                record.put(outputField, fieldValue);
            }
        }

        private String getPageHost(Record record) {
            return (String) record.get(pageHostInputField).get(0);
        }

        private URI getUri(Record record) {
            URI uri = null;
            try {
                uri = new URI((String) record.get(uriInputField).get(0));
            } catch (URISyntaxException e) {
                throw new RefererParserException("You must provide a valid referer URI.", e);
            }
            return uri;
        }
    }
}
