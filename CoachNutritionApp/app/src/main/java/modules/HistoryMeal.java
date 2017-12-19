package modules;

/**
 * Group K
 * 
 * @author Slimane SIROUKANE
 * @author Fatima CHIKH
 * 
 * History_Meal Module Class ==> Work with Pivot table between History and Meal "History Meal Table"
 */
public class HistoryMeal {
    private int id_history, id_meal;
    private float quantity, total_calories_HM;

    public HistoryMeal(int id_meal,int id_history, float quantity, float total_calories_HM){
        this.id_meal=id_meal;
        this.id_history=id_history;
        this.quantity=quantity;
        this.total_calories_HM=total_calories_HM;
    }

    public int getIdMeal() {
        return id_meal;
    }

    public int getIdHistory() {
        return id_history;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getTotalCaloriesHM() {
        return total_calories_HM;
    }

    public void setTotalCaloriesHM(float total_calories_HM) {
        this.total_calories_HM = total_calories_HM;
    }
}