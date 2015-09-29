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

package com.stratio.morphlines.refererparser;

public class Referer {
    //    public final Medium medium;
    public final String medium, source, term, campaign, content;

    public Referer(Medium medium, String source, String term, String campaign, String content) {
        this.medium = medium.toString();
        this.source = source;
        this.term = term;
        this.campaign = campaign;
        this.content = content;
    }

    public Referer(String medium, String source, String term, String campaign, String content) {

            this.medium = medium;
            this.source = source;
            this.term = term;
            this.campaign = campaign;
            this.content = content;

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Referer)) {
            return false;
        }

        Referer r = (Referer) other;
        return ((this.medium != null && this.medium.equals(r.medium)) || this.medium == r.medium) &&
                ((this.source != null && this.source.equals(r.source)) || this.source == r.source) &&
                ((this.term != null && this.term.equals(r.term)) || this.term == r.term) &&
                ((this.campaign != null && this.campaign.equals(r.campaign)) || this.campaign == r.campaign) &&
                ((this.content != null && this.content.equals(r.content)) || this.content == r.content);
    }

    @Override
    public int hashCode() {
        int h = medium == null ? 0 : medium.hashCode();
        h += source == null ? 0 : source.hashCode();
        h += term == null ? 0 : term.hashCode();
        h += campaign == null ? 0 : campaign.hashCode();
        h += content == null ? 0 : content.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return String.format("{medium: %s, source: %s, term: %s, campaign: %s, content: %s}",
                medium, source, term, campaign, content);
    }
}
