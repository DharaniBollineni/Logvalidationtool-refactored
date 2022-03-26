package utilTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This program read date values from XLSX file in Java using Apache POI.
 * 
 * @author MSDEV student
 */
public class ReadTestDataUtil {
	
	public static Map<String,List<String>> testDataMap = new HashMap<>();

	static{                                                               
		try {			
			readFromExcel("C:\\DharaniCh\\SWEN589_Project\\datastore\\EDX_TEST_DATA.xlsx");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*
 * https://javarevisited.blogspot.com/2015/06/how-to-read-write-excel-file-java-poi-example.html
 * https://stackoverflow.com/questions/36109744/when-to-use-rowiterator-and-iterator-for-iterating-through-the-rows-of-an-excel
 */
	public static void readFromExcel(String file) throws IOException {
		XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
        Iterator<Row> rowIterator = myExcelSheet.iterator();// contains all rows of the excel sheet
        ArrayList<String> columndata = null;
        String rowData = "";
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();// each row
            Iterator<Cell> cellIterator = row.cellIterator();//all cell of row
            columndata = new ArrayList<>();// new arraylist for cell data of that row
            while (cellIterator.hasNext()) { 
                Cell cell = cellIterator.next();//each cell
                if(row.getRowNum() > 0){ //To filter column headings
                	System.out.println("Test");
                	System.out.println(cell.getColumnIndex());
                	System.out.println(cell.getStringCellValue());
                    if(cell.getColumnIndex() == 0) { // testcasename
                        	rowData = cell.getStringCellValue();  
                        	System.out.println(rowData);
                    } else {
                    		columndata.add(cell.getStringCellValue()); //assertvalues
                    }
                }
            }
            if(!rowData.equals("")) { // testcasename is not empty
            	testDataMap.put(rowData,columndata); // add elements to map class name and assertvalues
            }
        }
        myExcelBook.close();
        System.out.println(testDataMap);

	}

}
