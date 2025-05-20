public class Customer {
    private int id;
    private String name;
    private String email;
    private String address;

    public Customer(int id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return name;
    }
}
