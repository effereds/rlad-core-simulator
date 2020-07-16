package rl.states;

import rl.simulator.Configuration;

public class State {
    public static int UTIL_STATES = (1 + (int)(Configuration.UTIL_MAX_VALUE/Configuration.UTIL_DISCRETIZATION_QUANTUM));

    public int k;      // parallelism
    public int util; // discretized utilization

    public State() {
    }

    public State(int k, int util) {
        this.k = k;
        this.util = util;
    }

    public boolean isEqual(State s) {
        return (this.k == s.k && this.util == s.util);
    }

    public static int discretizeUtil (double util_val)
    {
        if (util_val > Configuration.UTIL_MAX_VALUE)
            util_val = Configuration.UTIL_MAX_VALUE;

        return (int)((util_val / Configuration.UTIL_DISCRETIZATION_QUANTUM));
    }

    public double realUtil() {
        return this.util*Configuration.UTIL_DISCRETIZATION_QUANTUM;
    }

    public int hash() {
     //System.out.println("Util: " + this.util + ", "+ UTIL_STATES*(k-1));
        return this.util + UTIL_STATES*(k-1);
    }

    public int computeUtilization (double inputRateTps, int parallelism, int cpu) {

        double cpu_percent = ((double)cpu)/100;
        double servTimeMean = 1/ (Configuration.MU * cpu_percent);
        double lambdaTps = inputRateTps/(double)parallelism;
        double rho = lambdaTps * servTimeMean;
        return this.discretizeUtil(rho);
    }


    @Override
    public int hashCode() {
        return this.util + UTIL_STATES*(k-1);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof State))
            return false;

        State other = (State) obj;

        if (this.k == other.k &&
                this.util == other.util)
            return true;
        return false;
    }


}
