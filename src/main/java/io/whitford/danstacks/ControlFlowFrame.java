package io.whitford.danstacks;

public class ControlFlowFrame {
	private boolean conditionWasTrue;

	public ControlFlowFrame( final boolean conditionWasTrue ) {
		this.conditionWasTrue = conditionWasTrue;
	}

	public boolean isConditionWasTrue() {
		return conditionWasTrue;
	}
}
