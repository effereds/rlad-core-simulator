package rl.utils;

import rl.actions.Action;
import rl.states.State;

public class QTable {
    public double[] table;
    private long nActions;

    public QTable(long states, long actions) {

        this.nActions = actions;
        this.table= new double[(int)(states*actions)];
    }

    private long hash(State s, Action a) {
//        System.out.println("hash a: " + a.hash() + ", s.hash: " +
//                s.hash() + ", hash tot: " + (a.hash() + nActions * s.hash()));
        return a.hash() + nActions * s.hash();
    }

    public double get(State s, Action a) {
        return this.table[(int)hash(s,a)];
    }

    public void set(State s, Action a, double value) {
        this.table[(int) hash(s,a)] = value;
    }
}
