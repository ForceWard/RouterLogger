package it.albertus.router.gui.preference.field;

import it.albertus.router.resources.Resources;

import java.util.prefs.Preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class WrapStringFieldEditor extends StringFieldEditor {

	public static final int DEFAULT_TEXT_HEIGHT = 4;

	private final int height;

	private Text textField; // Do not set any value here!

	public WrapStringFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		this.height = DEFAULT_TEXT_HEIGHT;
		init();
	}

	public WrapStringFieldEditor(String name, String labelText, Composite parent, int height) {
		super(name, labelText, parent);
		this.height = height;
		init();
	}

	@Override
	public Text getTextControl(final Composite parent) {
		if (textField == null) {
			textField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
			textField.setFont(parent.getFont());
			textField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(final KeyEvent ke) {
					valueChanged();
				}
			});
			textField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent fe) {
					valueChanged();
				}
			});
			textField.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(final DisposeEvent de) {
					textField = null;
				}
			});
		}
		else {
			checkParent(textField, parent);
		}
		return textField;
	}

	protected void adjustTextHeight() {
		final GridData gd = (GridData) getTextControl().getLayoutData();
		gd.heightHint = getTextControl().getLineHeight() * height;
		gd.widthHint = 0;
	}

	protected void init() {
		adjustTextHeight();
		setErrorMessage(Resources.get("err.preferences.string"));
		setTextLimit(Preferences.MAX_VALUE_LENGTH);
	}

	protected Text getTextField() {
		return textField;
	}

	public int getHeight() {
		return height;
	}

}
