package rl.actions;

import java.util.ArrayList;

public class ActionVerticalAndHorizontalIterator {

    private static ArrayList<Action> actionIterator;
    private static ActionVerticalAndHorizontalIterator instance = null;

    protected ActionVerticalAndHorizontalIterator() {

    }

    public static ActionVerticalAndHorizontalIterator getInstance() {

        if (instance == null) {
            ArrayList<Action> tmp = new ArrayList<Action>();

            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.OUT_NULL));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.OUT_DOWN));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.OUT_UP));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.IN_NULL));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.IN_DOWN));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.IN_UP));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.NULL_NULL));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.NULL_UP));
            tmp.add(new ActionVerticalAndHorizontal(ActionVerticalAndHorizontal.NULL_DOWN));

            actionIterator = tmp;
            instance = new ActionVerticalAndHorizontalIterator();
        }
        return instance;
    }

    public ArrayList<Action> getActionIterator() {
        return actionIterator;
    }




}
