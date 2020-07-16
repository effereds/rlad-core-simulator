package rl.services;

import java.util.ArrayList;

public class ServiceApplication {

    private ArrayList<Service> services = new ArrayList<>();
    private double latencySla;

    public ServiceApplication() {
    };

    public ServiceApplication(double latencySla) {
        this.latencySla = latencySla;
    }

    public ArrayList<Service> getServices() {
        return this.services;
    }

    public void addService (Service service) {
        this.services.add(service);
    }

    public void setLatencySla (double latencySla) {
        this.latencySla = latencySla;
    }

    public double responseTime(double lambda_tps)
    {
        double latency = 0.0;
        for (Service serv : this.services) {
            latency += serv.responseTime(lambda_tps);
        }
        return latency;
    }

    public boolean isSlaViolated(double lambda_tps) {

        return (responseTime(lambda_tps) > this.latencySla);
    }

    public int getTotalInstances() {

        return this.services.size() ;
    }

    public int getMaxInstances() {

        int instances = 0;
        for (Service serv : this.services)
            instances += serv.getMaxParallelism();
        return instances;
    }





}
