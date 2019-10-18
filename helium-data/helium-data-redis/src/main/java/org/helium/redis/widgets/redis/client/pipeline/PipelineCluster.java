package org.helium.redis.widgets.redis.client.pipeline;

public class PipelineCluster {


    private PiplineSentinel piplineSentinel;

    public PipelineCluster(PiplineSentinel piplineSentinel) {
        this.piplineSentinel = piplineSentinel;
    }

    public PiplineSentinel getPipline() {
        return piplineSentinel;
    }

}
