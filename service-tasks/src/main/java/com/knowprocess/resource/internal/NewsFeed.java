/*******************************************************************************
 *Copyright 2011-2018 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.knowprocess.resource.internal;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import com.knowprocess.opml.api.OpmlFeed;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class NewsFeed implements JavaDelegate {

    protected Map<Date, SyndEntry> getNewsByDate(String[] urls) {
        XmlReader reader = null;
        Map<Date, SyndEntry> aggregatedMap = new TreeMap<Date, SyndEntry>();

        try {
            for (String sUrl : urls) {
                try {
                    URL url = new URL(sUrl);
                    reader = new XmlReader(url);
                    SyndFeed feed = new SyndFeedInput().build(reader);
                    System.out.println("Feed Title: " + feed.getTitle());

                    for (Object o : feed.getEntries()) {
                        SyndEntry entry = (SyndEntry) o;
                        aggregatedMap.put(entry.getPublishedDate(), entry);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            ;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }
        return aggregatedMap;
    }

    private Map<String, String> getNews(String[] childUrls, int lastN,
            Date dateSince) {
        Map<String, String> resources = new HashMap<String, String>();
        Map<Date, SyndEntry> sortedNews = getNewsByDate(childUrls);
        int count = 0;
        for (Entry<Date, SyndEntry> entry : sortedNews.entrySet()) {
            if (count <= lastN
                    && entry.getValue().getPublishedDate().after(dateSince)) {
                List<?> encs = entry.getValue().getEnclosures();
                if (encs.size() > 0) {
                    resources.put(entry.getValue().getTitle(),
                            ((SyndEnclosure) encs.get(0)).getUrl());
                }
            } else {
                break;
            }
        }
        return resources;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        OpmlFeed opml = (OpmlFeed) execution.getVariable("opml");
        Integer tmp = (Integer) execution.getVariable("lastN");
        int lastN = (tmp == null ? 1 : tmp.intValue());
        Date dateSince = (Date) execution.getVariable("dateSince");
        if (dateSince == null) {
            dateSince = new GregorianCalendar(1970, Calendar.JANUARY, 1)
                    .getTime();
        }
        System.out.println(String.format(
                "Configured to fetch last %1$d later than %2$s", lastN,
                dateSince.toString()));
        Map<String, String> news = getNews(opml.getChildUrls(), lastN,
                dateSince);
        execution.setVariable("resources", news);
        // System.out.println("feeds: " + execution.getVariable("feeds"));
    }

}
