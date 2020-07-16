package rl.actions;

import java.util.Random;

public class Action {

    public static int ACTION_MAX_INSTANCE_DELTA = 1;
    private int instanceDelta;

    public Action(int instanceDelta) {
        this.instanceDelta = instanceDelta;
    }


    public Action() {
        new Action(0);
    }

    public static int N_ACTIONS() {
        return 1 + 2*ACTION_MAX_INSTANCE_DELTA;
    }

    public void setInstanceDelta(int instanceDelta) {
        this.instanceDelta = instanceDelta;
    }

    public int getInstanceDelta() {
        return this.instanceDelta;

    }

    public boolean isReconfiguration() {
        return false;
    }

    public static Action scaleOut() {
        return new Action(1);
    }

    public static Action scaleIn() {
        return new Action(-1);
    }

    public static Action none() {
        return new Action();
    }

    public static Action randomAction() {
        Random generator = new Random();
        int delta = (int) (generator.nextInt(10) % N_ACTIONS()) - ACTION_MAX_INSTANCE_DELTA;
        return new Action(delta);
    }

    public int hash()
    { return this. instanceDelta+1; }


    public boolean isEqual(Action other){
        return (this.instanceDelta == other.getInstanceDelta());
    }

    public String test() {
        return "test Action";
    }

}
