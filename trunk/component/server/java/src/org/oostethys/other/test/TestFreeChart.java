package org.oostethys.other.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.keypoint.PngEncoder; 


import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class TestFreeChart {
	
	public static void main(String[] args) {
//		XYSeries series = new XYSeries("Average Size");
		
		TimeSeries closing = new TimeSeries( "Closing Value", Minute.class );
	
//		closing.add( new Day(new Date(1165006500000l), TimeZone.getTimeZone("Z")),29);
//		closing.add( new Day(new Date(1166006600000l), TimeZone.getTimeZone("Z")),30.3);
		closing.add( new Minute( 0,1,10, 5, 1971 ), 28.38 );
		closing.add( new Minute( 5,1,10, 5, 1971 ), 29.38 );
		closing.add( new Minute( 10,1,11, 5, 1971 ), 30.38 );
//		closing.add( new Day( 7, 10, 2004 ), 28.17 );
//		closing.add( new Day( 8, 10, 2004 ), 27.99 );
		
		
			
		String title ="salinity values";
		String timeAxisLabel ="time";
		String valueAxisLabel ="salinity(psu)";
		boolean legend =true;
		
		boolean tooltips = true;
		boolean urls = false;
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries( closing );
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset, legend, tooltips, urls);
		
		BufferedImage image =  chart.createBufferedImage(300, 200);
		
//		BufferedImage image = new BufferedImage( 300, 200, BufferedImage.TYPE_INT_RGB ); 
//		Graphics2D g = image.createGraphics(); 
//		g.setColor( Color.white ); 
//		g.fillRect(0, 0, 300, 200 ); 
//		g.setColor( Color.orange ); 
//		g.drawRect(0, 0, 290, 190 ); 
//		g.setBackground( Color.lightGray ); 
		/*g.setColor( Color.blue ); 
		g.fillRect(100, 50, 400, 400 ); 
		g.setColor( Color.red ); 
		g.drawOval( 150, 100, 300, 100 );*/ 

//		Rectangle drawArea = new Rectangle( 300, 200 ); 
//		Plot myPlot = chart.getPlot(); 
//		myPlot.draw( g, drawArea,null,null,null ); 
		
		PngEncoder encoder = new PngEncoder(image);

		try	{ 
		FileOutputStream fos = new FileOutputStream( "/Users/bermudez/test.png" ); 
		BufferedOutputStream bos = new BufferedOutputStream( fos ); 
		bos.write(encoder.pngEncode());
		
//		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( bos ) ; 
//		encoder.encode( image ); 
		bos.close(); 
		} catch( FileNotFoundException e ) { 
		System.out.println( "file not found " + e.getMessage() ); 
		System.exit(1); 
		} catch( IOException e ) { 
		System.out.println( "IOException " + e.getMessage() ); 
		System.exit(1); 
		} 
             
		                     
	}

}
