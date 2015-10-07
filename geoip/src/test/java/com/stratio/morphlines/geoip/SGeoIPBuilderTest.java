/**
 * Copyright (C) 2014 Stratio (http://stratio.com)
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
package com.stratio.morphlines.geoip;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineCompilationException;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by epeinado on 27/05/15.
 */

@RunWith(JUnit4.class)
public class SGeoIPBuilderTest {

    private static final String MORPH_OK_CONF = "/conf/basicConf.conf";
    private static final String MORPH_NOTOK_CONF = "/conf/notOkConf.conf";

    private Config configOk;

    private Config configNotOk;

    @Before
    public void setUp() throws IOException {
        configOk = parse(MORPH_OK_CONF).getConfigList("commands").get(0).getConfig("sgeoIP");
        configNotOk = parse(MORPH_NOTOK_CONF).getConfigList("commands").get(0).getConfig("sgeoIP");
    }

    protected Config parse(String file, Config... overrides) throws IOException {
        File tmpFile = File.createTempFile("morphlines_", ".conf");
        IOUtils.copy(getClass().getResourceAsStream(file), new FileOutputStream(tmpFile));
        Config config = new org.kitesdk.morphline.base.Compiler().parse(tmpFile, overrides);
        config = config.getConfigList("morphlines").get(0);
        Preconditions.checkNotNull(config);
        return config;
    }

    @Test
    public void testOk() throws IOException {
        final MorphlineContext context = new MorphlineContext.Builder().build();
        Collector collectorParent = new Collector();
        Collector collectorChild = new Collector();
        final Command command = new SGeoIPBuilder().build(configOk, collectorParent,
        collectorChild, context);

        Record record = new Record();
        record.put("log_host", "62.82.197.162");

        command.process(record);

        List<Record> records = collectorChild.getRecords();
        assertEquals(1, records.size());
        Record result = records.get(0);
        assertEquals(3, result.getFields().size());

        Assert.assertEquals(collectorChild.getFirstRecord().get("log_iso_code").get(0),
                "ES");
    }


    @Test(expected = MorphlineCompilationException.class)
    public void testNotOk() throws IOException {
        final MorphlineContext context = new MorphlineContext.Builder().build();
        Collector collectorParent = new Collector();
        Collector collectorChild = new Collector();
        final Command command = new SGeoIPBuilder().build(configNotOk, collectorParent,
                collectorChild, context);

        Record record = new Record();
        record.put("log_host", "62.82.197.162");

        command.process(record);
    }

    @Test
    public void testInvalidIP() throws IOException {
        final MorphlineContext context = new MorphlineContext.Builder().build();
        Collector collectorParent = new Collector();
        Collector collectorChild = new Collector();
        final Command command = new SGeoIPBuilder().build(configOk, collectorParent,
                collectorChild, context);

        Record record = new Record();
        record.put("log_host", "62.82.197");

        command.process(record);

        List<Record> records = collectorChild.getRecords();
        assertEquals(1, records.size());
        Record result = records.get(0);
        assertEquals(1, result.getFields().size());

        Assert.assertEquals(collectorChild.getFirstRecord().get("log_iso_code").size(),
                0);
    }

}
