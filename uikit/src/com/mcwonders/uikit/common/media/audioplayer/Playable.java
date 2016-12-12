package com.mcwonders.uikit.common.media.audioplayer;

public interface Playable {
	long getDuration();
	String getPath();
	boolean isAudioEqual(Playable audio);
}