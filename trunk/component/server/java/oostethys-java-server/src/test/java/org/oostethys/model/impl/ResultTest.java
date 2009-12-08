package org.oostethys.model.impl;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.oostethys.model.Record;
import org.oostethys.model.Result;

public class ResultTest extends TestCase {
	Result result=null;
	protected void setUp() throws Exception {
		 result = new Result();
		Record rec = new Record();
		rec.setLat(36.7);
		rec.setLon(-122.4646);
		rec.setElevation(-30.0);
		rec.setMillisec(10);
		rec.addValue("100"); 
		rec.addValue("200"); 
		
		result.addRecord(rec);
		
		 rec = new Record();
		rec.setLat(36.7);
		rec.setLon(-122.4647);
		rec.setElevation(-40.0);
		rec.setMillisec(10);
		rec.addValue("300"); 
		rec.addValue("400"); 
		
		result.addRecord(rec);
		
		
	
		
		super.setUp();
	}
	
	public  Record createRecord(){
		Record  rec = new Record();
			rec.setLat(36.7+Math.random());
			rec.setLon(-122.4647+Math.random());
			rec.setElevation(-40.0+Math.random());
			rec.setMillisec(System.currentTimeMillis());
			rec.addValue("300"+Math.random()); 
			rec.addValue("400"+Math.random()); 
			return rec;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		result=null;
	}
	
	public void testTenThousand(){
		int i=0;
		while (i<10000){
			result.addRecord(createRecord());
			i++;
		}
		int total=result.getRecords().size();
		
		assertEquals(10002,total);
		System.out.println(result.getRecords().get(10));;
	}

	
// assuming defaults tokens : ',' and \r
	public void testgetResult(){
		ByteArrayOutputStream  out = new ByteArrayOutputStream();
		result.getResult(out);
		String s = out.toString();
		String expected = "36.7,-122.4646,-30.0,10,100,200\r36.7,-122.4647,-40.0,10,300,400";
		assertEquals(expected,s);
	}
	
	

}
