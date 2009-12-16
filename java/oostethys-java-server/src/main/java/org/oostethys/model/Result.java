package org.oostethys.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Result {
	private List<Record> records;
	private String tokenSeparator = ",";
	private String recordSeparator = "\r";

	public void addRecord(Record record) {
		if (records == null) {
			records = new ArrayList<Record>();
		}
		records.add(record);

	}

	public List<Record> getRecords() {
		return records;
	}

	/**
	 * Populates a list of records given a query
	 * 
	 * @param query
	 * @return
	 */
	public List<Record> getResultQuery(Query query) {
		Iterator<Record> recordsIterator = records.iterator();
		List<Record> filteredRecords = new ArrayList<Record>();
		while (recordsIterator.hasNext()) {
			Record rec = recordsIterator.next();
			if (isContainedTime(rec, query.getMinTime(), query.getMaxTime())) {
				if (isContainedInBBOX(rec, query.getMinLonBBOX(), query
						.getMinLatBBOX(), query.getMaxLonBBOX(), query
						.getMaxLatBBOX()))
					filteredRecords.add(rec);
			}
		}
		return filteredRecords;

	}

	public boolean isContainedTime(Record rec, long minTime, long maxTime) {
		return rec.getMillisec() >= minTime && rec.getMillisec() <= maxTime;
	}

	public boolean isContainedInBBOX(Record rec, double minLonBBOX,
			double minLatBBOX, double maxLonBBOX, double maxLatBBOX) {

		if (rec.getLat() >= minLatBBOX && rec.getLat() <= maxLatBBOX) {
			if (rec.getLon() >= minLonBBOX && rec.getLon() <= maxLonBBOX) {
				return true;
			}

		}
		return false;
	}

	public void getResult(OutputStream out) {
		StringBuffer buff = new StringBuffer();
		for (Iterator iterator = records.iterator(); iterator.hasNext();) {
			Record record = (Record) iterator.next();

			buff.append(record.getLat());
			buff.append(tokenSeparator);
			buff.append(record.getLon());
			buff.append(tokenSeparator);
			buff.append(record.getElevation());
			buff.append(tokenSeparator);
			buff.append(record.getMillisec());

			List<String> values = record.getValues();
			for (Iterator iterator2 = values.iterator(); iterator2.hasNext();) {
				String string = (String) iterator2.next();
				buff.append(tokenSeparator);
				buff.append(string);

			}
			if (iterator.hasNext()) {
				buff.append(recordSeparator);
			}

		}
		String s = buff.toString();
		try {
			byte[] bytes = s.getBytes();
			for (int i = 0; i < bytes.length; i++) {

				out.write(bytes[i]);
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
