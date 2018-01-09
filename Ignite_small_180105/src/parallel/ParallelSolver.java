package parallel;

import javax.imageio.IIOException;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class ParallelSolver {
	IloCplex cplex;
//	static int iter_limit = 600000;
	static int iter_limit = 10000;
	public static double surrogatesubgradient1 = 0d;
	public static double surrogatesubgradient2 = 0d;
	public static double surrogatesubgradient3 = 0d;
	public static double mult1 = 0d;
	public static double mult2 = 0d;
	public static double mult3 = 0d;
	public static double normsquared = 100d;
	public static double oldnormsquared = 100d;
	public static double step = 0.05d;
	public static double oldstep = 0.05d;
	static int numSolver = 3;
	public static int k;
	static ThreadSolver subSolver1 = new ThreadSolver();
	static ThreadSolver subSolver2 = new ThreadSolver();
	static ThreadSolver subSolver3 = new ThreadSolver();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (k = 1; k <= iter_limit; k++) {
			if (k % numSolver == 1) {
				subSolver1.run();
			}else if(k % numSolver == 2) {
				subSolver2.run();
			}else if(k % numSolver == 3) {
				subSolver3.run();
			}
		}
	}

}
