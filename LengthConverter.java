import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LengthConverter {

	public static final String authorMail = "hjf004@126.com";

	private String inputFileName;
	private String outputFileName;

	private Map<String, Double> unitMap;
	private List<String> expressionList;
	private List<Double> resultList;

	private enum StringType {
		NUMBER, UNIT, OPERATOR_PLUS, OPERATOR_MINUS, UNKNOWN
	};

	/**
	 * 构造方法
	 * 
	 * @param in
	 *            输入文件名
	 * @param out
	 *            输出文件名
	 */
	public LengthConverter(String in, String out) {
		inputFileName = in;
		outputFileName = out;

		unitMap = new HashMap<String, Double>();
		expressionList = new ArrayList<String>();
		resultList = new ArrayList<Double>();
	}

	/**
	 * 读取输入文件
	 * 
	 * @return 读取成功则true，否则为false
	 */
	public boolean readInput() {
		boolean result = true;
		boolean readUnits = true;
		try {
			FileReader fr = new FileReader(inputFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (readUnits && line.isEmpty()) {
					readUnits = false;
					continue;
				}
				line.trim();
				if (readUnits) {
					parseUnit(line);
				} else {
					if (!line.isEmpty())
						expressionList.add(line);
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 进行单位转换和算式计算
	 */
	public void calculate() {
		for (String str : expressionList) {
			resultList.add(parseExpression(str));
		}
	}

	/**
	 * 输出结果
	 * 
	 * @return 输出成功则true，否则为false
	 */
	public boolean writeOutput() {
		boolean result = true;

		try {
			// FileOutputStream outStream=new FileOutputStream(outFile);
			// outStream.write(authorMail.getBytes());
			// outStream.write("\n".getBytes());
			PrintStream ps = new PrintStream(outputFileName);
			ps.println(authorMail);
			for (double d : resultList) {
				ps.println();
				ps.printf("%.2f m", d);
			}
			ps.flush();
			ps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 解析单位转换规则
	 * 
	 * @param line
	 *            转换规则
	 */
	private void parseUnit(String line) {
		String[] exps = line.split("=");
		if (exps.length == 2) {
			double left, right;
			String[] leftStrs = exps[0].trim().split(" ");
			left = Double.parseDouble(leftStrs[0]);
			String[] rightStrs = exps[1].trim().split(" ");
			right = Double.parseDouble(rightStrs[0]);
			unitMap.put(leftStrs[1], right / left);
		}
	}

	/**
	 * 解析计算表达式
	 * 
	 * @param exp表达式
	 * @return 表达式的结果
	 */
	private double parseExpression(String exp) {
		double result = 0;

		String[] strs = exp.split(" ");
		int operatorFlag = 1;
		for (int i = 0; i < strs.length; ++i) {
			switch (getStringType(strs[i])) {
			case NUMBER:
				result += operatorFlag * Double.valueOf(strs[i])
						* unitMap.get(plural2singular(strs[++i]));
				break;
			case UNIT:
				continue; // 事实上这一句也是不会运行到的，为了安全起见而加上
			case OPERATOR_PLUS:
				operatorFlag = 1;
				break;
			case OPERATOR_MINUS:
				operatorFlag = -1;
				break;
			case UNKNOWN:
				System.out
						.println("Unknown String, The result may be unbelievable!");
				break;
			}
		}
		return result;
	}

	/**
	 * 单词复数转单数。如果对效率要求更高，可以将单位的复数和单数字符串做成一个Map，使用时直接映射替换
	 * 
	 * @param plural
	 *            复数形式
	 * @return 单数形式
	 */
	private String plural2singular(String plural) {
		String singular = plural;

		if (plural.endsWith("hes"))
			singular = singular.substring(0, singular.length() - 2);
		else if (plural.endsWith("s"))
			singular = singular.substring(0, singular.length() - 1);
		else if (plural.contains("ee"))
			singular = singular.replace("ee", "oo");
		return singular;
	}

	/**
	 * 判断字符串类型
	 * 
	 * @param str
	 *            字符串
	 * @return 字符串的类型(数字、单位、加减符号等)
	 */
	private StringType getStringType(String str) {
		StringType type = StringType.UNKNOWN;
		if (!str.isEmpty()) {
			if (str.equals("+"))
				type = StringType.OPERATOR_PLUS;
			else if (str.equals("-"))
				type = StringType.OPERATOR_MINUS;
			else if (Character.isDigit(str.charAt(0)))
				type = StringType.NUMBER;
			else if (unitMap.containsKey(plural2singular(str)))
				type = StringType.UNIT;
			else
				type = StringType.UNKNOWN;
		}
		return type;
	}

	public static void main(String[] args) {
		LengthConverter lc = new LengthConverter("input.txt", "output.txt");
		if (lc.readInput()) {
			lc.calculate();
			if (lc.writeOutput())
				System.out.printf("Output Finished.");
		}
	}
}
