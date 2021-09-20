package com.awooga.ProfilesPaperGui.dao;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Builder(toBuilder = true)
@Value
public class ProfileEntity {
	Long id;
	UUID playerUuid;
	UUID profileUuid;
	boolean deleted;
	String cachedPlaceholderTitle;
	String cachedPlaceholderBody;
}
