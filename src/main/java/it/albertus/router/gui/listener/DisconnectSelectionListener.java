package it.albertus.router.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;

import it.albertus.router.gui.RouterLoggerGui;
import it.albertus.router.resources.Messages;

public class DisconnectSelectionListener extends SelectionAdapter {

	private final RouterLoggerGui gui;

	public DisconnectSelectionListener(final RouterLoggerGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (gui.canDisconnect()) {
			final MessageBox messageBox = new MessageBox(gui.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setMessage(Messages.get("msg.confirm.disconnect.message"));
			messageBox.setText(Messages.get("msg.confirm.disconnect.text"));
			if (messageBox.open() == SWT.YES && gui.canDisconnect()) {
				gui.disconnect();
			}
		}
	}

}
