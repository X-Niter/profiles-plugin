package com.awooga.ProfilesPaperGui.fsm;

@FunctionalInterface
public interface BoundUserFunction<U, S> {
	void onEvent(U user, String stateName, S stateData);
}
