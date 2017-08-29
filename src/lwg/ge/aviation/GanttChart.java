package lwg.ge.aviation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import net.sf.json.JSONArray;

public class GanttChart {
	static String dataPath = "";
	static String jsonPath = "";
	static String resultPath = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(parseArgs(args))
		{
			if(dataPath.equals("") ||jsonPath.equals("") ||resultPath.equals(""))
			{
				System.out.println("the data(or result,or json) file is null");
				return;
			}
			run();
		}
		else
		{
			System.out.println("The args format is wrong!");
			printHelp();
		}
	}
	
	public static void run(){
		ExcelParser excelParser = null;
		try {
			excelParser = new ExcelParser(dataPath,resultPath);
		} catch (InvalidFormatException | InvocationTargetException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Flight> list = excelParser.getFlights();
		JSONArray jsonArray = JSONArray.fromObject(list);
		//System.out.println(jsonArray);
		try {
			writeJSONFile(jsonArray.toString(),jsonPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		opeanBrowser();
	}
	
	public static void opeanBrowser(){
		/*//判断当前系统是否支持Java AWT Desktop扩展
        if(java.awt.Desktop.isDesktopSupported()){
            try{
                //创建一个URI实例,注意不是URL
                java.net.URI uri=java.net.URI.create("file:///C:/Users/502758724/Desktop/Flight Gantt Chart/FlightGanttChart.html");
                //获取当前系统桌面扩展
                java.awt.Desktop dp=java.awt.Desktop.getDesktop();
                //判断系统桌面是否支持要执行的功能
                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
                    //获取系统默认浏览器打开链接
                    dp.browse(uri);
                }
            }catch(java.lang.NullPointerException e){
                //此为uri为空时抛出异常
            }catch(java.io.IOException e){
                //此为无法获取系统默认浏览器
            }
        }*/
		File html = new File("resource/FlightGanttChart.html");
		
		String str = "cmd /c start \"\" \"" + html.getAbsolutePath() + "\"";  
        try {  
            Runtime.getRuntime().exec(str);  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
	}

	public static boolean parseArgs(String args[])
	{
		String entry;
		char flag;
		if(args.length==1 && (args[0].equals("-help") || args[0].equals("-Help") ||args[0].equals("-H") ||args[0].equals("-h")) )
		{
			//printHelp();
			return false;
		}
		for(int i=0;i<args.length;i++)
		{
			entry=args[i].trim();
			if(entry.charAt(1)=='=' &&(entry.charAt(0)=='A' || entry.charAt(0)=='a' ||
					entry.charAt(0)=='B' ||  entry.charAt(0)=='b' || 					
					entry.charAt(0)=='C' ||  entry.charAt(0)=='c' ))
			{
				flag=entry.charAt(0);
				if(flag=='A' ||flag=='a' )
				{					
					dataPath=entry.substring(2);
					if(!isCorrectFile(dataPath))
						return false;
				}
				else if(flag=='B' ||flag=='b')
				{
					resultPath=entry.substring(2);
					if(!isCorrectFile(resultPath))
						return false;
				}
				else if(flag=='C' ||flag=='c')
				{
					jsonPath=entry.substring(2);
				}
			}
			else 
			{
				System.out.println("this program only accept file args:A/a,B/b,C/c,and the format is like B=File_Path");
				return false;
			}
					
		}
		return true;
	}
	
	public static boolean isCorrectFile(String name)
	{
	
		if(name.length()<6)//at least X.xlsx
		{
			System.out.println("Are you adding the file format suffix,cause the name("+name+") is too short.you should input like basicdata.xlsx,rather than basicdata");
			return false;
		}
		
		if(name.charAt(0)=='"' )
		{
			if(name.charAt(name.length()-1)=='"')
				name=name.substring(1, name.length()-2);
			else
			{
				System.out.println("There is missing a right quote in the file path("+name+")");
				return false;
			}
		}
		File file=new File(name);
		if(!file.exists())
		{
			System.out.println("File("+name+") doesn't exist,please check it aggain and retry!");
			return false;
		}

		return true;
	}
	
	public static void printHelp()
	{
		System.out.println("************************************************************************");
		System.out.println("************************************************************************");
		System.out.println("************************************************************************");
		System.out.println("The cmd is like : A=data_file_dir  B=result_file dir  C=Json_file_dir \n");
		System.out.println("\nIf the dir has blank,then quotes the dir,for example B=\"Basic data.xlsx\"\n");
		System.out.println("\nMake sure the file is exisit and is correct format,and the args order is not relative!\n");
		System.out.println("\nPlus:the program only accept xlsx file!\n");
		System.out.println("For help:use -help or -h");
		System.out.println("************************************************************************");
		System.out.println("************************************************************************");
	}
	
	public static void writeJSONFile(String content, String fileName) throws Exception {
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		FileOutputStream o = null;
		try {
			o = new FileOutputStream(file);
			o.write(content.getBytes("GBK"));
			o.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
	}
}
