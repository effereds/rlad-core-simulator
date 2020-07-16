package rl.actions;

import rl.simulator.Configuration;

import java.util.Random;

public class ActionVerticalAndHorizontal extends Action{

    private int instanceDelta;
    private int instanceCpu;

    /* List of possible action */
    public static int OUT_DOWN = 0;     // {-1, -r} ->  scale out and scale down
    public static int OUT_NULL = 1;     // {-1,0} -> scale out
    public static int OUT_UP = 2;       // {-1, r} -> scale out and scale up
    public static int NULL_DOWN = 3;     // {0,-r} -> scale down
    public static int NULL_NULL = 4;     // {0,0} -> no horizontal and vertical scaling
    public static int NULL_UP = 5;      // {0, r} -> scale up
    public static int IN_DOWN = 6;      // {1, -r} -> scale in and scale down
    public static int IN_NULL = 7;      // {1, 0} -> scale in
    public static int IN_UP = 8;        // {1, r} -> scale in and scale up

    private int r = (int) Configuration.CPU_DICRETIZATION_QUANTUM; //cpu quantum
    private static int[] action = {OUT_DOWN, OUT_NULL, OUT_UP, NULL_DOWN, NULL_NULL, NULL_UP,
                                    IN_DOWN, IN_NULL, IN_UP};


    public ActionVerticalAndHorizontal(int instance) {

        if (instance == OUT_DOWN) {
            this.instanceCpu = -r;
            this.instanceDelta = -1;
        } else if (instance == OUT_NULL) {
            this.instanceCpu = 0;
            this.instanceDelta = -1;
        } else if (instance == OUT_UP) {
            this.instanceCpu = r;
            this.instanceDelta = -1;
        } else if (instance == NULL_DOWN) {
            this.instanceCpu = -r;
            this.instanceDelta = 0;
        } else if (instance == NULL_NULL) {
            this.instanceCpu = 0;
            this.instanceDelta = 0;
        } else if (instance == NULL_UP) {
            this.instanceDelta =0;
            this.instanceCpu = r;
        } else if (instance == IN_DOWN) {
            this.instanceDelta =1;
            this.instanceCpu = -r;
        } else if (instance == IN_NULL) {
            this.instanceDelta =1;
            this.instanceCpu =0;
        } else {
            this.instanceDelta = 1;
            this.instanceCpu = r;
        }
    }


    public ActionVerticalAndHorizontal() {
        new ActionVerticalAndHorizontal(NULL_NULL);
    }

    public static int N_ACTIONS() {
        return 9;
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


    public static ActionVerticalAndHorizontal randomAction() {
        Random generator = new Random();
        int index = (int) (generator.nextInt(10) % N_ACTIONS());
        int delta = action[index];
        return new ActionVerticalAndHorizontal(delta);
    }

    public int getInstanceCpu() {
        return instanceCpu;
    }

    public int hash() {
        if (instanceDelta == -1 && instanceCpu == -r) {
            return OUT_DOWN;

        }if (instanceCpu == 0 && instanceDelta == -1) {
            return OUT_NULL;

        } if (instanceDelta == -1 && instanceCpu == r) {
            return  OUT_UP;

        } if (instanceDelta == 0 && instanceCpu == -r) {
            return NULL_DOWN;

        } if (instanceCpu == 0 && instanceDelta == 0) {
            return  NULL_NULL;

        } if (instanceDelta == 0 && instanceCpu == r) {
            return NULL_UP;

        } if (instanceDelta == 1 && instanceCpu == -r) {
            return IN_DOWN;

        } if (instanceDelta == 1 && instanceCpu == 0) {
            return IN_NULL;
        }

        return IN_UP;

    }


    public boolean isEqual(Action other){
        ActionVerticalAndHorizontal o = (ActionVerticalAndHorizontal) other;
        return (this.instanceDelta == o.getInstanceDelta() && this.instanceCpu == o.getInstanceCpu());
    }

    public String test() {
        return "test 9 action model";
    }






}
