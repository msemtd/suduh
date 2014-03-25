package com.tecspy.suduh;

import lombok.extern.log4j.Log4j;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 
 * 
 * 
 * 
 * @author michael.erskine
 * 
 */
@Log4j
public class GameFace extends Composite {
	private Canvas canvas;
	private Geometry geom;
	Color boxLineSmall;
	Color boxLineBig;
	private Font fontHint;
	private int[] hints = new int[9 * 9];
	private int[] cellOffsets = null;
	private Point hintOffset;
	private int hintPos;

	// TODO Selected cell
	// TODO click to select cell
	private Point cellSelected = null;
	private ClickListener listener = new ClickListener();

	public class ClickListener implements MouseListener {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
			selectCellAt(e.x, e.y);
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (e.count == 1) {
				// System.out.println("Mouse up");
			}
		}
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GameFace(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		Suduh s = Suduh.getInstance();
		if (s == null)
			return;
		geom = s.getGeom();
		stealResources();

		canvas = new Canvas(this, SWT.NONE);
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		canvas.setLayout(null);
		FormData fd_canvas = new FormData();
		fd_canvas.bottom = new FormAttachment(0, 489);
		fd_canvas.right = new FormAttachment(0, 489);
		fd_canvas.top = new FormAttachment(0, 10);
		fd_canvas.left = new FormAttachment(0, 10);
		canvas.setLayoutData(fd_canvas);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (e.widget instanceof Canvas) {
					paintAll((Canvas) e.widget, e.gc, e.display);
				}
			}
		});
		canvas.addMouseListener(listener);
	}

	public void selectCellAt(int x, int y) {
		log.debug("selectCellAt " + x + " " + y);

	}

	private void stealResources() {
		boxLineSmall = SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY);
		boxLineBig = SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE);
		fontHint = SWTResourceManager.getFont("Bitstream Vera Sans Mono", 8,
				SWT.NORMAL);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Paint entire canvas.
	 * 
	 * @param c
	 * @param gc
	 * @param disp
	 */
	private void paintAll(Canvas c, GC gc, Display disp) {
		if (geom == null)
			return;

		int cs = geom.getCellSize();
		int gutter = geom.getGutter();

		// setup required...
		if (cellOffsets == null) {
			cellOffsets = new int[10];
			for (int ix = 0; ix < 10; ix++) {
				int gx = (ix * cs) + gutter;
				cellOffsets[ix] = gx;
			}
			hintOffset = gc.textExtent("0");
			hintOffset.x /= 2;
			hintOffset.y /= 2;
			hintPos = cs / 4;
		}

		// grid graphics...
		gc.setLineWidth(3);
		drawGridLines(gc, cs, gutter);
		// show hint text
		gc.setForeground(disp.getSystemColor(SWT.COLOR_YELLOW));
		gc.setFont(fontHint);
		drawHints(gc);
	}

	private void drawHints(GC gc) {
		// 9 * 9 array of sets of hints...
		for (int i = 0; i < hints.length; i++) {
			drawHintSet(gc, cellOffsets[i % 9], cellOffsets[i / 9], hints[i]);
		}
	}

	/**
	 * Draw this set of hints
	 * 
	 * @param gc
	 * @param cx
	 *            cell origin X
	 * @param cy
	 *            cell origin Y
	 * @param h
	 *            hint data
	 */
	private void drawHintSet(GC gc, int cx, int cy, int h) {
		for (int i = 0; i < 9; i++) {
			if (!hintOn(h, i + 1))
				continue;
			int tx = cx + (((i % 3) + 1) * hintPos) - hintOffset.x;
			int ty = cy + (((i / 3) + 1) * hintPos) - hintOffset.y;
			gc.drawText("" + (i + 1), tx, ty);
		}
	}

	/**
	 * @param hdata
	 *            encoded hint data
	 * @param hintNumber
	 *            hint number in range 1-9
	 * @return
	 */
	private static boolean hintOn(int hdata, int hintNumber) {
		return true;
		// return ((hdata >> (hintNumber - 1)) & 0x01) == 1;
	}

	private void drawGridLines(GC gc, int cs, int gutter) {
		// two passes: first for small box lines then big box lines...
		for (int i = 0; i < 2; i++) {
			boolean bigbox = (i == 1);
			gc.setForeground(bigbox ? boxLineBig : boxLineSmall);
			int y1 = gutter;
			int y2 = gutter + (cs * 9);
			for (int ix = 0; ix <= 9; ix++) {
				boolean bigboxline = (ix % 3 == 0);
				if ((bigboxline && !bigbox) || (!bigboxline && bigbox))
					continue;
				int x = gutter + (cs * ix);
				gc.drawLine(x, y1, x, y2);
			}
			int x1 = gutter;
			int x2 = gutter + (cs * 9);
			for (int ix = 0; ix <= 9; ix++) {
				boolean bigboxline = (ix % 3 == 0);
				if ((bigboxline && !bigbox) || (!bigboxline && bigbox))
					continue;
				int y = gutter + (cs * ix);
				gc.drawLine(x1, y, x2, y);
			}
		}
	}

	public int getCellSize() {
		return geom.getCellSize();
	}

	public void setCellSize(int sqSize) {
		this.geom.setCellSize(sqSize);
	}

}
