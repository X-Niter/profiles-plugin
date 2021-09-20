package com.awooga.ProfilesPaperGui.fsm;

import com.awooga.ProfilesPaperGui.fsm.core.DumbFSM;

@FunctionalInterface
public interface BoundFunction<S, E> {
	void onEvent(DumbFSM<S, E> fsm);
}
