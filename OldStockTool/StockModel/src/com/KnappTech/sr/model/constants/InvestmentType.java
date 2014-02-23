package com.KnappTech.sr.model.constants;

public enum InvestmentType {
	STOCK,
	FUTURE,
	OPTION,
	FORCURR;
	
	public String deflate() {
		InvestmentType[] options = InvestmentType.values();
		for (int i = 0;i<options.length;i++){
			if (options[i] == this) {
				return String.valueOf(i);
			}
		}
		return "";
	}
	
	public static InvestmentType inflate(String portion) {
		InvestmentType[] options = InvestmentType.values();
		int i = Integer.parseInt(portion);
		return options[i];
	}
}
