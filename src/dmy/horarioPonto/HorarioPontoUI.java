package dmy.horarioPonto;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

import net.miginfocom.swing.MigLayout;

public class HorarioPontoUI implements IHorarioPontoListener {

	private HorarioPontoProcess hpProcess = null;
	private JFrame frame;
	private JFormattedTextField txtArrival;
	private JPanel panelOutput;
	private JPanel panelBalance;
	private JLabel lblLeaveBetween;
	private JLabel lblDepartureInitial;
	private JLabel lblTimeToLeave;
	private JLabel lblDepartureFinal;
	private JLabel labelDash;
	private JLabel labelDash2;
	private JLabel lblMessage;
	private JLabel lblBalance;
	private JLabel lblHourBalance;
	private JPanel panelMain;
	private JLabel lblLastUpdate;
	private JLabel lastUpdateInfo;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	/**
	 * Create the application.
	 */
	public HorarioPontoUI() {
		hpProcess = new HorarioPontoProcess();
		hpProcess.addListener(this);
		initializeUI();
	}

	public JFrame getFrame() {
		return frame;
	}

	private void initializeUI() {
		frame = new JFrame();
		frame.setBounds(100, 100, 510, 280);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setResizable(false);
		// frame.getContentPane().setLayout(new MigLayout("", "[][grow][]",
		// "[][][][grow]"));
		frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));

		panelMain = new JPanel();
		panelMain.setLayout(new MigLayout("", "[][grow][]", "[][][][grow]"));
		frame.getContentPane().add(panelMain, "cell 0 0,alignx trailing");

		lblMessage = new JLabel("");
		lblMessage.setForeground(Color.RED);
		// frame.getContentPane().add(lblMessage, "cell 0 0 3 1");
		panelMain.add(lblMessage, "cell 0 0 3 1");

		JLabel lblArrival = new JLabel("ENTRADA: ");
		lblArrival.setFont(new Font("Tahoma", Font.PLAIN, 24));
		// frame.getContentPane().add(lblArrival, "cell 0 1,alignx trailing");
		panelMain.add(lblArrival, "cell 0 1,alignx trailing");

		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("##:##");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		mf.setValueContainsLiteralCharacters(false);

		txtArrival = new JFormattedTextField(mf);
		txtArrival.setFont(new Font("Tahoma", Font.PLAIN, 24));
		// txtArrival.addFocusListener(new FocusAdapter() {
		// @Override
		// public void focusLost(final FocusEvent e) {
		// String text = txtArrival.getText();
		// hpProcess.processUserInput(text);
		// }
		// });
		txtArrival.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				String text = txtArrival.getText();
				hpProcess.processUserInput(text);
			}
		});
		// frame.getContentPane().add(txtArrival, "cell 1 1,grow");
		panelMain.add(txtArrival, "cell 1 1,grow");
		txtArrival.setColumns(10);

		JButton btnMark = new JButton("Limpar");
		btnMark.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				txtArrival.setText("");
				lblMessage.setText("");
				lblTimeToLeave.setText("00:00");
				lblDepartureInitial.setText("00:00");
				lblDepartureFinal.setText("00:00");
				// timeArrived = new Date();
				// String nowAsString = sdf.format(timeArrived);
				// txtArrival.setText(nowAsString);
				// updateTimeToLeave(timeArrived);
			}
		});
		// frame.getContentPane().add(btnMark, "cell 2 1,growx,aligny center");
		panelMain.add(btnMark, "cell 2 1,growx,aligny center");

		panelOutput = new JPanel();
		// frame.getContentPane().add(panelOutput, "cell 0 2 3 1,grow");
		panelMain.add(panelOutput, "cell 0 2 3 1,grow");
		panelOutput.setLayout(new MigLayout("", "[][grow][][][][grow]", "[]"));

		lblLeaveBetween = new JLabel("SAIDA:");
		lblLeaveBetween.setFont(new Font("Tahoma", Font.PLAIN, 24));
		panelOutput.add(lblLeaveBetween, "cell 0 0");

		lblDepartureInitial = new JLabel("00:00");
		lblDepartureInitial.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblDepartureInitial.setForeground(Color.BLUE);
		panelOutput.add(lblDepartureInitial, "cell 1 0,alignx center");

		labelDash = new JLabel("-");
		labelDash.setFont(new Font("Tahoma", Font.PLAIN, 30));
		panelOutput.add(labelDash, "cell 2 0");

		lblTimeToLeave = new JLabel("00:00");
		lblTimeToLeave.setFont(new Font("Tahoma", Font.PLAIN, 46));
		panelOutput.add(lblTimeToLeave, "cell 3 0");

		labelDash2 = new JLabel("-");
		labelDash2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		panelOutput.add(labelDash2, "cell 4 0");

		lblDepartureFinal = new JLabel("00:00");
		lblDepartureFinal.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblDepartureFinal.setForeground(Color.BLUE);
		panelOutput.add(lblDepartureFinal, "cell 5 0,alignx center");

		panelBalance = new JPanel();
		// frame.getContentPane().add(panelBalance, "cell 0 3 3 1,grow");
		panelMain.add(panelBalance, "cell 0 3 3 1,grow");
		panelBalance.setLayout(new MigLayout("", "[][grow]", "[][]"));

		lblBalance = new JLabel("SALDO:");
		lblBalance.setFont(new Font("Tahoma", Font.PLAIN, 24));
		panelBalance.add(lblBalance, "cell 0 0,alignx left,aligny center");

		lblHourBalance = new JLabel("00:00");
		lblHourBalance.setFont(new Font("Tahoma", Font.PLAIN, 30));
		panelBalance.add(lblHourBalance, "cell 1 0,alignx center,aligny center");

		lblLastUpdate = new JLabel("\u00DAltima atualiza\u00E7\u00E3o:");
		panelBalance.add(lblLastUpdate, "cell 0 1");

		lastUpdateInfo = new JLabel("");
		panelBalance.add(lastUpdateInfo, "cell 1 1");
	}

	private void internalUpdateBalanceHourInfo(final String info, Date lastUpdated) {
		if (info.startsWith("+") && !lblHourBalance.getForeground().equals(Color.BLUE)) {
			lblHourBalance.setForeground(Color.BLUE);
		} else if (info.startsWith("-") && !lblHourBalance.getForeground().equals(Color.RED)) {
			lblHourBalance.setForeground(Color.RED);
		} else if (info.equals("00:00") && !lblHourBalance.getForeground().equals(Color.BLACK)) {
			lblHourBalance.setForeground(Color.BLACK);
		}
		lblHourBalance.setText(info);
		if (lastUpdated != null) {
			this.internalSetLastUpdated(lastUpdated);
		}
	}

	private void internalSetLastUpdated(Date lastUpdated) {
		String lastUpdatedAsString = sdf.format(lastUpdated);
		lastUpdateInfo.setText(lastUpdatedAsString);
	}

	public JLabel getLblDepartureInitial() {
		return lblDepartureInitial;
	}

	public JLabel getLblDepartureFinal() {
		return lblDepartureFinal;
	}

	@Override
	public void updateArrivalText(final String newText) {
		txtArrival.setText(newText);
	}

	@Override
	public void updateMessageText(final String text) {
		lblMessage.setText(text);
	}

	@Override
	public void updateTimeToLeaveInfo(final String info) {
		lblTimeToLeave.setText(info);
	}

	@Override
	public void updateDepartureFinalInfo(final String info) {
		lblDepartureInitial.setText(info);
	}

	@Override
	public void updateDepartureInitialInfo(final String info) {
		lblDepartureFinal.setText(info);
	}

	@Override
	public void setLastUpdated(final Date lastUpdated) {
		this.internalSetLastUpdated(lastUpdated);
	}

	@Override
	public void updateBalanceHourInfo(final String info) {
		this.internalUpdateBalanceHourInfo(info, null);
	}
}
