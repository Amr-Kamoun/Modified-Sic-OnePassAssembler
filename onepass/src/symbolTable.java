import java.util.*;
public class symbolTable{
    linkedList currentNode, newNode = new linkedList(), lastNode = new linkedList();
    private static Map<String, linkedList> labels = new HashMap<>();
    public ArrayList<String> textrecords = new ArrayList<>();
    Boolean trim = false;
    public void addsymboltotable(String symbol, String location){
        for (String sym : labels.keySet())
            if(labels.get(sym).equals(location))
                return;
        if(labels.get(symbol)!=null&&labels.get(symbol).getData()==null){
            currentNode =labels.get(symbol);
            while(currentNode!=null){
                if(currentNode.getNext()!=null){
                    currentNode = currentNode.getNext();
                    textrecords.add("T"+String.format("%06X",Integer.parseInt(currentNode.getData(),16))+"02"+"000"+location);
                    trim = true;
                }else{
                    break;
                }
            }
        }
        labels.put(symbol, new linkedList(location));
    }

    public String getsymboldata(String operand, String location){
        if(labels.containsKey(operand)){
            if(labels.get(operand).getData()!=null)
                return labels.get(operand).getData();
            else{
                newNode = new linkedList(Integer.toHexString(Integer.parseInt(location,16)+1).toUpperCase());
                lastNode = getlastnode(labels.get(operand));
                lastNode.setNext(newNode);
            }
        }
        else{
            labels.put(operand, new linkedList(null));
            newNode = new linkedList(Integer.toHexString(Integer.parseInt(location,16)+1).toUpperCase());
            labels.get(operand).setNext(newNode);
        }
        return "0000";
    }

    private linkedList getlastnode(linkedList node) {
        while (node.getNext() != null) {
            node = node.getNext();
        }
        return node;
    }
}
