public class locationCounter {
    instructions i = new instructions();
    private int counter;
    public void initcounter(int address){
        counter = address;
    }
    public String getcounter() {
        return Integer.toString(counter,16).toUpperCase();
    }
    public void incrcounterByte(int sizeofbyte){
        counter=counter+sizeofbyte;
    }
    public void incrcounterWord(){
        counter=counter+3;
    }
    public void incrcounterResW(int words){
        counter=counter + (words*3);
    }
    public void incrcounter(String opcode){
        i.initialize();
        if(i.getformat(opcode)=="3")
            counter+=3;
        else if(i.getformat(opcode)=="1")
            counter+=1;
    }
}
