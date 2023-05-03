package baylor.cloudhubs.prophetutils.contextmap;

public class Relatedness {

	private double score;
	private StringBuffer trace;
	private StringBuffer error;
	
	public Relatedness( double score ) {
		this.score = score;
		this.trace = new StringBuffer();
		this.error = new StringBuffer();
	}
	
	public Relatedness( double score, String trace, String error ) {
		this.score = score;
		this.trace = new StringBuffer(trace==null?"":trace);
		this.error = new StringBuffer(error==null?"":error);
	}
	
	/**
	 * @return the trace
	 */
	public String getTrace() {
		return trace.toString();
	}
	/**
	 * @param trace the trace to append
	 */
	public void appendTrace(String trace) {
		this.trace.append( trace );
	}
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error.toString();
	}

	/**
	 * @param error the error to append
	 */
	public void appendError(String error) {
		this.error.append( error );
	}
	
	
	
}