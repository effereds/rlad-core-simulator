package rl.statistics;

import java.util.ArrayList;

public class AgentStatistics {

    private ArrayList<Integer> k = new ArrayList<>();
    private ArrayList<Double> utilizationPercent = new ArrayList<>();
    private ArrayList<Double> lambda_tps = new ArrayList<>();
    private ArrayList<Double> responseTime = new ArrayList<>();
    private ArrayList<Double> reward = new ArrayList<>();
    private ArrayList<Integer> delta = new ArrayList<> ();
    private ArrayList<Boolean> slaViolation = new ArrayList<> ();
    private ArrayList<Integer> cpu = new ArrayList<> ();


    public ArrayList<Integer> getCpu() {
        return cpu;
    }

    public void updateCpuIncrement (int cpu) {
        this.cpu.add(cpu);

    }

    public void updateSlaViolation (boolean bool) {
        this.slaViolation.add(bool);
    }

    public void updateKStatistic (int k) {
        this.k.add(k);
    }

    public void updateUtilizationPercentStatistic (double util) {
        this.utilizationPercent.add(util);
    }

    public void updateLambdaTpsStatistic (double lambda) {
        this.lambda_tps.add(lambda);
    }

    public void updateResponseTimeStatistic (double responseTime) {
        this.responseTime.add(responseTime);
    }

    public void updateRewardStatistic (double reward) {
        this.reward.add(reward);
    }

    public void updateDeltaStatistic (int delta) {
        this.delta.add(delta);
    }

    public ArrayList<Integer> getDeltaStatistic () {
        return this.delta;
    }

    public ArrayList<Integer> getKStatistic() {
        return k;
    }

    public ArrayList<Double> getUtilizationPercentStatistic() {
        return utilizationPercent;
    }

    public ArrayList<Double> getLambdaTpsStatistic() {
        return lambda_tps;
    }

    public ArrayList<Double> getResponseTimeStatistic() {
        return responseTime;
    }

    public ArrayList<Double> getRewardStatistic() {
        return reward;
    }

    public ArrayList<Boolean> getSlaViolation() {
        return slaViolation;
    }
}
