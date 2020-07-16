package rl.utils;

public class Lambda {
    private int count;
    private double lastValue;
    private boolean up;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getLastValue() {
        return lastValue;
    }

    public void setLastValue(double lastValue) {
        this.lastValue = lastValue;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void updateCount() {
        this.count = count + 1;
    }

    public Lambda () {
        this.count = 0;
        this.lastValue = 0;
        this.up = true ;

    }
}
