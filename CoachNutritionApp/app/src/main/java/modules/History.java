package modules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * History Module Class ==> Work with "History Table"
 */
public class History {
    private int id;
    private Date date;
    private float min;
    private float max;
    private float total_cal;
    private float mark;

    public History(int id, Date date, float min, float max, float total_cal, float mark){
        this.id = id;
        this.date = date;
        this.min = min;
        this.max = max;
        this.total_cal = total_cal;
        this.mark = mark;
    }

    public History(Date date, float min, float max, float total_cal){
        this.id = -1;
        this.date = date;
        this.min = min;
        this.max = max;
        this.total_cal = total_cal;
        updateMack();
    }

    public int getId() {
        return id;
    }

    public Date getDate(){
        return date;
    }    

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
        updateMack();
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        updateMack();
    }

    public float getTotalCal() {
        return total_cal;
    }

    public void setTotalCal(float total_cal) {
        this.total_cal = total_cal;
        updateMack();
    }

    public void updateMack(){
        if(total_cal < min){
            this.mark = min - total_cal;
        }else if(max < total_cal){
            this.mark = max - total_cal;
        }else{
            this.mark = 0;
        }
    }

    public float getMark() {
        return mark;
    }

    public String toString(){
        return 	"id " + id + ", " +
                "date " + new SimpleDateFormat("yyyy-MM-dd").format(date) + ", " +
                "min " + min + ", " +
                "max " + max + ", " +
                "total_cal " + total_cal +
                "mark " + mark ;
    }

    /**
     * Static Method that return any date from Date 
     * type to String recpecting "yyyy-MM-dd" format
     * 
     * @param  date 
     * @return      
     */
    public static String getDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * Method that return any String date on "yyyy-MM-dd" 
     * format to Date
     * 
     * @param  date 
     * @return      
     */
    public static Date setDate(String date) {
        try {
            return (new SimpleDateFormat("yyyy-MM-dd")).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
