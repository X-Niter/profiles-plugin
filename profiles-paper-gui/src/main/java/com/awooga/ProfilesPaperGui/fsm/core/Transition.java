package com.awooga.ProfilesPaperGui.fsm.core;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Transition<S, E> {
	String from;
	String to;
	Class<? extends E> onEvent;
	OnTransitLambda<S, E> onTransit;
}
