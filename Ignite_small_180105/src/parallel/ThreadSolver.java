package parallel;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class ThreadSolver implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			IloCplex cplex = new IloCplex();
			cplex.setOut(null);
			IloNumVar x1 = cplex.numVar(0, 3);
			IloNumVar x2 = cplex.numVar(0, 3);
			IloNumVar x3 = cplex.numVar(0, 3);

			IloNumExpr obj = cplex.numExpr();
			IloNumExpr tmp1 = cplex.sum(x1, cplex.sum(cplex.prod(2, x2), cplex.prod(3, x3)));
			IloNumExpr tmp2 = cplex.prod(cplex.sum(x1, cplex.prod(3, x2), cplex.sum(cplex.prod(5, x3), -7)), ParallelSolver.mult1);
			IloNumExpr tmp3 = cplex
					.prod(cplex.sum(cplex.prod(2, x1), cplex.prod(0.5, x2), cplex.sum(cplex.prod(5, x3), -8)), ParallelSolver.mult2);
			IloNumExpr tmp4 = cplex.prod(cplex.sum(cplex.prod(3, x1), cplex.prod(5, x2), cplex.sum(x3, -5)), ParallelSolver.mult3);
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
			ParallelSolver.surrogatesubgradient1 = m2_x1 + 3 * m2_x2 + 5 * m2_x3 - 7;
			ParallelSolver.surrogatesubgradient2 = 2 * m2_x1 + 1 / 2 * m2_x2 + 5 * m2_x3 - 8;
			ParallelSolver.surrogatesubgradient3 = 3 * m2_x1 + 5 * m2_x2 + m2_x3 - 5;
			// normsquared = surrogatesubgradient1 * surrogatesubgradient1
			// + surrogatesubgradient2 * surrogatesubgradient2 + surrogatesubgradient3 *
			// surrogatesubgradient3;
			ParallelSolver.normsquared = ParallelSolver.normsquared + ParallelSolver.surrogatesubgradient1 * ParallelSolver.surrogatesubgradient1
					+ ParallelSolver.surrogatesubgradient2 * ParallelSolver.surrogatesubgradient2 + ParallelSolver.surrogatesubgradient3 * ParallelSolver.surrogatesubgradient3;

			int M = 100;
			// double r = 0.05d;
			double r = 0.075d;
			ParallelSolver.step = (1 - 1 / M / Math.pow(ParallelSolver.k, 1 - 1 / Math.pow(ParallelSolver.k, r))) * ParallelSolver.oldstep
					* Math.sqrt(ParallelSolver.oldnormsquared / ParallelSolver.normsquared);

			ParallelSolver.oldstep = ParallelSolver.step;
			ParallelSolver.oldnormsquared = ParallelSolver.normsquared;

			ParallelSolver.mult1 = ParallelSolver.mult1 + (ParallelSolver.step * ParallelSolver.surrogatesubgradient1);
			ParallelSolver.mult2 = ParallelSolver.mult2 + (ParallelSolver.step * ParallelSolver.surrogatesubgradient2);
			ParallelSolver.mult3 = ParallelSolver.mult3 + (ParallelSolver.step * ParallelSolver.surrogatesubgradient3);

			System.out.println(
					Lagrangian + "	" + ParallelSolver.step + "	" + ParallelSolver.normsquared + "	" + ParallelSolver.mult1 + "	" + ParallelSolver.mult2 + "	" + ParallelSolver.mult3);
			cplex.end();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
