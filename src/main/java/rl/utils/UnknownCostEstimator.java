package rl.utils;

import rl.states.State;

import java.util.HashMap;

public class UnknownCostEstimator {

    private HashMap<State, Double> unknownCost;

    public UnknownCostEstimator(){
        this.unknownCost = new HashMap<>();
    }
    public UnknownCostEstimator(long numStates){
        this.unknownCost = new HashMap<>((int) numStates);
    }

    public double get(State state){

        Double value = this.unknownCost.get(state);

        if (value == null)
            return 0;

        return value;

    }

    public void update(State state, double value){

        this.unknownCost.put(state, value);

    }


}
