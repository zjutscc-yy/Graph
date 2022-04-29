package agent;

import structure.Graph;

import java.util.ArrayList;

public class GraphAgent extends AbstractAgent{

    /**
     * @param id  agent的name
     * @param bs  beliefs
     */
    public GraphAgent(String id, ArrayList<Belief> bs) {
        super(id, bs);
    }

    @Override
    public boolean deliberate() {
        return false;
    }

    @Override
    public void exeSucceed() {

    }

    @Override
    public void exeFail() {

    }
}
