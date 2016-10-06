package in.thefleet.thefuelfilling;

public class Globals{
    private static Globals instance;

    // Global variable
    private String fleetSelected;
    private double avgSelected;
    private double lqtySelected;

    private int fidSelected;
    private int sidSelected;
    private int fpSelected;

    private String imei;


    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setfleetSelected(String f){
        this.fleetSelected=f;
    }
    public String getfleetSelected(){
        return this.fleetSelected;
    }

    public void setavgSelected(double o){
        this.avgSelected=o;
    }
    public double getavgSelected(){
        return this.avgSelected;
    }

    public void setlqtySelected(double l){
        this.lqtySelected=l;
    }
    public double getlqtySelected(){
        return this.lqtySelected;
    }

    public void setFidSelected(int fid) {this.fidSelected = fid;}
    public int getFidSelected() {return this.fidSelected;}

    public int getSidSelected() {return sidSelected;}
    public void setSidSelected(int sid) {this.sidSelected = sid;}

    public int getFpSelected() {return fpSelected;}
    public void setFpSelected(int fp) {this.fpSelected = fp;}

    public String getImei() {return imei;}

    public void setImei(String imei) {this.imei = imei;}

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}

