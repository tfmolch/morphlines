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
package com.stratio.morphlines.ldap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.typesafe.config.Config;

public class LDAPBuilder implements CommandBuilder {

    private final static String COMMAND_NAME = "parseLDAP";

    public Collection<String> getNames() {
        return Collections.singletonList(COMMAND_NAME);
    }

    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new LDAP(this, config, parent, child, context);
    }


    private static final class LDAP extends AbstractCommand {

        private final static String INPUT_FIELD = "input";

        private final String inputFieldName;

        protected LDAP(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            this.inputFieldName = getConfigs().getString(config, INPUT_FIELD);
        }

        @Override
        protected boolean doProcess(Record record) {
            String ldapValue = String.valueOf(record.getFirstValue(inputFieldName));

            Multimap<String,Object> multimap = ArrayListMultimap.create();
            try {
                LdapName dn = new LdapName(ldapValue);
                List<Rdn> rdns = dn.getRdns();
                for(Rdn rdn : rdns){
                    multimap.put(rdn.getType(), rdn.getValue());
                }
            } catch (InvalidNameException e) {
                e.printStackTrace();
            }

            Set<String> keys = multimap.keySet();
            for(String key : keys) {
                Collection<Object> values = multimap.get(key);
                if (values.size() > 1){
                    String commaSeparatedValue = Joiner.on(",").join(values);
                    record.put(key, commaSeparatedValue);
                } else {
                    String value = values.toArray()[0].toString();
                    record.put(key, value);
                }
            }

            // pass record to next command in chain:
            return super.doProcess(record);
        }
    }
}
