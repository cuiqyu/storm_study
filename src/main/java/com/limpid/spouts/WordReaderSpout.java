package com.limpid.spouts;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/**
 * @auther cuiqiongyu
 * @create 2019/3/19 15:32
 */
public class WordReaderSpout implements IRichSpout {

    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    private TopologyContext context;

    @Override
    public boolean isDistributed() {
        return false;
    }

    @Override
    public void ack(Object o) {
        System.out.println("OK:" + o);
    }

    @Override
    public void close() {

    }

    @Override
    public void fail(Object o) {
        System.out.println("FAIL:" + o);
    }

    /**
     * 这个方法做的惟一一件事情就是分发文件中的文本行
     */
    @Override
    public void nextTuple() {
        if (completed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            return;
        }

        String str;
        // 创建BufferedReader
        BufferedReader reader = new BufferedReader(fileReader);
        try {
            // 遍历读取所有的文本行
            while ((str = reader.readLine()) != null) {
                /**
                 * 按行发布一个新值
                 */
                this.collector.emit(new Values(str), str);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading tuple", e);
        } finally {
            completed = true;
        }

    }

    /**
     * 我们将创建一个文件并维持一个collector对象，第一个被调用
     *
     * @param map
     * @param topologyContext
     * @param spoutOutputCollector
     */
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        try {
            this.context = context;
            this.fileReader = new FileReader(map.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file [" + map.get("wordFile") + "]");
        }
        this.collector = collector;
    }

    /**
     * 声明输入域
     *
     * @param outputFieldsDeclarer
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("line"));
    }

}
