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
	 * ���췽��
	 * 
	 * @param in
	 *            �����ļ���
	 * @param out
	 *            ����ļ���
	 */
	public LengthConverter(String in, String out) {
		inputFileName = in;
		outputFileName = out;

		unitMap = new HashMap<String, Double>();
		expressionList = new ArrayList<String>();
		resultList = new ArrayList<Double>();
	}

	/**
	 * ��ȡ�����ļ�
	 * 
	 * @return ��ȡ�ɹ���true������Ϊfalse
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
	 * ���е�λת������ʽ����
	 */
	public void calculate() {
		for (String str : expressionList) {
			resultList.add(parseExpression(str));
		}
	}

	/**
	 * ������
	 * 
	 * @return ����ɹ���true������Ϊfalse
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
	 * ������λת������
	 * 
	 * @param line
	 *            ת������
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
	 * ����������ʽ
	 * 
	 * @param exp���ʽ
	 * @return ���ʽ�Ľ��
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
				continue; // ��ʵ����һ��Ҳ�ǲ������е��ģ�Ϊ�˰�ȫ���������
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
	 * ���ʸ���ת�����������Ч��Ҫ����ߣ����Խ���λ�ĸ����͵����ַ�������һ��Map��ʹ��ʱֱ��ӳ���滻
	 * 
	 * @param plural
	 *            ������ʽ
	 * @return ������ʽ
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
	 * �ж��ַ�������
	 * 
	 * @param str
	 *            �ַ���
	 * @return �ַ���������(���֡���λ���Ӽ����ŵ�)
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
