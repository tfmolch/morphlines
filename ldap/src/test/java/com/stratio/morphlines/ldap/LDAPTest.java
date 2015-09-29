package com.stratio.morphlines.ldap;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

@RunWith(JUnit4.class)
public class LDAPTest {

    private static final String MORPH_OK_CONF = "/parseLDAP.conf";

    private Config configOk;

    @Before
    public void setUp() throws IOException {
        configOk = parse(MORPH_OK_CONF).getConfigList("commands").get(0).getConfig("parseLDAP");
    }

    @Test
    public void test(){
        final MorphlineContext context = new MorphlineContext.Builder().build();
        Collector collectorParent = new Collector();
        Collector collectorChild = new Collector();
        final Command command = new LDAPBuilder().build(configOk, collectorParent,
                collectorChild, context);

        Record record = new Record();
        record.put("field1", "cn=dsameuser,ou=DSAME Users,dc=pre,dc=company,dc=id");

        command.process(record);
        List<Record> records = collectorChild.getRecords();

        assertEquals(1, records.size());
        Record result = records.get(0);
        assertEquals(4, result.getFields().size());
        assertEquals(result.get("cn").get(0), "dsameuser");
    }

    protected Config parse(String file, Config... overrides) throws IOException {
        File tmpFile = File.createTempFile("morphlines_", ".conf");
        IOUtils.copy(getClass().getResourceAsStream(file), new FileOutputStream(tmpFile));
        Config config = new org.kitesdk.morphline.base.Compiler().parse(tmpFile, overrides);
        config = config.getConfigList("morphlines").get(0);
        Preconditions.checkNotNull(config);
        return config;
    }

    private void processRecords(Command command, List<Record> records){
        for(Record record : records){
            command.process(record);
        }
    }
}
