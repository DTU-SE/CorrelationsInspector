package moderare.correlations2.loader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import moderare.correlations.utils.TypeDetector.FIELD_TYPE;
import moderare.correlations.utils.TypeDetector;
import moderare.correlations.utils.UnicodeBOMInputStream;
import moderare.correlations2.model.Dataset;
import moderare.correlations2.model.Record;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;
import com.opencsv.CSVParser;

public class CSVLoader implements Loader {
	
	private static final String[] CANDIDATE_FIELDS_SEPARATORS = {";", ",", "\t", "|"};
	public static final int LINES_FOR_AUTO_DETECT = 100;
	
	private List<List<String>> fieldsLog = new ArrayList<List<String>>();
	private List<FIELD_TYPE> fieldsType = new ArrayList<FIELD_TYPE>();
	private List<String> fieldsHeader = new ArrayList<String>();
	
	private String fileName;
	private Deque<String> linesLog = new ArrayDeque<String>();
	private int fieldsNumber = -1;
	private char fieldSeparator = '\0';
	private CSVParser parser;
	
	@Override
	public void loadFile(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public Dataset exportDataset() throws Exception {
		prepare();
		
		Dataset d = new Dataset();
		boolean headerParsed = false;
		for(List<String> line : fieldsLog) {
			if (!headerParsed) {
				headerParsed = true;
				continue;
			}
			
			Record r = new Record();
			for(int i = 0; i < fieldsNumber; i++) {
				String name = fieldsHeader.get(i);
				String value = line.get(i);
				
				if (fieldsType.get(i) == FIELD_TYPE.STRING) {
					r.addValue(name, value);
				} else if (fieldsType.get(i) == FIELD_TYPE.DOUBLE) {
					if (line.get(i).isEmpty()) {
						r.addValue(name, (Double) null);
					} else {
						r.addValue(name, Double.parseDouble(value));
					}
				}
			}
			d.add(r);
		}
		return d;
	}
	
	private void prepare() throws Exception {
		load();
		
		if(identifyFieldSeparator()) {
			parser = new CSVParser(fieldSeparator);
			splitLines();
		} else {
			throw new Exception("Cannot find proper field separator");
		}

		detectHeaders();
		detectFieldsType();
	}
	
	private void load() throws IOException {
		UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(new FileInputStream(fileName));
		ubis.skipBOM();
		InputStreamReader isr = new InputStreamReader(ubis);
		BufferedReader reader = new BufferedReader(isr);
		String readLine;
		while ((readLine = reader.readLine()) != null) {
			linesLog.add(readLine);
		}
		
		reader.close();
		isr.close();
	}
	
	private void splitLines() {
		String line;
		while((line = linesLog.poll()) != null) {
			line = CharMatcher.whitespace().trimFrom(line);
			if (line.isEmpty()) {
				continue;
			}
			try {
				List<String> toAdd = new ArrayList<String>();
				for (String i : Arrays.asList(parser.parseLine(line))) {
					toAdd.add(CharMatcher.whitespace().trimFrom(i));
				}
				fieldsLog.add(toAdd);
				fieldsNumber = (fieldsNumber == -1)? toAdd.size() : Math.min(fieldsNumber, toAdd.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void detectHeaders() {
		for(int i = 0; i < fieldsNumber; ++i) {
			String value = CharMatcher.whitespace().trimFrom(fieldsLog.get(0).get(i));
			if (TypeDetector.detectType(value) != FIELD_TYPE.STRING) {
				return;
			}
		}
		for(int i = 0; i < fieldsNumber; ++i ) {
			fieldsHeader.add(fieldsLog.get(0).get(i));
		}
	}
	
	private void detectFieldsType() {
		for(int i = 0; i < fieldsNumber; i++) {
			fieldsType.add(getFieldType(i));
		}
	}
	
	private FIELD_TYPE getFieldType(int fieldNo) {
		FIELD_TYPE t = FIELD_TYPE.STRING;
		boolean first = !fieldsHeader.isEmpty();
		int i = 0;
		for (List<String> l : fieldsLog) {
			if (i++ >= LINES_FOR_AUTO_DETECT) {
				break;
			}
			if (first) {
				first = false;
				continue;
			}
			t = TypeDetector.detectType(l.get(fieldNo));
		}
		return t;
	}
	
	private boolean identifyFieldSeparator() {
		for(String candidateSeparator : CANDIDATE_FIELDS_SEPARATORS) {
			int i = 0;
			double average = 0;
			double variance = 0;
			for (String line : linesLog) {
				if (i++ >= LINES_FOR_AUTO_DETECT) {
					break;
				}
				int x = StringUtils.countMatches(line, candidateSeparator);
				double delta = x - average;
				average = average + ((1.0 / i) * delta);
				variance = variance + delta * (x - average);
			}
			variance = variance / (i - 1);
			if (average > 0 && variance <= average * 0.1) {
				fieldSeparator = candidateSeparator.toCharArray()[0];
				return true;
			}
		}
		return false;
	}

}
