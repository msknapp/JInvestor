package com.KnappTech.sr.model;

import com.KnappTech.model.KTObject;
//import com.KnappTech.model.ReportableSet;
import com.KnappTech.sr.model.comp.Currency;

public interface Asset extends KTObject
//ReportableSet 
{

	public Currency getValueOfShare();

}
