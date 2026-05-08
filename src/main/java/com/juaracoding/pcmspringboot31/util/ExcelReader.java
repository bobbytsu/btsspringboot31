package com.juaracoding.pcmspringboot31.util;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

public class ExcelReader {
	
	private XSSFWorkbook wBook ;
	private XSSFSheet sheet ;	
	private String values ;
	private DataFormatter dFormatter ;
	private int intRowCount;
	private int intColCount;
	private String[][] strAllData;
	private String[][] arrWithoutHeader;
	private int loopRows;
	private FileInputStream excelFile;
	private BufferedInputStream inputBuff;
	private Map<String,String> map = new HashMap<>();
	public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private List<Map<String,String>> list = new ArrayList<>();

	/** ini validasi dari mime type file yang diupload */
	public static boolean hasWorkBookFormat(MultipartFile file) {
		if (!TYPE.equals(file.getContentType())) {
			return false;
		}
		return true;
	}

	/** kalau mau coba file langsung dari controller */
	public ExcelReader(InputStream inputStream, String sheetName) {
		try {
			inputBuff = new BufferedInputStream(inputStream);
			setDataFromExcel(inputBuff,sheetName);
		} catch (Exception e) {
//			LoggingFile.logException("ExcelReader","ExcelReader(InputStream inputStream, String sheetName) --> Line 46",e, OtherConfig.getEnableLog());
		}
		finally {
			try {
				inputBuff.close();
				wBook.close();
			} catch (IOException e) {
//				LoggingFile.logException("ExcelReader","ExcelReader(InputStream inputStream, String sheetName) --> Line 53",e, OtherConfig.getEnableLog());
			}
		}
	}

//	/** kalau mau coba langsung dari file di active directory */
//	public ExcelReader(String excelPath, String sheetName) {
//		try {
//			excelFile = new FileInputStream(new File(excelPath));
//			inputBuff = new BufferedInputStream(excelFile);
//			setDataFromExcel(inputBuff,sheetName);
//		} catch (Exception e) {
////			LoggingFile.logException("ExcelReader","ExcelReader(String excelPath, String sheetName) --> Line 64",e, OtherConfig.getEnableLog());
//		}
//		finally {
//			try {
//				excelFile.close();
//				inputBuff.close();
//				wBook.close();
//			} catch (IOException e) {
////				LoggingFile.logException("ExcelReader","ExcelReader(String excelPath, String sheetName) --> Line 72",e, OtherConfig.getEnableLog());
//			}
//		}
//	}

	/**
	 * xlsx 2007 sampai saat ini
	 * xls (2003-2007)
	 * @param inputBuff
	 * @param sheetName
	 * @throws IOException
	 */
	public void setDataFromExcel(BufferedInputStream inputBuff,String sheetName) throws IOException {
		wBook = new XSSFWorkbook(inputBuff);/**/
		sheet = wBook.getSheet(sheetName);
		getRowCount();/*Must Generated first*/
		getColCount();/*Must Generated first*/
		setData();/*SET ALL DATA*/
	}
	
	/*
	 * if you want to handle manual the loops of all data in another method , you can use this method
	 */
	public Iterator<Row> getIter()
	{
		Iterator<Row> r = sheet.iterator();
		return r;
	}
	
	
	public void setData()
	{
		try
		{
			arrWithoutHeader = new String[intRowCount-1][intColCount];/*BECAUSE OF remove a Header so Row for this object must be minus 1 */
			loopRows =0;
			Iterator<Row> rX = sheet.iterator();

			while(rX.hasNext())
			{
				rX.next();//ini wajib ada sebagai penggeser cursor row di file excel
				for(int j=0;j<intColCount;j++)
				{
					if(loopRows!=0)// kunci untuk melewatkan kolom
					{
						/*BECAUSE OF remove a Header so Row for this object must be minus 1 */
						arrWithoutHeader [loopRows-1][j] = getCellData(loopRows,j).toString();
					}
				}
				loopRows++;
			}
		}catch(Exception e)
		{
		}
		this.arrWithoutHeader = arrWithoutHeader;
	}

	public String[][] getDataWithoutHeader()
	{
		return arrWithoutHeader;
	}
	
	/*GET SPECIFIC DATA USING ROW NUMBER AND COLUMN NUMBER*/
	public Object getCellData(int rowNum, int colNum)
	{
		dFormatter = new DataFormatter();
		values = dFormatter.formatCellValue(sheet.getRow(rowNum).getCell(colNum));

		return values;
	}
	
	public int getRowCount()
	{
		intRowCount = sheet.getPhysicalNumberOfRows();
		return intRowCount;
	}
	
	public int getColCount()
	{		
		intColCount = sheet.getRow(0).getPhysicalNumberOfCells();
		return intColCount;
	}
}