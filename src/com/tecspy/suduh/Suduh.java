package com.tecspy.suduh;

import lombok.Getter;
import lombok.Setter;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Suduh extends ApplicationWindow {
	private GameFace gameFace;
	private Text textSqSize;
	@Getter
	private static Suduh instance = null;
	@Getter
	@Setter
	private Geometry geom = new Geometry();

	/**
	 * Create the application window.
	 */
	public Suduh() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		gameFace = new GameFace(container, SWT.NONE);
		gameFace.setBounds(0, 0, 621, 418);
		gameFace.setLayout(null);

		textSqSize = new Text(container, SWT.BORDER);
		textSqSize.setBounds(665, 99, 41, 21);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(627, 102, 32, 15);
		lblNewLabel.setText("sqSize");

		Button btnApply = new Button(container, SWT.NONE);
		btnApply.setBounds(665, 44, 75, 25);
		btnApply.setText("Apply");
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateGeometry();
			}
		});

		return container;
	}

	/**
	 * Hook for GUI ready upon startup. NB: review if SWT API changes.
	 * 
	 */
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				ready();
			}
		});
	}

	private void ready() {
		int sqSize = gameFace.getCellSize();
		textSqSize.setText("" + sqSize);
		textSqSize.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Suduh window = new Suduh();
			instance = window;
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Suduh");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(810, 539);
	}

	private void updateGeometry() {
		String text = textSqSize.getText();
		try {
			int i = Integer.parseInt(text);
			gameFace.setCellSize(i);
			// how do we redraw?
			gameFace.redraw();
			gameFace.update();
		} catch (NumberFormatException e1) {
		}
	}
}
