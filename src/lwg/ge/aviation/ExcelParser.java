package lwg.ge.aviation;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelParser {
	private static Logger logger = Logger.getLogger(ExcelParser.class);
	static int basic_Aircraft_Sheet = 7;
	static String basic_Aircraft_Sheet_Name = "7_Aircraft_Data";
	//basic
	static int basic_Sheet = 8;
	static String basic_Sheet_Name = "8_Flight_Data";
	static int basic_Hour_Offset = 0;
	static int basic_Minute_Offset = 0;
	static int basic_Fight_Col = 0;
	static int basic_Tail_Col = 6;
	static int basic_Departure_Col = 2; // format :14122016_0955
	static int basic_Arrival_Col = 3; // format :14122016_0955
	static int basic_Takeoff_col = 4;
	static int basic_Arrival_col = 5;
	static int basic_Header_Row = 1;
	static int basic_Footer_Row = 0;
	//result
	static int result_Sheet = 0;
	static String result_Sheet_Name = "Final flight sheet";
	static int result_Hour_Offset = 0;
	static int result_Minute_Offset = 0;
	static int reslut_Fight_Col = 2; //例如:MF8642
	static int result_Tail_Col = 23; //例如：B-1708
	static int result_Departure_Col = 10; // 预计起飞时间
	static int result_Arrival_Col = 14; // 预计到达时间
	static int result_Takeoff_col = 6;
	static int result_Arrival_col = 7;
	static int result_Header_Row = 1;
	static int result_Footer_Row = 0;
	static int result_delay_Row = 27;//D
	static int result_cancel_Row = 30;//C
	
	
	static SimpleDateFormat basicTimeFormat = new SimpleDateFormat("ddMMyyyy_HHmm");
	static String BasicTimeFormatString = "ddMMyyyy_HHmm";
	private ArrayList<Flight> flights = new ArrayList<Flight>();
	private Map<String, String> aircraft = new HashMap<String, String>();
	
	enum InfoLevel {
		Info, Warn, Error;
		public String getName() {
			return this.name() + ":";
		}
	}
	
	private void initAirplane(XSSFWorkbook wb){
		Sheet basic = wb.getSheetAt(basic_Aircraft_Sheet);
		Row source;
		for (int i = basic_Header_Row; i <= basic.getLastRowNum() - basic_Footer_Row; i++) {
			source = basic.getRow(i);
			String tailNumber = source.getCell(0).getStringCellValue().trim();
			Cell cell = source.getCell(1);
			String fleetID = "";
			switch (cell.getCellTypeEnum()) {
			case STRING:
				fleetID = cell.getStringCellValue().trim();
				break;
			case NUMERIC:
				fleetID = String.valueOf((int) cell.getNumericCellValue());
				break;
			default:
				break;
			}
			aircraft.put(tailNumber, fleetID);
		}
	}

	public ExcelParser(String file, String resultFile) throws InvalidFormatException, IOException, InvocationTargetException, InterruptedException {
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFWorkbook re = new XSSFWorkbook(resultFile);
		initAirplane(wb);
		boolean hasFerry = false;
		Sheet basic = wb.getSheetAt(basic_Sheet);
		Sheet result = re.getSheetAt(result_Sheet);
		Row source;
		Row resource;
		if((basic.getLastRowNum() - basic_Footer_Row) < (result.getLastRowNum() - result_Footer_Row)){
			hasFerry = true;
		}
		for (int i = result_Header_Row; i <= basic.getLastRowNum() - result_Footer_Row; i++) {
			source = basic.getRow(i);
			resource = result.getRow(i);
			Cell delayCell = resource.getCell(result_delay_Row);
			Cell swapCell = resource.getCell(result_Tail_Col);
			Cell cancelCell = resource.getCell(result_cancel_Row);
			String flightNumber = String.valueOf((int) source.getCell(basic_Fight_Col).getNumericCellValue());
			String departure = source.getCell(basic_Takeoff_col).getStringCellValue().trim();
			String arrival = source.getCell(basic_Arrival_col).getStringCellValue().trim();
			String arrivalTime = source.getCell(basic_Arrival_Col).getStringCellValue().trim();
			String departureTime = source.getCell(basic_Departure_Col).getStringCellValue().trim();
			String tailNumber = source.getCell(basic_Tail_Col).getStringCellValue().trim();
			Flight flight = new Flight(flightNumber, tailNumber, departureTime, arrivalTime, departure, arrival,aircraft.get(tailNumber));
			if(!checkCell(delayCell)){
				String delay = delayCell.getStringCellValue().trim();
				if(delay.equals("D")){
					String execD = resource.getCell(result_Departure_Col).getStringCellValue().trim().replaceAll("  ","T");
					String execA = resource.getCell(result_Arrival_Col).getStringCellValue().trim().replaceAll("  ","T");
					flight.setExecDepartureTime(execD);
					flight.setExecArrivalTime(execA);
					flight.setStatus(Status.delay);
				}
			}
			String reTailNumber = swapCell.getStringCellValue().trim().replaceAll("-","");
			if(!reTailNumber.equals(tailNumber)){
				flight.setStatus(Status.swap);
				flight.setSwapTailNumber(reTailNumber);
				flight.setSwapFleetID(aircraft.get(reTailNumber));
			}
			if(!checkCell(cancelCell)){
				String cancel = cancelCell.getStringCellValue().trim();
				if(cancel.equals("C")){
					flight.setStatus(Status.cancel);
				}
			}
			flights.add(flight);
		}
	}

	private boolean checkCell(Cell cell) {
		boolean result = true;
		String value = "";
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				value = String.valueOf((int) cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				value = String.valueOf(cell.getCellFormula());
				break;
			// case Cell.CELL_TYPE_BLANK:
			// break;
			default:
				break;
			}
			if (!value.trim().equals("")) {
				result = false;
			}
		}
		return result;
	}
	public ArrayList<Flight> getFlights() {
		return flights;
	}
	static void printInfo(InfoLevel level, String info) throws InvocationTargetException, InterruptedException {
		if (level == InfoLevel.Info)
			logger.info(info);
		else if (level == InfoLevel.Warn)
			logger.warn(info);
		else
			logger.error(info);

		String val = level.getName() + info;
	}
}
