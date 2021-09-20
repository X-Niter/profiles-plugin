package com.awooga.ProfilesPaperGui.fsm.core;

@FunctionalInterface
public interface OnTransitLambda<S, E> {
	S onTransit(String from, String to, S state, E event) throws Exception;
}