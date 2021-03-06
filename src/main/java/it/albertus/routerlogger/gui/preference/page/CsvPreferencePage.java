package it.albertus.routerlogger.gui.preference.page;

import it.albertus.jface.preference.LocalizedLabelsAndValues;
import it.albertus.jface.preference.page.BasePreferencePage;
import it.albertus.routerlogger.resources.Messages;
import it.albertus.util.Localized;

public class CsvPreferencePage extends BasePreferencePage {

	private enum Separator {
		COMMA("lbl.preferences.separator.comma", ","),
		TAB("lbl.preferences.separator.tab", "\t"),
		COLON("lbl.preferences.separator.colon", ":"),
		SEMICOLON("lbl.preferences.separator.semicolon", ";"),
		PIPE("lbl.preferences.separator.pipe", "|");

		private final String resourceKey;
		private final String value;

		private Separator(final String resourceKey, final String separator) {
			this.resourceKey = resourceKey;
			this.value = separator;
		}
	}

	public static LocalizedLabelsAndValues getSeparatorComboOptions() {
		final Separator[] values = Separator.values();
		final LocalizedLabelsAndValues options = new LocalizedLabelsAndValues(values.length);
		for (final Separator separator : values) {
			final String value = separator.value;
			final Localized name = new Localized() {
				@Override
				public String getString() {
					return Messages.get(separator.resourceKey);
				}
			};
			options.add(name, value);
		}
		return options;
	}

}
