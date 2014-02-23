package com.KnappTech.util;

import org.junit.Assert;
import org.junit.Test;

import com.KnappTech.util.DataPoints;

public class DataPointsTest {
	private static double[] d00 = {0,1,2,3,4,5};
	private static double[] d01 = {2,3,5,4,5,7};
	private static double[] d1 = {1,2,3};
	private static double[] d2 = {0,2,4};
	private static double[] d3 = {1,1,1};
	private static double[] d4 = {0,0,0};
	
	@Test
	public void testGetVariance() {
		DataPoints dp1 = new DataPoints(d1,d1);
		double v1 = dp1.getVarianceY(false);
		Assert.assertTrue(v1>0.999 && v1<1.0001);
		DataPoints dp2 = new DataPoints(d1,d2);
		double v2 = dp2.getVarianceY(false);
		Assert.assertTrue(v2>3.999 && v2<4.001);
		DataPoints dp0 = new DataPoints(d1,d3);
		double v0 = dp0.getVarianceY(false);
		Assert.assertTrue(v0>-.0001 && v0<0.0001);
		DataPoints dp00 = new DataPoints(d00,d01);
		double v00 = dp00.getVarianceY(false);
		Assert.assertTrue(v00>3.066 && v00<3.067);
	}
	
	@Test
	public void testGetPopulationVariance() {
		DataPoints dp1 = new DataPoints(d1,d1);
		double v1 = dp1.getVarianceY(true);
		Assert.assertTrue(v1>0.666 && v1<0.667);
		DataPoints dp2 = new DataPoints(d1,d2);
		double v2 = dp2.getVarianceY(true);
		Assert.assertTrue(v2>2.666 && v2<2.667);
		DataPoints dp0 = new DataPoints(d1,d3);
		double v0 = dp0.getVarianceY(true);
		Assert.assertTrue(v0>-.0001 && v0<0.0001);
		DataPoints dp00 = new DataPoints(d00,d01);
		double v00 = dp00.getVarianceY(true);
		Assert.assertTrue(v00>2.555 && v00<2.556);
	}
	
	@Test
	public void testGetCovariance() {
		DataPoints dp1 = new DataPoints(d1,d1);
		double cv1 = dp1.getCovariance();
		Assert.assertTrue(cv1>0.666 && cv1<0.667);
		DataPoints dp2 = new DataPoints(d1,d2);
		double cv2 = dp2.getCovariance();
		Assert.assertTrue(cv2>1.333 && cv2<1.334);
		DataPoints dp0 = new DataPoints(d1,d3);
		double cv0 = dp0.getCovariance();
		Assert.assertTrue(cv0>-.0001 && cv0<0.0001);
		DataPoints dp00 = new DataPoints(d00,d01);
		double cv00 = dp00.getCovariance();
		Assert.assertTrue(cv00>2.4999 && cv00<2.5001);
	}
	
	@Test
	public void testGetSlope() {
		DataPoints dp1 = new DataPoints(d1,d1);
		double sl1 = dp1.getSlope();
		Assert.assertTrue(sl1==1);
		DataPoints dp2 = new DataPoints(d1,d2);
		double sl2 = dp2.getSlope();
		Assert.assertTrue(sl2==2);
		DataPoints dp0 = new DataPoints(d1,d3);
		double sl0 = dp0.getSlope();
		Assert.assertTrue(sl0==0);
		DataPoints dp00 = new DataPoints(d00,d01);
		double sl00 = dp00.getSlope();
		Assert.assertTrue(sl00>0.8571 && sl00<0.8572);
	}
	
	@Test
	public void testGetIntercept() {
		DataPoints dp1 = new DataPoints(d1,d1);
		double in1 = dp1.getIntercept();
		Assert.assertTrue(in1==0);
		DataPoints dp2 = new DataPoints(d1,d2);
		double in2 = dp2.getIntercept();
		Assert.assertTrue(in2==-2);
		DataPoints dp0 = new DataPoints(d1,d3);
		double in0 = dp0.getIntercept();
		Assert.assertTrue(in0==1);
		DataPoints dp00 = new DataPoints(d00,d01);
		double in00 = dp00.getIntercept();
		Assert.assertTrue(in00>2.19 && in00<2.2);
	}
}
