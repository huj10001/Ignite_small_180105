import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Solver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IloCplex cplex;
		int iter_limit = 600000;
		double surrogatesubgradient1 = 0d;
		double surrogatesubgradient2 = 0d;
		double surrogatesubgradient3 = 0d;
		double mult1 = 0d;
		double mult2 = 0d;
		double mult3 = 0d;
		double normsquared = 100d;
		double oldnormsquared = 100d;
		double step = 0.05d;
		double oldstep = 0.05d;

		for (int k = 1; k < iter_limit; k++) {
			try {
				cplex = new IloCplex();
				cplex.setOut(null);

				IloNumVar x1 = cplex.numVar(0, 3);
				IloNumVar x2 = cplex.numVar(0, 3);
				IloNumVar x3 = cplex.numVar(0, 3);

				IloNumExpr obj = cplex.numExpr();
				IloNumExpr tmp1 = cplex.sum(x1, cplex.sum(cplex.prod(2, x2), x3));
				IloNumExpr tmp2 = cplex.prod(cplex.sum(x1, cplex.prod(3, x2), cplex.sum(cplex.prod(5, x3), -7)), mult1);
				IloNumExpr tmp3 = cplex.prod(
						cplex.sum(cplex.prod(2, x1), cplex.prod(0.5, x2), cplex.sum(cplex.prod(5, x3), -8)), mult2);
				IloNumExpr tmp4 = cplex.prod(cplex.sum(cplex.prod(3, x1), cplex.prod(5, x2), cplex.sum(x3, -5)), mult3);
				obj = cplex.sum(tmp1, tmp2, tmp3, tmp4);

				cplex.setParam(IloCplex.IntParam.IntSolLim, 2);
				cplex.setParam(IloCplex.IntParam.TimeLimit, 200);
				cplex.setParam(IloCplex.IntParam.NodeLim, 100000);
				cplex.setParam(IloCplex.DoubleParam.EpGap, 0.000001);
				cplex.setParam(IloCplex.DoubleParam.EpAGap, 0.000001);
				cplex.addMinimize(obj);
				cplex.solve();
				double Lagrangian = cplex.getObjValue();

				double m2_x1 = cplex.getValue(x1);
				double m2_x2 = cplex.getValue(x2);
				double m2_x3 = cplex.getValue(x3);
				surrogatesubgradient1 = m2_x1 + 3 * m2_x2 + 5 * m2_x3 - 7;
				surrogatesubgradient2 = 2 * m2_x1 + 1 / 2 * m2_x2 + 5 * m2_x3 - 8;
				surrogatesubgradient3 = 3 * m2_x1 + 5 * m2_x2 + m2_x3 - 5;
				normsquared = normsquared + surrogatesubgradient1 * surrogatesubgradient1
						+ surrogatesubgradient2 * surrogatesubgradient2 + surrogatesubgradient3 * surrogatesubgradient3;

				int M = 100;
				double r = 0.075d;
				step = (1 - 1 / M / Math.pow(k, 1 - 1 / Math.pow(k, r))) * oldstep
						* Math.sqrt(oldnormsquared / normsquared);

				oldstep = step;
				oldnormsquared = normsquared;

				mult1 = mult1 + (step * surrogatesubgradient1);
				mult2 = mult2 + (step * surrogatesubgradient2);
				mult3 = mult3 + (step * surrogatesubgradient3);

				System.out.println(
						Lagrangian + "	" + step + "	" + normsquared + "	" + mult1 + "	" + mult2 + "	" + mult3);
			} catch (IloException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
