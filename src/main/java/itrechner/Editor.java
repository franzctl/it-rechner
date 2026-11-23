package itrechner;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Editor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	private File inhalt;
	private boolean geaendert = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Editor frame = new Editor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Editor() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 705, 666);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!geaendert) {
					dispose();
					return;
				}
				int antwort = JOptionPane.showConfirmDialog(contentPane,
						"Es gibt ungespeicherte Aenderungen.\nTrotzdem schliessen?",
						"Editor schliessen",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (antwort == JOptionPane.YES_OPTION) {
					dispose();
				}
			}
		});

		// --- ContentPane zuerst aufbauen ---
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

		JButton btnSpeichern = new JButton("Speichern");
		buttonPanel.add(btnSpeichern);

		JButton btnLaden = new JButton("Laden");
		buttonPanel.add(btnLaden);

		contentPane.add(buttonPanel, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				geaendert = true;
			}
			public void removeUpdate(DocumentEvent e) {
				geaendert = true;
			}
			public void changedUpdate(DocumentEvent e) {
				geaendert = true;
			}
		});
		JScrollPane scrollPane = new JScrollPane(textArea);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		btnLaden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser datei = new JFileChooser();
				if (datei.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					inhalt = datei.getSelectedFile();
					try (Scanner lesen = new Scanner(inhalt)) {
						textArea.setText("");
						while (lesen.hasNextLine()) {
							textArea.append(lesen.nextLine() + "\n");
						}
						geaendert = false;
					} catch (FileNotFoundException ex) {
						JOptionPane.showMessageDialog(contentPane,
								"Datei konnte nicht gelesen werden: " + ex.getMessage());
					}
				}
			}
		});

		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser neu = new JFileChooser();
				if (neu.showSaveDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					File inhalt_neu = neu.getSelectedFile();

					if (inhalt_neu.exists()) {
						int antwort = JOptionPane.showConfirmDialog(contentPane,
							"Die Datei " + inhalt_neu.getName() + " gibt es schon.\nÜberschreiben?",
							"Datei vorhanden",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
						if (antwort != JOptionPane.YES_OPTION) {
							return;
						}
					}

					try (PrintWriter schreiben = new PrintWriter(inhalt_neu)) {
						schreiben.print(textArea.getText());
						geaendert = false;

					} catch (FileNotFoundException ex) {
							JOptionPane.showMessageDialog(contentPane,
								"Datei konnte nicht geschrieben werden: " + ex.getMessage());
					}
				}
			}
		});

		// --- Menüleiste ---
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnEinst = new JMenu("Einstellungen");
		menuBar.add(mnEinst);

		JMenuItem mntmHintergrund = new JMenuItem("Hintergrundfarbe");
		mnEinst.add(mntmHintergrund);
		mntmHintergrund.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color farbe = JColorChooser.showDialog(contentPane, "Hintergrundfarbe",
						contentPane.getBackground());
				if (farbe != null) {
					contentPane.setBackground(farbe);
					textArea.setBackground(farbe);
				}
			}
		});

		JMenuItem mntmSchriftfarbe = new JMenuItem("Schriftfarbe");
		mnEinst.add(mntmSchriftfarbe);
		mntmSchriftfarbe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color farbe = JColorChooser.showDialog(contentPane, "Schriftfarbe",
						textArea.getForeground());
				if (farbe != null) {
					textArea.setForeground(farbe);
				}
			}
		});

		JMenuItem mntmDrucken = new JMenuItem("Drucken");
		mnEinst.add(mntmDrucken);
		mntmDrucken.addActionListener(e -> {
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setJobName("Editor Druck");

			String text = textArea.getText();

			job.setPrintable((graphics, pageFormat, pageIndex) -> {
				if (pageIndex > 0) {
					return Printable.NO_SUCH_PAGE;
				}

				graphics.setFont(new Font("Monospaced", Font.PLAIN, 12));

				int x = (int) pageFormat.getImageableX();
				int y = (int) pageFormat.getImageableY() + graphics.getFontMetrics().getAscent();

				for (String line : text.split("\n")) {
					graphics.drawString(line, x, y);
					y += graphics.getFontMetrics().getHeight();
				}

				return Printable.PAGE_EXISTS;
			});

			if (job.printDialog()) {
				try {
					job.print();
				} catch (PrinterException ex) {
					JOptionPane.showMessageDialog(contentPane,
							"Druck fehlgeschlagen: " + ex.getMessage());
				}
			}
		});

		JMenu mnInfo = new JMenu("Info");
		menuBar.add(mnInfo);

		JMenuItem mntmAutor = new JMenuItem("Autor");
		mnInfo.add(mntmAutor);
		mntmAutor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contentPane,
						"Autor: Franz Schkommodau\nBFW Dresden\n03.09.2026");
			}
		});
		mntmAutor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));
	}
	public void setzeText(String text) {
		textArea.setText(text);
		geaendert = false;
	}

}
