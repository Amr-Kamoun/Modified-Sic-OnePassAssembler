import java.util.ArrayList;

public class instructions {
    private ArrayList<instructions> table = new ArrayList<>(32);
    private String instruction;
    private String opcode;
    private String format;
    public String getinstruction() {
        return instruction;
    }
    public String getOpcode() {
        return opcode;
    }
    public String getFormat() {
        return format;
    }
    public instructions(){
    }
    public instructions(String instruction, String  opcode, String format){
        this.instruction = instruction;
        this.opcode = opcode;
        this.format = format;
    }
    public String getopcode(String instruction){
        for (instructions op : table){
            if (op.getinstruction().equals(instruction))
                return op.getOpcode();
        }
        return null;
    }
    public String getformat(String instructions){
        for(instructions op : table){
            if(op.getOpcode().equals(instructions))
                return op.getFormat();
        }
        return "0";
    }
    public String getkeyword(String instruction){
        for(instructions op : table){
            if(op.getinstruction().equals(instruction))
                return op.getFormat();
        }
        return "0";
    }
    public void initialize(){
        table.add(new instructions("ADD  ","18","3"));
        table.add(new instructions("AND  ","40","3"));
        table.add(new instructions("COMP ","28","3"));
        table.add(new instructions("DIV  ","24","3"));
        table.add(new instructions("J    ","3C","3"));
        table.add(new instructions("JEQ  ","30","3"));
        table.add(new instructions("JGT  ","34","3"));
        table.add(new instructions("JLT  ","38","3"));
        table.add(new instructions("JSUB ","48","3"));
        table.add(new instructions("LDA  ","00","3"));
        table.add(new instructions("LDCH ","50","3"));
        table.add(new instructions("LDL  ","08","3"));
        table.add(new instructions("LDX  ","04","3"));
        table.add(new instructions("MUL  ","20","3"));
        table.add(new instructions("OR   ","44","3"));
        table.add(new instructions("RD   ","D8","3"));
        table.add(new instructions("RSUB ","4C","3"));
        table.add(new instructions("STA  ","0C","3"));
        table.add(new instructions("STCH ","54","3"));
        table.add(new instructions("STL  ","14","3"));
        table.add(new instructions("STSW ","E8","3"));
        table.add(new instructions("STX  ","10","3"));
        table.add(new instructions("SUB  ","1C","3"));
        table.add(new instructions("TD   ","E0","3"));
        table.add(new instructions("TIX  ","2C","3"));
        table.add(new instructions("WD   ","DC","3"));
        table.add(new instructions("FIX  ","C4","1"));
        table.add(new instructions("FLOAT","C0","1"));
        table.add(new instructions("HIO  ","F4","1"));
        table.add(new instructions("NORM ","C8","1"));
        table.add(new instructions("SIO  ","F0","1"));
        table.add(new instructions("TIO  ","F8","1"));
    }
}
