package rl.services;

import rl.simulator.Configuration;

public class Service extends MGNQueue {

    protected int id;
    protected int maxParallelism;
    protected int instances;
    protected double targetResponseTime;
    protected double utilization;
    protected int cpu;

    public Service() {};

    public Service (int id, int maxParallelism) {
        this.id = id;
        this.instances = 1;
        this.maxParallelism = maxParallelism;
        this.utilization = 0;
        this.cpu = Configuration.INITIAL_VALUE;
    }

    public Service (int id, double servTimeMean, double servTimeVariance, int maxParallelism) {
            super(servTimeMean,servTimeVariance);
            this.id = id;
            this.instances = 1;
            this.maxParallelism = maxParallelism;
            this.utilization = 0;
            this.cpu = Configuration.INITIAL_VALUE;

    }

    public double responseTime(double inputRateTps) {
        return super.responseTime(this.instances, inputRateTps, this.utilization);
    }

    public double getUtilization () {
        return this.utilization;
    }


    public void updateUtilization (double inputRateTps) {
        this.utilization = super.getTheoricalUtilization(this.instances,inputRateTps);
    }

    public void adjustParallelism(int delta) {
        this.instances += delta;
        assert(instances > 0);
        assert(instances <= maxParallelism);
    }

    public int getCpu() {
        return cpu;
    }


    public void updateServiceTimeMean (int cpu) {
        this.cpu += cpu;
        double cpu_percent = ((double)this.cpu)/100;
        super.servTimeMean = 1/ (Configuration.MU * cpu_percent);
       // System.out.println("value of cpu: " +this.cpu);
        assert(this.cpu > 0);
        assert(this.cpu <= 100);

    }

    public int getId() {
        return id;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public int getInstances() {
        return instances;
    }


    public double getTargetResponseTime() {
        return targetResponseTime;
    }

    public void setTargetResponseTime (double responseTime) {
        this.targetResponseTime = responseTime;

    }

    public boolean isSlaViolated (double inputRateTps) {
        return (this.responseTime(inputRateTps) > this.targetResponseTime);
    }

    public double realUtil() {
        return this.utilization*Configuration.UTIL_DISCRETIZATION_QUANTUM;
    }




}
