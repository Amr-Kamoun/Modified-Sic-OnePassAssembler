public class linkedList {
    private String data;
    private linkedList next;
    public linkedList(){}
    public linkedList(String data){
        this.data= data;
        this.next = null;
    }
    public String getData() {
        return data;
    }
    public linkedList getNext() {
        return next;
    }
    public void setNext(linkedList next) {
        this.next = next;
    }
}
