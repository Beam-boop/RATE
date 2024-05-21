package contrastexperiment.dp;

import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import utils.sg.smu.securecom.utils.Good;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class NsgaProblem extends AbstractBinaryProblem {
    private int bitLength;
    private int weightLimit;
    private List<Good> goods;

    public NsgaProblem(Integer numberOfVariables, Integer bitLength, ArrayList<Good> goods, int weightLimit) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(2);
        setName("NsgaProblem");

        this.bitLength = bitLength;
        this.weightLimit = weightLimit;
        this.goods = goods;
    }

    @Override
    protected int getBitsPerVariable(int i) {
        return bitLength;
    }

    @Override
    public BinarySolution createSolution() {
        return new DefaultBinarySolution(this);
    }

    @Override
    public int getNumberOfConstraints() {
        return 1;
    }

    public void evaluateConstraints(BinarySolution solution) {
        double totalWeight = 0;
        for (int i = 0; i < bitLength; i++) {
            if (solution.getVariableValue(0).get(i)) {
                totalWeight += goods.get(i).weight;
            }
        }
        double constraint = totalWeight - weightLimit;
        if (constraint > 0.0) {
            solution.setAttribute("violation", constraint);
        } else {
            solution.setAttribute("violation", 0.0);
        }
    }

    @Override
    public void evaluate(BinarySolution solution) {
        BitSet bitSet = solution.getVariableValue(0);
        double totalValue = 0;
        int totalItems = 0;
        double totalWeight = 0;

        for (int i = 0; i < bitLength; i++) {
            if (bitSet.get(i)) {
                double tentativeWeight = totalWeight + goods.get(i).weight;
                if (tentativeWeight <= weightLimit) {
                    totalValue += goods.get(i).value;
                    totalWeight += goods.get(i).weight;
                    totalItems++;
                }
            }
        }

        solution.setObjective(0, -1.0 * totalValue);
        solution.setObjective(1, -1.0 * totalItems);

        this.evaluateConstraints(solution);
    }
}