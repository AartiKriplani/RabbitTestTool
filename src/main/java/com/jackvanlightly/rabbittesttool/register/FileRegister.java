package com.jackvanlightly.rabbittesttool.register;

import com.jackvanlightly.rabbittesttool.InstanceConfiguration;
import com.jackvanlightly.rabbittesttool.topology.model.Topology;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class FileRegister implements BenchmarkRegister {

    private FileWriter fileWriter;
    private PrintWriter printWriter;
    private LocalDateTime startTime;

    public FileRegister(String path) throws IOException {
        startTime = LocalDateTime.now();
        fileWriter = new FileWriter(path + "/" + startTime.toString() + ".log");
        printWriter = new PrintWriter(fileWriter);
    }

    @Override
    public void logBenchmarkStart(String runId,
                                  String technology,
                                  String version,
                                  InstanceConfiguration instanceConfig,
                                  Topology topology) {
        printWriter.println(MessageFormat.format("StartTime={0,time} {0,date},RunId={1},Tech={2},Version={3},Hosting={4},Instance={5},Volume={6}, Tenancy={7}",
                new Date(), runId, technology, version, instanceConfig.getHosting(), instanceConfig.getInstanceType(), instanceConfig.getVolume(), instanceConfig.getTenancy()));
        printWriter.flush();
    }

    @Override
    public void logLiveStatistics(String benchmarkId, int step, StepStatistics stepStatistics) {}

    @Override
    public void logBenchmarkEnd(String benchmarkId) {
        printWriter.println(MessageFormat.format("EndTime={0,time} {0,date}", new Date()));
        printWriter.flush();
        printWriter.close();
    }

    @Override
    public void logException(String benchmarkId, Exception e) {
        printWriter.println(e);
        printWriter.flush();
    }

    @Override
    public void logStepStart(String benchmarkId, int step, int durationSeconds, String stepValue) {
        printWriter.println(MessageFormat.format("Step {0} started", step));
        printWriter.flush();
    }

    @Override
    public void logStepEnd(String benchmarkId, int step, StepStatistics stepStatistics) {
        printWriter.println(MessageFormat.format("Step {0} ended with statistics:", step));
        printWriter.println(MessageFormat.format("    Duration seconds: {0}", stepStatistics.getDurationSeconds()));
        printWriter.println(MessageFormat.format("    Sent: {0}", stepStatistics.getSentCount()));
        printWriter.println(MessageFormat.format("    Received: {0}", stepStatistics.getReceivedCount()));

        StringBuilder latenciesSb = new StringBuilder();
        StringBuilder sendRatesSb = new StringBuilder();
        StringBuilder receiveRatesSb = new StringBuilder();
        String comma = ", ";

        for(int i = 0; i<stepStatistics.getLatencyPercentiles().length; i++) {
            if(i == stepStatistics.getLatencyPercentiles().length-1)
                comma = "";

            latenciesSb.append(stepStatistics.getLatencyPercentiles()[i] + "=" + stepStatistics.getLatencies()[i] + comma);
            sendRatesSb.append(stepStatistics.getLatencyPercentiles()[i] + "=" + stepStatistics.getSendRates()[i] + comma);
            receiveRatesSb.append(stepStatistics.getLatencyPercentiles()[i] + "=" + stepStatistics.getReceiveRates()[i] + comma);
        }

        printWriter.println("    Latencies: " + latenciesSb.toString());
        printWriter.println("    Send rates: " + sendRatesSb.toString());
        printWriter.println("    Receive rates: " + receiveRatesSb.toString());
        printWriter.println("");
        printWriter.flush();
    }

    @Override
    public List<StepStatistics> getStepStatistics(String runId, String technology, String version, String configTag) {
        return null;
    }

    @Override
    public InstanceConfiguration getInstanceConfiguration(String runId, String technology, String version, String configTag) {
        return null;
    }
}