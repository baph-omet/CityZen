package io.github.griffenx.CityZen;

public enum ProtectionLevel {
	PUBLIC, COMMUNAL, PROTECTED;
	
	public static int getIndex(ProtectionLevel level) {
		for (int i = 0; i < ProtectionLevel.values().length; i++) {
			if (ProtectionLevel.values()[i] == level) return i;
		}
		return -1;
	}
}