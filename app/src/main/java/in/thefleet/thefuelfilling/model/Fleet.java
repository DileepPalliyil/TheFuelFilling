package in.thefleet.thefuelfilling.model;

public class Fleet {

    private int Fleet_ID;

    private String Fleet_Reg_No;
    private String Make_Name;
    private String Model_Name;
    // private double price;
    private String Type_Name;
    private String Fuel_Type;
    private int Opening_KM;

    private double Fleet_Avg_Milage;

    private double LastQty;

    public int getFleet_ID() {
        return Fleet_ID;
    }
    public void setFleet_ID(int fleet_ID) {
        Fleet_ID = fleet_ID;
    }

    public String getRegNo() {return Fleet_Reg_No; }
    public void setRegNo(String Fleet_Reg_No) {
        this.Fleet_Reg_No = Fleet_Reg_No;
    }

    public String getMakeName() {
        return  Make_Name;
    }
    public void setMakeName(String Make_Name) {
        this.Make_Name = Make_Name;
    }

    public String getModelName() {
        return Model_Name;
    }
    public void setModelName(String Model_Name) {
        this.Model_Name = Model_Name;
    }

    public String getTypName() {
        return Type_Name;
    }
    public void setTypName(String Type_Name) {
        this.Type_Name = Type_Name;
    }

    public int getOpening_KM() {
        return Opening_KM;
    }

    public void setOpening_KM(int opening_KM) {
        Opening_KM = opening_KM;
    }
    public String getFuel_Type() {
        return Fuel_Type;
    }

    public void setFuel_Type(String fuel_Type) {
        Fuel_Type = fuel_Type;
    }

    public double getFleet_Avg_Milage() {
        return Fleet_Avg_Milage;
    }

    public void setFleet_Avg_Milage(double fleet_Avg_Milage) {
        Fleet_Avg_Milage = fleet_Avg_Milage;
    }

    public double getLastQty() {
        return LastQty;
    }

    public void setLastQty(double lastQty) {
        LastQty = lastQty;
    }

}