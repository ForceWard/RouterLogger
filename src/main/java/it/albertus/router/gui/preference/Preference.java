package it.albertus.router.gui.preference;

import it.albertus.router.console.RouterLoggerConsole;
import it.albertus.router.engine.RouterLoggerConfiguration;
import it.albertus.router.engine.RouterLoggerEngine;
import it.albertus.router.gui.CloseMessageBox;
import it.albertus.router.gui.DataTable;
import it.albertus.router.gui.RouterLoggerGui;
import it.albertus.router.gui.TextConsole;
import it.albertus.router.gui.TrayIcon;
import it.albertus.router.gui.preference.field.FormattedIntegerFieldEditor;
import it.albertus.router.gui.preference.field.FormattedStringFieldEditor;
import it.albertus.router.gui.preference.field.ScaleFormattedIntegerFieldEditor;
import it.albertus.router.gui.preference.field.ThresholdsFieldEditor;
import it.albertus.router.gui.preference.page.Page;
import it.albertus.router.reader.AsusDslN12EReader;
import it.albertus.router.reader.AsusDslN14UReader;
import it.albertus.router.reader.DLinkDsl2750Reader;
import it.albertus.router.reader.Reader;
import it.albertus.router.reader.TpLink8970Reader;
import it.albertus.router.resources.Resources;
import it.albertus.router.util.Logger;
import it.albertus.router.writer.CsvWriter;
import it.albertus.router.writer.DatabaseWriter;
import it.albertus.util.NewLine;

import java.util.Locale;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;

public enum Preference {
	LANGUAGE(Page.GENERAL, ComboFieldEditor.class, Locale.getDefault().getLanguage(), getLanguageOptions()),

	READER_CLASS_NAME(Page.READER, FormattedStringFieldEditor.class, TpLink8970Reader.class.getSimpleName()),
	ROUTER_USERNAME(Page.READER, FormattedStringFieldEditor.class),
	ROUTER_PASSWORD(Page.READER, FormattedStringFieldEditor.class),
	ROUTER_ADDRESS(Page.READER, FormattedStringFieldEditor.class, Reader.Defaults.ROUTER_ADDRESS),
	ROUTER_PORT(Page.READER, FormattedIntegerFieldEditor.class, Integer.toString(Reader.Defaults.ROUTER_PORT), 5),

	SOCKET_TIMEOUT_MS(Page.READER, FormattedIntegerFieldEditor.class, Integer.toString(Reader.Defaults.SOCKET_TIMEOUT_IN_MILLIS), Integer.toString(Integer.MAX_VALUE).length() - 1),
	CONNECTION_TIMEOUT_MS(Page.READER, FormattedIntegerFieldEditor.class, Integer.toString(Reader.Defaults.CONNECTION_TIMEOUT_IN_MILLIS), Integer.toString(Integer.MAX_VALUE).length() - 1),
	TELNET_NEWLINE_CHARACTERS(Page.READER, ComboFieldEditor.class, Reader.Defaults.TELNET_NEWLINE_CHARACTERS, getNewLineOptions()),

	LOGGER_ITERATIONS(Page.GENERAL, FormattedIntegerFieldEditor.class, Integer.toString(RouterLoggerEngine.Defaults.ITERATIONS), Integer.toString(Integer.MAX_VALUE).length() - 1),
	LOGGER_INTERVAL_NORMAL_MS(Page.GENERAL, ScaleFormattedIntegerFieldEditor.class, Long.toString(RouterLoggerEngine.Defaults.INTERVAL_NORMAL_IN_MILLIS), new int[] { 0, 15000, 1, 1000 }),
	LOGGER_INTERVAL_FAST_MS(Page.GENERAL, ScaleFormattedIntegerFieldEditor.class, Long.toString(RouterLoggerEngine.Defaults.INTERVAL_FAST_IN_MILLIS), new int[] { 0, 15000, 1, 1000 }),
	LOGGER_HYSTERESIS_MS(Page.GENERAL, FormattedIntegerFieldEditor.class, Long.toString(RouterLoggerEngine.Defaults.HYSTERESIS_IN_MILLIS), Integer.toString(Integer.MAX_VALUE).length() - 1),
	LOGGER_RETRY_COUNT(Page.GENERAL, FormattedIntegerFieldEditor.class, Integer.toString(RouterLoggerEngine.Defaults.RETRIES), Integer.toString(Integer.MAX_VALUE).length() - 1),
	LOGGER_RETRY_INTERVAL_MS(Page.GENERAL, FormattedIntegerFieldEditor.class, Long.toString(RouterLoggerEngine.Defaults.RETRY_INTERVAL_IN_MILLIS), Integer.toString(Integer.MAX_VALUE).length() - 1),
	LOGGER_ERROR_LOG_DESTINATION_PATH(Page.GENERAL, DirectoryFieldEditor.class),

	TPLINK_8970_COMMAND_INFO_ADSL(Page.TPLINK_8970, FormattedStringFieldEditor.class, TpLink8970Reader.Defaults.COMMAND_INFO_ADSL),
	TPLINK_8970_COMMAND_INFO_WAN(Page.TPLINK_8970, FormattedStringFieldEditor.class),
	ASUS_DSLN12E_COMMAND_INFO_ADSL(Page.ASUS_N12E, FormattedStringFieldEditor.class, AsusDslN12EReader.Defaults.COMMAND_INFO_ADSL),
	ASUS_DSLN12E_COMMAND_INFO_WAN(Page.ASUS_N12E, FormattedStringFieldEditor.class, AsusDslN12EReader.Defaults.COMMAND_INFO_WAN),
	ASUS_DSLN14U_COMMAND_INFO_ADSL(Page.ASUS_N14U, FormattedStringFieldEditor.class, AsusDslN14UReader.Defaults.COMMAND_INFO_ADSL),
	ASUS_DSLN14U_COMMAND_INFO_WAN(Page.ASUS_N14U, FormattedStringFieldEditor.class),
	DLINK_2750_COMMAND_INFO_ADSL_STATUS(Page.DLINK_2750, FormattedStringFieldEditor.class, DLinkDsl2750Reader.Defaults.COMMAND_INFO_ADSL_STATUS),
	DLINK_2750_COMMAND_INFO_ADSL_SNR(Page.DLINK_2750, FormattedStringFieldEditor.class, DLinkDsl2750Reader.Defaults.COMMAND_INFO_ADSL_SNR),

	GUI_TABLE_ITEMS_MAX(Page.APPEARANCE, FormattedIntegerFieldEditor.class, Integer.toString(DataTable.Defaults.GUI_TABLE_MAX_ITEMS), 4),
	GUI_CONSOLE_MAX_CHARS(Page.APPEARANCE, FormattedIntegerFieldEditor.class, Integer.toString(TextConsole.Defaults.GUI_CONSOLE_MAX_CHARS), 6),
	GUI_TABLE_COLUMNS_PACK(Page.APPEARANCE, BooleanFieldEditor.class, Boolean.toString(DataTable.Defaults.GUI_TABLE_COLUMNS_PACK)),
	GUI_MINIMIZE_TRAY(Page.APPEARANCE, BooleanFieldEditor.class, Boolean.toString(TrayIcon.Defaults.GUI_MINIMIZE_TRAY)),
	GUI_START_MINIMIZED(Page.APPEARANCE, BooleanFieldEditor.class, Boolean.toString(RouterLoggerGui.Defaults.GUI_START_MINIMIZED)),
	GUI_TRAY_TOOLTIP(Page.APPEARANCE, BooleanFieldEditor.class, Boolean.toString(TrayIcon.Defaults.GUI_TRAY_TOOLTIP)),
	GUI_CONFIRM_CLOSE(Page.APPEARANCE, BooleanFieldEditor.class, Boolean.toString(CloseMessageBox.Defaults.GUI_CONFIRM_CLOSE)),
	GUI_IMPORTANT_KEYS(Page.APPEARANCE, FormattedStringFieldEditor.class),
	GUI_IMPORTANT_KEYS_SEPARATOR(Page.APPEARANCE, FormattedStringFieldEditor.class, RouterLoggerConfiguration.Defaults.GUI_IMPORTANT_KEYS_SEPARATOR),

	CONSOLE_ANIMATION(Page.CONSOLE, BooleanFieldEditor.class, Boolean.toString(RouterLoggerConsole.Defaults.CONSOLE_ANIMATION)),
	CONSOLE_SHOW_CONFIGURATION(Page.GENERAL, BooleanFieldEditor.class, Boolean.toString(RouterLoggerEngine.Defaults.CONSOLE_SHOW_CONFIGURATION)),
	CONSOLE_SHOW_KEYS(Page.CONSOLE, FormattedStringFieldEditor.class),
	CONSOLE_SHOW_KEYS_SEPARATOR(Page.CONSOLE, FormattedStringFieldEditor.class, RouterLoggerConfiguration.Defaults.CONSOLE_SHOW_KEYS_SEPARATOR),
	CONSOLE_DEBUG(Page.GENERAL, BooleanFieldEditor.class, Boolean.toString(Logger.Defaults.DEBUG)),

	WRITER_CLASS_NAME(Page.WRITER, FormattedStringFieldEditor.class, RouterLoggerEngine.Defaults.WRITER_CLASS.getSimpleName()),

	CSV_DESTINATION_PATH(Page.CSV, DirectoryFieldEditor.class),
	CSV_NEWLINE_CHARACTERS(Page.CSV, ComboFieldEditor.class, CsvWriter.Defaults.NEW_LINE.name(), getNewLineOptions()),
	CSV_FIELD_SEPARATOR(Page.CSV, FormattedStringFieldEditor.class, CsvWriter.Defaults.FIELD_SEPARATOR),
	CSV_FIELD_SEPARATOR_REPLACEMENT(Page.CSV, FormattedStringFieldEditor.class, CsvWriter.Defaults.FIELD_SEPARATOR_REPLACEMENT),

	DATABASE_DRIVER_CLASS_NAME(Page.DATABASE, FormattedStringFieldEditor.class),
	DATABASE_URL(Page.DATABASE, FormattedStringFieldEditor.class),
	DATABASE_USERNAME(Page.DATABASE, FormattedStringFieldEditor.class),
	DATABASE_PASSWORD(Page.DATABASE, FormattedStringFieldEditor.class),
	DATABASE_TABLE_NAME(Page.DATABASE, FormattedStringFieldEditor.class, DatabaseWriter.Defaults.TABLE_NAME),
	DATABASE_CONNECTION_VALIDATION_TIMEOUT_MS(Page.DATABASE, FormattedIntegerFieldEditor.class, Integer.toString(DatabaseWriter.Defaults.CONNECTION_VALIDATION_TIMEOUT_IN_MILLIS), 5),
	DATABASE_TIMESTAMP_COLUMN_TYPE(Page.DATABASE, FormattedStringFieldEditor.class, DatabaseWriter.Defaults.TIMESTAMP_COLUMN_TYPE),
	DATABASE_RESPONSE_COLUMN_TYPE(Page.DATABASE, FormattedStringFieldEditor.class, DatabaseWriter.Defaults.RESPONSE_TIME_COLUMN_TYPE),
	DATABASE_INFO_COLUMN_TYPE(Page.DATABASE, FormattedStringFieldEditor.class, DatabaseWriter.Defaults.INFO_COLUMN_TYPE),
	DATABASE_COLUMN_NAME_PREFIX(Page.DATABASE, FormattedStringFieldEditor.class, DatabaseWriter.Defaults.COLUMN_NAME_PREFIX),
	DATABASE_COLUMN_NAME_MAX_LENGTH(Page.DATABASE, FormattedIntegerFieldEditor.class, Integer.toString(DatabaseWriter.Defaults.COLUMN_NAME_MAX_LENGTH), 2),

	THRESHOLDS_SPLIT(Page.THRESHOLDS, BooleanFieldEditor.class, Boolean.toString(RouterLoggerConfiguration.Defaults.THRESHOLDS_SPLIT)),
	THRESHOLDS_EXCLUDED(Page.THRESHOLDS, FormattedStringFieldEditor.class),
	THRESHOLDS_EXCLUDED_SEPARATOR(Page.THRESHOLDS, FormattedStringFieldEditor.class, RouterLoggerConfiguration.Defaults.THRESHOLDS_EXCLUDED_SEPARATOR),

	THRESHOLDS_EXPRESSIONS(Page.EXPRESSIONS, ThresholdsFieldEditor.class);

	private static String[][] getNewLineOptions() {
		final int length = NewLine.values().length;
		final String[][] options = new String[length][2];
		for (int index = 0; index < length; index++) {
			options[index][0] = options[index][1] = NewLine.values()[index].name();
		}
		return options;
	}

	private static String[][] getLanguageOptions() {
		final int length = Resources.Language.values().length;
		final String[][] options = new String[length][];
		for (int index = 0; index < length; index++) {
			options[index] = new String[] { Resources.Language.values()[index].getLocale().getDisplayLanguage(Resources.Language.values()[index].getLocale()), Resources.Language.values()[index].getLocale().getLanguage() };
		}
		return options;
	}

	private static final String RESOURCE_KEY_PREFIX = "lbl.preferences.";

	private final String configurationKey;
	private final String resourceKey;
	private final Page page;
	private final Class<? extends FieldEditor> fieldEditorClass;
	private final String defaultValue;
	private final Object fieldEditorData;

	private Preference(final Page page, final Class<? extends FieldEditor> fieldEditorClass) {
		this(null, null, page, fieldEditorClass, null, null);
	}

	private Preference(final String configurationKey, final Page page, final Class<? extends FieldEditor> fieldEditorClass) {
		this(configurationKey, null, page, fieldEditorClass, null, null);
	}

	private Preference(final String configurationKey, final String resourceKey, final Page page, final Class<? extends FieldEditor> fieldEditorClass) {
		this(configurationKey, resourceKey, page, fieldEditorClass, null, null);
	}

	private Preference(final Page page, final Class<? extends FieldEditor> fieldEditorClass, final String defaultValue) {
		this(null, null, page, fieldEditorClass, defaultValue, null);
	}

	private Preference(final String configurationKey, final Page page, final Class<? extends FieldEditor> fieldEditorClass, final String defaultValue) {
		this(configurationKey, null, page, fieldEditorClass, defaultValue, null);
	}

	private Preference(final String configurationKey, final String resourceKey, final Page page, final Class<? extends FieldEditor> fieldEditorClass, final String defaultValue) {
		this(configurationKey, resourceKey, page, fieldEditorClass, defaultValue, null);
	}

	private Preference(final Page page, final Class<? extends FieldEditor> fieldEditorClass, final String defaultValue, final Object fieldEditorData) {
		this(null, null, page, fieldEditorClass, defaultValue, fieldEditorData);
	}

	private Preference(final String configurationKey, final Page page, final Class<? extends FieldEditor> fieldEditorClass, final String defaultValue, final Object fieldEditorData) {
		this(configurationKey, null, page, fieldEditorClass, defaultValue, fieldEditorData);
	}

	private Preference(final String configurationKey, final String resourceKey, final Page page, final Class<? extends FieldEditor> fieldEditorClass, final String defaultValue, final Object fieldEditorData) {
		if (configurationKey != null && !configurationKey.isEmpty()) {
			this.configurationKey = configurationKey;
		}
		else {
			this.configurationKey = name().toLowerCase().replace('_', '.');
		}
		if (resourceKey != null && !resourceKey.isEmpty()) {
			this.resourceKey = resourceKey;
		}
		else {
			this.resourceKey = RESOURCE_KEY_PREFIX + this.configurationKey;
		}
		this.fieldEditorData = fieldEditorData;
		this.defaultValue = defaultValue;
		this.fieldEditorClass = fieldEditorClass;
		this.page = page;
	}

	public String getConfigurationKey() {
		return configurationKey;
	}

	public String getResourceKey() {
		return resourceKey;
	}

	public Page getPage() {
		return page;
	}

	public Class<? extends FieldEditor> getFieldEditorClass() {
		return fieldEditorClass;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public Object getFieldEditorData() {
		return fieldEditorData;
	}

}