/**
 
    WikiCrimes (http://www.wikicrimes.org) is a project/software that allows posting and accessing criminal occurrences in a digital map.
    The philosophy that drives Wikicrimes is the same as Wikipedia: mass collaboration produces valuable knowledge.
    That is to say, if everybody participates, the criminal mapping will be made collaboratively and everybody
    will leverage crime information digitalized in the map. That is the reason for the slogan "Share crime information. Keep safe!". 
    Wikicrimes is not a project developed by any security institution. 
    In fact it is a project from the citizen to the citizen. 
     
    
    Copyright (C) 2008  Wikinova Solutions (http://www.wikinova.com.br)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.wikicrimes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValueCompression {

	/*
	 * padrao de float usado no Java: IEEE_754
	 * 1 bit sign, 8 bit exponent, 23 bit mantissa
	 * detalhes: http://people.uncw.edu/tompkinsj/133/numbers/Reals.htm
	 */
	
	//FLOAT TO BYTE
	
	private static final int maskByte = 0x1FE00000;  //0001 1111 1110 0000 0000 0000 0000 0000
	private static final int shiftByte = 21; //quantidade de zeros a direita na mascara
	private static final int fixedByte = 0x20000000; //0010 0000 0000 0000 0000 0000 0000 0000
	
	public static byte compressByte(float f){
		if(f == 0) return 0;
		int i = Float.floatToIntBits(f);
//		/*DEBUG*/byteString(i);
//		/*DEBUG*/byteString(mask);
//		/*DEBUG*/byteString(i & mask);
//		/*DEBUG*/byteString((i & mask)>>shift);
//		/*DEBUG*/byteString((byte)((i & mask)>>shift));
		return (byte)((i & maskByte)>>shiftByte);
	}
	
	public static float uncompressByte(byte b){
		if(b == 0) return 0;
//		/*DEBUG*/byteString(b);
		int i = fixedByte | ((b  << shiftByte) & maskByte);
//		/*DEBUG*/byteString(i);
		return Float.intBitsToFloat(i);
	}
	
	
	//FLOAT TO SHORT
	
	private static final int maskShort = 0x7FFF8000;  //0111 1111 1111 1111 1000 0000 0000 0000
	private static final int shiftShort = 15; //quantidade de zeros a direita na mascara
	private static final int fixedShort = 0x00000000; //0000 0000 0000 0000 0000 0000 0000 0000
	
	public static short compressShort(float f){
		if(f == 0) return 0;
		int i = Float.floatToIntBits(f);
//		/*DEBUG*/byteString(i);
//		/*DEBUG*/byteString(mask);
//		/*DEBUG*/byteString(i & mask);
//		/*DEBUG*/byteString((i & mask)>>shift);
//		/*DEBUG*/byteString((byte)((i & mask)>>shift));
		return (short)((i & maskShort)>>shiftShort);
	}
	
	public static float uncompressShort(short s){
		if(s == 0) return 0;
//		/*DEBUG*/byteString(b);
		int i = fixedShort | ((s  << shiftShort) & maskShort);
//		/*DEBUG*/byteString(i);
		return Float.intBitsToFloat(i);
	}
	
	
	//TEST
	
	public static void main(String[] args) {
		for(int i=1; i<=1000; i++){
			teste(i/1000f);
		}
	}
	
	public static void teste(float f){
		System.out.printf("%#30.20f: %32s\n", f, byteString(f));
		float f2 = uncompressShort(compressShort(f));
		System.out.printf("%#30.20f: %32s\n", f2, byteString(f2));
	}
	
	public static void teste(float[][] grid){
		for(Float f : sort(grid))
			teste(f);
	}
	
	private static Float[] sort(float[][] matrix) {
		int cols = matrix[0].length;
		int rows = matrix.length;
		List<Float> list = new ArrayList<Float>();
		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				if(matrix[i][j] > 0) {
					list.add(matrix[i][j]);
				}
			}
		}
		Collections.sort(list);
		return list.toArray(new Float[list.size()]);
	}
	
	public static String byteString(int i){
		StringBuilder str = new StringBuilder(Integer.toBinaryString(i));
		int zeros = 32-str.length();
		for(int j=0; j<zeros; j++) str.insert(0, 0);
		return str.toString();
	}
	
	public static String byteString(float f){
		return byteString(Float.floatToIntBits(f));
	}
	
	public static String toStringUncompress(Byte[] array){
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			s.append(uncompressByte(array[i]));
			s.append(", ");
		}
		return s.toString();
	}
	
	public static String toStringUncompress(Short[] array){
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			s.append(uncompressShort(array[i]));
			s.append(", ");
		}
		return s.toString();
	}
	
	
}
