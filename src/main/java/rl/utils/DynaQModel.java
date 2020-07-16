package rl.utils;

import rl.actions.Action;
import rl.states.State;

import java.util.ArrayList;


public class DynaQModel {
    public DynaQElement[] table;
    private long nActions;
    private ArrayList<Integer> obs;

    public DynaQModel(long states, long actions) {

        this.nActions = actions;
        this.table= new DynaQElement[(int)(states*actions)];
        this.obs = new ArrayList<>();
    }

    private long hash(State s, Action a) {
//        System.out.println("hash a of dynaq_model: " + a.hash() + ", s.hash: " +
//                s.hash() + ", hash tot dynaq_model: " + (a.hash() + nActions * s.hash()));
        return a.hash() + nActions * s.hash();
    }

    public DynaQElement get(State s, Action a) {
        return this.table[(int)hash(s,a)];

    }

    public void set(State s, Action a, State n_s, double value) {
        DynaQElement tmp = this.table[(int) hash(s,a)];
        if (tmp == null) {
            this.table[(int) hash(s,a)] = new DynaQElement(s, a, n_s, value);
            this.obs.add((int) hash(s,a));

        } else {
            tmp.setCost(value); //update cost
            tmp.setN_state(n_s); //update n_state
            tmp.setState(s); //state
            tmp.setAction(a); //action
        }
    }


    public DynaQElement randomDynaQElement() {
        int n = (int) ((Math.random() * this.obs.size()) % this.obs.size());
        int m = this.obs.get(n);
        return this.table[m];


    }
}
