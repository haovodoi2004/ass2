package fpoly.pro1121.asm2client.Model;

import java.util.ArrayList;

public class Order {
    private String _id;
    private String userID;
    private ArrayList<CartItem> item;
    private double totalAmount;

    public static class CartItem {
        private String productID;
        private int quantity;

        public CartItem(String productID, int quantity) {
            this.productID = productID;
            this.quantity = quantity;
        }

        public CartItem() {
        }

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public Order(String _id, String userID, ArrayList<CartItem> item, double totalAmount) {
        this._id = _id;
        this.userID = userID;
        this.item = item;
        this.totalAmount = totalAmount;
    }

    public Order() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<CartItem> getItem() {
        return item;
    }

    public void setItems(ArrayList<CartItem> item) {
        this.item = item;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
