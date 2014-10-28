package com.webex.qd.widget.tims;

import com.webex.qd.apiclient.HttpEngine;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.XPath;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-27
 * Time: 下午5:10
 */
public class TimsSearchRunner {

    public static void main(String args[]) {
        TimsSearchRunner runner = new TimsSearchRunner();
        runner.runSearches(new String[]{
                "Tyb14294s", "Tyb14297s", "Tyb14296s",
                "Tyb18421s", "Tyb18424s", "Tyb18425s",
                "Tyb14305s", "Tyb14308s", "Tyb14306s",
                "Tyb14314s", "Tyb14316s", "Tyb14315s",
                "Tyb14350s", "Tyb14352s", "Tyb14351s",
                "Tyb18049s", "Tyb18052s", "Tyb18053s"
        });
    }


    public Map<String, SearchResult> runSearches(String[] searchIds) {
        Map<String, SearchResult> results = new HashMap<String, SearchResult>();

        ExecutorService executor = Executors.newCachedThreadPool();
        List<TimsSearchTask> tasks = new LinkedList<TimsSearchTask>();
        for (String searchId : searchIds) {
            tasks.add(new TimsSearchTask(searchId));
        }

        try {
            List<Future<SearchResult>> futures = executor.invokeAll(tasks, 1, TimeUnit.MINUTES);
            for (Future<SearchResult> future : futures) {
                SearchResult hits = future.get();
                results.put(hits.getSearchId(), hits);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        return results;
    }



    private static class TimsSearchTask implements Callable<SearchResult> {
        private String searchId;

        public TimsSearchTask(String searchId) {
            this.searchId = searchId;
        }

        @Override
        public SearchResult call() throws Exception {
            return countSearchHits(searchId);
        }

        private SearchResult countSearchHits(String searchId) {
            HttpEngine engine = new HttpEngine();
            if (StringUtils.isBlank(searchId))
                return new SearchResult(searchId, -1);

            try {
                Document xml = engine.get("http://tims.cisex.com/xml/" + searchId + "/search.svc").getXmlDocument();
                Map<String, String> uris = new HashMap<String, String>();
                uris.put("tims", "http://tims.cisex.com/namespace");
                XPath xpath = xml.createXPath("//tims:SearchHit");
                xpath.setNamespaceURIs(uris);
                List searchHits = xpath.selectNodes(xml);
                return new SearchResult(searchId, searchHits.size());
            } catch (Exception e) {
                return new SearchResult(searchId, -1);
            }
        }
    }
}
