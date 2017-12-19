package modules;

/**
 * Meal Module Class ==> Work with "Meal Table"
 */
public class Meal {
    private int id;
    private String nom;
    private float  calorie;

    public Meal(String nom){
        this.nom=nom;
    }

    public Meal(String nom, float calorie){
        this.nom=nom;
        this.calorie=calorie;
    }

    public Meal(int id, String nom, float calorie){
        this.id=id;
        this.nom=nom;
        this.calorie=calorie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom(){
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getCalorie(){
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public String toString(){
        return  "id " + id + ", " +
                "nom " + nom + ", " +
                "calorie " + calorie;
    }
}