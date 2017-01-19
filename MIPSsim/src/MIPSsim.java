// On my honor, I have neither given nor received unauthorized aid on this assignment
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
public class MIPSsim {
static int PC = 64;
static ArrayList<String> mipOP = new ArrayList<String>();
static ArrayList<Integer> mipAddr = new ArrayList<Integer>();
static ArrayList<Integer> mipData = new ArrayList<Integer>();
static ArrayList<Integer> mipReg = new ArrayList<Integer>((Collections.nCopies(32, 0)));
static PrintWriter writer2;
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in); 
		System.out.println("Enter the name of the text file(no .txt): ");
		String itxt=reader.next();
		ArrayList<String> content = new ArrayList<String>();
		try {
			File file = new File(itxt+".txt");
			FileReader fileR = new FileReader(file);
			BufferedReader bufferedR = new BufferedReader(fileR);
			String line;
			while ((line = bufferedR.readLine()) != null) {
				content.add(line);
			}
			fileR.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			PrintWriter writer = new PrintWriter("disassembly.txt", "UTF-8");
			writer2 = new PrintWriter("simulation.txt", "UTF-8");
			String dis="";
			int breakI=0;
			//Writes Operations
			for(int i=0; i<content.size(); i++){
				if(catCheck(content.get(i)).equals("BREAK")){
					mipOP.add(catCheck(content.get(i)));
					mipAddr.add(PC);
					dis=content.get(i)+"\t"+PC+"\t"+catCheck(content.get(i));
					writer.println(dis);
					PC=PC+4;
					breakI= i+1;
					break;
				}
				else{
					mipOP.add(catCheck(content.get(i)));
					mipAddr.add(PC);
					dis=content.get(i)+"\t"+PC+"\t"+catCheck(content.get(i));
					writer.println(dis);
					PC=PC+4;
				}
			}
			//Writes Data
			for(int i =breakI;i<content.size();i++){
				mipAddr.add(PC);
				mipData.add(bintodec(twoComp(content.get(i))));
				dis=content.get(i)+"\t"+PC+"\t"+bintodec(twoComp(content.get(i)));
				writer.println(dis);
				PC=PC+4;
			}
			writer.close();
			mipsI(mipOP.get(0));
			writer2.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public static int bintodec(String txt){
		if(txt.substring(0,1).equals("-")){
			txt = txt.substring(1);
			int dec= (Integer.parseInt(txt, 2)+ 1) * -1;
			return dec;
		}
		else
			return Integer.parseInt(txt, 2);
	}
	public static String twoComp(String txt){
		if(txt.substring(0,1).equals("1")){
			txt = txt.replace("0", " "); 
		    txt = txt.replace("1", "0"); 
		    txt = txt.replace(" ", "1");
		    txt = "-" + txt;
		    return txt;
		}
		else
			return txt;
	}
	public static String catCheck(String txt){
		String cat = txt.substring(0,3);
		String restOf= txt.substring(3);
		String instr="";
		if(cat.equals("001")){
			instr= cat1(restOf);
			return instr;
		}
		else if(cat.equals("010")){
			instr= cat2(restOf);
			return instr;
		}
		else if(cat.equals("100")){
			instr= cat3(restOf);
			return instr;
		}
		else{
			return "Binary does not fit in any category.";
		}
	}
	public static String cat1(String txt){
		String instr= "";
		String op = txt.substring(0,3);
		String restOf= txt.substring(3);
		if(op.equals("000")){
			instr= "NOP";
			return instr;
		}
		else{
			if(op.equals("001")){
				instr="J ";
				int addr = bintodec(restOf)*4;
				instr=instr+"#"+addr;
				return instr;
			}
			else if(op.equals("010")){
				instr="BEQ ";
				int rs= bintodec(restOf.substring(0,5));
				int rt= bintodec(restOf.substring(5,10));
				int offset = bintodec(twoComp(restOf.substring(10)));
				instr = instr+"R"+rs+", "+"R"+rt+", #"+offset;
				return instr;
			}
			else if(op.equals("011")){
				instr="BNE ";
				int rs= bintodec(restOf.substring(0,5));
				int rt= bintodec(restOf.substring(5,10));
				int offset = bintodec(twoComp(restOf.substring(10)));
				instr = instr+"R"+rs+", "+"R"+rt+",  #"+offset;
				return instr;
			}
			else if(op.equals("100")){
				instr="BGTZ ";
				int rs= bintodec(restOf.substring(0,5));
				int offset = bintodec(twoComp(restOf.substring(10)));
				instr = instr+"R"+rs+", #"+offset;
				return instr;
			}
			else if(op.equals("101")){
				instr="SW ";
				int base= bintodec(restOf.substring(0,5));
				int rt= bintodec(restOf.substring(5,10));
				int offset = bintodec(twoComp(restOf.substring(10)));
				instr=instr+"R"+rt+", "+offset+"(R"+base+")";
				return instr;
			}
			else if(op.equals("110")){
				instr="LW ";
				int base= bintodec(restOf.substring(0,5));
				int rt= bintodec(restOf.substring(5,10));
				int offset = bintodec(twoComp(restOf.substring(10)));
				instr=instr+"R"+rt+", "+offset+"(R"+base+")";
				return instr;
			}
			else if(op.equals("111")){
				instr="BREAK";
				return instr;
			}
			else
				return "Opcode did not match any operations";
		}
	}
	public static String cat2(String txt){
		String instr= "";
		String op = txt.substring(0,3);
		String restOf= txt.substring(3);
		if(op.equals("000")){
			instr= "XOR ";
			int dest= bintodec(restOf.substring(0,5));
			int src1= bintodec(restOf.substring(5,10));
			int src2= bintodec(restOf.substring(10,15));
			instr=instr+"R"+dest+", R"+src1+", R"+src2;
			return instr;
		}
		else if(op.equals("001")){
				instr="MUL ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int src2= bintodec(restOf.substring(10,15));
				instr=instr+"R"+dest+", R"+src1+", R"+src2;
				return instr;
			}
			else if(op.equals("010")){
				instr="ADD ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int src2= bintodec(restOf.substring(10,15));
				instr=instr+"R"+dest+", R"+src1+", R"+src2;
				return instr;
			}
			else if(op.equals("011")){
				instr="SUB ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int src2= bintodec(restOf.substring(10,15));
				instr=instr+"R"+dest+", R"+src1+", R"+src2;
				return instr;
			}
			else if(op.equals("100")){
				instr="AND ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int src2= bintodec(restOf.substring(10,15));
				instr=instr+"R"+dest+", R"+src1+", R"+src2;
				return instr;
			}
			else if(op.equals("101")){
				instr="OR ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int src2= bintodec(restOf.substring(10,15));
				instr=instr+"R"+dest+", R"+src1+", R"+src2;
				return instr;
			}
			else if(op.equals("110")){
				instr="ADDU ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int src2= bintodec(restOf.substring(10,15));
				instr=instr+"R"+dest+", R"+src1+", R"+src2;
				return instr;
			}
			else if(op.equals("111")){
				instr="SUBU ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int src2= bintodec(restOf.substring(10,15));
				instr=instr+"R"+dest+", R"+src1+", R"+src2;
				return instr;
			}
			else
				return "Opcode did not match any operations";
	}
	public static String cat3(String txt){
		String instr= "";
		String op = txt.substring(0,3);
		String restOf= txt.substring(3);
		if(op.equals("000")){
			instr= "ORI ";
			int dest= bintodec(restOf.substring(0,5));
			int src1= bintodec(restOf.substring(5,10));
			int iv= bintodec(restOf.substring(10));
			instr=instr+"R"+dest+", R"+src1+", #"+iv;
			return instr;
		}
		else if(op.equals("001")){
				instr="XORI ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int iv= bintodec(restOf.substring(10));
				instr=instr+"R"+dest+", R"+src1+", #"+iv;
				return instr;
			}
			else if(op.equals("010")){
				instr="ADDI ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int iv= bintodec(twoComp(restOf.substring(10)));
				instr=instr+"R"+dest+", R"+src1+", #"+iv;
				return instr;
			}
			else if(op.equals("011")){
				instr="SUBI ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int iv= bintodec(twoComp(restOf.substring(10)));
				instr=instr+"R"+dest+", R"+src1+", #"+iv;
				return instr;
			}
			else if(op.equals("100")){
				instr="ANDI ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int iv= bintodec(restOf.substring(10));
				instr=instr+"R"+dest+", R"+src1+", #"+iv;
				return instr;
			}
			else if(op.equals("101")){
				instr="SRL ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int iv= bintodec(restOf.substring(22));
				instr=instr+"R"+dest+", R"+src1+", #"+iv;
				return instr;
			}
			else if(op.equals("110")){
				instr="SRA ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int iv= bintodec(restOf.substring(22));
				instr=instr+"R"+dest+", R"+src1+", #"+iv;
				return instr;
			}
			else if(op.equals("111")){
				instr="SLL ";
				int dest= bintodec(restOf.substring(0,5));
				int src1= bintodec(restOf.substring(5,10));
				int iv= bintodec(restOf.substring(22));
				instr=instr+"R"+dest+", R"+src1+", #"+iv;
				return instr;
			}
			else
				return "Opcode did not match any operations";
	}
	public static void mipsI(String txt){
		PC=64;
		int cycle=0;
		int index=0;
		while(!(txt.equals("BREAK"))){
			cycle++;
			if(txt.equals("NOP")){
				simPrint(txt,cycle);
				PC=PC+4;
				index=mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,2).equals("J ")){
				simPrint(txt,cycle);
				PC = Integer.parseInt(txt.substring(3));
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("BEQ ")){
				int indexrs= txt.indexOf(",");
				int indexrt = txt.indexOf(",",indexrs+1);
				int indexos = txt.indexOf("#");
				int rs = Integer.parseInt(txt.substring(5,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3,indexrt));
				int offset= (Integer.parseInt(txt.substring(indexos+1))) *4 ;
				if(mipReg.get(rs)==mipReg.get(rt)){
					simPrint(txt,cycle);
					PC = PC + 4 + offset;
					index= mipAddr.indexOf(PC);
					txt=mipOP.get(index);
				}
				else{
					simPrint(txt,cycle);
					PC=PC+4;
					index= mipAddr.indexOf(PC);
					txt=mipOP.get(index);
				}
			}
			else if(txt.substring(0,4).equals("BNE ")){
				int indexrs= txt.indexOf(",");
				int indexrt = txt.indexOf(",",indexrs+1);
				int indexos = txt.indexOf("#");
				int rs = Integer.parseInt(txt.substring(5,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3,indexrt));
				int offset= (Integer.parseInt(txt.substring(indexos+1))) *4 ;
				if(mipReg.get(rs)!=mipReg.get(rt)){
					simPrint(txt,cycle);
					PC = PC + 4 + offset;
					index= mipAddr.indexOf(PC);
					txt=mipOP.get(index);
				}
				else{
					simPrint(txt,cycle);
					PC=PC+4;
					index= mipAddr.indexOf(PC);
					txt=mipOP.get(index);
				}
			}
			else if(txt.substring(0,5).equals("BGTZ ")){
				int indexrs= txt.indexOf(",");
				int indexos = txt.indexOf("#");
				int rs = Integer.parseInt(txt.substring(6,indexrs));
				int offset= (Integer.parseInt(txt.substring(indexos+1))) *4 ;
				if(mipReg.get(rs)>0){
					simPrint(txt,cycle);
					PC = PC + 4 + offset;
					index= mipAddr.indexOf(PC);
					txt=mipOP.get(index);
				}
				else{
					simPrint(txt,cycle);
					PC=PC+4;
					index= mipAddr.indexOf(PC);
					txt=mipOP.get(index);
				}
			}
			else if(txt.substring(0,3).equals("SW ")){
				int indexrs= txt.indexOf(",");
				int indexrt = txt.indexOf("(");
				int indexrt2 = txt.indexOf(")");
				int indexos = indexrs+2;
				int rs = Integer.parseInt(txt.substring(4,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrt+2,indexrt2));
				int offset= (Integer.parseInt(txt.substring(indexos,indexrt)));
				int sAddr=offset+mipReg.get(rt);
				int sIndex=mipAddr.indexOf(sAddr)-mipOP.size();
				mipData.set(sIndex,mipReg.get(rs));
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,3).equals("LW ")){
				int indexrs= txt.indexOf(",");
				int indexrt = txt.indexOf("(");
				int indexrt2 = txt.indexOf(")");
				int indexos = indexrs+2;
				int rs = Integer.parseInt(txt.substring(4,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrt+2,indexrt2));
				int offset= (Integer.parseInt(txt.substring(indexos,indexrt))) ;
				int sAddr=offset+mipReg.get(rt);
				int sIndex=mipAddr.indexOf(sAddr)-mipOP.size();
				mipReg.set(rs,mipData.get(sIndex));
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("XOR ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) ^ mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("MUL ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) * mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("ADD ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) + mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("SUB ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) - mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("AND ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) & mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,3).equals("OR ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(4,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) | mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,5).equals("ADDU ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(6,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) + mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,5).equals("SUBU ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(6,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) - mipReg.get(rt);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("ORI ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) | rt;
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,5).equals("XORI ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(6,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) ^ rt;
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,5).equals("ADDI ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(6,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) + rt;
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,5).equals("SUBI ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(6,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) - rt;
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,5).equals("ANDI ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(6,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs) & rt;
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("SRL ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = Math.floorDiv(mipReg.get(rs), (int) Math.pow(2, rt));
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("SRA ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = Math.floorDiv(mipReg.get(rs), (int) Math.pow(2, rt));
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
			else if(txt.substring(0,4).equals("SLL ")){
				int indexrd= txt.indexOf(",");
				int indexrs = txt.indexOf(",",indexrd+1);
				int rd = Integer.parseInt(txt.substring(5,indexrd));
				int rs = Integer.parseInt(txt.substring(indexrd+3,indexrs));
				int rt = Integer.parseInt(txt.substring(indexrs+3));
				int result = mipReg.get(rs)*(int) Math.pow(2, rt);
				result = (int) Math.floor(result);
				mipReg.set(rd, result);
				simPrint(txt,cycle);
				PC=PC+4;
				index= mipAddr.indexOf(PC);
				txt=mipOP.get(index);
			}
		}
		cycle++;
		simPrint(txt,cycle);
	}
	public static void simPrint(String txt, int cycle){
		int idata=mipOP.size();
		int calcA= mipAddr.get(idata);
		writer2.println("--------------------");
		writer2.println("Cycle "+cycle+":\t"+PC+"\t"+txt+"\n");
		writer2.println("Registers");
		writer2.println("R00:\t"+mipReg.get(0)+"\t"+mipReg.get(1)+"\t"+mipReg.get(2)+"\t"+mipReg.get(3)+"\t"+mipReg.get(4)+"\t"+mipReg.get(5)+"\t"+mipReg.get(6)+"\t"+mipReg.get(7));
		writer2.println("R08:\t"+mipReg.get(8)+"\t"+mipReg.get(9)+"\t"+mipReg.get(10)+"\t"+mipReg.get(11)+"\t"+mipReg.get(12)+"\t"+mipReg.get(13)+"\t"+mipReg.get(14)+"\t"+mipReg.get(15));
		writer2.println("R16:\t"+mipReg.get(16)+"\t"+mipReg.get(17)+"\t"+mipReg.get(18)+"\t"+mipReg.get(19)+"\t"+mipReg.get(20)+"\t"+mipReg.get(21)+"\t"+mipReg.get(22)+"\t"+mipReg.get(23));
		writer2.println("R24:\t"+mipReg.get(24)+"\t"+mipReg.get(25)+"\t"+mipReg.get(26)+"\t"+mipReg.get(27)+"\t"+mipReg.get(28)+"\t"+mipReg.get(29)+"\t"+mipReg.get(30)+"\t"+mipReg.get(31));
		writer2.println("");
		writer2.print("Data");
		for(int i =0; i<mipData.size();i++){
			double d = i/8.0;
			if(d%1==0){
				int naddr= calcA+(32*i/8);
				writer2.print("\n"+naddr+":\t"+mipData.get(i)+"\t");
			}
			else
				writer2.print(mipData.get(i)+"\t");
		}
		writer2.println("\n");
	}
}

