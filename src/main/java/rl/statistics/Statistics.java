package rl.statistics;

public class Statistics {

    private double avgWeightedCost;
    private long weightedCostSamples = 0;

    private double avgTotalReward;
    private long totalRewardSamples = 0;

    private double avgLearningDelta;
    private long learningDeltaSamples = 0;

    private long reconfigurations = 0;
    private long slaViolations = 0;

    private double avgInstances;
    private long instancesSamples = 0;

    public Statistics() {};

    public void submitWeightedCost (double cost) {
        double t = this.weightedCostSamples;
        this.avgWeightedCost = (t * this.avgWeightedCost + cost)/(t+1);
        this.weightedCostSamples = this.weightedCostSamples + 1;
    }

    public void submitTotalReward (double totalReward) {
        double t = this.totalRewardSamples;
        this.avgTotalReward = (t * this.avgTotalReward + totalReward)/(t+1);
        this.totalRewardSamples = this.totalRewardSamples + 1;
    }

    public void submitLearningDelta (double delta) {
        double t = this.learningDeltaSamples;
        this.avgLearningDelta = (t * this.avgLearningDelta + delta)/(t+1);
        this.learningDeltaSamples = this.learningDeltaSamples + 1;
    }

    public void submitInstances (int instances) {
        double t = this.instancesSamples;
        this.avgInstances = (t * this.avgInstances + instances)/(t+1);
        this.instancesSamples = this.instancesSamples + 1;
    }

    public void submitViolation () {
        this.slaViolations = this.slaViolations + 1;
    }

    public void submitReconfiguration () {
        this.reconfigurations = this.reconfigurations + 1;
    }

    public double getReconfigurationsPercent () {
        long cnt = this.reconfigurations;
        return 100.0*(double)cnt/(double) this.instancesSamples; /* instances_samples */
    }

    public double getSlaViolationPercent () {
        long cnt = this.slaViolations;
        return 100.0*(double)cnt/(double) this.instancesSamples; /* instances_samples*/

    }

}
