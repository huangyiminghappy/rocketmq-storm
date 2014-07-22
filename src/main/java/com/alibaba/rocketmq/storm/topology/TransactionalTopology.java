package com.alibaba.rocketmq.storm.topology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;
import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;

import com.alibaba.jstorm.local.LocalCluster;
import com.alibaba.rocketmq.storm.trident.RocketMQTridentSpout;

/**
 * @author Von Gosling
 */
public class TransactionalTopology {

    public static final Logger LOG = LoggerFactory.getLogger(TransactionalTopology.class);

    public static StormTopology buildTopology() {
        TridentTopology topology = new TridentTopology();
        RocketMQTridentSpout spout = new RocketMQTridentSpout();
        Stream stream = topology.newStream("rocketmq-txId", spout);
        stream.each(new Fields("message"), new BaseFilter() {
            private static final long serialVersionUID = -9056745088794551960L;

            @Override
            public boolean isKeep(TridentTuple tuple) {
                LOG.debug("Entering filter...");
                return true;
            }

        });
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        Config conf = new Config();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(String.valueOf(conf.get("topology.name")), conf, buildTopology());

        Thread.sleep(50000);

        cluster.shutdown();
    }
}