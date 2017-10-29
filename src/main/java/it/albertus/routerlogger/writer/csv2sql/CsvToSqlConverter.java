package it.albertus.routerlogger.writer.csv2sql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import it.albertus.routerlogger.resources.Messages;
import it.albertus.util.IOUtils;
import it.albertus.util.sql.SqlUtils;

public class CsvToSqlConverter {

	protected static final String CSV_FILE_EXTENSION = ".csv";
	protected static final String SQL_FILE_EXTENSION = ".sql";

	private final DateFormat ansiSqlTimestampFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss.SSS"); // '1998-3-24 04:21:23.456'

	private final String sqlTableName;
	private final String sqlColumnNamesPrefix;
	private final String sqlTimestampColumnName;
	private final String sqlResponseTimeColumnName;
	private final int sqlMaxLengthColumnNames;
	private final String csvSeparator;
	private final DateFormat csvDateFormat;

	public CsvToSqlConverter(final String sqlTableName, final String sqlColumnNamesPrefix, final String sqlTimestampColumnName, final String sqlResponseTimeColumnName, final int sqlMaxLengthColumnNames, final String csvSeparator, final String csvTimestampPattern) {
		if (sqlTableName == null || sqlTableName.trim().isEmpty()) {
			throw new IllegalArgumentException("sqlTableName must not be blank");
		}
		if (csvSeparator == null || csvSeparator.isEmpty()) {
			throw new IllegalArgumentException("csvSeparator must not be empty");
		}
		this.sqlTableName = sqlTableName;
		this.sqlColumnNamesPrefix = sqlColumnNamesPrefix;
		this.sqlTimestampColumnName = sqlTimestampColumnName;
		this.sqlResponseTimeColumnName = sqlResponseTimeColumnName;
		this.sqlMaxLengthColumnNames = sqlMaxLengthColumnNames;
		this.csvSeparator = csvSeparator;
		this.csvDateFormat = new SimpleDateFormat(csvTimestampPattern);
	}

	public void convert(final File csvFile, final String destDir) throws ParseException, IOException {
		FileReader fr = null;
		LineNumberReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			final File sqlFile = getDestinationFile(csvFile, destDir);
			fr = new FileReader(csvFile);
			br = new LineNumberReader(fr);
			final String firstLine = br.readLine();
			final List<String> sqlColumnNames = new ArrayList<String>();
			if (firstLine != null) {
				final String[] csvColumnNames = firstLine.trim().split(csvSeparator);
				sqlColumnNames.add(getSqlColumnName(sqlTimestampColumnName, sqlColumnNamesPrefix, sqlMaxLengthColumnNames));
				sqlColumnNames.add(getSqlColumnName(sqlResponseTimeColumnName, sqlColumnNamesPrefix, sqlMaxLengthColumnNames));
				for (int i = 2; i < csvColumnNames.length; i++) {
					sqlColumnNames.add(getSqlColumnName(csvColumnNames[i], sqlColumnNamesPrefix, sqlMaxLengthColumnNames));
				}
				fw = new FileWriter(sqlFile);
				bw = new BufferedWriter(fw);
				String line;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty()) { // skip empty lines
						try {
							writeLine(line, bw, sqlColumnNames);
						}
						catch (final Exception e) {
							throw new IOException(Messages.get("err.csv2sql.runnable", csvFile, br.getLineNumber()), e);
						}
					}
				}
				bw.write("COMMIT;");
				bw.newLine();
			}
		}
		finally {
			IOUtils.closeQuietly(bw, fw, br, fr);
		}
	}

	protected void writeLine(final String csv, final BufferedWriter sql, final List<? extends CharSequence> tableColumnNames) throws IOException, ParseException {
		sql.append("INSERT INTO ").append(sqlTableName).append(" (");
		final String[] values = csv.split(csvSeparator);
		for (int i = 0; i < values.length; i++) {
			sql.append(tableColumnNames.get(i));
			if (i != values.length - 1) {
				sql.append(',');
			}
		}
		sql.append(") VALUES (TIMESTAMP '").append(ansiSqlTimestampFormat.format(csvDateFormat.parse(values[0]))).append("',").append(values[1]).append(',');
		for (int i = 2; i < values.length; i++) {
			sql.append('\'').append(values[i].replace("'", "''")).append('\'');
			if (i != values.length - 1) {
				sql.append(',');
			}
		}
		sql.append(");");
		sql.newLine();
	}

	protected File getDestinationFile(final File csvFile, final String destDir) throws IOException {
		final String csvFileName = csvFile.getName();
		final String sqlFileName;
		if (csvFileName.toLowerCase().endsWith(CSV_FILE_EXTENSION)) {
			sqlFileName = csvFileName.substring(0, csvFileName.lastIndexOf('.')) + SQL_FILE_EXTENSION;
		}
		else {
			sqlFileName = csvFileName + SQL_FILE_EXTENSION;
		}
		final File sqlFile = new File(destDir + File.separator + sqlFileName);
		if (sqlFile.exists() || sqlFile.isDirectory()) {
			throw new IOException(Messages.get("err.csv2sql.destination.exists", sqlFile));
		}
		return sqlFile;
	}

	protected String getSqlColumnName(final String name, final String prefix, final int maxLength) {
		String completeName = SqlUtils.sanitizeName(prefix + name);
		if (completeName.length() > maxLength) {
			completeName = completeName.substring(0, maxLength);
		}
		return completeName;
	}

}