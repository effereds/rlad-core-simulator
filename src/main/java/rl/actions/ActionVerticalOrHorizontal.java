package rl.actions;

import rl.simulator.Configuration;

import java.util.Random;

public class ActionVerticalOrHorizontal extends Action{

    public static int ACTION_MAX_INSTANCE_DELTA = 1;
    private int instanceDelta;
    private int instanceCpu;

    private int r = (int) Configuration.CPU_DICRETIZATION_QUANTUM; //cpu quantum

    //List of possible action
    public static int OUT_NULL = 0;     // {-1,0} -> scale out
    public static int NULL_NULL = 1;     // {0,0} -> no horizontal and vertical scaling
    public static int IN_NULL = 2;      // {1, 0} -> scale in
    public static int NULL_DOWN = 3;     // {0,-r} -> scale down
    public static int NULL_UP = 4;      // {0, r} -> scale up


    private static int[] action = {OUT_NULL, NULL_NULL, IN_NULL, NULL_DOWN, NULL_UP};

    public ActionVerticalOrHorizontal(int instance) {

         if (instance == OUT_NULL) {
             this.instanceCpu = 0;
             this.instanceDelta = -1;
         } else if (instance == NULL_DOWN) {
            this.instanceCpu = -r;
            this.instanceDelta = 0;
        } else if (instance == NULL_NULL) {
            this.instanceCpu = 0;
            this.instanceDelta = 0;
        } else if (instance == NULL_UP) {
             this.instanceDelta = 0;
             this.instanceCpu = r;
         } else {
            this.instanceDelta = 1;
            this.instanceCpu = 0;
        }
    }


    public ActionVerticalOrHorizontal() {
        new ActionVerticalOrHorizontal(NULL_NULL);
    }

    public static int N_ACTIONS() {
        return 3 + 2*ACTION_MAX_INSTANCE_DELTA;
    }

    public void setInstanceDelta(int instanceDelta) {
        this.instanceDelta = instanceDelta;
    }

    public int getInstanceDelta() {
        return this.instanceDelta;

    }

    public boolean isReconfiguration() {
        return (instanceCpu != 0);
    }


    public static ActionVerticalOrHorizontal randomAction() {
        Random generator = new Random();
        int index = (int) (generator.nextInt(10) % N_ACTIONS());
        int delta = action[index];
        return new ActionVerticalOrHorizontal(delta);
    }

    public int getInstanceCpu() {
        return instanceCpu;
    }

    public int hash() {
        if (instanceDelta != 0) {
            return instanceDelta + 1;
        }if (instanceCpu != 0) {
            if (instanceCpu == r) {
                return NULL_UP;
            } else {
                return NULL_DOWN;
            }
        } return NULL_NULL;

    }


    public boolean isEqual(Action other){
        ActionVerticalOrHorizontal o = (ActionVerticalOrHorizontal) other;
        return (this.instanceDelta == o.getInstanceDelta() && this.instanceCpu == o.getInstanceCpu());
    }

    public String test() {
        return "test 5 action model";
    }


}
