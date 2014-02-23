package com.KnappTech.sr.model.constants;

public enum InvestmentStatus {
	LONG,
	SHORT,
	CLOSED;

	public String deflate() {
		InvestmentStatus[] options = InvestmentStatus.values();
		for (int i = 0;i<options.length;i++){
			if (options[i] == this) {
				return String.valueOf(i);
			}
		}
		return "";
	}
	
	public static InvestmentStatus inflate(String portion) {
		InvestmentStatus[] options = InvestmentStatus.values();
		int i = Integer.parseInt(portion);
		return options[i];
	}
}
