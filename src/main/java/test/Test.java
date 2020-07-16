package test;

import rl.actions.Action;
import rl.actions.ActionVerticalOrHorizontal;
import rl.states.State;
import rl.states.StateVerticalScaling;

import java.util.ArrayList;

public class Test {
    public static void main (String[] args) {
        ArrayList<Integer> array = new ArrayList<>();
        array.add(1);
        array.add(2);
        array.add(3);
        for(int i : array) {
            System.out.println(i);
        }
        System.out.println("Remove...");
        array.remove(0);
        array.add(4);
        array.add(5);
        for(int i : array) {
            System.out.println(i);
        }
        System.out.println("Remove...");
        array.remove(0);
        array.remove(0);
        array.add(6);
        array.add(7);
        for(int i : array) {
            System.out.println(i);
        }
        double c = 10;
        System.out.println((2*c)/100);

        State s1 = new StateVerticalScaling(1,1,10);
        State s2 = new StateVerticalScaling(1,1,10);
        Action a1 = new ActionVerticalOrHorizontal(1);
        Action a2 = new ActionVerticalOrHorizontal(1);
        if (s1.isEqual(s2) && a1.isEqual(a2)) {
            System.out.println(a1.getInstanceDelta());
        } else {
            System.out.println("no");
        }

    }

}
