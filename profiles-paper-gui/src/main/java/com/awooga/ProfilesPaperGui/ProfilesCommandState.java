package com.awooga.ProfilesPaperGui;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Builder(toBuilder=true)
@Value
public class ProfilesCommandState {
	String menuMode;
	UUID attemptDeleteUuid;
}
