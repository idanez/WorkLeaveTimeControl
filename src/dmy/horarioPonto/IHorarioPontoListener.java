package dmy.horarioPonto;

import java.util.Date;

public interface IHorarioPontoListener {

	void updateArrivalText(String newText);

	void updateMessageText(String text);

	void updateTimeToLeaveInfo(String info);

	void updateDepartureFinalInfo(String info);

	void updateDepartureInitialInfo(String info);

	void updateBalanceHourInfo(String info);

	void setLastUpdated(Date lastUpdated);

}
