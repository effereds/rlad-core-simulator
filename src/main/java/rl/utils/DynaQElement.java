package rl.utils;

import rl.actions.Action;
import rl.states.State;

public class DynaQElement {

    private State state;
    private Action action;
    private State n_state;
    private double cost;


    public DynaQElement(State state, Action action, State n_state, double cost) {
        this.state = state;
        this.action = action;
        this.n_state = n_state;
        this.cost = cost;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public State getN_state() {
        return n_state;
    }

    public void setN_state(State n_state) {
        this.n_state = n_state;
    }
}
