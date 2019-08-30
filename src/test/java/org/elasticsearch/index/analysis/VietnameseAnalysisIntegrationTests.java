package org.elasticsearch.index.analysis;

import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.plugin.analysis.vi.AnalysisViPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;

import static org.hamcrest.Matchers.is;

public class VietnameseAnalysisIntegrationTests extends ESIntegTestCase {
    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singleton(AnalysisViPlugin.class);
    }

    public void testPluginIsLoaded() throws Exception {
        NodesInfoResponse response = client().admin().cluster().prepareNodesInfo().setPlugins(true).get();

        for (NodeInfo nodeInfo : response.getNodes()) {
            boolean pluginLoaded = nodeInfo.getPlugins().getPluginInfos()
                .stream().anyMatch(plugin -> plugin.getName().equals("analysis-vi"));

            assertThat(pluginLoaded, is(true));
        }
    }
}
