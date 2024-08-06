package fpoly.pro1121.asm2client.Model;

import java.util.ArrayList;

public class Cart {
    private String _id;
    private String userID;
    private ArrayList<CartItem> items;

    public static class CartItem {
        private Product productID;
        private int quantity;

        public CartItem(Product productID, int quantity) {
            this.productID = productID;
            this.quantity = quantity;
        }

        public CartItem() {
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public Cart(String _id, String userID, ArrayList<CartItem> items) {
        this._id = _id;
        this.userID = userID;
        this.items = items;
    }

    public Cart() {
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

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }
}
