package rl.services;


public class MGNQueue {

    protected double servTimeMean;
    protected double servTimeVariance;

    public MGNQueue() {};

    public MGNQueue(double servTimeMean, double servTimeVariance) {
        this.servTimeMean = servTimeMean;
        this.servTimeVariance = servTimeVariance;

    }

    public double responseTime(int parallelism, double inputRateTps, double rho) {

        if (rho >= 1.0) {
            return 999999.0;
        }

        double lambdaTps = inputRateTps /(double)parallelism;
        double es2 = this.servTimeVariance + this.servTimeMean * this.servTimeMean;
        double r = this.servTimeMean + lambdaTps/2.0*es2/(1.0-rho);

        return r;
    }

    public double getTheoricalUtilization(int parallelism, double inputRateTps) {

        double lambdaTps = inputRateTps/(double)parallelism;
        double rho = lambdaTps * this.servTimeMean;
        return rho;
    }

    public double getServTimeMean() {
        return servTimeMean;
    }

    public double getServTimeVariance() {
        return servTimeVariance;
    }


}
