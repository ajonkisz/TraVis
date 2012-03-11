package travis.view.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import travis.model.script.ScriptPrinter;
import travis.util.Messages;

import net.miginfocom.swing.MigLayout;

public class ConsolePanel extends JPanel implements ActionListener, Printer {

	private static final long serialVersionUID = -8355796638620826169L;

	private final JTextArea console;
	private final JScrollPane pane;
	private final Timer scrollTimer;

	public ConsolePanel() {
		super(new MigLayout("fill, insets 0"));
		scrollTimer = new Timer(30, this);
		scrollTimer.setRepeats(false);

		ScriptPrinter.getInstance().addListeningPrinter(this);
		System.setErr(new ErrOut(System.err));
		
		console = new JTextArea();
		console.setEditable(false);
		console.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem item = new JMenuItem(
							Messages.get("clear.console"));
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							console.setText("");
						}
					});
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		pane = new JScrollPane(console);
		add(pane, "grow");
	}
	
	private class ErrOut extends PrintStream {

		public ErrOut(OutputStream out) {
			super(out);
		}
		
		@Override
		public void write(byte[] buf, int off, int len) {
			super.write(buf, off, len);
			console.append(new String(buf, off, len));
			scrollTimer.restart();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		pane.getVerticalScrollBar().setValue(
				pane.getVerticalScrollBar().getMaximum());
	}

	@Override
	public void write(String s) {
		console.append(s);
		scrollTimer.restart();
	}

}
