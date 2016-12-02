package com.qfree.bo.report.birt.scripted;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Example of how to create a BIRT "scripted data source". The next() method of
 * this class returns {@link StockData} instances, each of which generates a
 * row of the data set.
 * 
 * @author Jeffrey Zelt
 */
public class StockScriptedDataSet {

	public List<StockData> getStockValues(String company) {
		// Ignore the company and always return the data
		// A real implementation would of course use the company string
		List<StockData> history = new ArrayList<>();
		// We fake the values, we will return fake value for 01.01.2009 -
		// 31.01.2009
		double begin = 2.5;
		for (int i = 1; i <= 31; i++) {
			Calendar day = Calendar.getInstance();
			day.set(Calendar.HOUR, 0);
			day.set(Calendar.MINUTE, 0);
			day.set(Calendar.SECOND, 0);
			day.set(Calendar.MILLISECOND, 0);
			day.set(Calendar.YEAR, 2009);
			day.set(Calendar.MONTH, 0);
			day.set(Calendar.DAY_OF_MONTH, i);
			StockData data = new StockData();
			data.setOpen(begin);
//			double close = Math.round(begin + Math.random() * begin * 0.1);
			double close = begin + Math.random() * begin * 0.1;
			data.setClose(close);
			data.setLow(Math.round(Math.min(begin, begin - Math.random() * begin * 0.1)));
			data.setHigh(Math.round(Math.max(begin, close) + Math.random() * 2));
			data.setVolume(1000 + (int) (Math.random() * 500));
			begin = close;
			data.setDate(day.getTime());
			history.add(data);
		}
		return history;
	}
	
	private Iterator<StockData> iterator;

	/* The "open" method will be called by BIRT engine once when the report 
	 * is invoked. It is a mandatory method.
	 */
	public void open(Object appContext, Map<String, Object> dataSetParamValues) {
		List<StockData> stockDataList = new StockScriptedDataSet().getStockValues("Not Used");
		this.iterator = stockDataList.iterator();
	}

	/* The "next" method is called by the BIRT engine once for each row of the
	 * data set . It is a mandatory method.
	 */
	public Object next() {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	/* The "close" method is called by the BIRT engine once at the end of the 
	 * report. It is a mandatory method.
	 */
	public void close() {
		this.iterator = null;
	}

} 