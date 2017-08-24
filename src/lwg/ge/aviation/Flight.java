package lwg.ge.aviation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
enum Status{
	cancel,delay,ferry,swap,normal
}
public class Flight {
	String flightNumber;
	String tailNumber;
	String fleetID;
	String departureTime;
	String arrivalTime;
	String execDepartureTime;
	String execArrivalTime;
	String departure;
	String arrival;
	String swapTailNumber;
	String swapFleetID;
	Status status = Status.normal;
	public Flight(String flightNumber, String tailNumber, String departureTime, String arrivalTime, String departure,
			String arrival, String fleetID) {
		this.flightNumber = flightNumber;
		this.tailNumber = tailNumber;
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmm");
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss"); 
		Date arrivalDate = null;
		try {
			arrivalDate = format.parse(arrivalTime);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Date departureDate = null;
		try {
			departureDate = format.parse(departureTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.departureTime = formatter.format(departureDate);
		this.arrivalTime = formatter.format(arrivalDate);
		this.departure = departure;
		this.arrival = arrival;
		this.fleetID = fleetID;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getTailNumber() {
		return tailNumber;
	}

	public void setTailNumber(String tailNumber) {
		this.tailNumber = tailNumber;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public String getFleetID() {
		return fleetID;
	}

	public void setFleetID(String fleetID) {
		this.fleetID = fleetID;
	}

	public String getExecDepartureTime() {
		return execDepartureTime;
	}

	public void setExecDepartureTime(String execDepartureTime) {
		this.execDepartureTime = execDepartureTime;
	}

	public String getExecArrivalTime() {
		return execArrivalTime;
	}

	public void setExecArrivalTime(String execArrivalTime) {
		this.execArrivalTime = execArrivalTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getSwapTailNumber() {
		return swapTailNumber;
	}

	public void setSwapTailNumber(String swapTailNumber) {
		this.swapTailNumber = swapTailNumber;
	}

	public String getSwapFleetID() {
		return swapFleetID;
	}

	public void setSwapFleetID(String swapFleetID) {
		this.swapFleetID = swapFleetID;
	}
}
