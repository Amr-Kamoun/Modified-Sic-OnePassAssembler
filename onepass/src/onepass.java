import java.io.*;
import java.util.*;

public class onepass {
    ArrayList<String> code = new ArrayList<>();
    StringBuilder textRecord = new StringBuilder();
    List<String> textrecords = new ArrayList<>();
    locationCounter LocCTR = new locationCounter();
    instructions i = new instructions();
    symbolTable sym = new symbolTable();

    public onepass() throws IOException {}
    BufferedWriter writerecord = new BufferedWriter(new FileWriter("src/objectcode.txt"));
    public void start(){
        readfile();
        i.initialize();
        try{
                int i=0;
                while (i<code.size()) {
                    if (code.get(i).substring(6, 11).toUpperCase().equals("START")) {
                        headerRecord(i);
                    } else if (code.get(i).substring(6, 11).toUpperCase().equals("END  ")) {
                        endRecord();
                    } else {
                        textRecord(i);
                    }
                    i++;
                }
            System.out.println("done!");
        }catch (Exception e){
            System.out.println("error: "+e);
        }finally {
            try{
                if (writerecord != null)
                    writerecord.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    void readfile(){
        try{
            BufferedReader read = new BufferedReader(new FileReader("src/in.txt"));
            String line;
            while((line = read.readLine()) != null)
                code.add(line);
            read.close();
        }catch (FileNotFoundException e){
            System.out.println("no file");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    BufferedWriter writesumbol = new BufferedWriter(new FileWriter("src/symbolTable.txt"));
     void writeSymbolTable(String instruction, String address) throws IOException {
        writesumbol.write(instruction+"\t"+address+"\n");
        writesumbol.flush();
    }
    StringBuilder headerRecord = new StringBuilder();
    String startaddress = "";
     void headerRecord(int index) throws IOException {
        String name = code.get(index).substring(0,6).toUpperCase();
        startaddress = code.get(index).substring(12,16);
        LocCTR.initcounter(Integer.parseInt(startaddress,16));
        headerRecord.append("H"+String.format("%6s", name).replace(' ', '0')+String.format("%06d",Integer.parseInt(LocCTR.getcounter())));
        writeSymbolTable(name,startaddress);
    }
    BufferedWriter writeassembly = new BufferedWriter(new FileWriter("src/assembly.txt"));
    String maskbit ="";
    int Tlength = 0;
    void textRecord(int index) throws IOException {
        String objectcode = "";
        String symbol = code.get(index).substring(0,6).toUpperCase();
        String instruction = code.get(index).substring(6,11).toUpperCase();
        String opcode = i.getopcode(instruction);
        String format = i.getkeyword(instruction);
        String operand = code.get(index).substring(12).toUpperCase();

        generateTextRecord(instruction);

        if(symbol.equals("      "))
            symbol = "      ";
        else{
            sym.textrecords = new ArrayList<>();
            sym.addsymboltotable(symbol,LocCTR.getcounter());
            if(sym.trim){
                trimtrecord();
                ArrayList<String> strings = sym.textrecords;
                for (int j = 0; j < strings.size(); j++) {
                    String text = strings.get(j);
                    textrecords.add(text);
                }
                sym.trim = false;
                generatetrecord();
                maskbit = "";
            }
            writeSymbolTable(symbol,LocCTR.getcounter());
        }
        if(instruction.equals("BYTE ")){
            if (code.get(index).charAt(12) == 'X') {
                Tlength += code.get(index).substring(14, code.get(index).length() - 1).length(); //Tlength takes the length of Hexanumbers
                objectcode = code.get(index).substring(14, code.get(index).length() - 1).substring(0,objectcode.indexOf("'"));
                textRecord.append(objectcode);
            }else if (code.get(index).charAt(12) == 'C'){
                Tlength += code.get(index).substring(14, code.get(index).length() - 1).length(); //Tlength takes the length of Characters
                String opcharacters = code.get(index).substring(14, code.get(index).length() - 1);
                    int i = 0;
                    while (i < opcharacters.length()) {
                        char character = opcharacters.charAt(i);
                        String hex = Integer.toHexString(character).toUpperCase();
                        objectcode += hex;
                        i++;
                    }
                textRecord.append(objectcode);
            }
            maskbit += "0";
        } else if (instruction.equals("WORD ")) {
            objectcode = String.format("%06X",Integer.parseInt(operand));
            textRecord.append(objectcode);
            Tlength +=3;
            maskbit += "0";
        }else if (instruction.equals("RESW ")||instruction.equals("RESB ")){
            maskbit += "0";
        } else if (instruction.equals("RSUB ")) {
            objectcode = "4C0000";
            textRecord.append(objectcode);
            Tlength +=3;
            maskbit += "0";
        } else {
            if(format == "1"){
                objectcode = opcode;
                textRecord.append(objectcode);
                maskbit += "0";
                Tlength +=1;
            }else if (format =="3") {
                String opcodeI ="";
                String opcodeB = String.format("%07d",Long.parseLong(Long.toBinaryString(Long.parseLong(opcode,16))));
                if(operand.substring(0,1).equals("#"))
                    opcodeB = opcodeB.substring(0,6)+'1'+opcodeB.substring(7);
                else
                    opcodeB = opcodeB.substring(0,6)+'0'+opcodeB.substring(7);
                if(operand.split(",").length>1)
                    opcodeI = opcodeB +'1';
                else
                    opcodeI = opcodeB +'0';
                if (operand.substring(6,7).equals(",")) {
                    objectcode = Integer.toHexString(Integer.parseInt((opcodeI + String.format("%015d",Long.parseLong(Long.toBinaryString(Long.parseLong(sym.getsymboldata(operand.substring(0,6),LocCTR.getcounter()),16))))),2)).toUpperCase();
                    textRecord.append(objectcode);
                    maskbit += "1";
                }else if(operand.substring(0,1).equals("#")){
                    objectcode = String.format("%06d", Integer.parseInt(Integer.toHexString(Integer.parseInt((opcodeI + String.format("%015d",Long.parseLong(Long.toBinaryString(Long.parseLong(operand.substring(1,2),16))))),2)).toUpperCase()));
                    textRecord.append(objectcode);
                    maskbit += "0";
                } else{
                    objectcode = opcode + sym.getsymboldata(operand.substring(0,6),LocCTR.getcounter());
                    textRecord.append(objectcode);
                    maskbit += "1";
                }
                Tlength +=3;
            }
        }

        writeassembly.write(LocCTR.getcounter()+"\t"+symbol+"\t"+instruction+"\t"+operand+"\t"+objectcode+"\n");
        writeassembly.flush();

        if (instruction.equals("BYTE ")) {
            if (code.get(index).charAt(12) == 'X')
                LocCTR.incrcounterByte((code.get(index).substring(14, code.get(index).length() - 4).length()) / 2);
            else if (code.get(index).charAt(12) == 'C')
                LocCTR.incrcounterByte(code.get(index).substring(14, code.get(index).length() - 1).length());
        }  else if (instruction.equals("RESW ")) {
            LocCTR.incrcounterResW(Integer.parseInt(code.get(index).substring(12)));
        } else if (instruction.equals("RESB ")) {
            LocCTR.incrcounterByte(Integer.parseInt(code.get(index).substring(12)));
        } else if (instruction.equals("WORD ")) {
            LocCTR.incrcounterWord();
        } else {
            LocCTR.incrcounter(opcode);
        }
    }
    int skip = 0;
    boolean newTR = false;
    boolean firstTrecord = false;
    String firstinstruction = "";
    String firstinstructionlocation ="";
    void generatetrecord(){
        textrecords.add(textRecord.toString());
        textRecord = new StringBuilder();
        firstTrecord=false;
        generateStartTextRecord();
        newTR = false;
        skip=0;
        Tlength=0;
    }
    void trimtrecord(){
        if (!newTR){
            textRecord.insert(7,String.format("%02X",Integer.parseInt(Integer.toString(Integer.parseInt(LocCTR.getcounter(),16)-Integer.parseInt(firstinstructionlocation,16))),16));
            if(Integer.parseInt(String.format("%-" + 12 + "s", maskbit).replace(' ', '0'),2) == 0)
                textRecord.insert(9,String.format("%03d",Integer.parseInt(String.format("%-" + 12 + "s", maskbit).replace(' ', '0'),2)));
            else
                textRecord.insert(9,Integer.toHexString(Integer.parseInt(String.format("%-" + 12 + "s", maskbit).replace(' ', '0'),2)).toUpperCase());
            textrecords.add(textRecord.toString());
            textRecord = new StringBuilder();
            firstTrecord = true;
        }
        Tlength=0;
        newTR = true;
    }
    void generateStartTextRecord(){
        if(!firstTrecord){
            textRecord.append("T"+String.format("%06X",Integer.parseInt(LocCTR.getcounter(),16)));
            firstinstructionlocation = LocCTR.getcounter();
            firstTrecord = true;
        }
    }
    void generateTextRecord(String instruction){
        generateStartTextRecord();

        if(instruction.equals("RESW ")||instruction.equals("RESB ")){
            skip++;
            trimtrecord();
        }

        if(!(instruction.equals("RESW ")||instruction.equals("RESB "))&&(skip==0)&&Tlength>27){
            textRecord.insert(7,String.format("%02X",Integer.parseInt(Integer.toString(Integer.parseInt(LocCTR.getcounter(),16)-Integer.parseInt(firstinstructionlocation,16))),16));
            textRecord.insert(9,Integer.toHexString(Integer.parseInt(String.format("%-" + 12 + "s", maskbit).replace(' ', '0'),2)).toUpperCase());
            generatetrecord();
            maskbit = "";
        }

        if(!(instruction.equals("RESW ")||instruction.equals("RESB "))&&(skip>0)&&newTR&&Tlength==0){
            generatetrecord();
            firstinstruction = LocCTR.getcounter();
            maskbit = "";
        }
    }
    private void endRecord() throws IOException{
        trimtrecord();
        headerRecord.append(String.format("%06X",Integer.parseInt(LocCTR.getcounter(),16) - Integer.parseInt(startaddress,16),16).toUpperCase());
        writerecord.write(headerRecord.toString()+"\n");
        for (int j = 0; j < textrecords.size(); j++) {
            String text = textrecords.get(j);
            if (text.length() > 10) {
                writerecord.write(text + "\n");
            }
        }
        writerecord.write("E"+ String.format("%06X",Integer.parseInt(firstinstruction,16)));
    }
}