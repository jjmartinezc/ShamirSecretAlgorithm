import java.util.ArrayList;
import java.util.Scanner;

/**
 * Shamir Secret Sharing Algorithm
 * @author Juan Jose Martinez Calle 201729865
 * MSIN 4101 - Ingenieria Criptografica
 */
public class main {

	/**
	 * Lista de los puntos a usar para construir el polinomio
	 */
	public static ArrayList<Punto> curva = new ArrayList<Punto>();
	
	/**
	 * Primo usado para la funcion modulo
	 */
	public static int p = 1301; 
	
	/**
	 * Valor en X para resolver el polinomio
	 */
	public static int x = 0;

	/**
	 * Metodo principal
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("TALLER SHAMIR SECRET ALGORITHM");
		System.out.println("JUAN JOSE MARTINEZ CALLE");
		System.out.println("201729865");
		System.out.println("MSIN 4101 - ING CRIPTOGRAFICA");
		System.out.println("");
		System.out.println("Este programa soluciona matrices nxn a partir de dos puntos usando interpolación de Lagrange");
		System.out.println("Ingrese un punto como dos numeros separados por una coma ej: 1,360 ");
		System.out.println("Si desea dejar de ingresar puntos escriba Y");
		boolean ciclo = true;
		Scanner sc = new Scanner(System.in);
		while(ciclo){
			String input = sc.nextLine();
			if(input.equals("Y")) {
				ciclo=false;
			}else {
				String[] coord = new String[2];
				coord = input.split(",");
				curva.add(new Punto(Double.parseDouble(coord[0]), Double.parseDouble(coord[1])));
				System.out.println("Se agregó el punto ("+Double.parseDouble(coord[0]) +", "+Double.parseDouble(coord[1]) +")");
			}
		}
		poly();
	}

	/**
	 * Estructura que representa un punto
	 * @author Juan
	 *x corresponde a la coordenada X del punto, y corresponde a su coordenanda Y
	 */
	static class Punto{
		Double x;
		Double y;
		public Punto(Double x1, Double y1) {
			x=x1;
			y=y1;
		}
	}

	/**
	 * Funcion encargada de construir el polinomio
	 */
	public static void poly() {
		System.out.println("Se tienen los puntos: ");
		for(int i = 0; i < curva.size(); i++) {
			System.out.println("P"+(i+1)+" ("+curva.get(i).x +", "+curva.get(i).y +")");
		}
		System.out.println("Un polinomio de grado n = "+(curva.size()-1));
		Double secreto = 0.0;
		Double[] coef = new Double[curva.size()];
		Double[] polinomio = new Double[curva.size()];
		int iter = (int)Math.pow(2, curva.size()-1)-1;
		Double[][] lagrange = new Double[curva.size()][curva.size()];
		//Construye la matriz nxn
		for(int i = 0; i < curva.size(); i++) {
			polinomio[i] = 0.0;
			for(int j = 0; j < curva.size(); j++) {
				lagrange[i][j] = 0.0;
			}
		}
		char[] bitsarr = new char[curva.size()-1];
		for(int i = 0; i < bitsarr.length; i++) {
			bitsarr[i]=0;
		}
		for(int i = 0; i < curva.size(); i++) {
			Double denom = 1.0;
			//Lista para manejar el polinomio j
			ArrayList<Double> polinomioJ = new ArrayList<Double>();
			//Encuentra el denominador para cada polinomio j
			for(int j = 0; j < curva.size(); j++) {
				if(j!=i) {
					denom = denom*(curva.get(i).x - curva.get(j).x);
					polinomioJ.add(curva.get(j).x);
				}
			}
			coef[i]=curva.get(i).y/denom;
			Boolean fin = true;
			for(int j = iter; fin; j--) {
				String bits = Integer.toBinaryString(j);
				while (bits.length()<curva.size()-1) {
					bits = 0+bits;
				}
				bitsarr = bits.toCharArray();
				Double val = 1.0;
				int exp = 0;
				//Se construyen los polinomios "parciales"
				for(int k = 0; k < curva.size()-1; k++) {
					if(bitsarr[k]=='1') {
						val = val*polinomioJ.get(k)*(-1);
						exp++;
					}
				}
				lagrange[i][exp] = lagrange[i][exp] + val;
				if(j==0) {
					fin=false;
				}
			}
		}
		//Se realiza la sumatoria de polinomios
		for(int i = 0; i < curva.size(); i++) {
			for(int j = 0; j < curva.size(); j++) {
				lagrange[i][j] = lagrange[i][j]*coef[i];
				polinomio[j] = polinomio[j] +  lagrange[i][j];
			}
		}
		String print = "";
		for(int i = 0; i < curva.size(); i++) {
			print = print + polinomio[i] +" ";
			secreto = secreto + (polinomio[i]*Math.pow(x, (curva.size()-1)-i));
		}
		System.out.println("Los coeficientes (an, an-1, ..., a0) respectivamente son:");
		System.out.println(print);
		System.out.println("El valor para f("+x +") mod "+p +" es igual a:");
		System.out.println(secreto%p);
	}
}
