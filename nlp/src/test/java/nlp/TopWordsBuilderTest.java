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
package nlp;

import com.google.common.base.Preconditions;
import com.stratio.morphlines.nlp.TopWordsBuilder;
import com.typesafe.config.Config;
import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TopWordsBuilderTest {

	private static final String MORPH_OK_CONF = "/parseTopWords.conf";

	private Config configOk;

	@Before
	public void setUp() throws IOException {
		configOk = parse(MORPH_OK_CONF).getConfigList("commands").get(0).getConfig("topWords");
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
	public void test() {
		final MorphlineContext context = new MorphlineContext.Builder().build();
		Collector collectorParent = new Collector();
		Collector collectorChild = new Collector();
		final Command command = new TopWordsBuilder().build(configOk, collectorParent,
				collectorChild, context);

		Record record = new Record();
		record.put("input_field", "This is a test test test");
		record.put("language_field", "en");

		command.process(record);
		List<Record> records = collectorChild.getRecords();

		assertEquals(1, records.size());
		Record result = records.get(0);
		assertEquals(3, result.getFields().size());
		assertEquals(result.get("output_field").get(0), "test");
	}
}
