package rl.states;

import rl.simulator.Configuration;

public class StateVerticalScaling extends State {
    public int cpu;
    public static int CPU_STATES = (int)(Configuration.CPU_MAX_VALUE/Configuration.CPU_DICRETIZATION_QUANTUM );

    public StateVerticalScaling () {
        super();
    }
    public StateVerticalScaling (int k, int util, int cpu) {
        super(k,util);
        this.cpu = cpu;
    }

    public int hash() {
        return ((int)(this.cpu/Configuration.CPU_DICRETIZATION_QUANTUM) -1) +
                this.util * CPU_STATES +UTIL_STATES *(this.k - 1) * CPU_STATES;
    }


    @Override
    public int hashCode() {
        return ((int)(this.cpu/Configuration.CPU_DICRETIZATION_QUANTUM) -1) +
                this.util * CPU_STATES +UTIL_STATES *(this.k - 1) * CPU_STATES;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof StateVerticalScaling))
            return false;

        StateVerticalScaling other = (StateVerticalScaling) obj;

        if (this.k == other.k &&
                this.util == other.util &&
                this.cpu == other.cpu)
            return true;
        return false;
    }
}
