package dmy.horarioPonto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class HorarioPontoProcess {

	private Date timeArrived;
	private Date timeToLeave;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	GregorianCalendar gregCalendar = new GregorianCalendar();
	List<IHorarioPontoListener> listenerList = new ArrayList<>();
	Object hourBalanceThreadSync = new Object();
	protected boolean isArrivalTimeDefined = false;
	private Date departureInitialTime;
	private Date departureFinalTime;
	private Thread balanceHourThread;
	private String debtorText;
	private Object systemClockThreadSync;
	private int systemClockSyncTimeout;

	/**
	 * Create the application.
	 */
	public HorarioPontoProcess() {
	}

	public void processUserInput(final String text) {
		try {
			String newText = text.trim();
			if (newText != null && !newText.isEmpty()) {
				boolean isValid = validateHour(newText);
				if (isValid) {
					timeArrived = sdf.parse(newText);
					updateArrivalInfo(newText);
					updateTimeToLeave();
					updateMessageText("");
					isArrivalTimeDefined = true;
					startBalanceThread();
				} else {
					isArrivalTimeDefined = false;
					updateMessageText("");
				}
			}
		} catch (ParseException e1) {
			isArrivalTimeDefined = false;
			updateMessageText("Formato de hora invalido. Deve ser: HH:mm");
		} catch (DateTimeException e) {
			isArrivalTimeDefined = false;
			updateMessageText(e.getMessage());
		} finally {
			if (!isArrivalTimeDefined) {
				notifySyncHourBalanceThread();
			}
		}
	}

	private void notifySyncHourBalanceThread() {
		synchronized (hourBalanceThreadSync) {
			hourBalanceThreadSync.notify();
		}
	}

	private void notifySyncSystemClockThread() {
		synchronized (systemClockThreadSync) {
			systemClockThreadSync.notifyAll();
		}
	}

	private void waitSyncHourBalanceThread(final int timeout) {
		synchronized (hourBalanceThreadSync) {
			try {
				hourBalanceThreadSync.wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addListener(final IHorarioPontoListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	public void removeListener(final IHorarioPontoListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	private void updateArrivalInfo(final String info) {
		for (IHorarioPontoListener listener : listenerList) {
			listener.updateArrivalText(info);
		}
	}

	private void updateMessageText(String text) {
		for (IHorarioPontoListener listener : listenerList) {
			listener.updateMessageText(text);
		}
	}

	private void updateTimeToLeaveInfo(final String info) {
		for (IHorarioPontoListener listener : listenerList) {
			listener.updateTimeToLeaveInfo(info);
		}
	}

	private void updateDepartureFinalInfo(String info) {
		for (IHorarioPontoListener listener : listenerList) {
			listener.updateDepartureFinalInfo(info);
		}
	}

	private void updateDepartureInitialInfo(String info) {
		for (IHorarioPontoListener listener : listenerList) {
			listener.updateDepartureInitialInfo(info);
		}
	}

	private void updateBalanceHourInfo(String info) {
		for (IHorarioPontoListener listener : listenerList) {
			listener.updateBalanceHourInfo(info);
		}
	}

	private void setLastUpdated(final Date now) {
		for (IHorarioPontoListener listener : listenerList) {
			listener.setLastUpdated(now);
		}
	}

	private boolean validateHour(final String text) throws ParseException {
		boolean isValid = false;
		if (text != null && !text.isEmpty() && text.length() > 1) {
			String fisrtDigitsAsString = text.substring(0, 2);
			try {
				Integer firstDigits = Integer.parseInt(fisrtDigitsAsString);
				if (firstDigits > 23) {
					throw new DateTimeException("O valor da hora deve ser menor que 23");
				}
				isValid = true;
			} catch (NumberFormatException e) {
				throw new ParseException(e.getMessage(), 0);
			}
		}
		return isValid;
	}

	private void updateTimeToLeave() {
		gregCalendar.setTime(timeArrived);
		gregCalendar.add(GregorianCalendar.HOUR, 9);
		gregCalendar.add(GregorianCalendar.MINUTE, 30);
		timeToLeave = gregCalendar.getTime();
		updateTimeToLeaveInfo(sdf.format(timeToLeave));
		gregCalendar.clear();
		updateLabelDepartureInitial(timeArrived);
		updateLabelDepartureFinal(timeArrived);
	}

	private void updateLabelDepartureInitial(final Date now) {
		gregCalendar.setTime(now);
		gregCalendar.add(GregorianCalendar.HOUR, 9);
		gregCalendar.add(GregorianCalendar.MINUTE, 20);
		departureInitialTime = gregCalendar.getTime();
		String finalAsString = sdf.format(departureInitialTime);
		updateDepartureFinalInfo(finalAsString);
		gregCalendar.clear();
	}

	private void updateLabelDepartureFinal(final Date now) {
		gregCalendar.setTime(now);
		gregCalendar.add(GregorianCalendar.HOUR, 9);
		gregCalendar.add(GregorianCalendar.MINUTE, 40);
		departureFinalTime = gregCalendar.getTime();
		String initialAsString = sdf.format(departureFinalTime);
		updateDepartureInitialInfo(initialAsString);
		gregCalendar.clear();
	}

	private void startBalanceThread() {
		if (balanceHourThread == null) {
			createBalanceHourThread();
		} else if (isArrivalTimeDefined) {
			systemClockSyncTimeout = 500;
			notifySyncHourBalanceThread();
			notifySyncSystemClockThread();
		}
	}

	private void createBalanceHourThread() {
		Runnable balanceHourRunnable = createBalanceHourRunnable();
		balanceHourThread = new Thread(balanceHourRunnable);
		balanceHourThread.setName("HourBalanceThread");
		balanceHourThread.start();
		syncWithSystemClock();
	}

	private Runnable createBalanceHourRunnable() {
		Runnable balanceHourRunnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (isArrivalTimeDefined) {
						updateHourBalance();
						waitSyncHourBalanceThread(60000);
					} else {
						waitSyncHourBalanceThread(60000);
					}
				}
			}
		};
		return balanceHourRunnable;

	}

	private void syncWithSystemClock() {
		systemClockThreadSync = new Object();
		systemClockSyncTimeout = 500;
		createSystemClockSyncThread();
	}

	private void createSystemClockSyncThread() {
		Runnable syncSystemClockRunnable = createSystemClockRunnable();
		Thread syncSystemClockThread = new Thread(syncSystemClockRunnable);
		syncSystemClockThread.setName("SystemClockSyncThread");
		syncSystemClockThread.start();
	}

	private Runnable createSystemClockRunnable() {
		Runnable systemClockRunnable = new Runnable() {
			@Override
			public void run() {
				int lastMinute = Calendar.getInstance().get(Calendar.MINUTE);
				while (true) {
					synchronized (systemClockThreadSync) {
						try {
							systemClockThreadSync.wait(systemClockSyncTimeout);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
					if (lastMinute != currentMinute) {
						lastMinute = currentMinute;
						notifySyncHourBalanceThread();
						systemClockSyncTimeout = 360000;
					}
				}
			}
		};
		return systemClockRunnable;
	}

	private void updateHourBalance() {
		if (departureInitialTime != null && departureFinalTime != null) {
			Date now = new Date();

			gregCalendar.setTime(timeToLeave);
			int timeToLeaveHour = gregCalendar.get(Calendar.HOUR_OF_DAY);
			int timeToLeaveMin = gregCalendar.get(Calendar.MINUTE);

			gregCalendar.setTime(departureInitialTime);
			int depInitialHour = gregCalendar.get(Calendar.HOUR_OF_DAY);
			int depInitialMin = gregCalendar.get(Calendar.MINUTE);

			gregCalendar.setTime(departureFinalTime);
			int depFinalHour = gregCalendar.get(Calendar.HOUR_OF_DAY);
			int depFinalMin = gregCalendar.get(Calendar.MINUTE);

			gregCalendar.setTime(now);
			int nowHour = gregCalendar.get(Calendar.HOUR_OF_DAY);
			int nowMin = gregCalendar.get(Calendar.MINUTE);

			String balanceText = "00:00";

			if (nowHour < depInitialHour || (nowHour == depInitialHour && nowMin < depInitialMin)) {
				balanceText = calculateDebtorTime(timeToLeaveHour, timeToLeaveMin, nowHour, nowMin);
			} else if (nowHour > depFinalHour || (nowHour == depFinalHour && nowMin > depFinalMin)) {
				balanceText = calculateExtraTime(timeToLeaveHour, timeToLeaveMin, nowHour, nowMin);
			}
			updateBalanceHourInfo(balanceText);
			setLastUpdated(now);
			gregCalendar.clear();
		}
	}

	private String calculateDebtorTime(int timeToLeaveHour, int timeToLeaveMin, int nowHour, int nowMin) {
		if (nowHour < 12 || nowHour > 13 || (nowHour == 13 && nowMin > 30)) {
			int balanceHour = timeToLeaveHour - nowHour;
			if (nowHour < 12) {
				balanceHour = timeToLeaveHour - nowHour - 1;
			}
			int balanceMin = timeToLeaveMin - nowMin;
			if (nowHour < 12) {
				balanceMin = timeToLeaveMin - nowMin - 30;
			}
			if (balanceMin < 0) {
				balanceHour = balanceHour > 0 ? (balanceHour - 1) : 0;
				balanceMin *= -1;
				if (balanceMin < 60) {
					balanceMin = 60 - balanceMin;
				} else {
					balanceMin = (balanceMin - 60 - 60) * -1;
					balanceHour -= 1;
				}
			}
			debtorText = "-" + getTwoDigitsNumber(balanceHour) + ":" + getTwoDigitsNumber(balanceMin);
		}
		return debtorText;
	}

	private String calculateExtraTime(int timeToLeaveHour, int timeToLeaveMin, int nowHour, int nowMin) {
		int balanceHour = nowHour - timeToLeaveHour;
		int balanceMin = 0;
		balanceMin = nowMin - timeToLeaveMin;
		if (balanceMin < 0) {
			balanceHour = balanceHour > 0 ? balanceHour - 1 : 0;
			balanceMin *= -1;
			balanceMin = 60 - balanceMin;
		}
		String balanceText = "+" + getTwoDigitsNumber(balanceHour) + ":" + getTwoDigitsNumber(balanceMin);
		return balanceText;
	}

	private String getTwoDigitsNumber(int number) {
		return number < 10 ? "0" + number : String.valueOf(number);
	}

}
