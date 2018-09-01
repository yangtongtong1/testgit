package PSM.Tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import hibernate.Commonfile;
import hibernate.Commonlaw;
import hibernate.Constructionelec;
import hibernate.ElecEquip;
import hibernate.FireSafety;
import hibernate.Managepara;
import hibernate.ODHequipment;
import hibernate.PODgoods;
import hibernate.Proapproval;
import hibernate.Saftyaccounts;
import hibernate.SpEquip;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.TransportSafety;
import hibernate.Weixianyuan;
import hibernate.Xiandongtai;

public class ReadExcel {
	//-----------------liuchi-readExcel----------------------//
	
		 /**
		 * 读取excel中的数据
		 * @param path
		 * @return List<Commonlaw>
		 * @author chi 2017-04-13
		 */
		public static List<Commonlaw> readCommonlawExcel(String path, String type) {

			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readCommonlawXls(path, type);
					} else if (ext.equals("xlsx")) {
						return readCommonlawXlsx(path, type);
					}
				}
			}
			return new ArrayList<Commonlaw>();
		}
		
		/**
		 * 读取后缀为xls的excel文件的数据
		 * @param path
		 * @return List<Commonlaw>
		 * @author chi 2017-04-13
		 */
		private static List<Commonlaw> readCommonlawXls(String path, String type) {

			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Commonlaw commonlaw = null;
			List<Commonlaw> list = new ArrayList<Commonlaw>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							commonlaw = new Commonlaw();
							HSSFCell name = hssfRow.getCell(0);
							HSSFCell enactDate = hssfRow.getCell(1);
							HSSFCell applyDate = hssfRow.getCell(2);
							HSSFCell fromUnit = hssfRow.getCell(3);
							HSSFCell no = hssfRow.getCell(4);
							Date enact = enactDate.getDateCellValue();
							Date apply = applyDate.getDateCellValue();
							if (!getValue(name).equals("") && enact != null && apply != null) {
								commonlaw.setName(getValue(name));
								commonlaw.setApplyDate(apply);
								commonlaw.setEnactDate(enact);
								commonlaw.setFromUnit(getValue(fromUnit));
								commonlaw.setNo(getValue(no));
								commonlaw.setType(type);
								commonlaw.setAccessory("");
								list.add(commonlaw);
							}
						}
					}
				}
			}
			return list;
		}
		
		/**
		 * 读取后缀为xlsx的excel文件的数据
		 * @param path
		 * @return List<Commonlaw>
		 * @author chi 2017-04-13
		 */
		private static List<Commonlaw> readCommonlawXlsx(String path, String type) {

			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Commonlaw commonlaw = null;
			List<Commonlaw> list = new ArrayList<Commonlaw>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							commonlaw = new Commonlaw();
							//从excel获取数据
							XSSFCell name = xssfRow.getCell(0);
							XSSFCell enactDate = xssfRow.getCell(1);
							XSSFCell applyDate = xssfRow.getCell(2);
							XSSFCell fromUnit = xssfRow.getCell(3);
							XSSFCell no = xssfRow.getCell(4);
							//将数据塞进对象中
							Date enact = enactDate.getDateCellValue();
							Date apply = applyDate.getDateCellValue();
							if (!getValue(name).equals("") && enact != null && apply != null) {
								commonlaw.setName(getValue(name));
								commonlaw.setApplyDate(apply);
								commonlaw.setEnactDate(enact);
								commonlaw.setFromUnit(getValue(fromUnit));
								commonlaw.setNo(getValue(no));
								commonlaw.setType(type);
								commonlaw.setAccessory("");
								list.add(commonlaw);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<Commonfile> readCommonfileExcel(String path, String type) {
			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readCommonfileXls(path, type);
					} else if (ext.equals("xlsx")) {
						return readCommonfileXlsx(path, type);
					}
				}
			}
			return new ArrayList<Commonfile>();
		}
		
		private static List<Commonfile> readCommonfileXls(String path, String type) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Commonfile commonfile = null;
			List<Commonfile> list = new ArrayList<Commonfile>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							commonfile = new Commonfile();
							HSSFCell name = hssfRow.getCell(0);
							HSSFCell fromUnit = hssfRow.getCell(1);
							HSSFCell no = hssfRow.getCell(2);
							if (!getValue(name).equals("")) {
								commonfile.setName(getValue(name));
								commonfile.setFromUnit(getValue(fromUnit));
								commonfile.setNo(getValue(no));
								commonfile.setType(type);
								commonfile.setAccessory("");
								list.add(commonfile);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Commonfile> readCommonfileXlsx(String path, String type) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Commonfile commonfile = null;
			List<Commonfile> list = new ArrayList<Commonfile>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							commonfile = new Commonfile();
							XSSFCell name = xssfRow.getCell(0);
							XSSFCell fromUnit = xssfRow.getCell(1);
							XSSFCell no = xssfRow.getCell(2);
							if (!getValue(name).equals("")) {
								commonfile.setName(getValue(name));
								commonfile.setFromUnit(getValue(fromUnit));
								commonfile.setNo(getValue(no));
								commonfile.setType(type);
								commonfile.setAccessory("");
								list.add(commonfile);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<Tezhongpeople> readTezhongpeopleExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readTezhongpeopleXls(path);
					} else if (ext.equals("xlsx")) {
						return readTezhongpeopleXlsx(path);
					}
				}
			}
			return new ArrayList<Tezhongpeople>();
		}
		
		private static List<Tezhongpeople> readTezhongpeopleXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Tezhongpeople tezhongpeople = null;
			List<Tezhongpeople> list = new ArrayList<Tezhongpeople>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							tezhongpeople = new Tezhongpeople();
							HSSFCell fbname = hssfRow.getCell(0);
							HSSFCell type = hssfRow.getCell(1);
							HSSFCell name = hssfRow.getCell(2);
							HSSFCell sex = hssfRow.getCell(3);
							HSSFCell cardName = hssfRow.getCell(4);
							HSSFCell cardTime = hssfRow.getCell(5);
							HSSFCell useTime = hssfRow.getCell(6);
							HSSFCell cardNo = hssfRow.getCell(7);
							HSSFCell cardPlace = hssfRow.getCell(8);
							HSSFCell ps = hssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								tezhongpeople.setFbName(getValue(fbname));
								tezhongpeople.setType(getValue(type));
								tezhongpeople.setName(getValue(name));
								tezhongpeople.setSex(getValue(sex));
								tezhongpeople.setCardName(getValue(cardName));								
								tezhongpeople.setCardTime(cardTime.getDateCellValue());
								tezhongpeople.setUseTime(useTime.getDateCellValue());
								tezhongpeople.setCardNo(getValue(cardNo));
								tezhongpeople.setCardPlace(getValue(cardPlace));
								tezhongpeople.setPs(getValue(ps));
								tezhongpeople.setAccessory("");
								list.add(tezhongpeople);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Tezhongpeople> readTezhongpeopleXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Tezhongpeople tezhongpeople = null;
			List<Tezhongpeople> list = new ArrayList<Tezhongpeople>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							tezhongpeople = new Tezhongpeople();
							XSSFCell fbname = xssfRow.getCell(0);
							XSSFCell type = xssfRow.getCell(1);
							XSSFCell name = xssfRow.getCell(2);
							XSSFCell sex = xssfRow.getCell(3);
							XSSFCell cardName = xssfRow.getCell(4);							
							XSSFCell cardTime = xssfRow.getCell(5);
							XSSFCell useTime = xssfRow.getCell(6);
							XSSFCell cardNo = xssfRow.getCell(7);
							XSSFCell cardPlace = xssfRow.getCell(8);
							XSSFCell ps = xssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								tezhongpeople.setFbName(getValue(fbname));
								tezhongpeople.setType(getValue(type));
								tezhongpeople.setName(getValue(name));
								tezhongpeople.setSex(getValue(sex));
								tezhongpeople.setCardName(getValue(cardName));
								tezhongpeople.setCardTime(cardTime.getDateCellValue());
								tezhongpeople.setUseTime(useTime.getDateCellValue());
								tezhongpeople.setCardNo(getValue(cardNo));
								tezhongpeople.setCardPlace(getValue(cardPlace));
								tezhongpeople.setPs(getValue(ps));
								tezhongpeople.setAccessory("");
								list.add(tezhongpeople);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<Tezhongsbpeople> readTezhongsbpeopleExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readTezhongsbpeopleXls(path);
					} else if (ext.equals("xlsx")) {
						return readTezhongsbpeopleXlsx(path);
					}
				}
			}
			return new ArrayList<Tezhongsbpeople>();
		}
		
		private static List<Tezhongsbpeople> readTezhongsbpeopleXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Tezhongsbpeople tezhongsbpeople = null;
			List<Tezhongsbpeople> list = new ArrayList<Tezhongsbpeople>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							tezhongsbpeople = new Tezhongsbpeople();
							HSSFCell fbname = hssfRow.getCell(0);
							HSSFCell type = hssfRow.getCell(1);
							HSSFCell project = hssfRow.getCell(2);
							HSSFCell name = hssfRow.getCell(3);							
							HSSFCell gender = hssfRow.getCell(4);							
							HSSFCell cardNo = hssfRow.getCell(5);
							HSSFCell beginTime = hssfRow.getCell(6);
							HSSFCell validTime = hssfRow.getCell(7);
							HSSFCell unit = hssfRow.getCell(8);
							HSSFCell ps = hssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								tezhongsbpeople.setFbName(getValue(fbname));
								tezhongsbpeople.setType(getValue(type));
								tezhongsbpeople.setProject(getValue(project));
								tezhongsbpeople.setName(getValue(name));
								tezhongsbpeople.setGender(getValue(gender));
								tezhongsbpeople.setCardNo(getValue(cardNo));
								tezhongsbpeople.setBeginTime(beginTime.getDateCellValue());
								tezhongsbpeople.setValidTime(validTime.getDateCellValue());
								tezhongsbpeople.setUnit(getValue(unit));
								tezhongsbpeople.setPs(getValue(ps));
								tezhongsbpeople.setAccessory("");
								list.add(tezhongsbpeople);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Tezhongsbpeople> readTezhongsbpeopleXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Tezhongsbpeople tezhongsbpeople = null;
			List<Tezhongsbpeople> list = new ArrayList<Tezhongsbpeople>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							tezhongsbpeople = new Tezhongsbpeople();
							XSSFCell fbname = xssfRow.getCell(0);
							XSSFCell type = xssfRow.getCell(1);
							XSSFCell project = xssfRow.getCell(2);
							XSSFCell name = xssfRow.getCell(3);							
							XSSFCell gender = xssfRow.getCell(4);							
							XSSFCell cardNo = xssfRow.getCell(5);
							XSSFCell beginTime = xssfRow.getCell(6);
							XSSFCell validTime = xssfRow.getCell(7);
							XSSFCell unit = xssfRow.getCell(8);
							XSSFCell ps = xssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								tezhongsbpeople.setFbName(getValue(fbname));
								tezhongsbpeople.setType(getValue(type));
								tezhongsbpeople.setProject(getValue(project));
								tezhongsbpeople.setName(getValue(name));
								tezhongsbpeople.setGender(getValue(gender));
								tezhongsbpeople.setCardNo(getValue(cardNo));
								tezhongsbpeople.setBeginTime(beginTime.getDateCellValue());
								tezhongsbpeople.setValidTime(validTime.getDateCellValue());
								tezhongsbpeople.setUnit(getValue(unit));
								tezhongsbpeople.setPs(getValue(ps));
								tezhongsbpeople.setAccessory("");
								list.add(tezhongsbpeople);
							}
						}
					}
				}
			}
			return list;
		}

		public static List<Xiandongtai> readXiandongtaiExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readXiandongtaiXls(path);
					} else if (ext.equals("xlsx")) {
						return readXiandongtaiXlsx(path);
					}
				}
			}
			return new ArrayList<Xiandongtai>();
		}
		
		private static List<Xiandongtai> readXiandongtaiXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Xiandongtai xiandongtai = null;
			List<Xiandongtai> list = new ArrayList<Xiandongtai>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							xiandongtai = new Xiandongtai();
							HSSFCell repoter = hssfRow.getCell(0);
							HSSFCell gangwei = hssfRow.getCell(1);
							HSSFCell name = hssfRow.getCell(2);							
							HSSFCell sex = hssfRow.getCell(3);
							HSSFCell sfz = hssfRow.getCell(4);
							HSSFCell intimeplan = hssfRow.getCell(5);							
							HSSFCell intimereal = hssfRow.getCell(6);					
							HSSFCell lvetimeplan = hssfRow.getCell(7);							
							HSSFCell lvetimereal = hssfRow.getCell(8);							
							HSSFCell phone = hssfRow.getCell(9);
							HSSFCell istijian = hssfRow.getCell(10);							
							HSSFCell isgsbx = hssfRow.getCell(11);
							if (!getValue(name).equals("")) {
								xiandongtai.setRepoter(getValue(repoter));
								xiandongtai.setGangwei(getValue(gangwei));
								xiandongtai.setName(getValue(name));
								xiandongtai.setSex(getValue(sex));
								xiandongtai.setSfz(getValue(sfz));
								xiandongtai.setIntimeplan(intimeplan.getDateCellValue());
								xiandongtai.setIntimereal(getValue(intimereal));
								xiandongtai.setLvetimeplan(lvetimeplan.getDateCellValue());
								xiandongtai.setLvetimereal(getValue(lvetimereal));
								xiandongtai.setPhone(getValue(phone));
								xiandongtai.setIstijian(getValue(istijian));
								xiandongtai.setIsgsbx(getValue(isgsbx));
								xiandongtai.setAccessory("");
								list.add(xiandongtai);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Xiandongtai> readXiandongtaiXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Xiandongtai xiandongtai = null;
			List<Xiandongtai> list = new ArrayList<Xiandongtai>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							xiandongtai = new Xiandongtai();
							XSSFCell repoter = xssfRow.getCell(0);
							XSSFCell gangwei = xssfRow.getCell(1);
							XSSFCell name = xssfRow.getCell(2);							
							XSSFCell sex = xssfRow.getCell(3);
							XSSFCell sfz = xssfRow.getCell(4);
							XSSFCell intimeplan = xssfRow.getCell(5);							
							XSSFCell intimereal = xssfRow.getCell(6);					
							XSSFCell lvetimeplan = xssfRow.getCell(7);							
							XSSFCell lvetimereal = xssfRow.getCell(8);							
							XSSFCell phone = xssfRow.getCell(9);
							XSSFCell istijian = xssfRow.getCell(10);							
							XSSFCell isgsbx = xssfRow.getCell(11);
							if (!getValue(name).equals("")) {
								xiandongtai.setRepoter(getValue(repoter));
								xiandongtai.setGangwei(getValue(gangwei));
								xiandongtai.setName(getValue(name));
								xiandongtai.setSex(getValue(sex));
								xiandongtai.setSfz(getValue(sfz));
								xiandongtai.setIntimeplan(intimeplan.getDateCellValue());
								xiandongtai.setIntimereal(getValue(intimereal));
								xiandongtai.setLvetimeplan(lvetimeplan.getDateCellValue());
								xiandongtai.setLvetimereal(getValue(lvetimereal));
								xiandongtai.setPhone(getValue(phone));
								xiandongtai.setIstijian(getValue(istijian));
								xiandongtai.setIsgsbx(getValue(isgsbx));
								xiandongtai.setAccessory("");
								list.add(xiandongtai);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<SpEquip> readSpEquipExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readSpEquipXls(path);
					} else if (ext.equals("xlsx")) {
						return readSpEquipXlsx(path);
					}
				}
			}
			return new ArrayList<SpEquip>();
		}
		
		private static List<SpEquip> readSpEquipXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SpEquip spEquip = null;
			List<SpEquip> list = new ArrayList<SpEquip>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							spEquip = new SpEquip();
							HSSFCell name = hssfRow.getCell(0);
							HSSFCell type = hssfRow.getCell(1);
							HSSFCell purpose = hssfRow.getCell(2);
							HSSFCell inDate = hssfRow.getCell(3);
							HSSFCell outDate = hssfRow.getCell(4);
							HSSFCell registNo = hssfRow.getCell(5);
							HSSFCell kind = hssfRow.getCell(6);
							HSSFCell manuUnit = hssfRow.getCell(7);
							HSSFCell purchaseDate = hssfRow.getCell(8);
							HSSFCell installUnit = hssfRow.getCell(9);
							HSSFCell checkStatus = hssfRow.getCell(10);
							HSSFCell useStatus = hssfRow.getCell(11);	
							HSSFCell majorStatus = hssfRow.getCell(12);	
							HSSFCell otherStatus = hssfRow.getCell(13);
							if (!getValue(name).equals("")) {
								spEquip.setName(getValue(name));
								spEquip.setType(getValue(type));
								spEquip.setPurpose(getValue(purpose));
								spEquip.setInDate(getValue(inDate));
								spEquip.setOutDate(getValue(outDate));
								spEquip.setRegistNo(getValue(registNo));
								spEquip.setKind(getValue(kind));
								spEquip.setManuUnit(getValue(manuUnit));
								spEquip.setPurchaseDate(getValue(purchaseDate));
								spEquip.setInstallUnit(getValue(installUnit));
								spEquip.setCheckStatus(getValue(checkStatus));
								spEquip.setUseStatus(getValue(useStatus));
								spEquip.setMajorStatus(getValue(majorStatus));
								spEquip.setOtherStatus(getValue(otherStatus));
								spEquip.setAccessory("");
								list.add(spEquip);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<SpEquip> readSpEquipXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			SpEquip spEquip = null;
			List<SpEquip> list = new ArrayList<SpEquip>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							spEquip = new SpEquip();
							XSSFCell name = xssfRow.getCell(0);
							XSSFCell type = xssfRow.getCell(1);
							XSSFCell purpose = xssfRow.getCell(2);
							XSSFCell inDate = xssfRow.getCell(3);
							XSSFCell outDate = xssfRow.getCell(4);
							XSSFCell registNo = xssfRow.getCell(5);
							XSSFCell kind = xssfRow.getCell(6);
							XSSFCell manuUnit = xssfRow.getCell(7);
							XSSFCell purchaseDate = xssfRow.getCell(8);
							XSSFCell installUnit = xssfRow.getCell(9);
							XSSFCell checkStatus = xssfRow.getCell(10);
							XSSFCell useStatus = xssfRow.getCell(11);	
							XSSFCell majorStatus = xssfRow.getCell(12);	
							XSSFCell otherStatus = xssfRow.getCell(13);
							if (!getValue(name).equals("")) {
								spEquip.setName(getValue(name));
								spEquip.setType(getValue(type));
								spEquip.setPurpose(getValue(purpose));
								spEquip.setInDate(getValue(inDate));
								spEquip.setOutDate(getValue(outDate));
								spEquip.setRegistNo(getValue(registNo));
								spEquip.setKind(getValue(kind));
								spEquip.setManuUnit(getValue(manuUnit));
								spEquip.setPurchaseDate(getValue(outDate));
								spEquip.setInstallUnit(getValue(installUnit));
								spEquip.setCheckStatus(getValue(checkStatus));
								spEquip.setUseStatus(getValue(useStatus));
								spEquip.setMajorStatus(getValue(majorStatus));
								spEquip.setOtherStatus(getValue(otherStatus));
								spEquip.setAccessory("");
								list.add(spEquip);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<ElecEquip> readElecEquipExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readElecEquipXls(path);
					} else if (ext.equals("xlsx")) {
						return readElecEquipXlsx(path);
					}
				}
			}
			return new ArrayList<ElecEquip>();
		}
		
		private static List<ElecEquip> readElecEquipXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ElecEquip elecEquip = null;
			List<ElecEquip> list = new ArrayList<ElecEquip>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							elecEquip = new ElecEquip();
							HSSFCell equipNo = hssfRow.getCell(0);
							HSSFCell name = hssfRow.getCell(1);
							HSSFCell type = hssfRow.getCell(2);
							HSSFCell manuUnit = hssfRow.getCell(3);
							HSSFCell quantity = hssfRow.getCell(4);
							HSSFCell unit = hssfRow.getCell(5);
							HSSFCell inDate = hssfRow.getCell(6);
							HSSFCell registNo = hssfRow.getCell(7);
							HSSFCell usePlace = hssfRow.getCell(8);
							HSSFCell responser = hssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								elecEquip.setEquipNo(getValue(equipNo));
								elecEquip.setName(getValue(name));
								elecEquip.setType(getValue(type));
								elecEquip.setManuUnit(getValue(manuUnit));
								elecEquip.setQuantity(getValue(quantity));
								elecEquip.setUnit(getValue(unit));
								elecEquip.setInDate(inDate.getDateCellValue());
								elecEquip.setRegistNo(getValue(registNo));
								elecEquip.setUsePlace(getValue(usePlace));
								elecEquip.setResponser(getValue(responser));
								elecEquip.setAccessory("");
								list.add(elecEquip);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<ElecEquip> readElecEquipXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ElecEquip elecEquip = null;
			List<ElecEquip> list = new ArrayList<ElecEquip>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							elecEquip = new ElecEquip();
							XSSFCell equipNo = xssfRow.getCell(0);
							XSSFCell name = xssfRow.getCell(1);
							XSSFCell type = xssfRow.getCell(2);
							XSSFCell manuUnit = xssfRow.getCell(3);
							XSSFCell quantity = xssfRow.getCell(4);
							XSSFCell unit = xssfRow.getCell(5);
							XSSFCell inDate = xssfRow.getCell(6);
							XSSFCell registNo = xssfRow.getCell(7);
							XSSFCell usePlace = xssfRow.getCell(8);
							XSSFCell responser = xssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								elecEquip.setEquipNo(getValue(equipNo));
								elecEquip.setName(getValue(name));
								elecEquip.setType(getValue(type));
								elecEquip.setManuUnit(getValue(manuUnit));
								elecEquip.setQuantity(getValue(quantity));
								elecEquip.setUnit(getValue(unit));
								elecEquip.setInDate(inDate.getDateCellValue());
								elecEquip.setRegistNo(getValue(registNo));
								elecEquip.setUsePlace(getValue(usePlace));
								elecEquip.setResponser(getValue(responser));
								elecEquip.setAccessory("");
								list.add(elecEquip);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<FireSafety> readFireSafetyExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readFireSafetyXls(path);
					} else if (ext.equals("xlsx")) {
						return readFireSafetyXlsx(path);
					}
				}
			}
			return new ArrayList<FireSafety>();
		}
		
		private static List<FireSafety> readFireSafetyXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FireSafety fireSafety = null;
			List<FireSafety> list = new ArrayList<FireSafety>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							fireSafety = new FireSafety();
							HSSFCell name = hssfRow.getCell(0);
							HSSFCell model = hssfRow.getCell(1);
							HSSFCell num = hssfRow.getCell(2);
							HSSFCell department = hssfRow.getCell(3);
							HSSFCell acqDate = hssfRow.getCell(4);
							HSSFCell changeDate = hssfRow.getCell(5);
							HSSFCell place = hssfRow.getCell(6);
							HSSFCell chargePerson = hssfRow.getCell(7);
							HSSFCell checkPeriodically = hssfRow.getCell(8);
							HSSFCell checkResult = hssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								fireSafety.setName(getValue(name));
								fireSafety.setModel(getValue(model));
								fireSafety.setNum(getValue(num));
								fireSafety.setDepartment(getValue(department));
								fireSafety.setAcqDate(getValue(acqDate));
								fireSafety.setChangeDate(getValue(changeDate));
								fireSafety.setPlace(getValue(place));
								fireSafety.setChargePerson(getValue(chargePerson));
								fireSafety.setCheckPeriodically(getValue(checkPeriodically));
								fireSafety.setCheckResult(getValue(checkResult));
								list.add(fireSafety);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<FireSafety> readFireSafetyXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FireSafety fireSafety = null;
			List<FireSafety> list = new ArrayList<FireSafety>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							fireSafety = new FireSafety();
							XSSFCell name = xssfRow.getCell(0);
							XSSFCell model = xssfRow.getCell(1);
							XSSFCell num = xssfRow.getCell(2);
							XSSFCell department = xssfRow.getCell(3);
							XSSFCell acqDate = xssfRow.getCell(4);
							XSSFCell changeDate = xssfRow.getCell(5);
							XSSFCell place = xssfRow.getCell(6);
							XSSFCell chargePerson = xssfRow.getCell(7);
							XSSFCell checkPeriodically = xssfRow.getCell(8);
							XSSFCell checkResult = xssfRow.getCell(9);
							if (!getValue(name).equals("")) {
								fireSafety.setName(getValue(name));
								fireSafety.setModel(getValue(model));
								fireSafety.setNum(getValue(num));
								fireSafety.setDepartment(getValue(department));
								fireSafety.setAcqDate(getValue(acqDate));
								fireSafety.setChangeDate(getValue(changeDate));
								fireSafety.setPlace(getValue(place));
								fireSafety.setChargePerson(getValue(chargePerson));
								fireSafety.setCheckPeriodically(getValue(checkPeriodically));
								fireSafety.setCheckResult(getValue(checkResult));
								list.add(fireSafety);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<PODgoods> readPODgoodsExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readPODgoodsXls(path);
					} else if (ext.equals("xlsx")) {
						return readPODgoodsXlsx(path);
					}
				}
			}
			return new ArrayList<PODgoods>();
		}
		
		private static List<PODgoods> readPODgoodsXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			PODgoods pODgoods = null;
			List<PODgoods> list = new ArrayList<PODgoods>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							pODgoods = new PODgoods();
							HSSFCell name = hssfRow.getCell(0);
							HSSFCell deliveryTime = hssfRow.getCell(1);
							HSSFCell num = hssfRow.getCell(2);
							HSSFCell personName = hssfRow.getCell(3);
							HSSFCell department = hssfRow.getCell(4);
							HSSFCell timeLimit = hssfRow.getCell(5);
							HSSFCell comment = hssfRow.getCell(6);
							if (!getValue(name).equals("")) {
								pODgoods.setName(getValue(name));
								pODgoods.setDeliveryTime(getValue(deliveryTime));
								pODgoods.setNum(getValue(num));
								pODgoods.setPersonName(getValue(personName));
								pODgoods.setDepartment(getValue(department));
								pODgoods.setTimeLimit(getValue(timeLimit));
								pODgoods.setComment(getValue(comment));
								list.add(pODgoods);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<PODgoods> readPODgoodsXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			PODgoods pODgoods = null;
			List<PODgoods> list = new ArrayList<PODgoods>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							pODgoods = new PODgoods();
							XSSFCell name = xssfRow.getCell(0);
							XSSFCell deliveryTime = xssfRow.getCell(1);
							XSSFCell num = xssfRow.getCell(2);
							XSSFCell personName = xssfRow.getCell(3);
							XSSFCell department = xssfRow.getCell(4);
							XSSFCell timeLimit = xssfRow.getCell(5);
							XSSFCell comment = xssfRow.getCell(6);
							if (!getValue(name).equals("")) {
								pODgoods.setName(getValue(name));
								pODgoods.setDeliveryTime(getValue(deliveryTime));
								pODgoods.setNum(getValue(num));
								pODgoods.setPersonName(getValue(personName));
								pODgoods.setDepartment(getValue(department));
								pODgoods.setTimeLimit(getValue(timeLimit));
								pODgoods.setComment(getValue(comment));
								list.add(pODgoods);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<ODHequipment> readODHequipmentExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readODHequipmentXls(path);
					} else if (ext.equals("xlsx")) {
						return readODHequipmentXlsx(path);
					}
				}
			}
			return new ArrayList<ODHequipment>();
		}
		
		private static List<ODHequipment> readODHequipmentXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ODHequipment oDHequipment = null;
			List<ODHequipment> list = new ArrayList<ODHequipment>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							oDHequipment = new ODHequipment();
							HSSFCell name = hssfRow.getCell(0);
							HSSFCell model = hssfRow.getCell(1);
							HSSFCell department = hssfRow.getCell(2);
							HSSFCell num = hssfRow.getCell(3);
							HSSFCell buyTime = hssfRow.getCell(4);
							HSSFCell serialNumber = hssfRow.getCell(5);
							HSSFCell place = hssfRow.getCell(6);
							HSSFCell responsibility = hssfRow.getCell(7);
							if (!getValue(name).equals("")) {
								oDHequipment.setName(getValue(name));
								oDHequipment.setModel(getValue(model));
								oDHequipment.setDepartment(getValue(department));
								oDHequipment.setNum(getValue(num));
								oDHequipment.setBuyTime(getValue(buyTime));
								oDHequipment.setSerialNumber(getValue(serialNumber));
								oDHequipment.setPlace(getValue(place));
								oDHequipment.setResponsibility(getValue(responsibility));
								list.add(oDHequipment);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<ODHequipment> readODHequipmentXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ODHequipment oDHequipment = null;
			List<ODHequipment> list = new ArrayList<ODHequipment>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							oDHequipment = new ODHequipment();
							XSSFCell name = xssfRow.getCell(0);
							XSSFCell model = xssfRow.getCell(1);
							XSSFCell department = xssfRow.getCell(2);
							XSSFCell num = xssfRow.getCell(3);
							XSSFCell buyTime = xssfRow.getCell(4);
							XSSFCell serialNumber = xssfRow.getCell(5);
							XSSFCell place = xssfRow.getCell(6);
							XSSFCell responsibility = xssfRow.getCell(7);
							if (!getValue(name).equals("")) {
								oDHequipment.setName(getValue(name));
								oDHequipment.setModel(getValue(model));
								oDHequipment.setDepartment(getValue(department));
								oDHequipment.setNum(getValue(num));
								oDHequipment.setBuyTime(getValue(buyTime));
								oDHequipment.setSerialNumber(getValue(serialNumber));
								oDHequipment.setPlace(getValue(place));
								oDHequipment.setResponsibility(getValue(responsibility));
								list.add(oDHequipment);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<Weixianyuan> readWeixianyuanExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readWeixianyuanXls(path);
					} else if (ext.equals("xlsx")) {
						return readWeixianyuanXlsx(path);
					}
				}
			}
			return new ArrayList<Weixianyuan>();
		}
		
//		private static List<Weixianyuan> readWeixianyuanXls(String path) {
//			HSSFWorkbook hssfWorkbook = null;
//			try {
//				InputStream is = new FileInputStream(path);
//				hssfWorkbook = new HSSFWorkbook(is);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Weixianyuan weixianyuan = null;
//			List<Weixianyuan> list = new ArrayList<Weixianyuan>();
//			if (hssfWorkbook != null) {
//				// Read the Sheet
//				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
//					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
//					if (hssfSheet == null) {
//						continue;
//					}
//					// Read the Row
//					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
//						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
//						if (hssfRow != null) {
//							weixianyuan = new Weixianyuan();
//							HSSFCell cell0 = hssfRow.getCell(0);
//							HSSFCell cell1 = hssfRow.getCell(1);
//							HSSFCell cell2 = hssfRow.getCell(2);
//							HSSFCell cell3 = hssfRow.getCell(3);
//							HSSFCell cell4 = hssfRow.getCell(4);
//							if (!getValue(cell0).equals("")) {
//								weixianyuan.setJobActivity(getValue(cell0));
//								weixianyuan.setBadFactor(getValue(cell1));
//								weixianyuan.setBadEvent(getValue(cell2));
//								weixianyuan.setRank(getValue(cell3));
//								weixianyuan.setControlAction(getValue(cell4));
//								weixianyuan.setYijian("");
//								weixianyuan.setAccessory("");
//								list.add(weixianyuan);
//							}
//						}
//					}
//				}
//			}
//			return list;
//		}
//		
//		private static List<Weixianyuan> readWeixianyuanXlsx(String path) {
//			XSSFWorkbook xssfWorkbook = null;
//			try {
//				InputStream is = new FileInputStream(path);
//				xssfWorkbook = new XSSFWorkbook(is);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Weixianyuan weixianyuan = null;
//			List<Weixianyuan> list = new ArrayList<Weixianyuan>();
//			if(xssfWorkbook!=null){
//				// Read the Sheet
//				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
//					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
//					if (xssfSheet == null) {
//						continue;
//					}
//					// Read the Row
//					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
//						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
//						if (xssfRow != null) {
//							weixianyuan = new Weixianyuan();
//							XSSFCell cell0 = xssfRow.getCell(0);
//							XSSFCell cell1 = xssfRow.getCell(1);
//							XSSFCell cell2 = xssfRow.getCell(2);
//							XSSFCell cell3 = xssfRow.getCell(3);
//							XSSFCell cell4 = xssfRow.getCell(4);
//							if (!getValue(cell0).equals("")) {
//								weixianyuan.setJobActivity(getValue(cell0));
//								weixianyuan.setBadFactor(getValue(cell1));
//								weixianyuan.setBadEvent(getValue(cell2));
//								weixianyuan.setRank(getValue(cell3));
//								weixianyuan.setControlAction(getValue(cell4));
//								weixianyuan.setYijian("");
//								weixianyuan.setAccessory("");
//								list.add(weixianyuan);
//							}
//						}
//					}
//				}
//			}
//			return list;
//		}
		
		private static List<Weixianyuan> readWeixianyuanXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Weixianyuan> list = new ArrayList<Weixianyuan>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							HSSFCell cell0 = hssfRow.getCell(0);
							HSSFCell cell1 = hssfRow.getCell(1);
							HSSFCell cell2 = hssfRow.getCell(2);
							HSSFCell cell3 = hssfRow.getCell(3);
							HSSFCell cell4 = hssfRow.getCell(4);
							HSSFCell cell5 = hssfRow.getCell(5);
							HSSFCell cell6 = hssfRow.getCell(6);
							HSSFCell cell7 = hssfRow.getCell(7);
							if (!getValue(cell0).equals("")) {
								Weixianyuan weixianyuan = new Weixianyuan();
								weixianyuan.setJobTime(cell0.getDateCellValue());
								weixianyuan.setJobLocation(getValue(cell1));
								weixianyuan.setJobContent(getValue(cell2));
								weixianyuan.setMainRisk(getValue(cell3));
								weixianyuan.setRiskRank(getValue(cell4));
								weixianyuan.setPreAction(getValue(cell5));
								weixianyuan.setJobMan(getValue(cell6));
								weixianyuan.setJobJiandu(getValue(cell7));
								list.add(weixianyuan);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Weixianyuan> readWeixianyuanXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Weixianyuan> list = new ArrayList<Weixianyuan>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							XSSFCell cell0 = xssfRow.getCell(0);
							XSSFCell cell1 = xssfRow.getCell(1);
							XSSFCell cell2 = xssfRow.getCell(2);
							XSSFCell cell3 = xssfRow.getCell(3);
							XSSFCell cell4 = xssfRow.getCell(4);
							XSSFCell cell5 = xssfRow.getCell(5);
							XSSFCell cell6 = xssfRow.getCell(6);
							XSSFCell cell7 = xssfRow.getCell(7);
							if (!getValue(cell0).equals("")) {
								Weixianyuan weixianyuan = new Weixianyuan();
								weixianyuan.setJobTime(cell0.getDateCellValue());
								weixianyuan.setJobLocation(getValue(cell1));
								weixianyuan.setJobContent(getValue(cell2));
								weixianyuan.setMainRisk(getValue(cell3));
								weixianyuan.setRiskRank(getValue(cell4));
								weixianyuan.setPreAction(getValue(cell5));
								weixianyuan.setJobMan(getValue(cell6));
								weixianyuan.setJobJiandu(getValue(cell7));
								list.add(weixianyuan);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<Managepara> readManagepara1Excel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readManagepara1Xls(path);
					} else if (ext.equals("xlsx")) {
						return readManagepara1Xlsx(path);
					}
				}
			}
			return new ArrayList<Managepara>();
		}
		
		private static List<Managepara> readManagepara1Xls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Managepara managepara = null;
			List<Managepara> list = new ArrayList<Managepara>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							managepara = new Managepara();
							HSSFCell cell0 = hssfRow.getCell(0);
							HSSFCell cell1 = hssfRow.getCell(1);
							HSSFCell cell2 = hssfRow.getCell(2);
							HSSFCell cell3 = hssfRow.getCell(3);
							HSSFCell cell4 = hssfRow.getCell(4);
							HSSFCell cell5 = hssfRow.getCell(5);
							HSSFCell cell6 = hssfRow.getCell(6);
							if (!getValue(cell0).equals("")) {
								managepara.setContent(getValue(cell0));
								managepara.setType(getValue(cell1));
								managepara.setQuantity(getValue(cell2));
								managepara.setUnit(getValue(cell3));
								managepara.setState(getValue(cell4));
								managepara.setPlace(getValue(cell5));
								managepara.setResponsible(getValue(cell6));
								managepara.setAccessory("");
								managepara.setFbunit("");
								list.add(managepara);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Managepara> readManagepara1Xlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Managepara managepara = null;
			List<Managepara> list = new ArrayList<Managepara>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							managepara = new Managepara();
							XSSFCell cell0 = xssfRow.getCell(0);
							XSSFCell cell1 = xssfRow.getCell(1);
							XSSFCell cell2 = xssfRow.getCell(2);
							XSSFCell cell3 = xssfRow.getCell(3);
							XSSFCell cell4 = xssfRow.getCell(4);
							XSSFCell cell5 = xssfRow.getCell(5);
							XSSFCell cell6 = xssfRow.getCell(6);
							if (!getValue(cell0).equals("")) {
								managepara.setContent(getValue(cell0));
								managepara.setType(getValue(cell1));
								managepara.setQuantity(getValue(cell2));
								managepara.setUnit(getValue(cell3));
								managepara.setState(getValue(cell4));
								managepara.setPlace(getValue(cell5));
								managepara.setResponsible(getValue(cell6));
								managepara.setAccessory("");
								managepara.setFbunit("");
								list.add(managepara);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<Managepara> readManagepara2Excel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readManagepara2Xls(path);
					} else if (ext.equals("xlsx")) {
						return readManagepara2Xlsx(path);
					}
				}
			}
			return new ArrayList<Managepara>();
		}
		
		private static List<Managepara> readManagepara2Xls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Managepara managepara = null;
			List<Managepara> list = new ArrayList<Managepara>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							managepara = new Managepara();
							HSSFCell cell0 = hssfRow.getCell(0);
							HSSFCell cell1 = hssfRow.getCell(1);
							HSSFCell cell2 = hssfRow.getCell(2);
							HSSFCell cell3 = hssfRow.getCell(3);
							HSSFCell cell4 = hssfRow.getCell(4);
							HSSFCell cell5 = hssfRow.getCell(5);
							if (!getValue(cell0).equals("")) {
								managepara.setContent(getValue(cell0));
								managepara.setType(getValue(cell1));
								managepara.setQuantity(getValue(cell2));
								managepara.setState(getValue(cell3));
								managepara.setPlace(getValue(cell4));
								managepara.setResponsible(getValue(cell5));
								managepara.setUnit("");
								managepara.setAccessory("");
								managepara.setFbunit("");
								list.add(managepara);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Managepara> readManagepara2Xlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Managepara managepara = null;
			List<Managepara> list = new ArrayList<Managepara>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							managepara = new Managepara();
							XSSFCell cell0 = xssfRow.getCell(0);
							XSSFCell cell1 = xssfRow.getCell(1);
							XSSFCell cell2 = xssfRow.getCell(2);
							XSSFCell cell3 = xssfRow.getCell(3);
							XSSFCell cell4 = xssfRow.getCell(4);
							XSSFCell cell5 = xssfRow.getCell(5);
							if (!getValue(cell0).equals("")) {
								managepara.setContent(getValue(cell0));
								managepara.setType(getValue(cell1));
								managepara.setQuantity(getValue(cell2));
								managepara.setState(getValue(cell3));
								managepara.setPlace(getValue(cell4));
								managepara.setResponsible(getValue(cell5));
								managepara.setUnit("");
								managepara.setAccessory("");
								managepara.setFbunit("");
								list.add(managepara);
							}
						}
					}
				}
			}
			return list;
		}


		public static List<Managepara> readManagepara3Excel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readManagepara3Xls(path);
					} else if (ext.equals("xlsx")) {
						return readManagepara3Xlsx(path);
					}
				}
			}
			return new ArrayList<Managepara>();
		}
		
		private static List<Managepara> readManagepara3Xls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Managepara managepara = null;
			List<Managepara> list = new ArrayList<Managepara>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							managepara = new Managepara();
							HSSFCell cell0 = hssfRow.getCell(0);
							HSSFCell cell1 = hssfRow.getCell(1);
							HSSFCell cell2 = hssfRow.getCell(2);
							HSSFCell cell3 = hssfRow.getCell(3);
							HSSFCell cell4 = hssfRow.getCell(4);
							HSSFCell cell5 = hssfRow.getCell(5);
							HSSFCell cell6 = hssfRow.getCell(6);
							HSSFCell cell7 = hssfRow.getCell(7);
							if (!getValue(cell0).equals("")) {
								managepara.setFbunit(getValue(cell0));
								managepara.setContent(getValue(cell1));
								managepara.setType(getValue(cell2));
								managepara.setQuantity(getValue(cell3));
								managepara.setUnit(getValue(cell4));
								managepara.setState(getValue(cell5));
								managepara.setPlace(getValue(cell6));
								managepara.setResponsible(getValue(cell7));
								managepara.setAccessory("");
								list.add(managepara);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Managepara> readManagepara3Xlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Managepara managepara = null;
			List<Managepara> list = new ArrayList<Managepara>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							managepara = new Managepara();
							XSSFCell cell0 = xssfRow.getCell(0);
							XSSFCell cell1 = xssfRow.getCell(1);
							XSSFCell cell2 = xssfRow.getCell(2);
							XSSFCell cell3 = xssfRow.getCell(3);
							XSSFCell cell4 = xssfRow.getCell(4);
							XSSFCell cell5 = xssfRow.getCell(5);
							XSSFCell cell6 = xssfRow.getCell(6);
							XSSFCell cell7 = xssfRow.getCell(7);
							if (!getValue(cell0).equals("")) {
								managepara.setFbunit(getValue(cell0));
								managepara.setContent(getValue(cell1));
								managepara.setType(getValue(cell2));
								managepara.setQuantity(getValue(cell3));
								managepara.setUnit(getValue(cell4));
								managepara.setState(getValue(cell5));
								managepara.setPlace(getValue(cell6));
								managepara.setResponsible(getValue(cell7));
								managepara.setAccessory("");
								list.add(managepara);
							}
						}
					}
				}
			}
			return list;
		}


        public static List<Proapproval> readProapprovalExcel(String path) {          
            if (path != null && !path.equals("")) {
                String ext = getExt(path);
                if (ext!=null && !ext.equals("")) {
                    if (ext.equals("xls")) {
                        return readProapprovalXls(path);
                    } else if (ext.equals("xlsx")) {
                        return readProapprovalXlsx(path);
                    }
                }
            }
            return new ArrayList<Proapproval>();
        }
        
        private static List<Proapproval> readProapprovalXls(String path) {
            HSSFWorkbook hssfWorkbook = null;
            try {
                InputStream is = new FileInputStream(path);
                hssfWorkbook = new HSSFWorkbook(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Proapproval Proapproval = null;
            List<Proapproval> list = new ArrayList<Proapproval>();
            if (hssfWorkbook != null) {
                // Read the Sheet
                for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                    HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                    if (hssfSheet == null) {
                        continue;
                    }
                    // Read the Row
                    for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                        HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                        if (hssfRow != null) {
                            Proapproval = new Proapproval();
                            HSSFCell cell0 = hssfRow.getCell(0);
                            HSSFCell cell1 = hssfRow.getCell(1);
                            HSSFCell cell2 = hssfRow.getCell(2);
                            HSSFCell cell3 = hssfRow.getCell(3);
                            HSSFCell cell4 = hssfRow.getCell(4);
                            if (!getValue(cell0).equals("")) {
                                Proapproval.setName(getValue(cell0));
                                Proapproval.setType(getValue(cell1));
                                Proapproval.setUnit(getValue(cell2));
                                Proapproval.setTime(cell3.getDateCellValue());
                                Proapproval.setApproval(getValue(cell4));
                                Proapproval.setAccessory("");
                                list.add(Proapproval);
                            }
                        }
                    }
                }
            }
            return list;
        }
        
        private static List<Proapproval> readProapprovalXlsx(String path) {
            XSSFWorkbook xssfWorkbook = null;
            try {
                InputStream is = new FileInputStream(path);
                xssfWorkbook = new XSSFWorkbook(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Proapproval Proapproval = null;
            List<Proapproval> list = new ArrayList<Proapproval>();
            if(xssfWorkbook!=null){
                // Read the Sheet
                for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
                    XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
                    if (xssfSheet == null) {
                        continue;
                    }
                    // Read the Row
                    for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                        XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                        if (xssfRow != null) {
                            Proapproval = new Proapproval();
                            XSSFCell cell0 = xssfRow.getCell(0);
                            XSSFCell cell1 = xssfRow.getCell(1);
                            XSSFCell cell2 = xssfRow.getCell(2);
                            XSSFCell cell3 = xssfRow.getCell(3);
                            XSSFCell cell4 = xssfRow.getCell(4);
                            if (!getValue(cell0).equals("")) {
                                Proapproval.setName(getValue(cell0));
                                Proapproval.setType(getValue(cell1));
                                Proapproval.setUnit(getValue(cell2));
                                Proapproval.setTime(cell3.getDateCellValue());
                                Proapproval.setApproval(getValue(cell4));
                                Proapproval.setAccessory("");
                                list.add(Proapproval);
                            }
                        }
                    }
                }
            }
            return list;
        }
        
        public static List<Constructionelec> readConstructionelecExcel(String path) {          
            if (path != null && !path.equals("")) {
                String ext = getExt(path);
                if (ext!=null && !ext.equals("")) {
                    if (ext.equals("xls")) {
                        return readConstructionelecXls(path);
                    } else if (ext.equals("xlsx")) {
                        return readConstructionelecXlsx(path);
                    }
                }
            }
            return new ArrayList<Constructionelec>();
        }
        
        private static List<Constructionelec> readConstructionelecXls(String path) {
            HSSFWorkbook hssfWorkbook = null;
            try {
                InputStream is = new FileInputStream(path);
                hssfWorkbook = new HSSFWorkbook(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Constructionelec Constructionelec = null;
            List<Constructionelec> list = new ArrayList<Constructionelec>();
            if (hssfWorkbook != null) {
                // Read the Sheet
                for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                    HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                    if (hssfSheet == null) {
                        continue;
                    }
                    // Read the Row
                    for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                        HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                        if (hssfRow != null) {
                        	Constructionelec = new Constructionelec();
                            HSSFCell cell0 = hssfRow.getCell(0);
                            HSSFCell cell1 = hssfRow.getCell(1);
                            HSSFCell cell2 = hssfRow.getCell(2);
                            HSSFCell cell3 = hssfRow.getCell(3);
                            HSSFCell cell4 = hssfRow.getCell(4);
                            HSSFCell cell5 = hssfRow.getCell(5);
                            HSSFCell cell6 = hssfRow.getCell(6);
                            HSSFCell cell7 = hssfRow.getCell(7);
                            HSSFCell cell8 = hssfRow.getCell(8);
                            HSSFCell cell9 = hssfRow.getCell(9);
                            HSSFCell cell10 = hssfRow.getCell(10);
                            HSSFCell cell11 = hssfRow.getCell(11);
                            HSSFCell cell12 = hssfRow.getCell(12);
                            if (!getValue(cell0).equals("")) {
                            	Constructionelec.setName(getValue(cell0));
                                Constructionelec.setType(getValue(cell1));
                                Constructionelec.setNum(getValue(cell2));
                                Constructionelec.setSinglepower(getValue(cell3));
                                Constructionelec.setSumpower(getValue(cell4));
                                Constructionelec.setFactory(getValue(cell5));
                                Constructionelec.setShiyong(getValue(cell6));
                                Constructionelec.setOuttime(getValue(cell7));
                                Constructionelec.setIntime(getValue(cell8));
                                Constructionelec.setPlantime(getValue(cell9));
                                Constructionelec.setRealtime(getValue(cell10));
                                Constructionelec.setStatus(getValue(cell12));
                                Constructionelec.setApprove(getValue(cell11));
                                Constructionelec.setAccessory("");
                                list.add(Constructionelec);
                            }
                        }
                    }
                }
            }
            return list;
        }
        
        private static List<Constructionelec> readConstructionelecXlsx(String path) {
            XSSFWorkbook xssfWorkbook = null;
            try {
                InputStream is = new FileInputStream(path);
                xssfWorkbook = new XSSFWorkbook(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Constructionelec Constructionelec = null;
            List<Constructionelec> list = new ArrayList<Constructionelec>();
            if(xssfWorkbook!=null){
                // Read the Sheet
                for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
                    XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
                    if (xssfSheet == null) {
                        continue;
                    }
                    // Read the Row
                    for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                        XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                        if (xssfRow != null) {
                        	Constructionelec = new Constructionelec();
                            XSSFCell cell0 = xssfRow.getCell(0);
                            XSSFCell cell1 = xssfRow.getCell(1);
                            XSSFCell cell2 = xssfRow.getCell(2);
                            XSSFCell cell3 = xssfRow.getCell(3);
                            XSSFCell cell4 = xssfRow.getCell(4);
                            XSSFCell cell5 = xssfRow.getCell(5);
                            XSSFCell cell6 = xssfRow.getCell(6);
                            XSSFCell cell7 = xssfRow.getCell(7);
                            XSSFCell cell8 = xssfRow.getCell(8);
                            XSSFCell cell9 = xssfRow.getCell(9);
                            XSSFCell cell10 = xssfRow.getCell(10);
                            XSSFCell cell11 = xssfRow.getCell(11);
                            XSSFCell cell12 = xssfRow.getCell(12);
                            if (!getValue(cell0).equals("")) {
                            	Constructionelec.setName(getValue(cell0));
                                Constructionelec.setType(getValue(cell1));
                                Constructionelec.setNum(getValue(cell2));
                                Constructionelec.setSinglepower(getValue(cell3));
                                Constructionelec.setSumpower(getValue(cell4));
                                Constructionelec.setFactory(getValue(cell5));
                                Constructionelec.setShiyong(getValue(cell6));
                                Constructionelec.setOuttime(getValue(cell7));
                                Constructionelec.setIntime(getValue(cell8));
                                Constructionelec.setPlantime(getValue(cell9));
                                Constructionelec.setRealtime(getValue(cell10));
                                Constructionelec.setStatus(getValue(cell12));
                                Constructionelec.setApprove(getValue(cell11));
                                Constructionelec.setAccessory("");
                                list.add(Constructionelec);
                            }
                        }
                    }
                }
            }
            return list;
        }        
        
        public static List<Saftyaccounts> readSaftyaccounts(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readSaftyaccountsXls(path);
					} else if (ext.equals("xlsx")) {
						return readSaftyaccountsXlsx(path);
					}
				}
			}
			return new ArrayList<Saftyaccounts>();
		}
				
		private static List<Saftyaccounts> readSaftyaccountsXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Saftyaccounts> list = new ArrayList<Saftyaccounts>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							HSSFCell cell0 = hssfRow.getCell(0);
							HSSFCell cell1 = hssfRow.getCell(1);
							HSSFCell cell2 = hssfRow.getCell(2);
							HSSFCell cell3 = hssfRow.getCell(3);
							HSSFCell cell4 = hssfRow.getCell(4);
							HSSFCell cell5 = hssfRow.getCell(5);
							HSSFCell cell6 = hssfRow.getCell(6);
							HSSFCell cell7 = hssfRow.getCell(7);
							HSSFCell cell8 = hssfRow.getCell(8);
							HSSFCell cell9 = hssfRow.getCell(9);
							if (!getValue(cell0).equals("")) {
								Saftyaccounts item = new Saftyaccounts();
								item.setSubjectnum(getValue(cell0));
								item.setCostkind(getValue(cell1));
								item.setCostetails(getValue(cell2));
								item.setApplysector(getValue(cell3));
								item.setCostuse(getValue(cell4));
								item.setAmount(getValue(cell5));
								item.setManager(getValue(cell6));
								item.setRegisterperson(getValue(cell7));
								item.setApprotime(cell8.getDateCellValue());
								item.setRemarks(getValue(cell9));
								list.add(item);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<Saftyaccounts> readSaftyaccountsXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Saftyaccounts> list = new ArrayList<Saftyaccounts>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							XSSFCell cell0 = xssfRow.getCell(0);
							XSSFCell cell1 = xssfRow.getCell(1);
							XSSFCell cell2 = xssfRow.getCell(2);
							XSSFCell cell3 = xssfRow.getCell(3);
							XSSFCell cell4 = xssfRow.getCell(4);
							XSSFCell cell5 = xssfRow.getCell(5);
							XSSFCell cell6 = xssfRow.getCell(6);
							XSSFCell cell7 = xssfRow.getCell(7);
							XSSFCell cell8 = xssfRow.getCell(8);
							XSSFCell cell9 = xssfRow.getCell(9);
							if (!getValue(cell0).equals("")) {
								Saftyaccounts item = new Saftyaccounts();
								item.setSubjectnum(getValue(cell0));
								item.setCostkind(getValue(cell1));
								item.setCostetails(getValue(cell2));
								item.setApplysector(getValue(cell3));
								item.setCostuse(getValue(cell4));
								item.setAmount(getValue(cell5));
								item.setManager(getValue(cell6));
								item.setRegisterperson(getValue(cell7));
								item.setApprotime(cell8.getDateCellValue());
								item.setRemarks(getValue(cell9));
								list.add(item);
							}
						}
					}
				}
			}
			return list;
		}
		

        public static List<TransportSafety> readTransportSafetyExcel(String path) {			
			if (path != null && !path.equals("")) {
				String ext = getExt(path);
				if (ext!=null && !ext.equals("")) {
					if (ext.equals("xls")) {
						return readTransportSafetyXls(path);
					} else if (ext.equals("xlsx")) {
						return readTransportSafetyXlsx(path);
					}
				}
			}
			return new ArrayList<TransportSafety>();
		}
				
		private static List<TransportSafety> readTransportSafetyXls(String path) {
			HSSFWorkbook hssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				hssfWorkbook = new HSSFWorkbook(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<TransportSafety> list = new ArrayList<TransportSafety>();
			if (hssfWorkbook != null) {
				// Read the Sheet
				for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
					HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
					if (hssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if (hssfRow != null) {
							HSSFCell cell0 = hssfRow.getCell(0);
							HSSFCell cell1 = hssfRow.getCell(1);
							HSSFCell cell2 = hssfRow.getCell(2);
							HSSFCell cell3 = hssfRow.getCell(3);
							HSSFCell cell4 = hssfRow.getCell(4);
							HSSFCell cell5 = hssfRow.getCell(5);
							HSSFCell cell6 = hssfRow.getCell(6);
							if (!getValue(cell0).equals("")) {
								TransportSafety item = new TransportSafety();
								item.setCarNum(getValue(cell0));
								item.setCarName(getValue(cell1));
								item.setDepartment(getValue(cell2));
								item.setLicense(getValue(cell3));
								item.setDriver(getValue(cell4));
								item.setDriverNum(getValue(cell5));
								item.setMaintenance(getValue(cell6));
								list.add(item);
							}
						}
					}
				}
			}
			return list;
		}
		
		private static List<TransportSafety> readTransportSafetyXlsx(String path) {
			XSSFWorkbook xssfWorkbook = null;
			try {
				InputStream is = new FileInputStream(path);
				xssfWorkbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<TransportSafety> list = new ArrayList<>();
			if(xssfWorkbook!=null){
				// Read the Sheet
				for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
					if (xssfSheet == null) {
						continue;
					}
					// Read the Row
					for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
						XSSFRow xssfRow = xssfSheet.getRow(rowNum);
						if (xssfRow != null) {
							XSSFCell cell0 = xssfRow.getCell(0);
							XSSFCell cell1 = xssfRow.getCell(1);
							XSSFCell cell2 = xssfRow.getCell(2);
							XSSFCell cell3 = xssfRow.getCell(3);
							XSSFCell cell4 = xssfRow.getCell(4);
							XSSFCell cell5 = xssfRow.getCell(5);
							XSSFCell cell6 = xssfRow.getCell(6);
							if (!getValue(cell0).equals("")) {
								TransportSafety item = new TransportSafety();
								item.setCarNum(getValue(cell0));
								item.setCarName(getValue(cell1));
								item.setDepartment(getValue(cell2));
								item.setLicense(getValue(cell3));
								item.setDriver(getValue(cell4));
								item.setDriverNum(getValue(cell5));
								item.setMaintenance(getValue(cell6));
								list.add(item);
							}
						}
					}
				}
			}
			return list;
		}		

		/**
		 * 判断后缀为xlsx的excel文件的数据类型
		 * @param xssfRow
		 * @return String
		 * @author chi 2017-04-13
		 */
		@SuppressWarnings("static-access")
		private static String getValue(XSSFCell xssfCell) {
			if (xssfCell == null) {
				return "";
			} else if (xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC) {
				if (DateUtil.isCellDateFormatted(xssfCell)) {
					//这里根据自己的需求更改所需的时间类型的字符，这里也可以不需要，date类型直接塞进对象即可
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					return sdf.format(xssfCell.getDateCellValue());
				}
				DecimalFormat df = new DecimalFormat("0");  
				  
				//String whatYourWant = df.format(cell.getNumericCellValue()); 
				return String.valueOf(df.format(xssfCell.getNumericCellValue()));
			} else {
				return String.valueOf(xssfCell.getStringCellValue().replace("\"", "\\\""));
			}
		}

		/**
		 * 获取文件扩展名
		 * @param path
		 * @return List<Commonlaw>
		 * @author chi 2017-04-13
		 */
		private static String getExt(String path) {
			if (path == null || path.equals("") || !path.contains(".")) {
				return null;
			} else {
				return path.substring(path.lastIndexOf(".") + 1, path.length());
			}
		}


		/**
		 * 判断后缀为xls的excel文件的数据类型
		 * @param hssfCell
		 * @return String
		 * @author chi 2017-04-13
		 */
		@SuppressWarnings("static-access")
		private static String getValue(HSSFCell hssfCell) {
			if (hssfCell == null) {
				return "";
			} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
					//这里根据自己的需求更改所需的时间类型的字符，这里也可以不需要，date类型直接塞进对象即可
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					return sdf.format(hssfCell.getDateCellValue());
				}
				
				DecimalFormat df = new DecimalFormat("0");
				
				return String.valueOf(df.format(hssfCell.getNumericCellValue()));
			} else {
				return String.valueOf(hssfCell.getStringCellValue().replace("\"", "\\\""));
			}
		}
		
		//测试
		public static void main(String[] args) throws Exception {	        
//	        List<Commonlaw> list = ReadExcel.readCommonlawExcel("D:\\我的文档\\桌面\\标准EXCEL表-法律.xls", "法律");
//	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	        for(Commonlaw c : list) {
//	        	System.out.println(c.getName() + " " + c.getNo() + " " + c.getType() + " " +
//	        			sdf.format(c.getApplyDate()) + " " + c.getFromUnit() + " " + sdf.format(c.getEnactDate()));
//	        }
			String FileName = "123*456*5";
			System.err.println(FileName.split("\\*").length);
		}
		
	  //-----------------liuchi-readExcel----------------------//
}
