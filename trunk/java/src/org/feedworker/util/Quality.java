package org.feedworker.util;

/**
 * 
 * @author luca
 */
public enum Quality {
	ALL("*"), NORMAL("normale"), FORM_720p("720p"), FORM_1080p("1080p"), BLURAY(
			"bluray"), DVDRIP("dvdrip"), HR("hr"), DIFF("\\");

	private String quality;

	private Quality(String q) {
		quality = q;
	}

	@Override
	public String toString() {
		return quality.toLowerCase();
	}
}